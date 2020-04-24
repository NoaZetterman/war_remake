package me.noaz.testplugin.tasks;

import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Messages.BossBarMessage;
import me.noaz.testplugin.Messages.BroadcastMessage;
import me.noaz.testplugin.Messages.PlayerListMessage;
import me.noaz.testplugin.gamemodes.CaptureTheFlag;
import me.noaz.testplugin.gamemodes.FreeForAll;
import me.noaz.testplugin.gamemodes.Game;
import me.noaz.testplugin.gamemodes.TeamDeathMatch;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.GunConfiguration;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This class contains a bukkit runnable that takes care of things that should be done each second, and starting/ending
 * games.
 *
 * @author Noa Zetterman
 * @version 2020-03-01
 */
public class GameController {
    private Game game;
    private TestPlugin plugin;

    private BukkitRunnable mainGameTask;
    private BukkitRunnable updatePlayerActionBars;

    private BossBar bar;
    private List<String> mapNames = new ArrayList<>();
    private HashMap<String, HashMap<String, List<Location>>> maps = new HashMap<>(); //A bit ugly xd
    private List<GunConfiguration> gunConfigurations = new ArrayList<>();
    private HashMap<Player, PlayerExtension> playerExtensions = new HashMap<>();
    private String nextMapName = "";
    private String previousMapName = "";
    private String gamemode = "tdm";
    private String[] gamemodes = {"tdm", "ctf", "ffa"};

    //Bluespawn etc should probably be constants

    private int timeUntilGameEnds = -1;
    private int timeUntilNextGame = 60;

    /**
     * Starts the timer.
     * @param plugin This plugin
     */
    public GameController(TestPlugin plugin, Connection connection) {
        this.plugin = plugin;
        createGunConfigurations(connection);
        loadMaps();
        runTasks();
    }

    private void createGunConfigurations(Connection connection) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement getGunConfigurationsFromDatabase = connection.prepareStatement("SELECT * FROM test.gun_configuration");
                    ResultSet result = getGunConfigurationsFromDatabase.executeQuery();

                    while(result.next()) {
                        int gunId = result.getInt("gun_id");
                        String name = result.getString("gun_name");
                        String gunMaterial = result.getString("gun_material");
                        String weaponType = result.getString("weapon_type");
                        String fireType = result.getString("fire_type");
                        float accuracyNotScoped = result.getFloat("accuracy_not_scoped");
                        float accuracyScoped = result.getFloat("accuracy_scoped");
                        float bodyDamage = result.getFloat("body_damage");
                        float headDamage = result.getFloat("head_damage");
                        float bulletSpeed = result.getFloat("bullet_speed");
                        int gunRange = result.getInt("gun_range");
                        int reloadTimeInMs = result.getInt("reload_time_in_ms");
                        int burstDelayInMs = result.getInt("burst_delay_in_ms");
                        int bulletsPerBurst = result.getInt("bullets_per_burst");
                        int bulletsPerClick = result.getInt("bullets_per_click");
                        int startingBullets = result.getInt("starting_bullets");
                        int clipSize = result.getInt("clip_size");
                        int loadoutSlot = result.getInt("loadout_slot");
                        int unlockLevel = result.getInt("unlock_level");
                        int costToBuy = result.getInt("cost_to_buy");
                        String fireBulletSound = result.getString("fire_bullet_sound");
                        String fireWhileReloadingSound = result.getString("fire_while_reloading_sound");
                        String fireWithoutAmmoSound = result.getString("fire_without_ammo_sound");

                        gunConfigurations.add(new GunConfiguration(gunId, name, gunMaterial, weaponType,
                                fireType, accuracyNotScoped, accuracyScoped, bodyDamage, headDamage,
                                bulletSpeed, gunRange, reloadTimeInMs, burstDelayInMs, bulletsPerBurst,
                                bulletsPerClick, startingBullets, clipSize, loadoutSlot, unlockLevel,
                                costToBuy, fireBulletSound, fireWhileReloadingSound, fireWithoutAmmoSound));


                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);


        //Should grab the information for all guns from database later on

        // http://puttyland.com/share/TTYWy1vN.txt

