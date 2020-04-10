package me.noaz.testplugin.Utils;

import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;

/**
 * A class containing static methods to print messages in the action bar.
 *
 * @author Noa Zetterman
 * @version 2020-04-09
 */
public class ActionBarMessage {

    /**
     * Displays a reloadmessage in the actionbar for the player, shows how much is left of reload
     *
     * @param totalReloadTimeInTicks The total time it takes to reload the gun
     * @param ticksPassedSinceStart The amount of ticks passed since the reload started
     * @param player The players PlayerExtension
     * @param itemSlot The itemslot to put the message in (0-8)
     */
    public static void reload(int totalReloadTimeInTicks, int ticksPassedSinceStart, PlayerExtension player, int itemSlot) {
        if (totalReloadTimeInTicks / 5 >= ticksPassedSinceStart) {
            player.setActionBar(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "◇◇◇◇ Reloading", itemSlot);
        } else if (totalReloadTimeInTicks * 2 / 5 >= ticksPassedSinceStart) {
            player.setActionBar(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "◆◇◇◇ Reloading", itemSlot);
        } else if (totalReloadTimeInTicks * 3 / 5 >= ticksPassedSinceStart) {
            player.setActionBar(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "◆◆◇◇ Reloading", itemSlot);
        } else if (totalReloadTimeInTicks * 4 / 5 >= ticksPassedSinceStart) {
            player.setActionBar(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "◆◆◆◇ Reloading", itemSlot);
        } else {
            player.setActionBar(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "◆◆◆◆ Reloading", itemSlot);
        }
    }

    /**
     * Prints the total amount of ammo and the amount of bullets left in the current burst in the actionbar
     *
     * @param currentClip The bullets in the current clip
     * @param currentBullets The total amount of bullets the player has
     * @param player The players PlayerExtension
     */
    public static void ammunitionCurrentAndTotal(int currentClip, int currentBullets, PlayerExtension player, int itemSlot) {
        player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentClip + " / " + currentBullets, itemSlot);
    }
}
