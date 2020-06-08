package me.noaz.testplugin.player;

import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.UUID;

/**
 * This class stores information regarding one players statistics, both in a game and in spawn, should be updated
 * accordingly when player gets a kill etc.
 *
 * @author Noa Zetterman
 * @version 2019-12-10
 */
public class PlayerStatistic {
    private UUID playerUUID;
    private ScoreManager scoreManager;
    private Connection connection;
    private TestPlugin plugin;


    private int[] levels;

    private int totalKills;
    private int totalDeaths;
    private int level;
    private int xpOnCurrentLevel;
    private int totalXpOnCurrentLevel;
    private int totalFiredBullets;
    private int totalFiredBulletsThatHitEnemy;
    private int totalHeadshotKills;
    private int credits;
    private int totalCaptures;

    private int kills = 0;
    private int deaths = 0;
    private int killstreak = 0;
    private int firedBullets = 0;
    private int firedBulletsThatHitEnemy = 0;
    private int creditsThisGame;
    private int xpThisGame;

    private int captures = 0;

    /**
     * Create a new player statistic for given player (should only be done from PlayerHandler constructor
     *
     * @param player The player that "owns" this object
     * @param scoreManager The ScoreManager it communicates with
     */
    public PlayerStatistic(Player player, ScoreManager scoreManager, Connection connection, TestPlugin plugin) {
        this.connection = connection;
        this.plugin = plugin;
        this.scoreManager = scoreManager;
        playerUUID = player.getUniqueId();

        this.totalCaptures = 0;

        //level quickfix
        levels = new int[] {100, 120, 150, 200, 325, 450, 700, 1000, 1500, 3000, 5000, 8500, 12000, 15000, 20000, 30000,
                45000, 60000, 80000, 100000, 150000, 200000, 300000, 400000, 500000};
        level = 1;


        //Get current player stats from database
        //String getPlayerData = "SELECT * FROM test.player WHERE player_uuid=\"" + playerUUID + "\";";
        try {
            //ResultSet result = AccessDatabase.getQueryResult(sqlStatement, getPlayerData);
            PreparedStatement getPlayerData = connection.prepareStatement("SELECT * FROM test.player WHERE uuid=\"" + playerUUID + "\";");
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        totalXpOnCurrentLevel = levels[level-1];

        scoreManager.givePlayerNewScoreboard(playerUUID);
        updateLobbyScoreboard();

        player.setLevel(level);
        addXp(0);
        player.setDisplayName("Lvl " + level + " " + player.getName());
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    /**
     * @return This players level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return This players credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * @return This players killstreak
     */
    public int getKillstreak() {
        return killstreak;
    }

    /**
     * @return The amount of kills by this player in this game
     */
    public int getKillsThisGame() {
        return kills;
    }

    /**
     @return The amount of deaths by this player in this game
     */
    public int getDeathsThisGame() {
        return deaths;
    }

    public int getFiredBullets() {
        return firedBullets;
    }

    public int getFiredBulletsThatHitEnemy() {
        return firedBulletsThatHitEnemy;
    }

    /**
     * @return The amount of credits this player have earned during the current game.
     */
    public int getCreditsThisGame() {
        return creditsThisGame;
    }

    public int getXpThisGame() {
        return xpThisGame;
    }

    /**
     * Add xp to this player, levels the player if necessary.
     * Changes scoreboard accordingly
     *
     * @param amount The amount of xp
     */
    public void addXp(int amount) {
        Player player = Bukkit.getServer().getPlayer(playerUUID);
        xpOnCurrentLevel += amount;
        xpThisGame += amount;
        if(xpOnCurrentLevel >= totalXpOnCurrentLevel) {
            level++;
            player.setLevel(level);
            xpOnCurrentLevel -= totalXpOnCurrentLevel;
            player.setExp(0);
            totalXpOnCurrentLevel = levels[level-1]; //Get amount from database
            //much xp per level.
        }

        float xpToAddInPercent = ((float) xpOnCurrentLevel) / ((float) totalXpOnCurrentLevel);
        player.setExp(xpToAddInPercent);
    }

    /**
     * Gives the player a kill and changes scoreboard accordingly
     */
    public void addKill() {
        kills++;
        killstreak++;

        updateGameScoreboard();
    }

    public void addHeadshotKill() {
        totalHeadshotKills++;
    }

    public void addCredits(int amount) {
        credits += amount;

        //Player can only earn credits in a game
        if(amount >= 0) {
            creditsThisGame += amount;
        }

        updateGameScoreboard();
    }

    public void addReward(Reward reward) {
        addXp(reward.getXp());
        addCredits(reward.getCredits());

    }

    public void addCapture() {
        captures++;
    }

    /**
     * Gives the player a death and changes scoreboard accordingly
     */
    public void addDeath() {
        deaths++;
        killstreak = 0;
        updateGameScoreboard();
    }

    /**
     * Adds a bullets to the total amount of hit bullets and changes scoreboard accordingly
     */
    public void addBulletHit() {
        firedBulletsThatHitEnemy++;
        updateGameScoreboard();

    }

    /**
     * Adds bullets to the total amount of fired bullets and changes the scoreboard accordingly
     * @param amount The amount of bullets fired
     */
    public void addBulletsShot(int amount) {
        firedBullets += amount;
        updateGameScoreboard();
    }

    /**
     * Resets the stats from last game, and updates the overall stats accordingly
     */
    public void resetGameScoreboard() {
        totalKills += kills;
        totalDeaths += deaths;
        totalFiredBullets += firedBullets;
        totalFiredBulletsThatHitEnemy += firedBulletsThatHitEnemy;

        kills = 0;
        deaths = 0;
        killstreak = 0;
        firedBullets = 0;
        firedBulletsThatHitEnemy = 0;
        xpThisGame = 0;
        creditsThisGame = 0;
        captures = 0;
    }

    /**
     * Updates the total score, and gives player the lobby scoreboard with total score
     * Updates database with new score as well.
     */
    public void updateTotalScore() {
        resetGameScoreboard();
        updateLobbyScoreboard();

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement updatePlayerData = connection.prepareStatement("UPDATE test.Player SET " +
                            "kills=?, deaths=?, bullets_fired=?, bullets_hit=?, " +
                            "level=?, credits=?, xp_on_level=?, headshots=? " +
                            "WHERE uuid=?");

                    updatePlayerData.setInt(1, totalKills);
                    updatePlayerData.setInt(2, totalDeaths);
                    updatePlayerData.setInt(3, totalFiredBullets);
                    updatePlayerData.setInt(4, totalFiredBulletsThatHitEnemy);
                    updatePlayerData.setInt(5, level);
                    updatePlayerData.setInt(6, credits);
                    updatePlayerData.setInt(7, xpOnCurrentLevel);
                    updatePlayerData.setInt(8, totalHeadshotKills);
                    updatePlayerData.setString(9, playerUUID.toString());

                    updatePlayerData.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        };

        runnable.runTaskAsynchronously(plugin);
    }

    /**
     * Updates this players score to the database non-async, used when the server is getting shutdown (not possible to
     * register new tasks during shutdown)
     */
    public void forceUpdateScore() {
        resetGameScoreboard();

        try {
            PreparedStatement updatePlayerData = connection.prepareStatement("UPDATE test.Player SET " +
                    "kills=?, deaths=?, bullets_fired=?, bullets_hit=?, " +
                    "level=?, credits=?, xp_on_level=?, headshots=? " +
                    "WHERE uuid=?");

            updatePlayerData.setInt(1, totalKills);
            updatePlayerData.setInt(2, totalDeaths);
            updatePlayerData.setInt(3, totalFiredBullets);
            updatePlayerData.setInt(4, totalFiredBulletsThatHitEnemy);
            updatePlayerData.setInt(5, level);
            updatePlayerData.setInt(6, credits);
            updatePlayerData.setInt(7, xpOnCurrentLevel);
            updatePlayerData.setInt(8, totalHeadshotKills);
            updatePlayerData.setString(9, playerUUID.toString());

            updatePlayerData.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printStatistics(Player player) {
        player.sendMessage("Statistics of this player");
        player.sendMessage("Kills:" + totalKills);
        player.sendMessage("Deaths:" + totalDeaths);
        player.sendMessage("Level:" + level);
        //TODO: Kdr and stuff, Rework.

    }

    public void updateGameScoreboard() {
        scoreManager.giveGameScoreboard(playerUUID, kills, deaths, totalKills, totalDeaths, killstreak, level, credits, xpOnCurrentLevel,
                                        totalXpOnCurrentLevel, firedBulletsThatHitEnemy, firedBullets, totalFiredBulletsThatHitEnemy, totalFiredBullets);
    }

    private void updateLobbyScoreboard() {
        scoreManager.giveLobbyScoreboard(playerUUID, totalKills, totalDeaths, level, credits, xpOnCurrentLevel,
                totalXpOnCurrentLevel, totalFiredBulletsThatHitEnemy, totalFiredBullets);
    }
}
