package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.gamemodes.misc.CustomTeam;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.messages.PlayerListMessage;
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
        this.plugin = plugin;

        Location redFlagLocation = map.getLocationsByName("redflag").get(0);
        Location blueFlagLocation = map.getLocationsByName("blueflag").get(0);

        customTeams = new CustomTeam[] {new CustomTeam(Color.RED, ChatColor.RED, redFlagLocation, blueFlagLocation, map, plugin, players),
                            new CustomTeam(Color.BLUE, ChatColor.BLUE, blueFlagLocation, redFlagLocation, map, plugin, players)};
        customTeams[0].setSpawnPoints(map.getLocationsByName("redspawn"));
        customTeams[1].setSpawnPoints(map.getLocationsByName("bluespawn"));

        assignTeamToAllPlayers(players);
    }

    //Handle something for capturing the flag?? End on 3 caps?

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
        for(Player player : players.keySet())
        PlayerListMessage.setCaptureTheFlagHeader(player, customTeams[0].getCaptures(), customTeams[1].getCaptures());
    }

    @Override
    public void end(boolean forceEnd, Gamemode gamemode) {
        for(CustomTeam customTeam : customTeams) {
            customTeam.getFlag().stop();
        }

        String winner = "None";
        CustomTeam winnerCustomTeam = customTeams[0];
        CustomTeam loserCustomTeam = customTeams[1];
        //replace below with captures
        if(customTeams[1].getCaptures() < customTeams[0].getCaptures()) {
            winner = "Red";
            winnerCustomTeam = customTeams[0];
            loserCustomTeam = customTeams[1];
        } else if(customTeams[0].getCaptures() < customTeams[1].getCaptures()) {
            winner = "Blue";
            winnerCustomTeam = customTeams[1];
            loserCustomTeam = customTeams[0];
        }

        super.endGame(forceEnd, gamemode, winner, winnerCustomTeam, loserCustomTeam);

    }
}
