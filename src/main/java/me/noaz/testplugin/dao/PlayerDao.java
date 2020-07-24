package me.noaz.testplugin.dao;

import com.google.gson.*;
import me.noaz.testplugin.killstreaks.Killstreak;
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
    private static final String jsonKillstreakKey = "killstreaks";

    public PlayerDao(Connection connection) {
        PlayerDao.connection = connection;
    }

    public static void update(PlayerInformation playerInformation) {
        try {
            JsonObject ownedEquipmentAsJson = new JsonObject();

            ownedEquipmentAsJson.add(jsonPrimaryGunsKey, JsonUtils.stringListToJsonArray(playerInformation.getOwnedPrimaryGuns()));
            ownedEquipmentAsJson.add(jsonSecondaryGunsKey, JsonUtils.stringListToJsonArray(playerInformation.getOwnedSecondaryGuns()));
            ownedEquipmentAsJson.add(jsonPerksKey, JsonUtils.stringListToJsonArray(playerInformation.getOwnedPerks().stream()
                                    .map(Enum::name)
                                    .collect(Collectors.toList())));
            ownedEquipmentAsJson.add(jsonKillstreakKey, JsonUtils.stringListToJsonArray(playerInformation.getOwnedKillstreaks().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList())));

            PreparedStatement updatePlayerData = connection.prepareStatement("UPDATE test.Player SET " +
                    "kills=?, deaths=?, bullets_fired=?, bullets_hit=?, " +
                    "level=?, credits=?, xp_on_level=?, headshots=?, seconds_online=?, last_online=?, " +
                    "selected_primary=?, selected_secondary=?, " +
                    "selected_perk=?, selected_killstreak=?, selected_resourcepack=?, owned_equipment=? WHERE uuid=?");

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
            updatePlayerData.setString(14, playerInformation.getSelectedKillstreak().name());
            updatePlayerData.setString(15, playerInformation.getSelectedResourcepack().name());
            updatePlayerData.setString(16, ownedEquipmentAsJson.toString());
            updatePlayerData.setString(17, playerInformation.getPlayer().getUniqueId().toString());

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
        Killstreak selectedKillstreak = Killstreak.EMP;
        Resourcepack selectedResourcepack = Resourcepack.PACK_2D_16X16;
        long timePlayedInMinutes = 0;

        JsonObject ownedEquipmentAsJson = new JsonObject();

        try {
            PreparedStatement getPlayerData = connection.prepareStatement("SELECT * FROM test.player WHERE uuid=\"" + player.getUniqueId() + "\";");
            ResultSet playerDataResultSet = getPlayerData.executeQuery();
            while(playerDataResultSet.next()) {
                totalKills = playerDataResultSet.getInt("kills");
                totalDeaths = playerDataResultSet.getInt("deaths");
                totalFiredBullets = playerDataResultSet.getInt("bullets_fired");
                totalFiredBulletsThatHitEnemy = playerDataResultSet.getInt("bullets_hit");
                xpOnCurrentLevel = playerDataResultSet.getInt("xp_on_level");
                level = playerDataResultSet.getInt("level");
                credits = playerDataResultSet.getInt("credits");
                totalHeadshotKills = playerDataResultSet.getInt("headshots");
                selectedPrimaryGun = playerDataResultSet.getString("selected_primary");
                selectedSecondaryGun = playerDataResultSet.getString("selected_secondary");
                selectedPerk = Perk.valueOf(playerDataResultSet.getString("selected_perk"));
                selectedKillstreak = Killstreak.valueOf(playerDataResultSet.getString("selected_killstreak"));
                selectedResourcepack = Resourcepack.valueOf(playerDataResultSet.getString("selected_resourcepack"));
                ownedEquipmentAsJson = new JsonParser().parse(playerDataResultSet.getString("owned_equipment")).getAsJsonObject();
                timePlayedInMinutes = playerDataResultSet.getInt("seconds_online");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Try catch on those and if they flip up then give default stuff
        List<String> ownedPrimarys = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonPrimaryGunsKey);
        List<String> ownedSecondarys = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonSecondaryGunsKey);
        List<Perk> ownedPerks = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonPerksKey).stream()
                .map(Perk::valueOf)
                .collect(Collectors.toList());
        List<Killstreak> ownedKillstreaks = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonKillstreakKey).stream()
                .map(Killstreak::valueOf)
                .collect(Collectors.toList());

        return new PlayerInformation(player, ownedPrimarys, ownedSecondarys, ownedPerks, ownedKillstreaks, selectedPrimaryGun,
                selectedSecondaryGun, selectedPerk, selectedKillstreak, selectedResourcepack, timePlayedInMinutes, totalKills, totalDeaths,
                totalFiredBullets, totalFiredBulletsThatHitEnemy, xpOnCurrentLevel, level, credits, totalHeadshotKills);

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
