package me.noaz.testplugin.player;

import de.Herbystar.TTA.TTA_Methods;
import me.noaz.testplugin.dao.PlayerDao;
import me.noaz.testplugin.inventories.DefaultInventories;
import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.killstreaks.Killstreak;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.messages.ChatMessage;
import me.noaz.testplugin.gamemodes.misc.CustomTeam;
import me.noaz.testplugin.perk.Perk;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.guns.FireType;
import me.noaz.testplugin.weapons.guns.GunType;
import me.noaz.testplugin.weapons.guns.firemodes.BurstGun;
import me.noaz.testplugin.weapons.guns.firemodes.FullyAutomaticGun;
import me.noaz.testplugin.weapons.guns.Gun;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import me.noaz.testplugin.weapons.guns.firemodes.SingleBoltGun;
import me.noaz.testplugin.weapons.lethals.*;
import me.noaz.testplugin.weapons.tacticals.TacInsert;
import me.noaz.testplugin.weapons.tacticals.Tactical;
import me.noaz.testplugin.weapons.tacticals.TacticalEnum;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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

    private CustomTeam customTeam;
    private CustomTeam enemyCustomTeam;

    private Gun activePrimaryGun;
    private Gun activeSecondaryGun;
    private Perk activePerk;
    private LethalEnum activeLethalEnum;
    private Lethal activeLethal;
    private TacticalEnum activeTacticalEnum;
    private Tactical activeTactical;
    private Killstreak activeKillstreak;

    private String[] actionBarMessage;
    private boolean isDead = false;
    private PlayerExtension lastDamager = null;

    private BukkitRunnable respawnCountdown;


    /**
     * Initialises the handler, should ONLY be done on player login, and it should be put as
     * metadata on the player.
     *
     * @param plugin the plugin to use
     * @param player the player this handler belongs to
     */
    public PlayerExtension(TestPlugin plugin, Player player, ScoreManager scoreManager,
                           List<GunConfiguration> gunConfigurations) {
        this.plugin = plugin;
        this.player = player;
        this.scoreManager = scoreManager;
        playerInformation = PlayerDao.get(player, gunConfigurations);
        scoreManager.givePlayerNewScoreboard(player.getUniqueId());

        this.gunConfigurations = gunConfigurations;
        updateLobbyScoreboard();

        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
        DefaultInventories.setDefaultLobbyInventory(player.getInventory());

        setSelectedPrimaryGun(playerInformation.getSelectedPrimaryGun());
        setSelectedSecondaryGun(playerInformation.getSelectedSecondaryGun());

        activePerk = playerInformation.getSelectedPerk();
        activeKillstreak = playerInformation.getSelectedKillstreak();
        activeLethalEnum = playerInformation.getSelectedLethal();
        activeTacticalEnum = playerInformation.getSelectedTactical();
        setSelectedResourcepack(playerInformation.getSelectedResourcepack());

        actionBarMessage = new String[9];
        Arrays.fill(actionBarMessage, "");
    }

    /**
     * Respawn the player at a spawnpoint belonging to its team.
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
        if(enemyCustomTeam.getTeamColor() == Color.GREEN) {
            customTeam.removePlayer(this);
            enemyCustomTeam.addPlayer(this);
            CustomTeam temp = enemyCustomTeam;
            enemyCustomTeam = customTeam;
            customTeam = temp;
            BroadcastMessage.infectKill(getName());
        }
        player.setPlayerListName(customTeam.getTeamColorAsChatColor() + player.getName());

        player.setDisplayName("Lvl " + playerInformation.getLevel() + " " + customTeam.getTeamColorAsChatColor() + player.getName() + ChatColor.WHITE);

        if(killer != null && killer.getGameMode() != GameMode.SPECTATOR) {
            player.setSpectatorTarget(killer);
            player.setHealth(killer.getHealth());
        }

        respawnCountdown = new BukkitRunnable() {

            int i = 3;
            @Override
            public void run() {
                player.sendTitle("Respawning in " + i, "", 1,20,1);
                i--;
                if(customTeam == null) {
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
        activeTacticalEnum = playerInformation.getSelectedTactical();
        activeLethalEnum = playerInformation.getSelectedLethal();

        if(customTeam.getTeamColor() == Color.GREEN) {
            DefaultInventories.giveInfectedInventory(player.getInventory(), customTeam.getTeamColor());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 1, false, false, false));
            Arrays.fill(actionBarMessage, "");
        } else {
            activePrimaryGun = createGun(playerInformation.getSelectedPrimaryGun());
            activeSecondaryGun = createGun(playerInformation.getSelectedSecondaryGun());

            if(activePerk == Perk.LIGHTWEIGHT) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 0, false, false, false));
            }

            DefaultInventories.giveDefaultInGameInventory(player.getInventory(), customTeam.getTeamColor(), activePrimaryGun,
                    activeSecondaryGun, activeLethalEnum, activeTacticalEnum);
        }

        if(hasWeaponInMainHand() && playerInformation.getSelectedResourcepack() == Resourcepack.PACK_3D_DEFAULT) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 10000000, 10, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10000000, 10, false, false, false));
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 2, false, false, false));

        player.setHealth(20D);
        player.setGameMode(GameMode.ADVENTURE);

        activeLethal = activeLethalEnum.getAsWeapon(this, plugin);
        activeTactical = activeTacticalEnum.getAsWeapon(this, plugin);

        //If player uses TacInsert and has picked a new spawn location, spawn at that location instead of
        // a given spawnpoint.
        if(activeTactical instanceof TacInsert && ((TacInsert) activeTactical).hasSavedLocation()) {
            player.teleport(((TacInsert) activeTactical).getSavedLocation());
        } else {
            player.teleport(customTeam.getSpawnPoint());
        }
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

        if(customTeam != null) {
            customTeam.addKill();
        }

        if(enemyCustomTeam != null) {
            int killstreak = playerInformation.getKillstreak();
            if(activePerk == Perk.HARDLINE) {
                killstreak++;
            }

            if(killstreak == Killstreak.RESUPPLY.getKillAmount()) {
                Killstreak.RESUPPLY.use(this, customTeam, enemyCustomTeam);
            } else if(killstreak == activeKillstreak.getKillAmount()) {
                activeKillstreak.use(this, customTeam, enemyCustomTeam);
            } else if(killstreak == Killstreak.NUKE.getKillAmount()) {
                Killstreak.NUKE.use(this, customTeam, enemyCustomTeam);
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

        customTeam.captureFlag();
        enemyCustomTeam.enemyCapturedFlag();
        ChatMessage.playerCapturedFlag(player, customTeam.getTeamColorAsChatColor());
    }

    /**
     * Makes this player start playing a game at given location with correct equipment
     */
    public void startPlayingGame() {
        updateGameScoreboard();

        player.setPlayerListName(customTeam.getTeamColorAsChatColor() + player.getName());
        //TODO: Make a separate class for display name stuff
        player.setDisplayName("Lvl " + playerInformation.getLevel() + " " + customTeam.getTeamColorAsChatColor() + player.getName() + ChatColor.WHITE);

        spawn();
    }

    public void endGame(Gamemode gamemode, String winner, CustomTeam winnerCustomTeam, CustomTeam loserCustomTeam) {
        switch(gamemode) {
            case TEAM_DEATHMATCH:
                ChatMessage.displayTeamDeathmatchEndGame(winner, winnerCustomTeam, loserCustomTeam, player);
                break;
            case CAPTURE_THE_FLAG:
                ChatMessage.displayCaptureTheFlagEndGame(winner, winnerCustomTeam, loserCustomTeam, player);
                break;
            case INFECT:
                ChatMessage.displayInfectEndGame(winner, winnerCustomTeam, player);
                break;
        }

        ChatMessage.displayPersonalStats(player, playerInformation.getKillsThisGame(), playerInformation.getDeathsThisGame(),
                playerInformation.getTotalKills(), playerInformation.getTotalDeaths(), playerInformation.getXpThisGame(), playerInformation.getCreditsThisGame());
        leaveGame();
    }

    public void endGame(PlayerExtension winner, int winnerKills) {
        if(winner == this) {
            playerInformation.addFreeForAllWin();
        }

        ChatMessage.displayFreeForAllEndGame(winner, winnerKills, player);
        ChatMessage.displayPersonalStats(player, playerInformation.getKillsThisGame(), playerInformation.getDeathsThisGame(),
                playerInformation.getTotalKills(), playerInformation.getTotalDeaths(), playerInformation.getXpThisGame(), playerInformation.getCreditsThisGame());
        leaveGame();
    }

    /**
     * Ends the game for this player, teleports the player to spawn and gives spawn inventory, and other spawn configurations
     */
    public void leaveGame() {
        if(customTeam != null) {
            playerInformation.updateTotalScore();
            updateLobbyScoreboard();


            for(PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.setPlayerListName(player.getName());
            player.setDisplayName("Lvl " + playerInformation.getLevel() + " " + ChatColor.WHITE + player.getName());

            customTeam.removePlayer(this);
            enemyCustomTeam = null;
            customTeam = null;

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
        return (customTeam != null);
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
        //TODO: Change additional material to a name
        if (player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activePrimaryGun.getConfiguration().getName())) {
            return activePrimaryGun;
        } else if (player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activeSecondaryGun.getConfiguration().getName())) {
            return activeSecondaryGun;
        } else if (player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activeLethalEnum.toString())) {
            return activeLethal;
        } else if (player.getInventory().getItemInMainHand().getType() == activeLethalEnum.getAdditionalMaterial()) {
            return activeLethal;
        } else if (player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activeTacticalEnum.toString())) {
            return activeTactical;
        } else if (player.getInventory().getItemInMainHand().getType() == activeTacticalEnum.getAdditionalMaterial()) {
            return activeTactical;
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
        return (isPlayingGame() && player.getInventory().getItemInMainHand().getItemMeta() != null
                && (player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activePrimaryGun.getConfiguration().getName())
            || player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activeSecondaryGun.getConfiguration().getName())
            || player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activeLethalEnum.toString())
            || player.getInventory().getItemInMainHand().getType() == activeLethalEnum.getAdditionalMaterial()
            || player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activeTacticalEnum.toString())
            || player.getInventory().getItemInMainHand().getType() == activeTacticalEnum.getAdditionalMaterial()));
    }

    public boolean hasGunInMainHand() {
        return (isPlayingGame() && player.getInventory().getItemInMainHand().getItemMeta() != null
                && (player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activePrimaryGun.getConfiguration().getName())
                || player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(activeSecondaryGun.getConfiguration().getName())));
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
            if(gun.getName().equals(playerInformation.getSelectedPrimaryGun())) {
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
            if(gun.getName().equals(playerInformation.getSelectedSecondaryGun())) {
                return gun;
            }
        }
        return null;
    }

    public Perk getSelectedPerk() {
        return playerInformation.getSelectedPerk();
    }

    public LethalEnum getSelectedLethal() {
        return playerInformation.getSelectedLethal();
    }

    public TacticalEnum getSelectedTactical() {
        return playerInformation.getSelectedTactical();
    }

    public Killstreak getSelectedKillstreak() {
        return playerInformation.getSelectedKillstreak();
    }

    public Lethal getActiveLethal() {
        return activeLethal;
    }

    public Perk getActivePerk() {
        return activePerk;
    }

    public Killstreak getActiveKillstreak() {
        return activeKillstreak;
    }

    public ChatColor getTeamChatColor() {
        if(customTeam != null) {
            return customTeam.getTeamColorAsChatColor();
        } else {
            return ChatColor.WHITE;
        }
    }

    public Color getTeamColor() {
        if(customTeam != null) {
            return customTeam.getTeamColor();
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

    public void setSelectedLethal(LethalEnum lethal) {
        playerInformation.setSelectedLethal(lethal);
    }

    public void setSelectedTactical(TacticalEnum tactical) {
        playerInformation.setSelectedTactical(tactical);
    }

    public void buyGun(String gunName, List<GunConfiguration> gunConfigurations) {
        for(GunConfiguration gun: gunConfigurations) {
            if(gun.getName().equals(gunName)) {
                changeCredits(-gun.getCostToBuy());

                playerInformation.addGun(gunName);
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

    public void buyLethal(LethalEnum lethal) {
        changeCredits(-lethal.getCostToBuy());

        playerInformation.addLethal(lethal);
    }

    public void buyTactical(TacticalEnum tactical) {
        changeCredits(-tactical.getCostToBuy());

        playerInformation.addTactical(tactical);
    }

    private Gun createGun(String gunName) {
        GunConfiguration gunConfigurationForNewGun = null;
        for(GunConfiguration gun : gunConfigurations) {
            if(gun.getName().equals(gunName)) {
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
            default:
                gunToChange = null;
        }

        return gunToChange;
    }

    /**
     * @param customTeam The team that the player should be in
     */ //Makes a 2 way relation which is weird?
    public void setTeam(CustomTeam customTeam, CustomTeam enemyCustomTeam) {
        this.customTeam = customTeam;
        this.enemyCustomTeam = enemyCustomTeam;
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
        if(!isDead) {
            int itemSlot = player.getInventory().getHeldItemSlot();
            TTA_Methods.sendActionBar(player, actionBarMessage[itemSlot]);
        } else {
            TTA_Methods.sendActionBar(player, "");
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean ownsGun(String name) {
        return playerInformation.hasGun(name);
    }

    public boolean ownsKillstreak(Killstreak killstreak) {
        return playerInformation.hasKillstreak(killstreak);
    }

    public boolean ownsPerk(Perk perk) {
        return playerInformation.hasPerk(perk);
    }

    public boolean ownsLethal(LethalEnum lethal) {
        return playerInformation.hasLethal(lethal);
    }

    public boolean ownsTactical(TacticalEnum tactical) {
        return playerInformation.hasTactical(tactical);
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

    public PlayerExtension getLastDamager() {
        return lastDamager;
    }

    public void setLastDamager(PlayerExtension lastDamager) {
        this.lastDamager = lastDamager;
    }

    public boolean playerIsOnEnemyTeam(Player player) {
        return enemyCustomTeam.playerIsOnTeam(player);
    }
}
