package me.noaz.testplugin.Utils;

import org.bukkit.boss.BossBar;

/**
 * A class containing static methods to display a message in the bossbar.
 *
 * @author Noa Zetterman
 * @version 2020-04-09
 */
public class BossBarMessage {

    /**
     * Changes the bossbar to display a given amount of seconds until the game ends
     * @param bar The bossbar
     * @param timeUntilGameEnds The time until the game ends
     */
    public static void timeUntilGameEnds(BossBar bar, int timeUntilGameEnds) {
        bar.setTitle("Time until game ends: " + timeUntilGameEnds);
    }

    /**
     * Changes the bossbar to display a given amount of seconds until next game
     * @param bar The bossbar
     * @param timeUntilNextGame The time until the next game
     */
    public static void timeUntilNextGame(BossBar bar, int timeUntilNextGame) {
        bar.setTitle("Time until next game: " + timeUntilNextGame);
    }
}
