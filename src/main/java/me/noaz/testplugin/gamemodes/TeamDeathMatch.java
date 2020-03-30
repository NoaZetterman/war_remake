package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.gamemodes.teams.Team;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TeamDeathMatch extends Game {
    public TeamDeathMatch(String worldName, HashMap<String, List<Location>> locations, HashMap<Player, PlayerExtension> playerExtensions) {
        teams = new Team[] {new Team(Color.RED, ChatColor.RED), new Team(Color.BLUE, ChatColor.BLUE)};

        teams[0].setSpawnPoints(locations.get("redspawn"));
        teams[1].setSpawnPoints(locations.get("bluespawn"));

        init(playerExtensions);
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
                player.setTeam(teams[1]);
            } else {
                teams[0].addPlayer(player);
                player.setTeam(teams[0]);
            }
        } else if(teams[1].getTeamSize() > teams[0].getTeamSize()) {
            teams[0].addPlayer(player);
            player.setTeam(teams[0]);
        } else {
            teams[1].addPlayer(player);
            player.setTeam(teams[1]);
        }
    }

    @Override
    public void end(HashMap<Player, PlayerExtension> players, boolean forceEnd) {
        super.end(players, forceEnd);

        if(teams[0].getKills() > teams[1].getKills()) {
            Bukkit.getServer().broadcastMessage("Red won!");
        } else if(teams[0].getKills() < teams[1].getKills()) {
            Bukkit.getServer().broadcastMessage("Blue won!");
        } else {
            Bukkit.getServer().broadcastMessage("The game was a draw");
        }
    }
}