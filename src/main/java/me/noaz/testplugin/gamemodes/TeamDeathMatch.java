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
import java.util.Random;

public class TeamDeathMatch extends Game {
    public TeamDeathMatch(GameMap map, HashMap<Player, PlayerExtension> players) {
        this.players = players;
        this.map = map;

        customTeams = new CustomTeam[] {new CustomTeam(Color.RED, ChatColor.RED), new CustomTeam(Color.BLUE, ChatColor.BLUE)};

        customTeams[0].setSpawnPoints(map.getLocationsByName("redspawn"));
        customTeams[1].setSpawnPoints(map.getLocationsByName("bluespawn"));

        assignTeamToAllPlayers(players);
    }

    /**
     * Assigns a player to a team
     * @param player The player to assign a team
     */
    @Override
    public void assignTeam(PlayerExtension player) {
        if(customTeams[1].getTeamSize() == customTeams[0].getTeamSize()) {
            Random random = new Random();
            if(random.nextInt(2) == 0) {
                customTeams[1].addPlayer(player);
                player.setTeam(customTeams[1], customTeams[0]);
            } else {
                customTeams[0].addPlayer(player);
                player.setTeam(customTeams[0], customTeams[1]);
            }
        } else if(customTeams[1].getTeamSize() > customTeams[0].getTeamSize()) {
            customTeams[0].addPlayer(player);
            player.setTeam(customTeams[0], customTeams[1]);
        } else {
            customTeams[1].addPlayer(player);
            player.setTeam(customTeams[1], customTeams[0]);
        }
    }

    @Override
    public boolean teamHasWon() {
        return false;
    }

    @Override
    public void updatePlayerList() {
        for(Player player : players.keySet()) {
            PlayerListMessage.setTeamDeathMatchHeader(player, customTeams[0].getKills(), customTeams[1].getKills());
        }
    }

    @Override
    public void end(boolean forceEnd, Gamemode gamemode) {
        String winner = "None";
        CustomTeam winnerCustomTeam = customTeams[0];
        CustomTeam loserCustomTeam = customTeams[1];

        if(customTeams[0].getKills() > customTeams[1].getKills()) {
            winner = "Red";
            winnerCustomTeam = customTeams[0];
            loserCustomTeam = customTeams[1];
        } else if(customTeams[0].getKills() < customTeams[1].getKills()) {
            winner = "Blue";
            winnerCustomTeam = customTeams[1];
            loserCustomTeam = customTeams[0];
        }

        super.endGame(forceEnd, gamemode, winner, winnerCustomTeam, loserCustomTeam);
    }
}