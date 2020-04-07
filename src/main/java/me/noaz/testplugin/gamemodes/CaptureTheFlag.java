package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.gamemodes.misc.Flag;
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

public class CaptureTheFlag extends Game {
    private Flag blueFlag;
    private Flag redFlag;

    public CaptureTheFlag(String worldName, HashMap<String, List<Location>> locations, TestPlugin plugin, HashMap<Player, PlayerExtension> playerExtensions) {
        teams = new Team[] {new Team(Color.RED, ChatColor.RED), new Team(Color.BLUE, ChatColor.BLUE)};
        teams[0].setSpawnPoints(locations.get("redspawn"));
        teams[1].setSpawnPoints(locations.get("bluespawn"));

        List<Location> flags = locations.get("flags");
        //FLAGS ARE BROKEN :v

        //Flag position 0 is always red and pos 1 is always blue.
        redFlag = new Flag(Color.RED, flags.get(0), flags.get(1), worldName, plugin, playerExtensions);
        blueFlag = new Flag(Color.BLUE, flags.get(1), flags.get(0), worldName, plugin, playerExtensions);

        init(playerExtensions);
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
    public void end(boolean forceEnd) {
        blueFlag.stop();
        redFlag.stop();

        super.end(forceEnd);

        //replace below with captures
        if(redFlag.getCaptures() < blueFlag.getCaptures()) {
            //Red won by ... caps or something
            Bukkit.getServer().broadcastMessage("Red won!");
        } else if(blueFlag.getCaptures() < redFlag.getCaptures()) {
            Bukkit.getServer().broadcastMessage("Blue won!");
        } else {
            Bukkit.getServer().broadcastMessage("The game was a draw");
        }
    }
}
