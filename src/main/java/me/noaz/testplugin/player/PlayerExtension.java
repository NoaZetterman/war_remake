package me.noaz.testplugin.player;

import de.Herbystar.TTA.TTA_Methods;
import me.noaz.testplugin.Inventories.DefaultInventories;
import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Messages.BroadcastMessage;
import me.noaz.testplugin.Messages.ChatMessage;
import me.noaz.testplugin.gamemodes.teams.Team;
import me.noaz.testplugin.weapons.firemodes.BurstGun;
import me.noaz.testplugin.weapons.firemodes.FullyAutomaticGun;
import me.noaz.testplugin.weapons.firemodes.BuckGun;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import me.noaz.testplugin.weapons.firemodes.SingleBoltGun;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Player extension contains additional player specific methods and information
 *
 * @author Noa Zetterman
 * @version 2020-03-30
 */
public class PlayerExtension {
    private TestPlugin plugin;
    private Player player;
    private Weapon primaryWeapon;
    private Weapon secondaryWeapon;
    private PlayerStatistic statistics;
    private Team team;
    private Team enemyTeam;
    private HashMap<String, WeaponConfiguration> gunConfigurations;
    private List<String> ownedPrimaryGuns;
    private List<String> ownedSecondaryGuns;
    private List<String> ownedWeaponNames = new ArrayList<>();
    private String[] actionBarMessage;
    private boolean isDead = false;


    /**
     * Initialises the handler, should ONLY be done on player login, and it should be put as
     * metadata on the player.
     *
     * @param plugin the plugin to use
     * @param player the player this handler belongs to
     */
    public PlayerExtension(TestPlugin plugin, Player player, ScoreManager scoreManager,
                           HashMap<String, WeaponConfiguration> gunConfigurations, Connection connection) {
        this.plugin = plugin;
        this.player = player;
        this.gunConfigurations = gunConfigurations;
        statistics = new PlayerStatistic(player, scoreManager, connection, plugin);

        ownedPrimaryGuns = new ArrayList<>();
        ownedSecondaryGuns = new ArrayList<>();

        //TODO: Create a list with owned guns in database
        // And use that one

        for(String gun : gunConfigurations.keySet()) {
            if(gunConfigurations.get(gun).getWeaponType().equals("Secondary")) {
                ownedSecondaryGuns.add(gun);
            } else {
                ownedPrimaryGuns.add(gun);
            }
        }

        ownedPrimaryGuns.remove(1);

        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
        DefaultInventories.setDefaultLobbyInventory(player.getInventory());



        //Get current used guns from database instead
        primaryWeapon = createNewWeapon(gunConfigurations.get("Skullcrusher"));
        secondaryWeapon = createNewWeapon(gunConfigurations.get("Python"));

        //Below should be replaced by getting a list of the owned guns from the database
        ownedWeaponNames.addAll(gunConfigurations.keySet());

        actionBarMessage = new String[9];
        Arrays.fill(actionBarMessage, "");
    }

