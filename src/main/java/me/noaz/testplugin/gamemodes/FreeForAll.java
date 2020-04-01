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
import java.util.UUID;

public class FreeForAll extends Game {
    public FreeForAll(String worldName, HashMap<String, List<Location>> locations, HashMap<Player, PlayerExtension> players) {
        this.players = players;

        teams = new Team[] {new Team(Color.fromRGB(255,85,255), ChatColor.LIGHT_PURPLE)};

        teams[0].setSpawnPoints(locations.get("ffaspawn"));

        init(players);
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
        teams[0].addPlayer(player);
        player.setTeam(teams[0]);
    }

    @Override
    public void end(boolean forceEnd) {
        super.end( forceEnd);

        PlayerExtension leader = players.get(0);
        int leaderKills = 0;
        for(PlayerExtension player : teams[0].getPlayers()) {
            int kills = player.getPlayerStatistics().getKillsThisGame();
            if(leaderKills < kills) {
                leaderKills = kills;
                leader = player;
            }
        }

        String leaderName = leader.getName();

        Bukkit.getServer().broadcastMessage(leaderName + " won this ffa");
    }
}
