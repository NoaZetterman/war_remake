package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.messages.PlayerListMessage;
import me.noaz.testplugin.gamemodes.teams.Team;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.*;

public class Infect extends Game {
    public Infect(GameMap map, HashMap<Player,PlayerExtension> players) {
        this.map = map;
        this.players = players;

        teams = new Team[] {new Team(Color.GREEN, ChatColor.DARK_GREEN), new Team(Color.BLUE, ChatColor.BLUE)};

        teams[0].setSpawnPoints(map.getLocationsByName("redspawn"));
        teams[1].setSpawnPoints(map.getLocationsByName("bluespawn"));

        for(PlayerExtension player : players.values()) {
            teams[1].addPlayer(player);
            player.setTeam(teams[1], teams[0]);
        }

        Random random = new Random();
        PlayerExtension root = players.values().toArray(new PlayerExtension[0])[random.nextInt(players.size())];

        teams[1].removePlayer(root);
        teams[0].addPlayer(root);
        root.setTeam(teams[0], teams[1]);

        for(PlayerExtension player: players.values()) {
            player.startPlayingGame();
        }
        //Also add special root effect stuff I guess
    }


    @Override
    public void assignTeam(PlayerExtension player) {
        if(player.isPlayingGame() && teams[1].playerIsOnTeam(player)) {
            teams[1].removePlayer(player);
        }

        teams[0].addPlayer(player);

        player.setTeam(teams[0], teams[1]);
    }

    @Override
    public boolean teamHasWon() {
        return teams[1].getPlayers().size() == 0;
    }

    @Override
    public void updatePlayerList() {
        for(Player player : players.keySet()) {
            PlayerListMessage.setInfectHeader(player, teams[1].getTeamSize());
        }
    }

    @Override public void end(boolean forceEnd) {
        super.end(forceEnd);

        if(teams[1].getPlayers().size() == 0) {
            BroadcastMessage.teamWonGame("Zombies");
        } else {
            BroadcastMessage.teamWonGame("Survivors");
        }
    }

}