        /*
        SELECT * FROM test.player WHERE player_uuid=uuid;
SELECT * FROM test.owned_gun_list WHERE id=(player.owned_guns);
         */
        /* WORKS:

        SELECT * FROM test.gun_configuration
INNER JOIN test.player_own_gun ON test.gun_configuration.gun_id=test.player_own_gun.gun_id
WHERE player_own_gun.player_id=5

// INSERT INTO test.player_own_gun (player_id, gun_id) VALUES (5,1); Inserts gun with player id 5 and gun nr 1

         */

        // INNER JOIN test.owned_gun_list ON test.player.owned_guns=test.owned_gun_list.id
        /*INSERT INTO test.gun_configuration (gun_name, gun_material, weapon_type, fire_type, accuracy_not_scoped, accuracy_scoped, body_damage,
                head_damage, bullet_speed, gun_range, reload_time_in_ms, burst_delay_in_ms, bullets_per_burst, bullets_per_click, starting_bullets,
                clip_size, loadout_slot, unlock_level, cost_to_buy, fire_bullet_sound, fire_while_reloading_sound, fire_without_ammo_sound) VALUES
                ('Skullcrusher', 'GOLD_INGOT', 'Automatic', 'burst', 2.0, 100, 7.2, 7.5, 4, 76, 4000, 400, 3, 1, 72, 24, 13, 6, 300, 'ENTITY_SKELETON_HURT',
                        'ENTITY_ZOMBIE_BREAK_WOODEN_DOOR', 'ENTITY_GHAST_SHOOT');*/

