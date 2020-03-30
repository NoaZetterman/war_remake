package me.noaz.testplugin;

import de.Herbystar.TTA.TTA_Methods;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * This takes care of all players actionbars, and updates them accordingly
 *
 * @author Noa Zetterman
 * @version 2020-03-29
 */
public class ActionBar {

    private HashMap<Player, String> actionBars;
    private BukkitRunnable runnable;

    /**
     * Creates a HashMap to hold all actionBars.
     * Creates a BukkitRunnable that keeps the actionBars visible.
     */
    public ActionBar() {
        //Probs nothing or maybe plugin idk
        actionBars = new HashMap<>();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p : actionBars.keySet()) {
                    TTA_Methods.sendActionBar(p, actionBars.get(p));
                }
            }
        };

    }

    /**
     * Add a player to the action bar, should only be done when player joins
     * @param player The player
     */
    public void addPlayer(Player player) {
        actionBars.put(player, "");
    }

    /**
     * Remove a player from the action bar, should only be done when a player leaves
     * @param player The player
     */
    public void removePlayer(Player player) {
        actionBars.remove(player);
    }

    /**
     * Update the players action bar with a new message
     * @param player The player
     * @param message The message given as a chat message (use ChatColor for coloring etc)
     */
    public void update(Player player, String message) {
        actionBars.replace(player, message);
        TTA_Methods.sendActionBar(player,message);
    }
}
