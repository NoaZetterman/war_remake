package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.messages.PlayerListMessage;
import me.noaz.testplugin.gamemodes.misc.Flag;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;

public class CaptureTheFlag extends Game {
    public CaptureTheFlag(GameMap map, TestPlugin plugin, HashMap<Player, PlayerExtension> players) {
        this.players = players;
        this.map = map;

        Location redFlagLocation = map.getLocationsByName("redflag").get(0);
        Location blueFlagLocation = map.getLocationsByName("blueflag").get(0);

        teams = new Team[] {new Team(Color.RED, ChatColor.RED, redFlagLocation, blueFlagLocation, map, plugin, players),
                            new Team(Color.BLUE, ChatColor.BLUE, blueFlagLocation, redFlagLocation, map, plugin, players)};
        teams[0].setSpawnPoints(map.getLocationsByName("redspawn"));
        teams[1].setSpawnPoints(map.getLocationsByName("bluespawn"));

        assignTeamToAllPlayers(players);
    }

    //Handle something for capturing the flag?? End on 3 caps?

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
        for(Player player : players.keySet())
        PlayerListMessage.setCaptureTheFlagHeader(player, teams[1].getCaptures(), teams[0].getCaptures());
    }

    @Override
    public void end(boolean forceEnd, Gamemode gamemode) {
        for(Team team : teams) {
            team.getFlag().stop();
        }

        String winner = "None";
        Team winnerTeam = teams[0];
        Team loserTeam = teams[1];
        //replace below with captures
        if(teams[0].getCaptures() < teams[1].getCaptures()) {
            winner = "Red";
            winnerTeam = teams[0];
            loserTeam = teams[1];
        } else if(teams[1].getCaptures() < teams[0].getCaptures()) {
            winner = "Blue";
            winnerTeam = teams[1];
            loserTeam = teams[0];
        }

        super.endGame(forceEnd, gamemode, winner, winnerTeam, loserTeam);

    }
}
