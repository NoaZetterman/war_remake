package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.gamemodes.teams.Team;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Infect extends Game {
//TODO: The rest
    public Infect(String worldName, HashMap<String, List<Location>> locations, HashMap<Player,PlayerExtension> players) {
        this.players = players;

        teams = new Team[] {new Team(Color.GREEN, ChatColor.GREEN), new Team(Color.BLUE, ChatColor.BLUE)};

        teams[0].setSpawnPoints(locations.get("redspawn"));
        teams[1].setSpawnPoints(locations.get("bluespawn"));

        init(players);

        Random random = new Random();
        PlayerExtension root = players.get(random.nextInt(players.size()));

        teams[1].removePlayer(root);
        teams[0].addPlayer(root);
        root.setTeam(teams[0]);
        //Also add special root effect stuff I guess
    }


    @Override
    public void assignTeam(PlayerExtension player) {
        teams[1].addPlayer(player);
        player.setTeam(teams[1]);
    }

    @Override public void end(boolean forceEnd) {
        super.end(forceEnd);

        if(teams[1].getPlayers().size() == 0) {
            Bukkit.getServer().broadcastMessage("Zombies won!");
        } else {
            Bukkit.getServer().broadcastMessage("Humans won!");
        }
    }

}
