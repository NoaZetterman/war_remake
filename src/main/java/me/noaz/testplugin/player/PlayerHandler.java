package me.noaz.testplugin.player;

import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.gamemodes.teams.Team;
import me.noaz.testplugin.weapons.firemodes.BurstGun;
import me.noaz.testplugin.weapons.firemodes.SingleFireGun;
import me.noaz.testplugin.weapons.firemodes.BuckGun;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Player handler contains additional information about the player that cannot be stored normally within the Player class.
 * An instance of this class should be set as metadata on all players
 *
 * @author Noa Zetterman
 * @version 2019-12-10
 */
public class PlayerHandler {
    private TestPlugin plugin;
    private Player player;
    private Weapon primaryWeapon;
    private Weapon secondaryWeapon;
    private PlayerStatistic statistics;
    private Team team;
    private HashMap<String, WeaponConfiguration> gunConfigurations;
    private List<String> ownedWeaponNames = new ArrayList<>();


    /**
     * Initialises the handler, should ONLY be done on player login, and it should be put as
     * metadata on the player.
     *
     * @param plugin the plugin to use
     * @param player the player this handler belongs to
     */
    public PlayerHandler(TestPlugin plugin, Player player, ScoreManager scoreManager,
                         HashMap<String, WeaponConfiguration> gunConfigurations, Statement sqlStatement) {
        this.plugin = plugin;
        this.player = player;
        this.gunConfigurations = gunConfigurations;
        statistics = new PlayerStatistic(player, scoreManager, sqlStatement, plugin);

        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
        DefaultInventories.setDefaultLobbyInventory(player.getInventory());

        player.setPlayerListHeaderFooter("Score stuff \n more score stuff \n ",
                "\n Pls stay xd");

        //Get current used guns from database instead
        primaryWeapon = createNewWeapon(gunConfigurations.get("Skullcrusher"), primaryWeapon);
        secondaryWeapon = createNewWeapon(gunConfigurations.get("Python"), secondaryWeapon);

        //Below should be replaced by getting a list of the owned guns from the database
        ownedWeaponNames.addAll(gunConfigurations.keySet());

    }

    /**
     * Respawn the player in a one spawnpoint belonging to its team.
     */
    public Location respawn() {
        primaryWeapon.reset();
        secondaryWeapon.reset();
        player.setHealth(20D);
        DefaultInventories.giveDefaultInGameInventory(player.getInventory(), team.getTeamColor(), primaryWeapon, secondaryWeapon);
        return team.getSpawnPoint();
    }

    /**
     * Add one kill to this players statistics and team, if the player has a team
     */
    public void addKill() {
        statistics.addKill();
        if(team != null) {
            team.addKill();
        }
    }

    /**
     * Makes this player start playing a game at given location with correct equipment
     */
    public void startPlayingGame() {
        statistics.setGameScoreboard();

        player.teleport(team.getSpawnPoint());
        player.setPlayerListName(team.getTeamColorAsChatColor() + player.getName());
        player.setDisplayName("Lvl " + statistics.getLevel() + " " + team.getTeamColorAsChatColor() + player.getName() + ChatColor.WHITE);


        primaryWeapon.reset();
        secondaryWeapon.reset();

        //Replace with transparent wep corresponding to wep name/id/whatever
        DefaultInventories.giveDefaultInGameInventory(player.getInventory(), team.getTeamColor(), primaryWeapon, secondaryWeapon);
        player.setHealth(20D);
    }

    /**
     * Ends the game for this player, teleports the player to spawn and gives spawn inventory, and other spawn configurations
     */
    public void endGame() {
        statistics.updateTotalScore();
        DefaultInventories.setDefaultLobbyInventory(player.getInventory());
        player.removePotionEffect(PotionEffectType.SLOW);

        player.setPlayerListName(player.getName());
        player.setDisplayName("Lvl " + statistics.getLevel() + " " + ChatColor.WHITE + player.getName());
        team = null;

        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
    }

    /**
     * Ends the game for this player correctly when the server shuts down.
     */
    public void forceEndGame() {
        statistics.forceUpdateScore();
        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
    }

