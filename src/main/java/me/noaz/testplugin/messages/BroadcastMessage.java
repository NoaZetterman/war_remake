package me.noaz.testplugin.messages;

import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Bukkit;
import org.bukkit.Server;

/**
 * A class containing static methods to broadcast messages in the chat.
 *
 * @author Noa Zetterman
 * @version 2020-04-09
 */
public class BroadcastMessage {
    private static final Server server = Bukkit.getServer();

    /**
     * Broadcasts a message to everyone about what next map and gamemode will be
     * @param mapName The name of the map
     * @param gamemode The gamemode that will be played
     */
    public static void gameAndGamemode(String mapName, Gamemode gamemode) {
        server.broadcastMessage("Next map: " + mapName + " Next gamemode: " + gamemode);
    }

    /**
     * Broadcasts a message that shows time left util next game
     * @param timeUntilNextGame The time left until next game
     */
    public static void timeLeftUntilGameStarts(int timeUntilNextGame) {
        server.broadcastMessage(timeUntilNextGame + "s until game starts");
    }

    public static void pickedUpFlag(PlayerExtension player) {
        server.broadcastMessage(player.getName() + " picked up the flag");
    }

    public static void droppedFlag(PlayerExtension player) {
        server.broadcastMessage(player.getName() + " dropped flag");
    }

    public static void capturedFlag(PlayerExtension player) {
        server.broadcastMessage(player.getName() + " captured the flag");
    }

    public static void launchEmp(String playerName) {
        server.broadcastMessage(playerName + " launhed an EMP");
    }

    public static void launchNuke(String playerName) {
        server.broadcastMessage(playerName + " launched a Nuke");
    }

    public static void infectKill(String killedPlayer) {
        server.broadcastMessage(killedPlayer + " was infected");
    }

}
