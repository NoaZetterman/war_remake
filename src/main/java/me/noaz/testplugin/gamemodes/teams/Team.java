package me.noaz.testplugin.gamemodes.teams;

import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.*;

//This might be completely useless
/**
 * Represents one team of playrs (might make this an interface for ctf, tdm etc teams)
 *
 * @author Noa Zetterman
 * @version 2019-12-13
 */
public class Team {
    //TODO: Think over below implementation, maybe UUID is enough?
    private List<PlayerExtension> players = new ArrayList<>();
    private Color teamColor; //Maybe useless
    private ChatColor chatColor;
    private List<Location> spawnPoints;
    private Random random;
    private int teamKills = 0;

    /**
     * Create a new, empty team
     * @param teamColor The color this team should have, used on leather armor
     */
    public Team(Color teamColor, ChatColor chatColor) {
        this.teamColor = teamColor;
        this.chatColor = chatColor;
        random = new Random();
    }

    /**
     * @param player The player to add to this team
     */
    public void addPlayer(PlayerExtension player) {
        players.add(player);
        System.out.println("Adding player to team: " + player.getName());
    }

    /**
     * @param player The player to remove from this team
     */
    public void removePlayer(PlayerExtension player) {
        players.remove(player);
    }

    /**
     * @param player The player to check if its on this team
     * @return True if the player is on this team, false otherwise
     */
    public boolean playerIsOnTeam(PlayerExtension player) {
        return players.contains(player);
    }

    /**
     * @return Returns the amount of players in this team
     */
    public int getTeamSize() {
        return players.size();
    }

    /**
     * @return PlayerExtensions of all players in this team
     */
    public List<PlayerExtension> getPlayers() {
        return players;
    }

    /**
     * @return The color of this team
     */
    public Color getTeamColor() {
        return teamColor;
    }

    public ChatColor getTeamColorAsChatColor() {
        return chatColor;
    }

    public Location getSpawnPoint() {
        return spawnPoints.get(random.nextInt(spawnPoints.size()));
    }

    /**
     * Add a kill to the team
     */
    public void addKill() {
        teamKills++;
    }

    /**
     * @return The total amount of kills everyone in this team has
     */
    public int getKills() {
        return teamKills;
    }

    /**
     * Set the spawnpoints that this team should have
     * @param spawnPoints A list with all spawnpoints of this team
     */
    public void setSpawnPoints(List<Location> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }
}
