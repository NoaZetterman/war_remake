package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.gamemodes.teams.Team;
import me.noaz.testplugin.player.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TeamDeathMatch extends Game {
    public TeamDeathMatch(String worldName, HashMap<String, List<Location>> locations) {
        teams = new Team[] {new Team(Color.RED, ChatColor.RED), new Team(Color.BLUE, ChatColor.BLUE)};

        teams[0].setSpawnPoints(locations.get("redspawn"));
        teams[1].setSpawnPoints(locations.get("bluespawn"));

        init();
    }

    /**
     * Assigns a player to a team
     * @param player The player to assign a team
     * @param handler The players player handler.
     */
    @Override
    public void assignTeam(Player player, PlayerHandler handler) {
        if(teams[1].getTeamSize() == teams[0].getTeamSize()) {
            Random random = new Random();
            if(random.nextInt(2) == 0) {
                teams[1].addPlayer(player);
                handler.setTeam(teams[1]);
            } else {
                teams[0].addPlayer(player);
                handler.setTeam(teams[0]);
            }
        } else if(teams[1].getTeamSize() > teams[0].getTeamSize()) {
            teams[0].addPlayer(player);
            handler.setTeam(teams[0]);
        } else {
            teams[1].addPlayer(player);
            handler.setTeam(teams[1]);
        }
    }

    @Override
    public void end() {
        super.end();

        if(teams[0].getKills() > teams[1].getKills()) {
            Bukkit.getServer().broadcastMessage("Red won!");
        } else if(teams[0].getKills() < teams[1].getKills()) {
            Bukkit.getServer().broadcastMessage("Blue won!");
        } else {
            Bukkit.getServer().broadcastMessage("The game was a draw");
        }
    }
}