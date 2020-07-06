package me.noaz.testplugin.dao;

import me.noaz.testplugin.player.PlayerStatistic;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDao {
    private static Connection connection;

    public PlayerDao(Connection connection) {
        PlayerDao.connection = connection;
    }

    public static void update(PlayerStatistic playerStatistic) {
        try {
            PreparedStatement updatePlayerData = connection.prepareStatement("UPDATE test.Player SET " +
                    "kills=?, deaths=?, bullets_fired=?, bullets_hit=?, " +
                    "level=?, credits=?, xp_on_level=?, headshots=? " +
                    "WHERE uuid=?");

            updatePlayerData.setInt(1, playerStatistic.getTotalKills());
            updatePlayerData.setInt(2, playerStatistic.getTotalDeaths());
            updatePlayerData.setInt(3, playerStatistic.getTotalFiredBullets());
            updatePlayerData.setInt(4, playerStatistic.getTotalFiredBulletsThatHitEnemy());
            updatePlayerData.setInt(5, playerStatistic.getLevel());
            updatePlayerData.setInt(6, playerStatistic.getCredits());
            updatePlayerData.setInt(7, playerStatistic.getXpOnCurrentLevel());
            updatePlayerData.setInt(8, playerStatistic.getTotalHeadshotKills());
            updatePlayerData.setString(9, playerStatistic.getPlayer().getUniqueId().toString());

            updatePlayerData.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PlayerStatistic get(Player player) {
        PlayerStatistic playerStatistic = null;

        try {
            PreparedStatement getPlayerData = connection.prepareStatement("SELECT * FROM test.player WHERE uuid=\"" + player.getUniqueId() + "\";");
            ResultSet result = getPlayerData.executeQuery();
            while(result.next()) {
                int totalKills = result.getInt("kills");
                int totalDeaths = result.getInt("deaths");
                int totalFiredBullets = result.getInt("bullets_fired");
                int totalFiredBulletsThatHitEnemy = result.getInt("bullets_hit");
                int xpOnCurrentLevel = result.getInt("xp_on_level");
                int level = result.getInt("level");
                int credits = result.getInt("credits");
                int totalHeadshotKills = result.getInt("headshots");
                playerStatistic = new PlayerStatistic(player, totalKills, totalDeaths, totalFiredBullets, totalFiredBulletsThatHitEnemy,
                        xpOnCurrentLevel, level, credits, totalHeadshotKills);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerStatistic;
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

    public void delete(PlayerStatistic playerStatistic) {
        //deletes the player
    }
}
