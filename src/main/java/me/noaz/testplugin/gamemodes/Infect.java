package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.gamemodes.teams.Team;
import me.noaz.testplugin.player.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Infect extends Game {
//TODO: The rest
    public Infect(String worldName, HashMap<String, List<Location>> locations) {
        teams = new Team[] {new Team(Color.GREEN, ChatColor.GREEN), new Team(Color.BLUE, ChatColor.BLUE)};

        teams[0].setSpawnPoints(locations.get("redspawn"));
        teams[1].setSpawnPoints(locations.get("bluespawn"));

        init();

        Random random = new Random();
        int playersInGame = teams[1].getPlayerUUIDs().size();
        UUID rootUUID = teams[1].getPlayerUUIDs().get(random.nextInt(playersInGame));

        Player root = Bukkit.getServer().getPlayer(rootUUID);
        teams[1].removePlayer(root);
        teams[0].addPlayer(root);
        ((PlayerHandler) root.getMetadata("handler").get(0).value()).setTeam(teams[0]);
        //Also add special root effect stuff I guess
    }


    @Override
    public void assignTeam(Player player, PlayerHandler handler) {
        teams[1].addPlayer(player);
        handler.setTeam(teams[1]);
    }

    @Override public void end() {
        super.end();

        if(teams[1].getPlayerUUIDs().size() == 0) {
            Bukkit.getServer().broadcastMessage("Zombies won!");
        } else {
            Bukkit.getServer().broadcastMessage("Humans won!");
        }
    }

}
