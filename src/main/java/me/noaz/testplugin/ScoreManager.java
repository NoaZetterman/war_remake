package me.noaz.testplugin;

import me.noaz.testplugin.messages.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.UUID;

/**
 * Manages the scoreboards for all players, both in lobby and in game.
 *
 * @author Noa Zetterman
 * @version 2019-12-09
 */
public class ScoreManager {
    private TestPlugin plugin;
    private HashMap<UUID, Scoreboard> scoreboards = new HashMap<>();

    // https://wiki.vg/Protocol#Player_List_Item
    // https://bukkit.org/threads/custom-player-lists-create-your-own-tab-list-display.429333/
    /**
     * @param plugin This plugin
     */
    public ScoreManager(TestPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Give the lobby scoreboard to specified player with specified values
     *
     * @param playerUUID The UUID of the player whose scoreboard should change
     * @param kills The total amount of kills this player has
     * @param deaths The total amount of deaths this player has
     * @param level The level of this player
     */
    public void giveLobbyScoreboard(UUID playerUUID, int kills, int deaths, int level, int credits,
                                    int currentXp, int totalXp, int bulletsHit, int bulletsShot) {
            Scoreboard scoreboard = scoreboards.get(playerUUID);

            scoreboard.getObjective("sidebar").unregister();
            Objective sidebar = scoreboard.registerNewObjective("sidebar", "dummy", ChatColor.DARK_RED + "" + ChatColor.BOLD + "Statistics");

            sidebar.getScore(" ").setScore(10);
            sidebar.getScore(ChatColor.GOLD + "Kills: " + ChatColor.YELLOW + kills).setScore(9);
            sidebar.getScore(ChatColor.GOLD + "Deaths: " + ChatColor.YELLOW + deaths).setScore(8);
            sidebar.getScore(ChatColor.GOLD + "Kdr: " + ChatColor.YELLOW + getRatio(kills, deaths)).setScore(7);
            sidebar.getScore(ChatColor.GOLD + "Accuracy: " + ChatColor.YELLOW +getRatio(bulletsHit*100, bulletsShot) + "%").setScore(6);
            sidebar.getScore("").setScore(5);
            sidebar.getScore(ChatColor.GOLD + "Level: " + ChatColor.YELLOW + level).setScore(4);
            sidebar.getScore(ChatColor.GOLD + "Xp: " + ChatColor.YELLOW + currentXp + "/" + totalXp).setScore(3);
            sidebar.getScore(ChatColor.GOLD + "Credits: " + ChatColor.YELLOW + credits).setScore(2);

            sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Gives the player a new game scoreboard with specified stats, used to update the scoreboard since
     * there is no simple way of modifying a score.
     *
     * @param playerUUID The UUID of the player whose scoreboard should be updated
     * @param kills The amount of kills the player has in current game
     * @param deaths The amount of deaths the player has in current game
     * @param killstreak The killstreak the player has in current game
     * @param level The level of the player
     * @param currentXp The xp the player has on the current level
     * @param totalXp The total amount of xp required for current level
     * @param bulletsHit Amount of bullets the player has hit in the current game
     * @param bulletsShot Amount of bullets the player has shot in the current game
     */
    public void giveGameScoreboard(UUID playerUUID, int kills, int deaths, int totalKills, int totalDeaths, int killstreak, int level, int credits,
                                   int currentXp, int totalXp, int bulletsHit, int bulletsShot, int totalBulletsHit, int totalBulletsShot) {
        Scoreboard scoreboard = scoreboards.get(playerUUID);

        scoreboard.getObjective("sidebar").unregister();

        Objective sidebar = scoreboard.registerNewObjective("sidebar", "dummy", ChatColor.DARK_RED + "" + ChatColor.BOLD + "Statistics");

        sidebar.getScore(" ").setScore(10);
        sidebar.getScore(ChatColor.GOLD + "Kills: " + ChatColor.GREEN + kills).setScore(9);
        sidebar.getScore(ChatColor.GOLD + "Killstreak: " + ChatColor.GREEN + killstreak).setScore(8);
        sidebar.getScore(ChatColor.GOLD + "Deaths: " + ChatColor.RED + deaths).setScore(7);
        sidebar.getScore(ChatColor.GOLD + "Kdr: " + TextUtils.getRatioAsRedOrGreenString(kills, deaths, totalKills, totalDeaths)).setScore(6);
        sidebar.getScore(ChatColor.GOLD + "Accuracy: "
                + TextUtils.getRatioAsRedOrGreenString(bulletsHit*100, bulletsShot, totalBulletsHit*100, totalBulletsShot) + "%").setScore(5);
        sidebar.getScore("").setScore(4);
        sidebar.getScore(ChatColor.GOLD + "Level: " + ChatColor.YELLOW + level).setScore(3);
        sidebar.getScore(ChatColor.GOLD + "Xp: " + ChatColor.YELLOW + currentXp + "/" +  totalXp).setScore(2);
        sidebar.getScore(ChatColor.GOLD + "Credits: " + ChatColor.YELLOW + credits).setScore(1);

        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Gives a given player a scoreboard that is used in and out of the game.
     *
     * @param playerUUID The players UUID.
     */
    public void givePlayerNewScoreboard(UUID playerUUID) {
        Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        scoreboards.put(playerUUID, scoreboard);
        scoreboard.registerNewObjective("sidebar", "dummy", "");
        plugin.getServer().getPlayer(playerUUID).setScoreboard(scoreboard);
    }

    private String getRatio(int value1, int value2) {
        double ratio = (value2 == 0) ? (double)value1 : (((double)value1)/((double)value2));
        String ratioString = Double.toString(ratio);
        if(ratio >= 100) {
            return (ratioString.length() >= 5) ? ratioString.substring(0, 5) : ratioString;
        } else {
            return (ratioString.length() >= 4) ? ratioString.substring(0, 4) : ratioString;
        }
    }
}
