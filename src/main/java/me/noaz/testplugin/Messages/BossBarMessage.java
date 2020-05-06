package me.noaz.testplugin.Messages;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

/**
 * A class containing static methods to display a message in the bossbar.
 *
 * @author Noa Zetterman
 * @version 2020-04-09
 */
public class BossBarMessage {
    private static final BossBar bar = createVisibleTimer();

    /**
     * Changes the bossbar to display a given amount of seconds until the game ends
     * @param timeUntilGameEnds The time until the game ends
     */
    public static void timeUntilGameEnds(int timeUntilGameEnds) {
        String time = getTimeInClockFormat(timeUntilGameEnds);
        bar.setTitle("Time until game ends: " + time);
    }

    /**
     * Changes the bossbar to display a given amount of seconds until next game
     * @param timeUntilNextGame The time until the next game
     */
    public static void timeUntilNextGame(int timeUntilNextGame) {
        String time = getTimeInClockFormat(timeUntilNextGame);
        bar.setTitle("Time until next game: " + time);
    }

    private static String getTimeInClockFormat(int timeInSeconds) {
        String time = "";

        int seconds = timeInSeconds % 60;
        int minutes = (timeInSeconds-seconds)/60;
        time += minutes + ":";
        if(seconds >= 10) {
            time += seconds;
        } else if(seconds > 0){
            time += "0" + seconds;
        } else {
            time += "00";
        }

        return time;
    }

    private static BossBar createVisibleTimer() {
        BossBar bossBar = Bukkit.getServer().createBossBar(NamespacedKey.minecraft("timer"), "", BarColor.PURPLE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        return bossBar;
    }
}
