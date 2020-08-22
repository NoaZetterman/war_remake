package me.noaz.testplugin.gamemodes.misc;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.messages.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

//This might be completely useless
/**
 * Represents one team of playrs (might make this an interface for ctf, tdm etc teams)
 *
 * @author Noa Zetterman
 * @version 2019-12-13
 */
public class Team {
    private List<PlayerExtension> players = new ArrayList<>();
    private Color teamColor;
    private ChatColor chatColor;
    private List<Location> spawnPoints;
    private Random random = new Random();;

    private int kills = 0;
    private int captures = 0;

    private Flag flag;

    /**
     * Create a new, empty team
     * @param teamColor The color this team should have, used on leather armor
     */
    public Team(Color teamColor, ChatColor chatColor) {
        this.teamColor = teamColor;
        this.chatColor = chatColor;
    }

    public Team(Color teamColor, ChatColor chatColor, Location flagLocation, Location enemyFlagLocation, GameMap map, TestPlugin plugin,
                HashMap<Player, PlayerExtension> players) {
        this.teamColor = teamColor;
        this.chatColor = chatColor;
        this.flag = new Flag(teamColor, flagLocation, enemyFlagLocation, map, plugin, players);
    }

    /**
     * @param player The player to add to this team
     */
    public void addPlayer(PlayerExtension player) {
        players.add(player);
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

    public boolean playerIsOnTeam(Player player) {
        for (PlayerExtension playerExtension:
                players) {
            if(player == playerExtension.getPlayer()) {
                return true;
            }

        }
        return false;
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

    public int getCaptures() {
        return captures;
    }

    public Flag getFlag() {
        return flag;
    }

    /**
     * Add a kill to the team
     */
    public void addKill() {
        kills++;
    }

    public void captureFlag() {
        captures++;

        for(PlayerExtension player : players) {
            ChatMessage.teamCapturedFlag(player.getPlayer(), this);
        }

    }

    public void enemyCapturedFlag() {
        for(PlayerExtension player : players) {
            ChatMessage.enemyTeamCapturedFlag(player.getPlayer(), this);
        }
    }

    /**
     * @return The total amount of kills everyone in this team has
     */
    public int getKills() {
        return kills;
    }

    /**
     * Set the spawnpoints that this team should have
     * @param spawnPoints A list with all spawnpoints of this team
     */
    public void setSpawnPoints(List<Location> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }
}
