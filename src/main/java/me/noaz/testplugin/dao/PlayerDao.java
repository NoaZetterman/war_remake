package me.noaz.testplugin.dao;

import com.google.gson.*;
import me.noaz.testplugin.killstreaks.Killstreak;
import me.noaz.testplugin.perk.Perk;
import me.noaz.testplugin.player.PlayerInformation;
import me.noaz.testplugin.player.Resourcepack;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import me.noaz.testplugin.weapons.guns.GunType;
import me.noaz.testplugin.weapons.lethals.LethalEnum;
import me.noaz.testplugin.weapons.tacticals.TacticalEnum;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDao {
    private static Connection connection;
    private static final String jsonPrimaryGunsKey = "primary_guns";
    private static final String jsonSecondaryGunsKey = "secondary_guns";
    private static final String jsonPerksKey = "perks";
    private static final String jsonKillstreakKey = "killstreaks";
    private static final String jsonLethalKey = "lethals";
    private static final String jsonTacticalKey = "tacticals";

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
            ownedEquipmentAsJson.add(jsonLethalKey, JsonUtils.stringListToJsonArray(playerInformation.getOwnedLethals().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList())));
            ownedEquipmentAsJson.add(jsonTacticalKey, JsonUtils.stringListToJsonArray(playerInformation.getOwnedTacticals().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList())));

            PreparedStatement updatePlayerData = connection.prepareStatement("UPDATE test.Player SET " +
                    "kills=?, deaths=?,  bullets_hit=?, bullets_fired=?, headshots=?," +
                    "level=?, credits=?, xp_on_level=?, flag_captures=?, free_for_all_wins=?, seconds_online=?, last_online=?, " +
                    "selected_primary=?, selected_secondary=?, selected_perk=?, selected_killstreak=?, " +
                    "selected_lethal=?, selected_tactical=?, selected_resourcepack=?, owned_equipment=? WHERE uuid=?");

            updatePlayerData.setInt(1, playerInformation.getTotalKills());
            updatePlayerData.setInt(2, playerInformation.getTotalDeaths());
            updatePlayerData.setInt(3, playerInformation.getTotalFiredBulletsThatHitEnemy());
            updatePlayerData.setInt(4, playerInformation.getTotalFiredBullets());
            updatePlayerData.setInt(5, playerInformation.getTotalHeadshotKills());
            updatePlayerData.setInt(6, playerInformation.getLevel());
            updatePlayerData.setInt(7, playerInformation.getCredits());
            updatePlayerData.setInt(8, playerInformation.getXpOnCurrentLevel());
            updatePlayerData.setInt(9, playerInformation.getTotalFlagCaptures());
            updatePlayerData.setInt(10, playerInformation.getFreeForAllWins());
            updatePlayerData.setLong(11, playerInformation.getTotalOnlineTimeInSeconds());
            updatePlayerData.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
            updatePlayerData.setString(13, playerInformation.getSelectedPrimaryGun());
            updatePlayerData.setString(14, playerInformation.getSelectedSecondaryGun());
            updatePlayerData.setString(15, playerInformation.getSelectedPerk().name());
            updatePlayerData.setString(16, playerInformation.getSelectedKillstreak().name());
            updatePlayerData.setString(17, playerInformation.getSelectedLethal().name());
            updatePlayerData.setString(18, playerInformation.getSelectedTactical().name());
            updatePlayerData.setString(19, playerInformation.getSelectedResourcepack().name());
            updatePlayerData.setString(20, ownedEquipmentAsJson.toString());
            updatePlayerData.setString(21, playerInformation.getPlayer().getUniqueId().toString());

            updatePlayerData.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PlayerInformation get(Player player, List<GunConfiguration> gunConfigurations) {
        boolean exists = false;
        try {
            PreparedStatement s = connection.prepareStatement("SELECT EXISTS(SELECT * FROM test.player WHERE uuid=?)");
            s.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = s.executeQuery();
            while(resultSet.next()) {
                exists = resultSet.getBoolean(1);
            }

            resultSet.close();
            s.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(!exists) {
            add(player);
        }

        int totalKills = 0;
        int totalDeaths = 0;
        int totalFiredBulletsThatHitEnemy = 0;
        int totalFiredBullets = 0;
        int totalHeadshotKills = 0;
        int level = -1;
        int credits = 0;
        int xpOnCurrentLevel = 0;
        int flagCaptures = 0;
        int freeForAllWins = 0;


        String selectedPrimaryGun = "";
        String selectedSecondaryGun = "";
        Perk selectedPerk = Perk.SCAVENGER;
        Killstreak selectedKillstreak = Killstreak.EMP;
        LethalEnum selectedLethal = LethalEnum.NONE;
        TacticalEnum selectedTactical = TacticalEnum.NONE;
        Resourcepack selectedResourcepack = Resourcepack.PACK_2D_16X16;
        long timePlayedInMinutes = 0;

        JsonObject ownedEquipmentAsJson = new JsonObject();

        try {
            PreparedStatement getPlayerData = connection.prepareStatement("SELECT * FROM test.player WHERE uuid=?;");
            getPlayerData.setString(1, player.getUniqueId().toString());
            ResultSet playerDataResultSet = getPlayerData.executeQuery();
            while(playerDataResultSet.next()) {
                totalKills = playerDataResultSet.getInt("kills");
                totalDeaths = playerDataResultSet.getInt("deaths");
                totalFiredBulletsThatHitEnemy = playerDataResultSet.getInt("bullets_hit");
                totalFiredBullets = playerDataResultSet.getInt("bullets_fired");
                totalHeadshotKills = playerDataResultSet.getInt("headshots");
                level = playerDataResultSet.getInt("level");
                credits = playerDataResultSet.getInt("credits");
                xpOnCurrentLevel = playerDataResultSet.getInt("xp_on_level");
                flagCaptures = playerDataResultSet.getInt("flag_captures");
                freeForAllWins = playerDataResultSet.getInt("free_for_all_wins");
                selectedPrimaryGun = playerDataResultSet.getString("selected_primary");
                selectedSecondaryGun = playerDataResultSet.getString("selected_secondary");
                selectedPerk = Perk.valueOf(playerDataResultSet.getString("selected_perk"));
                selectedLethal = LethalEnum.valueOf(playerDataResultSet.getString("selected_lethal"));
                selectedTactical = TacticalEnum.valueOf(playerDataResultSet.getString("selected_tactical"));
                selectedKillstreak = Killstreak.valueOf(playerDataResultSet.getString("selected_killstreak"));
                selectedResourcepack = Resourcepack.valueOf(playerDataResultSet.getString("selected_resourcepack"));
                ownedEquipmentAsJson = new JsonParser().parse(playerDataResultSet.getString("owned_equipment")).getAsJsonObject();
                timePlayedInMinutes = playerDataResultSet.getInt("seconds_online");
            }

            playerDataResultSet.close();
            getPlayerData.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }



        List<String> ownedPrimarys = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonPrimaryGunsKey);
        for(GunConfiguration gunConfiguration : gunConfigurations) {
            if(gunConfiguration.getGunType() != GunType.SECONDARY && gunConfiguration.getUnlockLevel() == 0
                    && gunConfiguration.getCostToBuy() == 0 && !ownedPrimarys.contains(gunConfiguration.getName())) {
                ownedPrimarys.add(gunConfiguration.getName());
            }
        }

        List<String> ownedSecondarys = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonSecondaryGunsKey);
        for(GunConfiguration gunConfiguration : gunConfigurations) {
            if(gunConfiguration.getGunType() == GunType.SECONDARY && gunConfiguration.getUnlockLevel() == 0
                    && gunConfiguration.getCostToBuy() == 0 && !ownedSecondarys.contains(gunConfiguration.getName())) {
                ownedSecondarys.add(gunConfiguration.getName());
            }
        }

        List<Perk> ownedPerks = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonPerksKey).stream()
                .map(Perk::valueOf)
                .collect(Collectors.toList());
        for(Perk perk : Perk.values()) {
            if(perk.getUnlockLevel() == 0
                    && perk.getCostToBuy() == 0 && !ownedPerks.contains(perk)) {
                ownedPerks.add(perk);
            }
        }


        List<Killstreak> ownedKillstreaks = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonKillstreakKey).stream()
                .map(Killstreak::valueOf)
                .collect(Collectors.toList());
        for(Killstreak killstreak : Killstreak.values()) {
            if(killstreak.getUnlockLevel() == 0
                    && killstreak.getCostToBuy() == 0 && !ownedKillstreaks.contains(killstreak)) {
                ownedKillstreaks.add(killstreak);
            }
        }

        List<LethalEnum> ownedLethals = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonLethalKey).stream()
                .map(LethalEnum::valueOf)
                .collect(Collectors.toList());
        for(LethalEnum lethal : LethalEnum.values()) {
            if(lethal.getUnlockLevel() == 0
                    && lethal.getCostToBuy() == 0 && !ownedLethals.contains(lethal)) {
                ownedLethals.add(lethal);
            }
        }

        List<TacticalEnum> ownedTacticals = JsonUtils.jsonArrayToStringList(ownedEquipmentAsJson, jsonTacticalKey).stream()
                .map(TacticalEnum::valueOf)
                .collect(Collectors.toList());
        for(TacticalEnum tactical : TacticalEnum.values()) {
            if(tactical.getUnlockLevel() == 0
                    && tactical.getCostToBuy() == 0 && !ownedTacticals.contains(tactical)) {
                ownedTacticals.add(tactical);
            }
        }


        //TODO: Check if currently used gun/other item is not part of owned list. Then remove that.
        if(!gunExists(selectedPrimaryGun, gunConfigurations)) {
            selectedPrimaryGun = ownedPrimarys.get(0);
        }
        if(!gunExists(selectedSecondaryGun, gunConfigurations)) {
            selectedSecondaryGun = ownedSecondarys.get(0);
        }

        return new PlayerInformation(player, ownedPrimarys, ownedSecondarys, ownedPerks, ownedKillstreaks, ownedLethals, ownedTacticals, selectedPrimaryGun,
                selectedSecondaryGun, selectedPerk, selectedKillstreak, selectedLethal, selectedTactical, selectedResourcepack, timePlayedInMinutes, totalKills, totalDeaths,
                totalFiredBullets, totalFiredBulletsThatHitEnemy, xpOnCurrentLevel, level, credits, totalHeadshotKills, flagCaptures, freeForAllWins);

    }

    private static void add(Player player) {
        try {
            PreparedStatement createPlayerIfNotExist = connection.prepareStatement("INSERT INTO test.player (uuid) VALUES (?)");
            createPlayerIfNotExist.setString(1, player.getUniqueId().toString());
            createPlayerIfNotExist.execute();
            createPlayerIfNotExist.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean gunExists(String gunName, List<GunConfiguration> gunConfigurations) {
        for(GunConfiguration gunConfiguration : gunConfigurations) {
            if(gunName.equals(gunConfiguration.getName())) {
                return true;
            }
        }

        return false;
    }
}
