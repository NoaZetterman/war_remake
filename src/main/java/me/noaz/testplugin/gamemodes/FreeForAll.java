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
import java.util.UUID;

public class FreeForAll extends Game {
    public FreeForAll(String worldName, HashMap<String, List<Location>> locations) {
        teams = new Team[] {new Team(Color.fromRGB(255,85,255), ChatColor.LIGHT_PURPLE)};

        teams[0].setSpawnPoints(locations.get("ffaspawn"));

        init();
    }

    @Override
    public boolean playersOnSameTeam(Player player1, Player player2) {
        //Free for all has no teams
        return false;
    }

    /**
     * Assigns a player to a team
     * @param player The player to assign a team
     * @param handler The players player handler.
     */
    @Override
    public void assignTeam(Player player, PlayerHandler handler) {
        teams[0].addPlayer(player);
        handler.setTeam(teams[0]);
    }

    @Override
    public void end() {
        super.end();

        UUID leaderUUID = teams[0].getPlayerUUIDs().get(0);
        int leaderKills = 0;
        for(UUID playerUUID : teams[0].getPlayerUUIDs()) {
            int kills = ((PlayerHandler) Bukkit.getServer().getPlayer(playerUUID)
                    .getMetadata("handler").get(0).value()).getPlayerStatistics().getKillsThisGame();
            if(leaderKills < kills) {
                leaderKills = kills;
                leaderUUID = playerUUID;
            }
        }

        String leaderName = Bukkit.getServer().getPlayer(leaderUUID).getName();

        Bukkit.getServer().broadcastMessage(leaderName + " won this ffa");
    }
}
