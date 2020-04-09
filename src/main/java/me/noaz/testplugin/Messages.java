package me.noaz.testplugin;

import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.boss.BossBar;

/**
 * Chat and ActionBar messages are here, use this when printing a chat/actionbar message
 *
 * @author Noa Zetterman
 * @version 2020-04-09
 */
public class Messages{

    /**
     * Displays a reloadmessage in the actionbar for the player, shows how much is left of reload
     *
     * @param totalReloadTimeInTicks The total time it takes to reload the gun
     * @param ticksPassedSinceStart The amount of ticks passed since the reload started
     * @param player The players PlayerExtension
     * @param itemSlot The itemslot to put the message in (0-8)
     */
    public static void printReloadActionbarMessage(int totalReloadTimeInTicks, int ticksPassedSinceStart, PlayerExtension player, int itemSlot) {
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
    public static void printAmmunitionActionbarMessage(int currentClip, int currentBullets, PlayerExtension player) {
        player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentClip + " / " + currentBullets);
    }

    /**
     * Broadcasts a message to everyone about what next map and gamemode will be
     * @param mapName The name of the map
     * @param gamemode The gamemode that will be played
     * @param server This server
     */
    public static void broadcastGameAndGamemode(String mapName, String gamemode, Server server) {
        server.broadcastMessage("Next map: " + mapName + " Next gamemode: " + gamemode);
    }

    /**
     * Broadcasts a message that shows time left util next game
     * @param timeUntilNextGame The time left until next game
     * @param server This server
     */
    public static void broadcastTimeLeftUntilGameStarts(int timeUntilNextGame, Server server) {
        server.broadcastMessage(timeUntilNextGame + "s until game starts");
    }

    /**
     * Broadcasts a message that is used when the game ends
     * @param server This server
     */
    public static void broadcastEndGameMessage(Server server) {
        server.broadcastMessage("Ending Game, new game in 60 sec!");
    }

    /**
     * Changes the bossbar to display a given amount of seconds until the game ends
     * @param bar The bossbar
     * @param timeUntilGameEnds The time until the game ends
     */
    public static void bossBarMessageTimeUntilGameEnds(BossBar bar, int timeUntilGameEnds) {
        bar.setTitle("Time until game ends: " + timeUntilGameEnds);
    }

    /**
     * Changes the bossbar to display a given amount of seconds until next game
     * @param bar The bossbar
     * @param timeUntilNextGame The time until the next game
     */
    public static void bossBarMessageTimeUntilNextGame(BossBar bar, int timeUntilNextGame) {
        bar.setTitle("Time until next game: " + timeUntilNextGame);
    }



}

/* Not used for now - may be used if this is a better solution
package me.noaz.testplugin;

import me.noaz.testplugin.gamemodes.Game;

public class Information {
    private static Game currentGame = null;

    //This should probably be made better..
    public static void newGame(Game game) {
        currentGame = game;
    }

    public static Game getGame() {
    return currentGame;
    }
}*/
/*
Sounds:

ghast death: Nuke

Zombie attack iron door: Maybe shoot with python?
Also entity.ghast.shoot

Skeleton hurt

iron golem hit / wither shoot both for snipers

block.note_block.snare

entity.item.break


Spider death: Enemy has charlie
Skeleton death: Captured Charlie
Silverfish kill: Kill confirmed
Pig Death: We captured alpha
Irongolem death: We captured Bravo
Ghast Death: Tactical Nuke Incomming
Enderman death: Enemy has Alpha
Creeper Death: Enemy has Alpha
Blaze Death: Rock n roll at start of game

Cave 1-13 (all same but different): Spawn sounds

levelup: Weird rock sound (I guess when u lvl up)



 */