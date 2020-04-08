package me.noaz.testplugin.player;

import me.noaz.testplugin.AccessDatabase;
import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private Statement sqlStatement;
    private TestPlugin plugin;


    private int[] levels;

    private int totalKills;
    private int totalDeaths;
    private int level;
    private int xpOnCurrentLevel;
    private int totalXpOnCurrentLevel;
    private int totalFiredBullets;
    private int totalFiredBulletsThatHitEnemy;

    private int kills = 0;
    private int deaths = 0;
    private int killstreak = 0;
    private int firedBullets = 0;
    private int firedBulletsThatHitEnemy = 0;

    /**
     * Create a new player statistic for given player (should only be done from PlayerHandler constructor
     *
     * @param player The player that "owns" this object
     * @param scoreManager The ScoreManager it communicates with
     */
    public PlayerStatistic(Player player, ScoreManager scoreManager, Statement sqlStatement, TestPlugin plugin) {
        this.sqlStatement = sqlStatement;
        this.plugin = plugin;
        this.scoreManager = scoreManager;
        playerUUID = player.getUniqueId();

        //level "quickfix"
        levels = new int[] {100, 120, 150, 200, 325, 450, 700, 1000, 1500, 3000, 5000, 8500, 12000, 15000, 20000, 30000, 45000, 60000, 80000, 100000, 150000, 200000, 300000, 400000, 500000};
        level = 1;


        //Get current player stats from database
        String getPlayerData = "SELECT * FROM test.player WHERE player_uuid=\"" + playerUUID + "\";";
        try {
            ResultSet result = AccessDatabase.getQueryResult(sqlStatement, getPlayerData);
            while(result.next()) {
                totalKills = result.getInt("kills");
                totalDeaths = result.getInt("deaths");
                totalFiredBullets = result.getInt("bullets_fired");
                totalFiredBulletsThatHitEnemy = result.getInt("bullets_hit");
                xpOnCurrentLevel = result.getInt("xp_on_level");
                level = result.getInt("level");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        totalXpOnCurrentLevel = levels[level-1];

        scoreManager.givePlayerNewScoreboard(playerUUID);
        scoreManager.giveLobbyScoreboard(playerUUID, totalKills, totalDeaths, level, xpOnCurrentLevel,
                totalXpOnCurrentLevel, totalFiredBulletsThatHitEnemy, totalFiredBullets);

        player.setLevel(level);
        addXP(0);
        player.setDisplayName("Lvl " + level + " " + player.getName());
    }

    /**
     * @return The amount of kills by this player in this game, 0 if in lobby
     */
    public int getKillsThisGame() {
        return kills;
    }

    /**
     * @return This players level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Add xp to this player, levels the player if necessary.
     * Changes scoreboard accordingly
     *
     * @param amount The amount of xp
     */
    public void addXP(int amount) {
        Player player = Bukkit.getServer().getPlayer(playerUUID);
        xpOnCurrentLevel += amount;
        if(xpOnCurrentLevel >= totalXpOnCurrentLevel) {
            level++;
            player.setLevel(level);
            xpOnCurrentLevel -= totalXpOnCurrentLevel;
            player.setExp(0);
            totalXpOnCurrentLevel = levels[level-1]; //Get amount from database or from yml or somewhere where its defined how
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
        if(killstreak == 5) {
            //Activate some killstreak
        }
        scoreManager.giveGameScoreboard(playerUUID, kills, deaths, killstreak, level, xpOnCurrentLevel,
                totalXpOnCurrentLevel, firedBulletsThatHitEnemy, firedBullets);
    }

    /**
     * Gives the player a death and changes scoreboard accordingly
     */
    public void addDeath() {
        deaths++;
        killstreak = 0;
        scoreManager.giveGameScoreboard(playerUUID, kills, deaths, killstreak, level, xpOnCurrentLevel,
                totalXpOnCurrentLevel, firedBulletsThatHitEnemy, firedBullets);
    }

    /**
     * Adds a bullets to the total amount of hit bullets and changes scoreboard accordingly
     */
    public void addBulletHit() {
        firedBulletsThatHitEnemy++;
        scoreManager.giveGameScoreboard(playerUUID, kills, deaths, killstreak, level, xpOnCurrentLevel,
                totalXpOnCurrentLevel, firedBulletsThatHitEnemy, firedBullets);

    }

    /**
     * Adds bullets to the total amount of fired bullets and changes the scoreboard accordingly
     * @param amount The amount of bullets fired
     */
    public void addBulletsShot(int amount) {
        firedBullets += amount;
        scoreManager.giveGameScoreboard(playerUUID, kills, deaths, killstreak, level, xpOnCurrentLevel,
                totalXpOnCurrentLevel, firedBulletsThatHitEnemy, firedBullets);
    }

    /**
     * Give a game scoreboard
     */
    public void setGameScoreboard() {
        kills = 0;
        deaths = 0;
        killstreak = 0;
        firedBullets = 0;
        firedBulletsThatHitEnemy = 0;
        scoreManager.giveGameScoreboard(playerUUID, kills, deaths, killstreak, level, xpOnCurrentLevel,
                totalXpOnCurrentLevel, firedBulletsThatHitEnemy, firedBullets);

    }

    /**
     * Updates the total score, and gives player the lobby scoreboard with total score
     * Updates database with new score as well.
     */
    public void updateTotalScore() {
        totalKills += kills;
        totalDeaths += deaths;
        totalFiredBulletsThatHitEnemy += firedBulletsThatHitEnemy;
        totalFiredBullets += firedBullets;

        scoreManager.giveLobbyScoreboard(playerUUID, totalKills, totalDeaths, level, xpOnCurrentLevel,
                totalXpOnCurrentLevel, totalFiredBulletsThatHitEnemy, totalFiredBullets);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                String updatePlayerData = "UPDATE test.player SET kills=" + totalKills +
                        ", deaths=" + totalDeaths +
                        ", bullets_fired=" + totalFiredBullets +
                        ", bullets_hit=" + totalFiredBulletsThatHitEnemy +
                        ", level=" + level +
                        ", xp_on_level=" + xpOnCurrentLevel +
                        " WHERE player_uuid=\"" + playerUUID + "\";";
                try {
                    AccessDatabase.update(sqlStatement, updatePlayerData);
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
        totalKills += kills;
        totalDeaths += deaths;
        totalFiredBulletsThatHitEnemy += firedBulletsThatHitEnemy;
        totalFiredBullets += firedBullets;

        String updatePlayerData = "UPDATE test.player SET kills=" + totalKills +
                ", deaths=" + totalDeaths +
                ", bullets_fired=" + totalFiredBullets +
                ", bullets_hit=" + totalFiredBulletsThatHitEnemy +
                ", level=" + level +
                ", xp_on_level=" + xpOnCurrentLevel +
                " WHERE player_uuid=\"" + playerUUID + "\";";
        try {
            AccessDatabase.update(sqlStatement, updatePlayerData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printStatistics(Player player) {
        player.sendMessage("Statistics of this player");
        player.sendMessage("Kills:" + totalKills);
        player.sendMessage("Deaths:" + totalDeaths);
        player.sendMessage("Level:" + level);
        //Kdr and stuff, REDO THIS :V

    }
}