        /*gunConfigurations.put("Minigun", new WeaponConfiguration("Minigun", "DIAMOND",
                "Automatic", "buck", 3, 10,
                3.6, 4.5, 4, 128, 100, 50,
                1, 2, 64, 64,
                new Sound[] {Sound.ENTITY_SKELETON_HURT, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, Sound.ENTITY_GHAST_SHOOT},
                10, 35));*/
    }

    private void loadMaps() {
        //For loop with all map worlds??
        System.out.println("Loading maps");

        File f = plugin.getServer().getWorldContainer();
        String[] mapNameList = f.list();

        for(String mapName : mapNameList) {
            //Right now all files are under */maps, none-maps should not be there.
            if(!mapName.equals("world") && !mapName.equals("world_the_end")) {
                System.out.println("Loading " + mapName);
                //get from file /maps, all files in there should be maps with names corresponding.
                WorldCreator creator = new WorldCreator(mapName);
                World gameWorld = plugin.getServer().createWorld(creator);
                if(gameWorld != null) {
                    gameWorld.setDifficulty(Difficulty.PEACEFUL);
                    gameWorld.setAmbientSpawnLimit(0);
                    gameWorld.setAnimalSpawnLimit(0);
                    gameWorld.setMonsterSpawnLimit(0);
                    gameWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                    gameWorld.setWaterAnimalSpawnLimit(0);
                    gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    gameWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                    gameWorld.setTime(6000);
                    gameWorld.setGameRule(GameRule.NATURAL_REGENERATION, true);

                    gameWorld.setAutoSave(false);

                    List<Location> blueSpawnLocations = new ArrayList<>();
                    List<Location> redSpawnLocations = new ArrayList<>();
                    List<Location> ffaSpawnLocations = new ArrayList<>();

                    Location redFlag = null;
                    Location blueFlag = null;

                    //Add more such as location of something else

                    HashMap<String, List<Location>> locations = new HashMap<>();

                    Sign mapSize = (Sign) gameWorld.getBlockAt(gameWorld.getSpawnLocation()).getState();
                    gameWorld.getBlockAt(gameWorld.getSpawnLocation()).setType(Material.AIR);

                    String mostNegativeCornerLocation = mapSize.getLine(0);
                    String[] lowestCoordinate = mostNegativeCornerLocation.split(",");
                    int lowestX = Integer.parseInt(lowestCoordinate[0]);
                    int lowestY = Integer.parseInt(lowestCoordinate[1]);
                    int lowestZ = Integer.parseInt(lowestCoordinate[2]);

                    String mostPositiveCornerLocation = mapSize.getLine(1);
                    String[] highestCoordinate = mostPositiveCornerLocation.split(",");
                    int highestX = Integer.parseInt(highestCoordinate[0]);
                    int highestY = Integer.parseInt(highestCoordinate[1]);
                    int highestZ = Integer.parseInt(highestCoordinate[2]);

                    for (int x = lowestX; x < highestX; x++) {
                        for (int z = lowestZ; z < highestZ; z++) {
                            for (int y = lowestY; y < highestY; y++) {
                                if (gameWorld.getBlockAt(x, y, z).getState() instanceof Sign) {
                                    Location signLocation = new Location(gameWorld, x, y, z);
                                    Sign sign = (Sign) gameWorld.getBlockAt(x, y, z).getState();
                                    String eventualSpawnOrConfig = sign.getLine(0);
                                    boolean isConfigSign = true;

                                    switch (eventualSpawnOrConfig) {
                                        case "bluespawn":
                                            blueSpawnLocations.add(signLocation);
                                            break;
                                        case "redspawn":
                                            redSpawnLocations.add(signLocation);
                                            break;
                                        case "ffaspawn":
                                            ffaSpawnLocations.add(signLocation);
                                            break;
                                        case "redflag":
                                            redFlag = signLocation;
                                            break;
                                        case "blueflag":
                                            blueFlag = signLocation;
                                            break;
                                        default:
                                            isConfigSign = false;
                                    }

                                    if (isConfigSign) {
                                        signLocation.getBlock().setType(Material.AIR);
                                    }

                                }
                            }
                        }
                    }

                    System.out.println("Gamemodes:");
                    if (blueSpawnLocations.size() != 0 && redSpawnLocations.size() != 0) {
                        locations.put("bluespawn", blueSpawnLocations);
                        locations.put("redspawn", redSpawnLocations);
                        System.out.println("TDM");
                        if (redFlag != null && blueFlag != null) {
                            List<Location> l = new ArrayList<>(2);
                            l.add(redFlag);
                            l.add(blueFlag);
                            locations.put("flags", l);
                            System.out.println("CTF");
                        }
                    }

                    if (ffaSpawnLocations.size() != 0) {
                        locations.put("ffaspawn", ffaSpawnLocations);
                        System.out.println("FFA");
                    }

                    //Come up with something fun about flags and more xd

                    maps.put(mapName, locations);
                    mapNames.add(mapName);
                }
            }
        }

        nextMapName = mapNames.get(new Random().nextInt(mapNames.size()));
    }

    /**
     * Creates a BukkitRunnable task that takes counts time until next game and during a game, and starts games
     */
    private void runTasks() {
        createVisibleTimer();

        //Task that is used to count timer and take care of game start/end
        mainGameTask = new BukkitRunnable() {
            public void run() {
            if (timeUntilNextGame == 0) {
                if(timeUntilGameEnds == -1) {
                    startGame();
                    timeUntilGameEnds = game.getLength();
                } else if(timeUntilGameEnds == 0) {
                    endGame();
                }
                timeUntilGameEnds--;
                BossBarMessage.timeUntilGameEnds(bar, timeUntilGameEnds);
            } else {
                timeUntilNextGame--;
                BossBarMessage.timeUntilNextGame(bar, timeUntilNextGame);

                if(timeUntilNextGame % 10 == 0) {
                    BroadcastMessage.timeLeftUntilGameStarts(timeUntilNextGame, plugin.getServer());
                } else if(timeUntilNextGame % 10 == 5) {
                    BroadcastMessage.gameAndGamemode(nextMapName, gamemode, plugin.getServer());
                }
            }
            }
        };

        //Task that takes handles player action bars, so that it is always visible
        updatePlayerActionBars = new BukkitRunnable() {

            @Override
            public void run() {

                updatePlayerList();
                //TODO: (after release) Redo below to be more efficient
                for(PlayerExtension player : playerExtensions.values()) {
                    player.updateActionBar();
                }
            }
        };

        updatePlayerActionBars.runTaskTimer(plugin, 0L, 10L);
        mainGameTask.runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * @return The current or the upcoming game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Lets the player join the current game
     * @param player The player that should join the game
     * @return True if player is in game or joined game, false if there is no game to join
     */
    public boolean joinGame(Player player) {
        if(game == null)
            return false;

        game.join(playerExtensions.get(player));
        return true;
    }

    /**
     * Tries to leave the current game, if there is one, otherwise it does not
     * @param player The player that should leave the game
     * @return True if the player successfully left the game, false if the player is not in a game.
     */
    public boolean leaveGame(Player player) {
        if(game == null)
            return false;

        return game.leave(playerExtensions.get(player));
    }

    public String[] getMaps() {
        return mapNames.toArray(new String[0]);
    }

    /**
     * Stops the current game and saves all maps and player data.
     * Used for stopping the server safely.
     */
    public void stop() {
        if(game != null) {
            System.out.println("Ending game and saving player data");
            game.end(true);
        }

        mainGameTask.cancel();
        updatePlayerActionBars.cancel();

        System.out.println("Resetting maps:");

        File f = plugin.getServer().getWorldContainer();
        String[] mapNames = f.list();

        for(String mapName : mapNames) {
            System.out.println(mapName);
            plugin.getServer().unloadWorld(plugin.getServer().getWorld(mapName), false);
            plugin.getServer().getWorld(mapName);
        }
    }

    /**
     * Sets the next game to given input.
     * @param mapName The name of the map
     * @param gamemode The gamemode to use
     * @return True if the map and gamemode was set, False otherwise.
     */
    public boolean pickNextMapAndGamemode(String mapName, String gamemode) {
        if(!mapNames.contains(mapName))
            return false;

        if(!isValidGamemode(mapName, gamemode))
            return false;

        nextMapName = mapName;
        this.gamemode = gamemode;
        return true;
    }

    /**
     * @return All gun configurations as a HashMap, with gun name as key and a WeaponConfiguration object as value
     */
    public List<GunConfiguration> getGunConfigurations() {
        return gunConfigurations;

    }

    /**
     * Picks next map and gamemode randomly, will not be same map as previously.
     * But may be same gamemode.
     */
    public void pickNextMapAndGamemode() {
        Random random = new Random();

        //Note: This does not work when there's only one map.
        while(nextMapName.equals(previousMapName)) {
            nextMapName = mapNames.get(random.nextInt(mapNames.size()));
        }

        do {
            gamemode = gamemodes[random.nextInt(gamemodes.length)];
        } while(!isValidGamemode(nextMapName, gamemode));
    }

    private boolean isValidGamemode(String mapName, String gamemode) {
        switch(gamemode) {
            case "tdm":
                if(!maps.get(mapName).containsKey("bluespawn"))
                    return false;
                break;
            case "ctf":
                if(!maps.get(mapName).containsKey("bluespawn") || !maps.get(mapName).containsKey("flags"))
                    return false;
                break;
            case "ffa":
                if(!maps.get(mapName).containsKey("ffaspawn"))
                    return false;
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Start a game with the current settings for game and gamemode.
     */
    public void startGame() {
        if(game == null) {
            switch(gamemode) {
                case "tdm":
                    game = new TeamDeathMatch(nextMapName, maps.get(nextMapName), playerExtensions);
                    break;
                case "ctf":
                    game = new CaptureTheFlag(nextMapName, maps.get(nextMapName), plugin, playerExtensions);
                    break;
                case "ffa":
                    game = new FreeForAll(nextMapName, maps.get(nextMapName), playerExtensions);
                    break;
            }

            timeUntilNextGame = 0;
            timeUntilGameEnds = game.getLength();
            System.out.println("Starting game");
        }
    }

    /**
     * End the current game, and start a new one in 60 seconds
     */
    public void endGame() {
        if(game != null) {
            BroadcastMessage.endGameMessage(plugin.getServer());

            game.end(false);
            game = null;
            previousMapName = nextMapName;
            pickNextMapAndGamemode();
            timeUntilNextGame = 60;
            timeUntilGameEnds = 0;
        }
    }

    private void createVisibleTimer() {
        bar = plugin.getServer().createBossBar(NamespacedKey.minecraft("timer"), "", BarColor.PURPLE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
        bar.setVisible(true);
        bar.setProgress(1.0);
    }

    public void addPlayer(TestPlugin plugin, Player player, ScoreManager scoreManager, Connection connection) {
        playerExtensions.put(player, new PlayerExtension(plugin, player, scoreManager, getGunConfigurations(), connection));
    }

    public void removePlayer(Player player) {
        leaveGame(player);
        playerExtensions.get(player).saveCurrentLoadout(false);
        playerExtensions.remove(player);
    }

    public PlayerExtension getPlayerExtension(Player player) {
        return playerExtensions.get(player);
    }

    private void updatePlayerList() {
        if(game == null) {
            for(Player player : playerExtensions.keySet()) {
                PlayerListMessage.setLobbyHeader(player, gamemode, nextMapName);
            }
        } else  {
            game.updatePlayerList();
        }
    }
}
