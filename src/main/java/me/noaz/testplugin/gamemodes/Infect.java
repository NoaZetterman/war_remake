package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.gamemodes.misc.CustomTeam;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.messages.PlayerListMessage;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.*;

public class Infect extends Game {
    public Infect(GameMap map, HashMap<Player,PlayerExtension> players) {
        this.map = map;
        this.players = players;

        customTeams = new CustomTeam[] {new CustomTeam(Color.GREEN, ChatColor.DARK_GREEN), new CustomTeam(Color.BLUE, ChatColor.BLUE)};

        customTeams[0].setSpawnPoints(map.getLocationsByName("redspawn"));
        customTeams[1].setSpawnPoints(map.getLocationsByName("bluespawn"));

        for(PlayerExtension player : players.values()) {
            customTeams[1].addPlayer(player);
            player.setTeam(customTeams[1], customTeams[0]);
        }

        Random random = new Random();
        PlayerExtension root = players.values().toArray(new PlayerExtension[0])[random.nextInt(players.size())];

        customTeams[1].removePlayer(root);
        customTeams[0].addPlayer(root);
        root.setTeam(customTeams[0], customTeams[1]);

        for(PlayerExtension player: players.values()) {
            player.startPlayingGame();
        }
        //Also add special root effect stuff I guess
    }


    @Override
    public void assignTeam(PlayerExtension player) {
        if(player.isPlayingGame() && customTeams[1].playerIsOnTeam(player)) {
            customTeams[1].removePlayer(player);
        }

        customTeams[0].addPlayer(player);

        player.setTeam(customTeams[0], customTeams[1]);
    }

    @Override
    public boolean teamHasWon() {
        return customTeams[1].getPlayers().size() == 0;
    }

    @Override
    public void updatePlayerList() {
        for(Player player : players.keySet()) {
            PlayerListMessage.setInfectHeader(player, customTeams[1].getTeamSize());
        }
    }

    @Override public void end(boolean forceEnd, Gamemode gamemode) {
        String winner;
        CustomTeam winnerCustomTeam;
        CustomTeam loserCustomTeam;
        if(customTeams[1].getTeamSize() == 0) {
            winner = "Zombies";
            winnerCustomTeam = customTeams[0];
            loserCustomTeam = customTeams[1];
        } else {
            winner = "Humans";
            winnerCustomTeam = customTeams[1];
            loserCustomTeam = customTeams[0];
        }

        super.endGame(forceEnd, gamemode, winner, winnerCustomTeam, loserCustomTeam);
    }

}
