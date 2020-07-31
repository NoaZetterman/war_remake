package me.noaz.testplugin.player;

import de.Herbystar.TTA.TTA_Methods;
import me.noaz.testplugin.dao.PlayerDao;
import me.noaz.testplugin.inventories.DefaultInventories;
import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.killstreaks.Killstreak;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.messages.ChatMessage;
import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.perk.Perk;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.guns.FireType;
import me.noaz.testplugin.weapons.guns.GunType;
import me.noaz.testplugin.weapons.guns.firemodes.BurstGun;
import me.noaz.testplugin.weapons.guns.firemodes.FullyAutomaticGun;
import me.noaz.testplugin.weapons.guns.firemodes.BuckGun;
import me.noaz.testplugin.weapons.guns.Gun;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import me.noaz.testplugin.weapons.guns.firemodes.SingleBoltGun;
import me.noaz.testplugin.weapons.lethals.Grenade;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Player extension contains additional player specific methods and information
 *
 * @author Noa Zetterman
 * @version 2020-03-30
 */
public class PlayerExtension {
    private TestPlugin plugin;
    private List<GunConfiguration> gunConfigurations;

    private Player player;
    private PlayerInformation playerInformation;
    private ScoreManager scoreManager;

    private Team team;
    private Team enemyTeam;

    private Gun activePrimaryGun;
    private Gun activeSecondaryGun;
    private Perk activePerk;
    private Grenade activeLethal;
    private Killstreak activeKillstreak;

    private String[] actionBarMessage;
    private boolean isDead = false;

    private BukkitRunnable respawnCountdown;


    /**
     * Initialises the handler, should ONLY be done on player login, and it should be put as
     * metadata on the player.
     *
     * @param plugin the plugin to use
     * @param player the player this handler belongs to
     */
    public PlayerExtension(TestPlugin plugin, Player player, ScoreManager scoreManager,
                           List<GunConfiguration> gunConfigurations, Connection connection) {
        this.plugin = plugin;
        this.player = player;
        this.scoreManager = scoreManager;
        playerInformation = PlayerDao.get(player);
        scoreManager.givePlayerNewScoreboard(player.getUniqueId());

        this.gunConfigurations = gunConfigurations;
        updateLobbyScoreboard();

        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
        DefaultInventories.setDefaultLobbyInventory(player.getInventory());

        setSelectedPrimaryGun(playerInformation.getSelectedPrimaryGun());
        setSelectedSecondaryGun(playerInformation.getSelectedSecondaryGun());

        activePerk = playerInformation.getSelectedPerk();
        activeKillstreak = playerInformation.getSelectedKillstreak();
        //Get current used guns from database instead

        setSelectedResourcepack(playerInformation.getSelectedResourcepack());

        actionBarMessage = new String[9];
        Arrays.fill(actionBarMessage, "");
    }

    /**
     * Respawn the player in a one spawnpoint belonging to its team.
     */
    public void respawn(Player killer) {
        isDead = true;

        DefaultInventories.giveEmptyInventory(player.getInventory());
        player.setGameMode(GameMode.SPECTATOR);

        for(Entity passenger : player.getPassengers()) {
            player.removePassenger(passenger);
        }

        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.SLOW);

        //If killed by infected, switch team
        if(enemyTeam.getTeamColor() == Color.GREEN) {
            team.removePlayer(this);
            enemyTeam.addPlayer(this);
            Team temp = enemyTeam;
            enemyTeam = team;
            team = temp;
            BroadcastMessage.infectKill(getName());
        }
        player.setPlayerListName(team.getTeamColorAsChatColor() + player.getName());

        player.setDisplayName("Lvl " + playerInformation.getLevel() + " " + team.getTeamColorAsChatColor() + player.getName() + ChatColor.WHITE);

        if(killer != null && killer.getGameMode() != GameMode.SPECTATOR) {
            player.setSpectatorTarget(killer);
        }

