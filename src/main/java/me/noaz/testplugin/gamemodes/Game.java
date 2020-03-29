package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.gamemodes.teams.Team;
import me.noaz.testplugin.player.PlayerHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Abstract class for a game - Holds the basic components of a gamemode / type of game.
 */
public abstract class Game {
    Team[] teams;
    private int gameLength = 360;

    /**
     * Assigns each player a team and teleport them into a starting spawnpoint
     */
    protected void init() {
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            PlayerHandler handler = (PlayerHandler) p.getMetadata("handler").get(0).value();
            assignTeam(p, handler);
            handler.startPlayingGame();
        }
    }

    //Setup at start of game - start the game by removing spawn signs and gather their locations,
    //spawning people at random spawn locations and assigning them teams (in an equal way), or ffa way
    //Then give everyone their correct loadouts etc etc this is a lot...
    //setup scoreboard

    /**
     * Assigns a player to a team
     * @param player The player to assign a team
     * @param handler The players player handler.
     */
    abstract void assignTeam(Player player, PlayerHandler handler);

    /**
     * Lets player join the current game
     * @param player the player to join current game
     */
    public void join(Player player) {
        PlayerHandler handler = (PlayerHandler) player.getMetadata("handler").get(0).value();
        if(!handler.isPlayingGame()) {
            assignTeam(player, handler);
            handler.startPlayingGame();
        }
    }

    /**
     * Lets a player leave current game.
     * @param player The player to leave the game
     * @return True if player left game, false if player is not in game
     */
    public boolean leave(Player player) {
        boolean leftTeam = false;
        for(Team t : teams) {
            if(t.playerIsOnTeam(player)) {
                t.removePlayer(player);
                leftTeam = true;
            }
        }

        if(leftTeam)
            ((PlayerHandler) player.getMetadata("handler").get(0).value()).endGame();
            //More maybe

        return leftTeam;
    }

    /**
     * Checks if two players are on the same team
     * @param player1 First player to check
     * @param player2 Second player to check
     * @return True if players are on the same team, false otherwise
     */
    public boolean playersOnSameTeam(Player player1, Player player2) {
        if(teams[1].playerIsOnTeam(player1) && teams[1].playerIsOnTeam(player2)) {
            return true;
        } else if(teams[0].playerIsOnTeam(player1) && teams[0].playerIsOnTeam(player2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return The length of the game in seconds
     */
    public int getLength() {
        return gameLength; //Differs between gamemodes and may differ depending on player amount (?)
    }

    /**
     * Ends the game. Must be called before terminating the object.
     */
    public void end() {
        for(Team t : teams) {
            for(UUID playerUUID : t.getPlayerUUIDs()) {
                Player player = Bukkit.getServer().getPlayer(playerUUID);
                ((PlayerHandler) player.getMetadata("handler").get(0).value()).endGame();
            }
        }
    }

    /**
     * Ends the game, should only be used when the server is being shutdown.
     */
    public void forceEnd() {
        for(Team t : teams) {
            for(UUID playerUUID : t.getPlayerUUIDs()) {
                Player player = Bukkit.getServer().getPlayer(playerUUID);
                ((PlayerHandler) player.getMetadata("handler").get(0).value()).forceEndGame();
            }
        }
        //Save player stats
        //More?
    }

}
