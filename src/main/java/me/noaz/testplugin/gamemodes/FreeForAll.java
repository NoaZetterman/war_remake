package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.gamemodes.misc.CustomTeam;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.messages.PlayerListMessage;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class FreeForAll extends Game {
    int maxKills = 25;

    public FreeForAll(GameMap map, HashMap<Player, PlayerExtension> players) {
        this.players = players;
        this.map = map;

        customTeams = new CustomTeam[] {new CustomTeam(Color.fromRGB(255,85,255), ChatColor.LIGHT_PURPLE)};

        customTeams[0].setSpawnPoints(map.getLocationsByName("ffaspawn"));

        assignTeamToAllPlayers(players);
    }

    @Override
    public boolean playersOnSameTeam(PlayerExtension player1, PlayerExtension player2) {
        //Free for all has no teams
        return false;
    }

    /**
     * Assigns a player to a team
     * @param player The player to assign a team
     */
    @Override
    public void assignTeam(PlayerExtension player) {
        customTeams[0].addPlayer(player);
        player.setTeam(customTeams[0], customTeams[0]);
    }

    @Override
    public boolean teamHasWon() {
        for(PlayerExtension player : customTeams[0].getPlayers()) {
            int kills = player.getPlayerInformation().getKillsThisGame();
            if(kills >= maxKills) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updatePlayerList() {
        String leaderName = "";
        int leaderKills = 0;
        for(PlayerExtension player : customTeams[0].getPlayers()) {
            int kills = player.getPlayerInformation().getKillsThisGame();
            if(leaderKills < kills) {
                leaderKills = kills;
                leaderName = player.getName();
            }
        }

        for(Player player : players.keySet()) {
            PlayerListMessage.setFreeForAllHeader(player, leaderName, leaderKills);
        }
    }

    @Override
    public void end(boolean forceEnd, Gamemode gamemode) {
        PlayerExtension leader = null;
        int leaderKills = 0;
        for(PlayerExtension player : customTeams[0].getPlayers()) {
            int kills = player.getPlayerInformation().getKillsThisGame();
            if(leaderKills <= kills) {
                leaderKills = kills;
                leader = player;
            }
        }

        for (PlayerExtension player : players.values()) {
            if(player.isPlayingGame()) {
                if (forceEnd) {
                    player.forceEndGame();
                } else {
                    player.endGame(leader, leaderKills);
                }
            }
        }

    }
}