        respawnCountdown = new BukkitRunnable() {

            int i = 3;
            @Override
            public void run() {
                player.sendTitle("Respawning in " + i, "", 1,20,1);
                i--;
                if(team == null) {
                    player.setGameMode(GameMode.ADVENTURE);
                    this.cancel();
                } else if(i < 0) {
                    spawn();
                    this.cancel();
                }
            }
        };

        respawnCountdown.runTaskTimer(plugin, 0, 20);
    }

    private void spawn() {
        isDead = false;

        activePerk = playerInformation.getSelectedPerk();
        activeKillstreak = playerInformation.getSelectedKillstreak();

        if(team.getTeamColor() == Color.GREEN) {
            DefaultInventories.giveInfectedInventory(player.getInventory(), team.getTeamColor());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 1, false, false, false));
            Arrays.fill(actionBarMessage, "");
        } else {
            activePrimaryGun = createGun(playerInformation.getSelectedPrimaryGun());
            activeSecondaryGun = createGun(playerInformation.getSelectedSecondaryGun());

            if(activePerk == Perk.LIGHTWEIGHT) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 0, false, false, false));
            }

            DefaultInventories.giveDefaultInGameInventory(player.getInventory(), team.getTeamColor(), activePrimaryGun, activeSecondaryGun, activeLethal);
        }

        if(hasWeaponInMainHand() && playerInformation.getSelectedResourcepack() == Resourcepack.PACK_3D_DEFAULT) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 10000000, 10, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10000000, 10, false, false, false));
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 2, false, false, false));

        player.setHealth(20D);
        player.setGameMode(GameMode.ADVENTURE);

        player.teleport(team.getSpawnPoint());


    }

    /**
     * Add one kill to this players statistics and team, if the player has a team
     * @param reward The reward type of this kill
     */
    public void addKill(Reward reward) {
        playerInformation.addKill();
        playerInformation.addReward(reward);

        updateGameScoreboard();

        if(reward == Reward.HEADSHOT_KILL) {
            addHeadshotKill();
        }

        if(activePerk == Perk.SCAVENGER) {
            activePrimaryGun.addBullets(activePrimaryGun.getConfiguration().getScavengerAmmunition());
            activeSecondaryGun.addBullets(activeSecondaryGun.getConfiguration().getScavengerAmmunition());
        }

        if(team != null) {
            team.addKill();
        }

        if(enemyTeam != null) {
            int killstreak = playerInformation.getKillstreak();
            if(activePerk == Perk.HARDLINE) {
                killstreak++;
            }

            if(killstreak == Killstreak.RESUPPLY.getKillAmount()) {
                Killstreak.RESUPPLY.use(this, team, enemyTeam);
            } else if(killstreak == activeKillstreak.getKillAmount()) {
                activeKillstreak.use(this, team, enemyTeam);
            } else if(killstreak == Killstreak.NUKE.getKillAmount()) {
                Killstreak.NUKE.use(this, team, enemyTeam);
            }
        }
    }

    private void addHeadshotKill() {
        playerInformation.addHeadshotKill();
    }

    public void addDeath() {
        playerInformation.addDeath();
        updateGameScoreboard();
    }

    /**
     * Changes the credits, may be both negative and positive.
     * @param amount The amount to change with.
     */
    public void changeCredits(int amount) {
        playerInformation.addCredits(amount);
        if(isPlayingGame()) {
            updateGameScoreboard();
        } else {
            updateLobbyScoreboard();
        }
    }

    /**
     * Give this player a flag capture
     */
    public void captureFlag() {
        playerInformation.addCapture();
        playerInformation.addReward(Reward.CAPTURE_FLAG);

        updateGameScoreboard();

        team.captureFlag();
        enemyTeam.enemyCapturedFlag();
        ChatMessage.playerCapturedFlag(player, team.getTeamColorAsChatColor());
    }

    /**
     * Makes this player start playing a game at given location with correct equipment
     */
    public void startPlayingGame(GameMap map) {
        updateGameScoreboard();


        activeLethal = new Grenade(plugin, this, map);

        player.setPlayerListName(team.getTeamColorAsChatColor() + player.getName());
        //TODO: Make a separate class for display name stuff
        player.setDisplayName("Lvl " + playerInformation.getLevel() + " " + team.getTeamColorAsChatColor() + player.getName() + ChatColor.WHITE);

        spawn();
    }

    public void endGame(Gamemode gamemode, String winner, Team winnerTeam, Team loserTeam) {
        switch(gamemode) {
            case TEAM_DEATHMATCH:
                ChatMessage.displayTeamDeathmatchEndGame(winner, winnerTeam, loserTeam, player);
                break;
            case CAPTURE_THE_FLAG:
                ChatMessage.displayCaptureTheFlagEndGame(winner, winnerTeam, loserTeam, player);
                break;
            case INFECT:
                ChatMessage.displayInfectEndGame(winner, winnerTeam, player);
                break;
        }

        ChatMessage.displayPersonalStats(player, playerInformation.getKillsThisGame(), playerInformation.getDeathsThisGame(),
                playerInformation.getTotalKills(), playerInformation.getTotalDeaths(), playerInformation.getXpThisGame(), playerInformation.getCreditsThisGame());
        leaveGame();
    }

    public void endGame(PlayerExtension winner, int winnerKills) {
        ChatMessage.displayFreeForAllEndGame(winner, winnerKills, player);
        ChatMessage.displayPersonalStats(player, playerInformation.getKillsThisGame(), playerInformation.getDeathsThisGame(),
                playerInformation.getTotalKills(), playerInformation.getTotalDeaths(), playerInformation.getXpThisGame(), playerInformation.getCreditsThisGame());
        leaveGame();
    }

    /**
     * Ends the game for this player, teleports the player to spawn and gives spawn inventory, and other spawn configurations
     */
    public void leaveGame() {
        if(team != null) {
            playerInformation.updateTotalScore();
            updateLobbyScoreboard();

            for(PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.setPlayerListName(player.getName());
            player.setDisplayName("Lvl " + playerInformation.getLevel() + " " + ChatColor.WHITE + player.getName());

            team.removePlayer(this);
            enemyTeam = null;
            team = null;

            activePrimaryGun.reset();
            activeSecondaryGun.reset();

            Arrays.fill(actionBarMessage, "");

            if(respawnCountdown != null && !respawnCountdown.isCancelled()) {
                respawnCountdown.cancel();
            }

            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20.0);
            player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
            DefaultInventories.setDefaultLobbyInventory(player.getInventory());
        }

        PlayerDao.update(playerInformation);
    }

    /**
     * Ends the game for this player correctly when the server shuts down.
     */
    public void forceEndGame() {
        activePrimaryGun.reset();
        activeSecondaryGun.reset();
        playerInformation.updateTotalScore();
        PlayerDao.update(playerInformation);

        Collection<PotionEffect> activeEffects = player.getActivePotionEffects();

        for(PotionEffect effect : activeEffects) {
            player.removePotionEffect(effect.getType());
        }

        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
    }

    /**
     * Changes the scope, scopes if player is not in scope and vice versa
     */
    public void changeScope(int slot) {
        if(slot == 1 || slot == 2) {
            if(!((Gun) getWeaponInMainHand()).justStartedReloading()) {
                if(player.hasPotionEffect(PotionEffectType.SLOW)) {
                    unScope(slot);
                } else {
                    scope(slot);
                }
            }
        }
    }

    /**
     * Makes the player be stop scoping (or nothing if the player is not scoping).
     */
    public void unScope(int slot) {
        player.removePotionEffect(PotionEffectType.SLOW);
        if(slot == activePrimaryGun.getInventorySlot()) {
            Animation.unscopeAnimation(player, activePrimaryGun, slot, plugin);
        } else if(slot == activeSecondaryGun.getInventorySlot()) {
            Animation.unscopeAnimation(player, activeSecondaryGun, slot, plugin);
        }
    }

    public void scope(int slot) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 4, false, false, false));
        if(slot == activePrimaryGun.getInventorySlot()) {
            Animation.scopeAnimation(player, activePrimaryGun, slot, plugin);
        } else if(slot == activeSecondaryGun.getInventorySlot()) {
            Animation.scopeAnimation(player, activeSecondaryGun, slot, plugin);
        }
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
    public PlayerInformation getPlayerInformation() {
        return playerInformation;
    }

    /**
     * @return The weapon the player currently has in main hand (right hand, and currently selected), null if there's no weapon in main hand.
     */
    public Weapon getWeaponInMainHand() {
        if(player.getInventory().getItemInMainHand().getType() == activePrimaryGun.getMaterial()) {
            return activePrimaryGun;
        } else if(player.getInventory().getItemInMainHand().getType() == activeSecondaryGun.getMaterial()) {
            return activeSecondaryGun;
        } else if(player.getInventory().getItemInMainHand().getType() == activeLethal.getMaterial()) {
            return activeLethal;
        } else {
            return null;
        }
    }

    public Gun getActivePrimaryGun() {
        return activePrimaryGun;
    }

    public Gun getActiveSecondaryGun() {
        return activeSecondaryGun;
    }

    /**
     * @return true if player has weapon in main hand, false otherwise
     */
    public boolean hasWeaponInMainHand() {
        return (isPlayingGame()
                && player.getInventory().getItemInMainHand().getType() == activePrimaryGun.getMaterial()
            || player.getInventory().getItemInMainHand().getType() == activeSecondaryGun.getMaterial()
            || player.getInventory().getItemInMainHand().getType() == activeLethal.getMaterial());
    }

    public boolean hasGunInMainHand() {
        return (isPlayingGame()
                && player.getInventory().getItemInMainHand().getType() == activePrimaryGun.getMaterial()
                || player.getInventory().getItemInMainHand().getType() == activeSecondaryGun.getMaterial());
    }

    public void changeMainHand(int newSlot) {
        activePrimaryGun.stopShooting();
        activeSecondaryGun.stopShooting();

        //unScope();

        if(playerInformation.getSelectedResourcepack() == Resourcepack.PACK_3D_DEFAULT) {
            if (newSlot == 1) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 10000000, 10, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10000000, 10, false, false, false));
            } else {
                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
            }
        }
    }

    /**
     * Reloads a given weapon
     * @param gun The weapon to reload
     */
    public void reloadGun(Gun gun) {
        gun.reload();
    }

    public void reloadIfGun(int slot) {
        if(slot == activePrimaryGun.getInventorySlot()) {
            reloadGun(activePrimaryGun);
        } else if(slot == activeSecondaryGun.getInventorySlot()) {
            reloadGun(activeSecondaryGun);
        }
    }

    /**
     * @return the primary weapon the player has selected
     */
    public GunConfiguration getPrimaryGunConfiguration() {
        for(GunConfiguration gun : gunConfigurations) {
            if(gun.getDisplayName().equals(playerInformation.getSelectedPrimaryGun())) {
                return gun;
            }
        }
        return null;
    }

    /**
     * @return the secondary weapon the player has selected
     */
    public GunConfiguration getSecondaryGunConfiguration() {
        for(GunConfiguration gun : gunConfigurations) {
            if(gun.getDisplayName().equals(playerInformation.getSelectedSecondaryGun())) {
                return gun;
            }
        }
        return null;
    }

    public Perk getSelectedPerk() {
        return playerInformation.getSelectedPerk();
    }

    public Killstreak getSelectedKillstreak() {
        return playerInformation.getSelectedKillstreak();
    }

    public Grenade getActiveLethal() {
        return activeLethal;
    }

    public Perk getActivePerk() {
        return activePerk;
    }

    public Killstreak getActiveKillstreak() {
        return activeKillstreak;
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
     * @param gunName The Name of the gun
     */
    public void setSelectedPrimaryGun(String gunName) {
            playerInformation.setSelectedPrimaryGun(gunName);
    }

    /**
     * Sets the secondary gun of this player
     *
     * @param gunName The guns GunConfiguration
     */
    public void setSelectedSecondaryGun(String gunName) {
        playerInformation.setSelectedSecondaryGun(gunName);
    }

    public void setSelectedKillstreak(Killstreak killstreak) {
        playerInformation.setSelectedKillstreak(killstreak);
    }

    public void setSelectedPerk(Perk perk) {
        playerInformation.setSelectedPerk(perk);
    }

    public void buyGun(String gunName, List<GunConfiguration> gunConfigurations) {
        for(GunConfiguration gun: gunConfigurations) {
            if(gun.getDisplayName().equals(gunName)) {
                changeCredits(-gun.getCostToBuy());

                if(gun.getGunType() == GunType.SECONDARY) {
                    playerInformation.addSecondary(gunName);
                } else {
                    playerInformation.addPrimary(gunName);
                }
            }
        }
    }

    public void buyKillstreak(Killstreak killstreak) {
            changeCredits(-killstreak.getCostToBuy());

            playerInformation.addKillstreak(killstreak);
    }

    public void buyPerk(Perk perk) {
        changeCredits(-perk.getCostToBuy());

        playerInformation.addPerk(perk);
    }

    private Gun createGun(String gunName) {
        GunConfiguration gunConfigurationForNewGun = null;
        for(GunConfiguration gun : gunConfigurations) {
            if(gun.getDisplayName().equals(gunName)) {
                gunConfigurationForNewGun = gun;
            }
        }

        FireType fireType = gunConfigurationForNewGun.getFireType();

        Gun gunToChange;

        switch(fireType) {
            case BURST:
                gunToChange = new BurstGun(plugin, this, playerInformation, gunConfigurationForNewGun);
                break;
            case SINGLE:
                gunToChange = new SingleBoltGun(plugin, this, playerInformation, gunConfigurationForNewGun);
                break;
            case AUTOMATIC:
                gunToChange = new FullyAutomaticGun(plugin, this, playerInformation, gunConfigurationForNewGun);
                break;
            case BUCK:
                gunToChange = new BuckGun(plugin, this, playerInformation, gunConfigurationForNewGun);
                break;
            default:
                gunToChange = null;
        }

        return gunToChange;
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

    public boolean ownsPrimaryGun(String name) {
        return playerInformation.hasPrimary(name);
    }

    public boolean ownsSecondaryGun(String name) {
        return playerInformation.hasSecondary(name);
    }

    public boolean ownsKillstreak(Killstreak killstreak) {
        return playerInformation.hasKillstreak(killstreak);
    }

    public boolean ownsPerk(Perk perk) {
        return playerInformation.hasPerk(perk);
    }

    public int getLevel() {
        return playerInformation.getLevel();
    }

    public int getCredits() {
        return playerInformation.getCredits();
    }

    public void setSelectedResourcepack(Resourcepack pack) {
        player.setResourcePack(pack.getUrl(), pack.getSha1());
        playerInformation.setSelectedResourcepack(pack);


        if(isPlayingGame()) {
            if (playerInformation.getSelectedResourcepack() == Resourcepack.PACK_2D_16X16 || player.getInventory().getHeldItemSlot() != 1) {
                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
            } else if (playerInformation.getSelectedResourcepack() == Resourcepack.PACK_3D_DEFAULT && player.getInventory().getHeldItemSlot() == 1) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 10000000, 10, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10000000, 10, false, false, false));
            }
        }
    }

    public void updateGameScoreboard() {
        scoreManager.giveGameScoreboard(player.getUniqueId(), playerInformation);
    }

    private void updateLobbyScoreboard() {
        scoreManager.giveLobbyScoreboard(player.getUniqueId(), playerInformation);
    }
}
