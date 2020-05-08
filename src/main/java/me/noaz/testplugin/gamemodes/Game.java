package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.gamemodes.teams.Team;
import me.noaz.testplugin.player.PlayerExtension;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Abstract class for a game - Holds the basic components of a gamemode / type of game.
 */
public abstract class Game {
    protected Team[] teams;
    protected GameMap map;
    protected HashMap<Player,PlayerExtension> players;
    protected int gameLengthInSeconds = 360;
    protected TestPlugin plugin;

    /**
     * Assigns each player a team and teleport them into a starting spawnpoint
     */
    protected void assignTeamToAllPlayers(HashMap<Player, PlayerExtension> players) {
        for(PlayerExtension playerExtension : players.values()) {
            assignTeam(playerExtension);
            playerExtension.startPlayingGame();
        }
    }

    /**
     * Assigns a player to a team
     * @param player The player to assign a team
     */
    public abstract void assignTeam(PlayerExtension player);

    /**
     * Decides if a team has won the current game or not, for example if there are no
     * survivors in infect or a player have reached max amount of kills in free for all.
     * @return True if a team has won, false otherwise
     */
    public abstract boolean teamHasWon();

    /**
     * Lets player join the current game
     * @param player the player to join current game
     */
    public void join(PlayerExtension player) {
        if(!player.isPlayingGame()) {
            assignTeam(player);
            player.startPlayingGame();
        }
    }

    /**
     * Lets a player leave current game.
     * @param player The player to leave the game
     * @return True if player left game, false if player is not in game
     */
    public boolean leave(PlayerExtension player) {
        boolean leftTeam = false;
        for(Team t : teams) {
            if(t.playerIsOnTeam(player)) {
                t.removePlayer(player);
                leftTeam = true;
            }
        }

        if(leftTeam)
            player.leaveGame();
            //More maybe

        return leftTeam;
    }

    /**
     * Checks if two players are on the same team
     * @param player1 First player to check
     * @param player2 Second player to check
     * @return True if players are on the same team, false otherwise
     */
    public boolean playersOnSameTeam(PlayerExtension player1, PlayerExtension player2) {
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
        return gameLengthInSeconds;
    }

    public abstract void updatePlayerList();

    /**
     * Ends the game. Must be called before terminating the object.
     * @param forceEnd True if the game should force end (used for server shutdown), otherwise false
     */
    public void end(boolean forceEnd) {
        for (Team team : teams) {
            for (PlayerExtension player : team.getPlayers()) {
                if (forceEnd) {
                    player.forceEndGame();
                } else {
                    player.leaveGame();
                }
            }
        }
    }
}
