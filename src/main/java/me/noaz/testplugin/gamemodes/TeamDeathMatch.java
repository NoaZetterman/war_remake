package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.messages.PlayerListMessage;
import me.noaz.testplugin.gamemodes.teams.Team;
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

        teams = new Team[] {new Team(Color.RED, ChatColor.RED), new Team(Color.BLUE, ChatColor.BLUE)};

        teams[0].setSpawnPoints(map.getLocationsByName("redspawn"));
        teams[1].setSpawnPoints(map.getLocationsByName("bluespawn"));

        assignTeamToAllPlayers(players);
    }

    /**
     * Assigns a player to a team
     * @param player The player to assign a team
     */
    @Override
    public void assignTeam(PlayerExtension player) {
        if(teams[1].getTeamSize() == teams[0].getTeamSize()) {
            Random random = new Random();
            if(random.nextInt(2) == 0) {
                teams[1].addPlayer(player);
                player.setTeam(teams[1], teams[0]);
            } else {
                teams[0].addPlayer(player);
                player.setTeam(teams[0], teams[1]);
            }
        } else if(teams[1].getTeamSize() > teams[0].getTeamSize()) {
            teams[0].addPlayer(player);
            player.setTeam(teams[0], teams[1]);
        } else {
            teams[1].addPlayer(player);
            player.setTeam(teams[1], teams[0]);
        }
    }

    @Override
    public boolean teamHasWon() {
        return false;
    }

    @Override
    public void updatePlayerList() {
        for(Player player : players.keySet()) {
            PlayerListMessage.setTeamDeathMatchHeader(player, teams[0].getKills(), teams[1].getKills());
        }
    }

    @Override
    public void end(boolean forceEnd) {
        super.end(forceEnd);

        if(teams[0].getKills() > teams[1].getKills()) {
            BroadcastMessage.teamWonGame("Red");
        } else if(teams[0].getKills() < teams[1].getKills()) {
            BroadcastMessage.teamWonGame("Blue");
        } else {
            BroadcastMessage.nooneWonGame();
        }
    }
}