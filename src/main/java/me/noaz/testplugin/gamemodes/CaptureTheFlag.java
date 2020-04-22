package me.noaz.testplugin.gamemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Messages.BroadcastMessage;
import me.noaz.testplugin.Messages.PlayerListMessage;
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

    public CaptureTheFlag(String worldName, HashMap<String, List<Location>> locations, TestPlugin plugin, HashMap<Player, PlayerExtension> players) {
        this.players = players;
        teams = new Team[] {new Team(Color.RED, ChatColor.RED), new Team(Color.BLUE, ChatColor.BLUE)};
        teams[0].setSpawnPoints(locations.get("redspawn"));
        teams[1].setSpawnPoints(locations.get("bluespawn"));

        List<Location> flags = locations.get("flags");

        //Flag position 0 is always red and pos 1 is always blue.
        redFlag = new Flag(Color.RED, flags.get(0), flags.get(1), worldName, plugin, players);
        blueFlag = new Flag(Color.BLUE, flags.get(1), flags.get(0), worldName, plugin, players);

        init(players);
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
                player.setTeam(teams[1], teams[0]);
            } else {
                teams[0].addPlayer(player);
                player.setTeam(teams[0], teams[1]);
            }
        } else if(teams[1].getTeamSize() > teams[0].getTeamSize()) {
            teams[0].addPlayer(player);
            player.setTeam(teams[0], teams[1]);
        } else {
            teams[1].addPlayer(player);
            player.setTeam(teams[1], teams[0]);
        }
    }

    @Override
    public void updatePlayerList() {
        for(Player player : players.keySet())
        PlayerListMessage.setCaptureTheFlagHeader(player, blueFlag.getCaptures(), redFlag.getCaptures());
    }

    @Override
    public void end(boolean forceEnd) {
        blueFlag.stop();
        redFlag.stop();

        super.end(forceEnd);

        //replace below with captures
        if(redFlag.getCaptures() < blueFlag.getCaptures()) {
            //Red won by ... caps or something
            BroadcastMessage.teamWonGame("Red", Bukkit.getServer());
        } else if(blueFlag.getCaptures() < redFlag.getCaptures()) {
            BroadcastMessage.teamWonGame("Blue", Bukkit.getServer());
        } else {
            BroadcastMessage.nooneWonGame(Bukkit.getServer());
        }
    }
}
