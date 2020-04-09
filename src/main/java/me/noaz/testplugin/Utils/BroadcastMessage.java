package me.noaz.testplugin.Utils;

import org.bukkit.Server;

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
}