    /**
     * Respawn the player in a one spawnpoint belonging to its team.
     */
    public void respawn(Player killer) {
        primaryWeapon.reset();
        secondaryWeapon.reset();
        isDead = true;
        DefaultInventories.giveEmptyInventory(player.getInventory());
        player.setGameMode(GameMode.SPECTATOR);

        if(killer != null) {
            player.setSpectatorTarget(killer);
        } else {
            player.teleport(team.getSpawnPoint());
        }

        new BukkitRunnable() {

            int i = 3;
            @Override
            public void run() {
                player.sendTitle("Respawning in " + i, "", 1,20,1);
                i--;
                if(team == null) {
                    player.setGameMode(GameMode.ADVENTURE);
                    this.cancel();
                } else if(i < 0) {
                    primaryWeapon.reset();
                    secondaryWeapon.reset();

                    isDead = false;
                    player.setHealth(20D);

                    DefaultInventories.giveDefaultInGameInventory(player.getInventory(), team.getTeamColor(), primaryWeapon, secondaryWeapon);

                    player.teleport(team.getSpawnPoint());
                    player.setGameMode(GameMode.ADVENTURE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*5, 2));
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    /**
     * Add one kill to this players statistics and team, if the player has a team
     */
    public void addKill() {
        primaryWeapon.addBullets((int)Math.floor(primaryWeapon.getStartingBullets()*0.25));
        secondaryWeapon.addBullets((int)Math.floor(secondaryWeapon.getStartingBullets()*0.25));
        statistics.addKill();

        if(team != null) {
            team.addKill();
        }

        if(enemyTeam != null) {
            int killstreak = statistics.getKillstreak();
            switch (killstreak) {
                case 5:
                    primaryWeapon.addBullets(50);
                    secondaryWeapon.addBullets(50);
                    break;
                case 15:
                    //Launch emp
                    for(PlayerExtension enemyPlayer : enemyTeam.getPlayers()) {
                        enemyPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*15, 4));
                    }

                    BroadcastMessage.launchEmp(player.getName(), plugin.getServer());
                    break;
                case 21:
                    for(PlayerExtension enemyPlayer : enemyTeam.getPlayers()) {
                        //TODO: Remove double kill on player that gets nuked
                        if(!enemyPlayer.isDead() && !enemyPlayer.getPlayer().hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                            enemyPlayer.getPlayerStatistics().addDeath();
                            ChatMessage.playerShotKilled(player, enemyPlayer.getPlayer());
                            ChatMessage.playerWasShotToDeath(enemyPlayer.getPlayer(), player);
                            addKill();
                            enemyPlayer.respawn(player);

                        }
                    }
                    BroadcastMessage.launchNuke(player.getName(), plugin.getServer());
                    break;

            }
        }
    }

    public void addHeadshotKill() {
        addKill();
        statistics.addHeadshotKill();
    }

    public void addDeath() {
        statistics.addDeath();
    }

    public void addXp(int amount) {
        statistics.addXP(amount);
    }

    public void addCredits(int amount) {
        statistics.addCredits(amount);
    }

    /**
     * Makes this player start playing a game at given location with correct equipment
     */
    public void startPlayingGame() {
        statistics.setGameScoreboard();

        player.teleport(team.getSpawnPoint());
        player.setPlayerListName(team.getTeamColorAsChatColor() + player.getName());
        //TODO: Make a separate class for display name stuff
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
        primaryWeapon.reset();
        secondaryWeapon.reset();

        Arrays.fill(actionBarMessage, "");

        player.setPlayerListName(player.getName());
        player.setDisplayName("Lvl " + statistics.getLevel() + " " + ChatColor.WHITE + player.getName());
        team = null;
        enemyTeam = null;

        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
    }

    /**
     * Ends the game for this player correctly when the server shuts down.
     */
    public void forceEndGame() {
        primaryWeapon.reset();
        secondaryWeapon.reset();
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

    //TODO: Move scoping stuff to weapon
    /**
     * Makes the player be stop scoping (or nothing if the player is not scoping).
     */
    public void unScope() {
        player.removePotionEffect(PotionEffectType.SLOW);
    }

    public boolean isScoping() {
        return player.hasPotionEffect(PotionEffectType.SLOW);
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
     * @return The weapon the player currently has in main hand (right hand, and currently selected), null if there's no weapon in main hand.
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
    public HashMap<String, WeaponConfiguration> getWeaponConfigurations() {
        return gunConfigurations;
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
     * Sets the primary gun of this player
     *
     * @param gun The name of the primary gun
     */
    public void changePrimaryGun(String gun) {
        WeaponConfiguration configuration = gunConfigurations.get(gun);
        primaryWeapon = createNewWeapon(configuration);
    }

    /**
     * Sets the secondary gun of this player
     *
     * @param gun The name of the primary gun
     */
    public void changeSecondaryGun(String gun) {
        WeaponConfiguration configuration = gunConfigurations.get(gun);
        secondaryWeapon = createNewWeapon(configuration);
    }

    private Weapon createNewWeapon(WeaponConfiguration configuration) {
        String fireType = configuration.getFireType();

        Weapon weaponToChange;

        switch(fireType) {
            case "burst":
                weaponToChange = new BurstGun(plugin, this, statistics, configuration);
                break;
            case "single":
                weaponToChange = new SingleBoltGun(plugin, this, statistics, configuration);
                break;
            case "automatic":
                weaponToChange = new FullyAutomaticGun(plugin, this, statistics, configuration);
                break;
            case "buck":
                weaponToChange = new BuckGun(plugin, this, statistics, configuration);
                break;
            default:
                weaponToChange = null;
        }

        return weaponToChange;
    }

    /**
     * @param team The team that the player should be in
     */ //Makes a 2 way relation which is weird?
    public void setTeam(Team team, Team enemyTeam) {
        this.team = team;
        this.enemyTeam = enemyTeam;
    }

    public String getName() {
        return player.getName();
    }

    public Location getLocation() {
        return player.getLocation();
    }

    public Player getPlayer() {
        return player;
    }

    public void setActionBar(String message, int slot) {
        actionBarMessage[slot] = message;
        if(slot == player.getInventory().getHeldItemSlot()) {
            TTA_Methods.sendActionBar(player.getPlayer(), message);
        }
    }

    /**
     * Updates the players action bar corresponding to the current held item.
     */
    public void updateActionBar() {
        int itemSlot = player.getInventory().getHeldItemSlot();
        TTA_Methods.sendActionBar(player, actionBarMessage[itemSlot]);
    }

    public boolean isDead() {
        return isDead;
    }

    public List<String> getOwnedPrimaryGuns() {
        return ownedPrimaryGuns;
    }

    public List<String> getOwnedSecondaryGuns() {
        return ownedSecondaryGuns;
    }

    public int getLevel() {
        return statistics.getLevel();
    }
}