    /**
     * Changes the scope, scopes if player is not in scope and vice versa
     */
    public void changeScope() {
        if(player.hasPotionEffect(PotionEffectType.SLOW)) {
            unScope();
        } else {
            //Maybe change the effect to not show on screen/how much speed etc
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000, 4));
        }
    }

    /**
     * Makes the player be stop scoping (or nothing if the player is not scoping).
     */
    public void unScope() {
        player.removePotionEffect(PotionEffectType.SLOW);
    }

    /**
     * @return True if the player is in a game, false otherwise
     */
    public boolean isPlayingGame() {
        return (team != null);
    }

    /**
     * @return The statistics object that belongs to this player
     */
    public PlayerStatistic getPlayerStatistics() {
        return statistics;
    }

    /**
     * @return The weapon the player currently has in main hand, null if there's no weapon in main hand.
     */
    public Weapon getWeaponInMainHand() {
        if(player.getInventory().getItemInMainHand().getType().equals(primaryWeapon.getMaterialType())) {
            return primaryWeapon;
        } else if(player.getInventory().getItemInMainHand().getType().equals(secondaryWeapon.getMaterialType())) {
            return secondaryWeapon;
        } else {
            return null;
        }
    }

    /**
     * @return true if player has weapon in main hand, false otherwise
     */
    public boolean hasWeaponInMainHand() {
        if(player.getInventory().getItemInMainHand().getType().equals(primaryWeapon.getMaterialType())) {
            return true;
        } else if(player.getInventory().getItemInMainHand().getType().equals(secondaryWeapon.getMaterialType())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reloads a given weapon
     * @param wep The weapon to reload
     */
    public void reloadWeapon(Weapon wep) {
        wep.reload();
    }

    /**
     * @return the primary weapon the player has selected
     */
    public Weapon getPrimaryWeapon() {
        return primaryWeapon;
    }

    /**
     * @return the secondary weapon the player has selected
     */
    public Weapon getSecondaryWeapon() {
        return secondaryWeapon;
    }

    /**
     * @return An array with weapon configurations this player can use
     */
    public WeaponConfiguration[] getOwnedWeapons() {
        WeaponConfiguration[] configurations = new WeaponConfiguration[ownedWeaponNames.size()];

        for(int i = 0; i < configurations.length; i++) {
            configurations[i] = gunConfigurations.get(ownedWeaponNames.get(i));
        }

        return configurations;
    }

    /**
     * @return A list of weapons this player has access to
     */
    public List<String> getOwnedWeaponNames() {
        return ownedWeaponNames;
    }

    public ChatColor getTeamChatColor() {
        if(team != null) {
            return team.getTeamColorAsChatColor();
        } else {
            return ChatColor.WHITE;
        }
    }

    public Color getTeamColor() {
        if(team != null) {
            return team.getTeamColor();
        } else {
            return Color.WHITE;
        }
    }

    /**
     * Set a weapon, primary or secondary as the gun the player currently uses
     *
     * @param weaponName The name of the weapon
     */
    public void changeWeapon(String weaponName) {
        WeaponConfiguration configuration = gunConfigurations.get(weaponName);
        if(configuration.getWeaponType().equals("Secondary")) {
            secondaryWeapon = createNewWeapon(configuration, secondaryWeapon);
        } else {
            primaryWeapon = createNewWeapon(configuration, primaryWeapon);

        }
    }

    private Weapon createNewWeapon(WeaponConfiguration configuration, Weapon weaponToChange) {
        String fireType = configuration.getFireType();

        switch(fireType) {
            case "burst":
                weaponToChange = new BurstGun(plugin, player, statistics, configuration);
                break;
            case "single":
                weaponToChange = new SingleFireGun(plugin, player, statistics, configuration);
                break;
            case "buck":
                weaponToChange = new BuckGun(plugin, player, statistics, configuration);
                break;
        }
        return weaponToChange;
    }

    /**
     * @param team The team that the player should be in
     */ //Makes a 2 way relation which is weird?
    public void setTeam(Team team) {
        this.team = team;
    }
}
