package me.noaz.testplugin.Utils;

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

    /**
     * Broadcasts a message to everyone about what next map and gamemode will be
     * @param mapName The name of the map
     * @param gamemode The gamemode that will be played
     * @param server This server
     */
    public static void gameAndGamemode(String mapName, String gamemode, Server server) {
        server.broadcastMessage("Next map: " + mapName + " Next gamemode: " + gamemode);
    }

    /**
     * Broadcasts a message that shows time left util next game
     * @param timeUntilNextGame The time left until next game
     * @param server This server
     */
    public static void timeLeftUntilGameStarts(int timeUntilNextGame, Server server) {
        server.broadcastMessage(timeUntilNextGame + "s until game starts");
    }

    /**
     * Broadcasts a message that is used when the game ends
     * @param server This server
     */
    public static void endGameMessage(Server server) {
        server.broadcastMessage("Ending Game, new game in 60 sec!");
    }

    public static void pickedUpFlag(PlayerExtension player, Server server) {
        server.broadcastMessage(player.getName() + " picked up the flag");
    }

    public static void droppedFlag(PlayerExtension player, Server server) {
        server.broadcastMessage(player.getName() + " dropped flag");
    }

    public static void capturedFlag(PlayerExtension player, Server server) {
        server.broadcastMessage(player.getName() + " captured the flag");
    }

    public static void teamWonGame(String teamName, Server server) {
        server.broadcastMessage(teamName + " won!");
    }

    public static void nooneWonGame(Server server) {
        server.broadcastMessage("The game was a draw");
    }

}
