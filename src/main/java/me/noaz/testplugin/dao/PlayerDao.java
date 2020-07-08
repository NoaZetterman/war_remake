package me.noaz.testplugin.dao;

import com.google.gson.*;
import me.noaz.testplugin.perk.Perk;
import me.noaz.testplugin.player.PlayerInformation;
import me.noaz.testplugin.player.Resourcepack;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDao {
    private static Connection connection;
    private static final String jsonPrimaryGunsKey = "primary_guns";
    private static final String jsonSecondaryGunsKey = "secondary_guns";
    private static final String jsonPerksKey = "perks";

    public PlayerDao(Connection connection) {
        PlayerDao.connection = connection;
    }

    public static void update(PlayerInformation playerInformation) {
        try {
            JsonObject ownedEquipmentAsJson = new JsonObject();

            ownedEquipmentAsJson.add(jsonPrimaryGunsKey, stringListToJsonArray(playerInformation.getOwnedPrimaryGuns()));
            ownedEquipmentAsJson.add(jsonSecondaryGunsKey, stringListToJsonArray(playerInformation.getOwnedSecondaryGuns()));
            ownedEquipmentAsJson.add(jsonPerksKey, stringListToJsonArray(playerInformation.getOwnedPerks().stream()
                                    .map(Enum::name)
                                    .collect(Collectors.toList())));

            PreparedStatement updatePlayerData = connection.prepareStatement("UPDATE test.Player SET " +
                    "kills=?, deaths=?, bullets_fired=?, bullets_hit=?, " +
                    "level=?, credits=?, xp_on_level=?, headshots=?, seconds_online=?, last_online=?, " +
                    "selected_primary=?, selected_secondary=?, " +
                    "selected_perk=?, selected_resourcepack=?, owned_equipment=? WHERE uuid=?");

            updatePlayerData.setInt(1, playerInformation.getTotalKills());
            updatePlayerData.setInt(2, playerInformation.getTotalDeaths());
            updatePlayerData.setInt(3, playerInformation.getTotalFiredBullets());
            updatePlayerData.setInt(4, playerInformation.getTotalFiredBulletsThatHitEnemy());
            updatePlayerData.setInt(5, playerInformation.getLevel());
            updatePlayerData.setInt(6, playerInformation.getCredits());
            updatePlayerData.setInt(7, playerInformation.getXpOnCurrentLevel());
            updatePlayerData.setInt(8, playerInformation.getTotalHeadshotKills());
            updatePlayerData.setLong(9, playerInformation.getTotalOnlineTimeInSeconds());
            updatePlayerData.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            updatePlayerData.setString(11, playerInformation.getSelectedPrimaryGun());
            updatePlayerData.setString(12, playerInformation.getSelectedSecondaryGun());
            updatePlayerData.setString(13, playerInformation.getSelectedPerk().name());
            updatePlayerData.setString(14, playerInformation.getSelectedResourcepack().name());
            updatePlayerData.setString(15, ownedEquipmentAsJson.toString());
            updatePlayerData.setString(16, playerInformation.getPlayer().getUniqueId().toString());

            updatePlayerData.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PlayerInformation get(Player player) {
        int totalKills = 0;
        int totalDeaths = 0;
        int totalFiredBullets = 0;
        int totalFiredBulletsThatHitEnemy = 0;
        int xpOnCurrentLevel = 0;
        int level = -1;
        int credits = 0;
        int totalHeadshotKills = 0;


        String selectedPrimaryGun = "";
        String selectedSecondaryGun = "";
        Perk selectedPerk = Perk.SCAVENGER;
        Resourcepack selectedResourcepack = Resourcepack.PACK_2D_16X16;
        long timePlayedInMinutes = 0;

        JsonObject ownedEquipmentAsJson = new JsonObject();

        try {
            PreparedStatement getPlayerData = connection.prepareStatement("SELECT * FROM test.player WHERE uuid=\"" + player.getUniqueId() + "\";");
            ResultSet result = getPlayerData.executeQuery();
            while(result.next()) {
                totalKills = result.getInt("kills");
                totalDeaths = result.getInt("deaths");
                totalFiredBullets = result.getInt("bullets_fired");
                totalFiredBulletsThatHitEnemy = result.getInt("bullets_hit");
                xpOnCurrentLevel = result.getInt("xp_on_level");
                level = result.getInt("level");
                credits = result.getInt("credits");
                totalHeadshotKills = result.getInt("headshots");
                selectedPrimaryGun = result.getString("selected_primary");
                selectedSecondaryGun = result.getString("selected_secondary");
                selectedPerk = Perk.valueOf(result.getString("selected_perk"));
                selectedResourcepack = Resourcepack.valueOf(result.getString("selected_resourcepack"));
                ownedEquipmentAsJson = new JsonParser().parse(result.getString("owned_equipment")).getAsJsonObject();
                timePlayedInMinutes = result.getInt("seconds_online");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> ownedPrimarys = jsonArrayToStringList(ownedEquipmentAsJson.getAsJsonArray(jsonPrimaryGunsKey));
        List<String> ownedSecondarys = jsonArrayToStringList(ownedEquipmentAsJson.getAsJsonArray(jsonSecondaryGunsKey));
        List<Perk> ownedPerks = jsonArrayToStringList(ownedEquipmentAsJson.getAsJsonArray(jsonPerksKey)).stream()
                .map(Perk::valueOf)
                .collect(Collectors.toList());

        return new PlayerInformation(player, ownedPrimarys, ownedSecondarys, ownedPerks, selectedPrimaryGun,
                selectedSecondaryGun, selectedPerk, selectedResourcepack, timePlayedInMinutes, totalKills, totalDeaths,
                totalFiredBullets, totalFiredBulletsThatHitEnemy, xpOnCurrentLevel, level, credits, totalHeadshotKills);

    }

    private static List<String> jsonArrayToStringList(JsonArray jsonArray) {
        List<String> list = new ArrayList<>();

        for(JsonElement element : jsonArray) {
            list.add(element.getAsJsonPrimitive().getAsString());
        }

        return list;
    }

    private static JsonArray stringListToJsonArray(List<String> list) {
        JsonArray jsonArray = new JsonArray();

        for(String element : list) {
            jsonArray.add(element);
        }

        return jsonArray;
    }

    public static void add(Player player) {
        //Redo
        try {
            PreparedStatement createPlayerIfNotExist = connection.prepareStatement("INSERT IGNORE INTO test.player (uuid) VALUES (?)");
            createPlayerIfNotExist.setString(1, player.getUniqueId().toString());
            createPlayerIfNotExist.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
