package me.noaz.testplugin.player;

import de.Herbystar.TTA.TTA_Methods;
import me.noaz.testplugin.inventories.DefaultInventories;
import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.messages.ChatMessage;
import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.weapons.Firemode;
import me.noaz.testplugin.weapons.GunType;
import me.noaz.testplugin.weapons.firemodes.BurstGun;
import me.noaz.testplugin.weapons.firemodes.FullyAutomaticGun;
import me.noaz.testplugin.weapons.firemodes.BuckGun;
import me.noaz.testplugin.weapons.Gun;
import me.noaz.testplugin.weapons.GunConfiguration;
import me.noaz.testplugin.weapons.firemodes.SingleBoltGun;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private Connection connection;

    private Player player;
    private int playerId;
    private PlayerStatistic statistics;

    private Team team;
    private Team enemyTeam;

    private Gun primaryGun;
    private Gun secondaryGun;
    private List<String> ownedPrimaryGuns;
    private List<String> ownedSecondaryGuns;

    private String[] actionBarMessage;
    private boolean isDead = false;

    private Resourcepack selectedResourcepack = Resourcepack.PACK_2D_16X16;

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
        this.connection = connection;
        statistics = new PlayerStatistic(player, scoreManager, connection, plugin);

        ownedPrimaryGuns = new ArrayList<>();
        ownedSecondaryGuns = new ArrayList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement getPlayerInfo = connection.prepareStatement("SELECT player.id, player.selected_primary, player.selected_secondary" +
                            " FROM test.player WHERE player.uuid=?");
                    getPlayerInfo.setString(1, player.getUniqueId().toString());
                    ResultSet info = getPlayerInfo.executeQuery();
                    playerId = 0;
                    int selectedPrimaryGunId = 1;
                    int selectedSecondaryGunId = 2;
                    while(info.next()) {
                        playerId = info.getInt("id");
                        selectedPrimaryGunId = info.getInt("selected_primary");
                        selectedSecondaryGunId = info.getInt("selected_secondary");

                    }

                    if(playerId != 0) {
                        PreparedStatement getPlayerGuns = connection.prepareStatement("SELECT test.gun_configuration.gun_name FROM test.gun_configuration " +
                                "INNER JOIN test.player_own_gun " +
                                "ON test.gun_configuration.gun_id = test.player_own_gun.gun_id  " +
                                "WHERE test.player_own_gun.player_id = ?");

                        getPlayerGuns.setInt(1, playerId);
                        ResultSet playerGuns = getPlayerGuns.executeQuery();

                        List<String> ownedGuns = new ArrayList<>();

                        while(playerGuns.next()) {
                            ownedGuns.add(playerGuns.getString("gun_name"));

                        }

                        for(GunConfiguration gun : gunConfigurations) {
                            if(ownedGuns.contains(gun.name) || gun.unlockLevel == 0) {
                                if (gun.gunType == GunType.SECONDARY) {
                                    ownedSecondaryGuns.add(gun.name);
                                    if(gun.gunId == selectedSecondaryGunId) {
                                        secondaryGun = createNewGun(gun);
                                    }
                                } else {
                                    ownedPrimaryGuns.add(gun.name);
                                    if(gun.gunId == selectedPrimaryGunId) {
                                        primaryGun = createNewGun(gun);
                                    }
                                }

                            }
                        }

                        //TODO: Change this to get certain guns instead
                        if(primaryGun == null) {
                            primaryGun = createNewGun(gunConfigurations.get(1));
                        }

                        if(secondaryGun == null) {
                            secondaryGun = createNewGun(gunConfigurations.get(0));
                        }

                    } else {
                        player.sendMessage("Something went wrong when loading stats, try to rejoin, if that doesn't work " +
                                "please contact server admins");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);


        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
        DefaultInventories.setDefaultLobbyInventory(player.getInventory());



        //Get current used guns from database instead

        actionBarMessage = new String[9];
        Arrays.fill(actionBarMessage, "");
    }

    /**
     * Respawn the player in a one spawnpoint belonging to its team.
     */
    public void respawn(Player killer) {
        if(team.getTeamColor() != Color.GREEN) {
            primaryGun.reset();
            secondaryGun.reset();
        }

        isDead = true;
        DefaultInventories.giveEmptyInventory(player.getInventory());
        player.setGameMode(GameMode.SPECTATOR);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.SLOW);

        if(enemyTeam.getTeamColor() == Color.GREEN) {
            team.removePlayer(this);
            enemyTeam.addPlayer(this);
            Team temp = enemyTeam;
            enemyTeam = team;
            team = temp;
            BroadcastMessage.infectKill(getName());
        }
        player.setPlayerListName(team.getTeamColorAsChatColor() + player.getName());

        player.setDisplayName("Lvl " + statistics.getLevel() + " " + team.getTeamColorAsChatColor() + player.getName() + ChatColor.WHITE);

        if(killer != null) {
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
                    primaryGun.reset();
                    secondaryGun.reset();

                    isDead = false;
                    player.setHealth(20D);

                    if(team.getTeamColor() == Color.GREEN) {
                        DefaultInventories.giveInfectedInventory(player.getInventory(), team.getTeamColor());
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 1, false, false, false));
                        Arrays.fill(actionBarMessage, "");
                    } else {
                        DefaultInventories.giveDefaultInGameInventory(player.getInventory(), team.getTeamColor(), primaryGun, secondaryGun);
                    }

                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, 2, false, false, false));

                    player.teleport(team.getSpawnPoint());
                    player.setGameMode(GameMode.ADVENTURE);

                    this.cancel();
                }
            }
        };

        respawnCountdown.runTaskTimer(plugin, 0, 20);
    }

    /**
     * Add one kill to this players statistics and team, if the player has a team
     * @param reward The reward type of this kill
     */
    public void addKill(Reward reward) {
        statistics.addKill();
        statistics.addReward(reward);
        if(reward == Reward.HEADSHOT_KILL) {
            addHeadshotKill();
        }

        if(team != null) {
            team.addKill();
        }

        if(enemyTeam != null) {
            int killstreak = statistics.getKillstreak();
            switch (killstreak) {
                case 5:
                    primaryGun.addBullets(50);
                    secondaryGun.addBullets(25);
                    break;
                case 15:
                    //Launch emp
                    for(PlayerExtension enemyPlayer : enemyTeam.getPlayers()) {
                        if(enemyPlayer != this) {
                            enemyPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 15, 4));
                        }
                    }

                    BroadcastMessage.launchEmp(player.getName());
                    break;
                case 21:
                    BroadcastMessage.launchNuke(player.getName());
                    for(PlayerExtension enemyPlayer : enemyTeam.getPlayers()) {
                        if(!enemyPlayer.isDead() && enemyPlayer != this
                                && !enemyPlayer.getPlayer().hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                            enemyPlayer.addDeath();

                            int xpEarned = 25;
                            int creditsEarned = 1;

                            ChatMessage.playerNukeKilled(player, enemyPlayer.getPlayer(),
                                    enemyPlayer.getTeamChatColor());
                            ChatMessage.playerWasNukeKilled(enemyPlayer.getPlayer(), player, getTeamChatColor());
                            addKill(Reward.NUKE_KILL);
                            enemyPlayer.respawn(player);

                        }
                    }
                    break;

            }
        }
    }

    private void addHeadshotKill() {
        statistics.addHeadshotKill();
    }

    public void addDeath() {
        statistics.addDeath();
    }

    /**
     * Changes the credits, may be both negative and positive.
     * @param amount The amount to change with.
     */
    public void changeCredits(int amount) {
        statistics.addCredits(amount);
    }

    /**
     * Give this player a flag capture
     */
    public void captureFlag() {
        statistics.addCapture();
        statistics.addReward(Reward.CAPTURE_FLAG);

        team.captureFlag();
        enemyTeam.enemyCapturedFlag();
        ChatMessage.playerCapturedFlag(player, team.getTeamColorAsChatColor());
    }

    /**
     * Makes this player start playing a game at given location with correct equipment
     */
    public void startPlayingGame() {
        statistics.updateGameScoreboard();

        player.teleport(team.getSpawnPoint());
        player.setPlayerListName(team.getTeamColorAsChatColor() + player.getName());
        //TODO: Make a separate class for display name stuff
        player.setDisplayName("Lvl " + statistics.getLevel() + " " + team.getTeamColorAsChatColor() + player.getName() + ChatColor.WHITE);

        if(team.getTeamColor() == Color.GREEN) {
            DefaultInventories.giveInfectedInventory(player.getInventory(), team.getTeamColor());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 1, false, false, false));
        } else {
            DefaultInventories.giveDefaultInGameInventory(player.getInventory(), team.getTeamColor(), primaryGun, secondaryGun);
            primaryGun.reset();
            secondaryGun.reset();
        }

        if(hasWeaponInMainHand() && selectedResourcepack == Resourcepack.PACK_3D_128X128) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 10000000, 10, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10000000, 10, false, false, false));
        }

        player.setHealth(20D);
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

        ChatMessage.displayPersonalStats(player, statistics.getKillsThisGame(), statistics.getDeathsThisGame(),
                statistics.getTotalKills(), statistics.getTotalDeaths(), statistics.getXpThisGame(), statistics.getCreditsThisGame());
        leaveGame();
    }

    public void endGame(PlayerExtension winner, int winnerKills) {
        ChatMessage.displayFreeForAllEndGame(winner, winnerKills, player);
        ChatMessage.displayPersonalStats(player, statistics.getKillsThisGame(), statistics.getDeathsThisGame(),
                statistics.getTotalKills(), statistics.getTotalDeaths(), statistics.getXpThisGame(), statistics.getCreditsThisGame());
        leaveGame();
    }

    /**
     * Ends the game for this player, teleports the player to spawn and gives spawn inventory, and other spawn configurations
     */
    public void leaveGame() {
        if(team != null) {
            statistics.updateTotalScore();

            for(PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.setPlayerListName(player.getName());
            player.setDisplayName("Lvl " + statistics.getLevel() + " " + ChatColor.WHITE + player.getName());

            team.removePlayer(this);
            enemyTeam = null;
            team = null;

            primaryGun.reset();
            secondaryGun.reset();

            Arrays.fill(actionBarMessage, "");

            if(respawnCountdown != null && !respawnCountdown.isCancelled()) {
                respawnCountdown.cancel();
            }

            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20.0);
            player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
            DefaultInventories.setDefaultLobbyInventory(player.getInventory());
        }
    }

    /**
     * Ends the game for this player correctly when the server shuts down.
     */
    public void forceEndGame() {
        primaryGun.reset();
        secondaryGun.reset();
        statistics.forceUpdateScore();
        saveCurrentLoadout(true);

        Collection<PotionEffect> activeEffects = player.getActivePotionEffects();

        for(PotionEffect effect : activeEffects) {
            player.removePotionEffect(effect.getType());
        }

        player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
    }

    /**
     * Changes the scope, scopes if player is not in scope and vice versa
     */
    public void changeScope() {
        if(!getWeaponInMainHand().justStartedReloading()) {
            if (player.hasPotionEffect(PotionEffectType.SLOW)) {
                unScope();
            } else {
                scope();
            }
        }
    }

    /**
     * Makes the player be stop scoping (or nothing if the player is not scoping).
     */
    public void unScope() {
        player.removePotionEffect(PotionEffectType.SLOW);
        Animation.unscopeAnimation(player, getWeaponInMainHand(), player.getInventory().getHeldItemSlot(), plugin);
    }

    public void scope() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 4, false, false, false));
        Animation.scopeAnimation(player, getWeaponInMainHand(), player.getInventory().getHeldItemSlot(), plugin);
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
    public Gun getWeaponInMainHand() {
        if(player.getInventory().getItemInMainHand().getType().equals(primaryGun.getMaterialType())) {
            return primaryGun;
        } else if(player.getInventory().getItemInMainHand().getType().equals(secondaryGun.getMaterialType())) {
            return secondaryGun;
        } else {
            return null;
        }
    }

    /**
     * @return true if player has weapon in main hand, false otherwise
     */
    public boolean hasWeaponInMainHand() {
        if (player.getInventory().getItemInMainHand().getType() == primaryGun.getMaterialType()) {
            return true;
        } else if (player.getInventory().getItemInMainHand().getType() == secondaryGun.getMaterialType()) {
            return true;
        }

        return false;
    }

    public void changeMainHand(int newSlot) {
        primaryGun.stopShooting();
        secondaryGun.stopShooting();

        //unScope();

        if(selectedResourcepack == Resourcepack.PACK_3D_128X128) {
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
     * @param wep The weapon to reload
     */
    public void reloadWeapon(Gun wep) {
        wep.reload();
    }

    public void reloadIfGun(Material material) {
        if(primaryGun.getMaterialType() == material) {
            reloadWeapon(primaryGun);
        } else if(secondaryGun.getMaterialType() == material) {
            reloadWeapon(secondaryGun);
        }
    }

    /**
     * @return the primary weapon the player has selected
     */
    public Gun getPrimaryGun() {
        return primaryGun;
    }

    /**
     * @return the secondary weapon the player has selected
     */
    public Gun getSecondaryGun() {
        return secondaryGun;
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
     * @param gun The guns GunConfiguration
     */
    public void changePrimaryGun(GunConfiguration gun) {
            primaryGun = createNewGun(gun);
    }

    /**
     * Sets the secondary gun of this player
     *
     * @param gun The guns GunConfiguration
     */
    public void changeSecondaryGun(GunConfiguration gun) {
        secondaryGun = createNewGun(gun);
    }

    public void buyGun(String gunName, List<GunConfiguration> gunConfigurations) {
        for(GunConfiguration gun: gunConfigurations) {
            if(gun.name.equals(gunName)) {
                changeCredits(-gun.costToBuy);

                if(gun.gunType == GunType.SECONDARY) {
                    ownedSecondaryGuns.add(gunName);
                } else {
                    ownedPrimaryGuns.add(gunName);
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            PreparedStatement addGunToPlayer = connection.prepareStatement("INSERT INTO test.player_own_gun " +
                            "(player_id, gun_id) VALUES (?,?)");

                            addGunToPlayer.setInt(1, playerId);
                            addGunToPlayer.setInt(2, gun.gunId);

                            addGunToPlayer.execute();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(plugin);

            }
        }
    }

    private Gun createNewGun(GunConfiguration configuration) {
        Firemode firemode = configuration.firemode;

        Gun gunToChange;

        switch(firemode) {
            case BURST:
                gunToChange = new BurstGun(plugin, this, statistics, configuration);
                break;
            case SINGLE:
                gunToChange = new SingleBoltGun(plugin, this, statistics, configuration);
                break;
            case AUTOMATIC:
                gunToChange = new FullyAutomaticGun(plugin, this, statistics, configuration);
                break;
            case BUCK:
                gunToChange = new BuckGun(plugin, this, statistics, configuration);
                break;
            default:
                gunToChange = null;
        }

        return gunToChange;
    }

    /**
     * Saves the players current loadout, so that it will be remembered for when the player joins next time.
     * @param force True if this should be done during shutdown, false otherwise
     */
    public void saveCurrentLoadout(boolean force) {
        if(force) {
            saveLoadout();
        } else {
            new BukkitRunnable() {
                public void run() {
                    saveLoadout();
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    private void saveLoadout() {
        try {
            PreparedStatement saveLoadout = connection.prepareStatement("UPDATE test.player SET " +
                    "selected_primary=?, selected_secondary=? WHERE id=?");

            saveLoadout.setInt(1, primaryGun.getConfiguration().gunId);
            saveLoadout.setInt(2, secondaryGun.getConfiguration().gunId);
            saveLoadout.setInt(3, playerId);
            saveLoadout.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public int getCredits() {
        return statistics.getCredits();
    }

    public void setSelectedResourcepack(Resourcepack pack) {
        selectedResourcepack = pack;
    }
}
