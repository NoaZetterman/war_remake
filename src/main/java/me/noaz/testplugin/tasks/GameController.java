package me.noaz.testplugin.tasks;

import me.noaz.testplugin.Maps.CustomLocation;
import me.noaz.testplugin.Maps.GameMap;
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
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
    private List<GameMap> maps = new ArrayList<>();
    private List<GunConfiguration> gunConfigurations = new ArrayList<>();
    private HashMap<Player, PlayerExtension> playerExtensions = new HashMap<>();
    private GameMap nextMap = null;
    private GameMap previousMap = null;
    private String currentGamemode = "tdm";
    private final String pathToNewMaps = "C:/Users/Noa/MinecraftBukkitServer/newMaps";
    private final String pathToSavedMapsWithSigns = "C:/Users/Noa/MinecraftBukkitServer/mapsWithLocationSigns";
    private final String pathToPlayableMaps = "C:/Users/Noa/MinecraftBukkitServer/maps";

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
        loadMaps(connection);
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

    private void loadMaps(Connection connection) {
        File newMapFile = new File(pathToNewMaps);

        String[] newMaps = newMapFile.list();

        for(String mapName : newMaps) {
            addMap(mapName, connection);
        }

        try {
            PreparedStatement getExistingMaps = connection.prepareStatement("SELECT * FROM test.map");

            ResultSet existingMaps = getExistingMaps.executeQuery();

            while(existingMaps.next()) {
                int id = existingMaps.getInt("id");
                String name = existingMaps.getString("name");
                boolean hasTdm = existingMaps.getBoolean("has_tdm");
                boolean hasCtf = existingMaps.getBoolean("has_ctf");
                boolean hasFfa = existingMaps.getBoolean("has_ffa");
                boolean hasInfect = existingMaps.getBoolean("has_infect");
                String mapCreator = existingMaps.getString("creator");
                String creatorInformation = existingMaps.getString("creator_information");

                PreparedStatement getMapLocations = connection.prepareStatement("SELECT * FROM test.map_location " +
                        "WHERE map_id=?");
                getMapLocations.setInt(1, id);

                ResultSet mapLocations = getMapLocations.executeQuery();

                List<CustomLocation> locations = new ArrayList<>();

                while(mapLocations.next()) {
                    locations.add(new CustomLocation(mapLocations.getString("location_type"),
                            mapLocations.getInt("x_location"),
                            mapLocations.getInt("y_location"),
                            mapLocations.getInt("z_location")));
                }

                maps.add(new GameMap(name, locations, hasTdm, hasCtf, hasFfa, hasInfect, mapCreator,
                        creatorInformation));

                System.out.println("Successfully configured map: " + name);

            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

        nextMap = maps.get(new Random().nextInt(maps.size()));
        nextMap.loadMap();
    }

    /**
     * Copes given file/directory and all underdirectories
     * @param src The source file
     * @param target The target file
     */
    private void copyRecursive(File src, File target) throws IOException {
        if(src.isDirectory()) {
            if (!target.exists())
            {
                target.mkdir();
            }

            File[] files = src.listFiles();
            for(File f : files) {
                copyRecursive(f, new File(target, f.getName()));
            }
        } else {
            Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

    }

    private void deleteFile(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
           @Override
           public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               Files.delete(file);
               return FileVisitResult.CONTINUE;
           }

           @Override
           public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
               Files.delete(dir);
               return FileVisitResult.CONTINUE;
           }
        });
    }

    private void addMap(String mapName, Connection connection) {
        File src = new File(pathToNewMaps + "/" + mapName);
        File target = new File(pathToSavedMapsWithSigns + "/" + mapName);

        try {
            copyRecursive(src, target);

            if(new File(pathToPlayableMaps + "/" + mapName).exists()) {
                deleteFile(Paths.get(pathToPlayableMaps + "/" + mapName));
            }

            Files.move(Paths.get(pathToNewMaps + "/" + mapName), Paths.get(pathToPlayableMaps + "/" + mapName),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saving new Map: " + mapName);

        WorldCreator creator = new WorldCreator(mapName);
        World gameWorld = plugin.getServer().createWorld(creator);


        gameWorld.setDifficulty(Difficulty.PEACEFUL);
        gameWorld.setAmbientSpawnLimit(0);
        gameWorld.setAnimalSpawnLimit(0);
        gameWorld.setMonsterSpawnLimit(0);
        gameWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        gameWorld.setWaterAnimalSpawnLimit(0);
        gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        gameWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        gameWorld.setGameRule(GameRule.NATURAL_REGENERATION, true);
        gameWorld.setTime(6000);

        gameWorld.setAutoSave(true);

        List<CustomLocation> locations = new ArrayList<>();

        boolean hasTdm = false;
        boolean hasCtf = false;
        boolean hasFfa = false;
        boolean hasInfect = false;

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
                        Location signLocation = new Location(gameWorld,  x, y, z);
                        Sign sign = (Sign) gameWorld.getBlockAt(x, y, z).getState();
                        String eventualSpawnOrConfig = sign.getLine(0);

                        //+0.5 makes the player spawn at the center of a block, instead of in the corner
                        signLocation.add(0.5,0,0.5);
                        boolean isConfigSign = true;

                        switch (eventualSpawnOrConfig) {
                            case "bluespawn":
                            case "redspawn":
                                hasTdm = true;
                                hasInfect = true;
                                break;
                            case "ffaspawn":
                                hasFfa = true;
                                break;
                            case "redflag":
                            case "blueflag":
                                hasCtf = true;
                                break;
                            default:
                                isConfigSign = false;
                                break;
                        }

                        if (isConfigSign) {
                            signLocation.getBlock().setType(Material.AIR);
                            locations.add(new CustomLocation(eventualSpawnOrConfig,
                                    signLocation.getX(), signLocation.getY(), signLocation.getZ()));
                        }
                    }
                }
            }
        }

        try {
            /*
            PreparedStatement createMap = connection.prepareStatement("REPLACE INTO test.map" +
                    "(name, has_tdm, has_ctf, has_ffa, has_infect) VALUES (?,?,?,?,?)");*/
            PreparedStatement createMap = connection.prepareStatement("INSERT INTO test.map" +
                    "(name, has_tdm, has_ctf, has_ffa, has_infect) VALUES (?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE name=?, has_tdm=?, has_ctf=?, has_ffa=?, has_infect=?");
            createMap.setString(1, mapName);
            createMap.setBoolean(2, hasTdm);
            createMap.setBoolean(3, hasCtf);
            createMap.setBoolean(4, hasFfa);
            createMap.setBoolean(5, hasInfect);

            createMap.setString(6, mapName);
            createMap.setBoolean(7, hasTdm);
            createMap.setBoolean(8, hasCtf);
            createMap.setBoolean(9, hasFfa);
            createMap.setBoolean(10, hasInfect);
            createMap.execute();

            PreparedStatement getMapId = connection.prepareStatement("SELECT id FROM test.map " +
                    "WHERE name=?");

            getMapId.setString(1, mapName);
            ResultSet resultId = getMapId.executeQuery();
            int id = 0;

            while(resultId.next()) {
                id = resultId.getInt("id");
            }

            PreparedStatement removePreviousSigns = connection.prepareStatement("DELETE FROM test.map_location " +
                    "WHERE map_id=?");
            removePreviousSigns.setInt(1, id);
            removePreviousSigns.execute();

            for(CustomLocation location : locations) {
                PreparedStatement insertMapLocation = connection.prepareStatement("INSERT INTO test.map_location" +
                        "(map_id, location_type, x_location, y_location, z_location) VALUES (?,?,?,?,?)");

                insertMapLocation.setInt(1, id);
                insertMapLocation.setString(2, location.getLocationType());
                insertMapLocation.setDouble(3, location.getX());
                insertMapLocation.setDouble(4, location.getY());
                insertMapLocation.setDouble(5, location.getZ());

                insertMapLocation.execute();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        //Saves the world without the location signs.
        plugin.getServer().unloadWorld(gameWorld, true);
        System.out.println("Map saved: " + mapName);
    }

    /**
     * Creates a BukkitRunnable task that counts time until next game and during a game, and starts games
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
                        BroadcastMessage.gameAndGamemode(nextMap.getName(), currentGamemode, plugin.getServer());
                    }
                }
            }
        };

        //Task that takes handles player action bars, so that it is always visible
        updatePlayerActionBars = new BukkitRunnable() {

            @Override
            public void run() {

                updatePlayerList();

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

    public List<GameMap> getMaps() {
        return maps;
    }

    public List<String> getMapNames() {
        List<String> names = new ArrayList<>();
        for(GameMap map : maps) {
            names.add(map.getName());
        }

        return names;
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

        nextMap.unloadMap();
    }

    /**
     * Sets the next game to given input.
     * @param mapName The name of the map
     * @param gamemode The gamemode to use
     * @return True if the map and gamemode was set, False otherwise.
     */
    public boolean pickNextMapAndGamemode(String mapName, String gamemode) {
        for(GameMap map : maps) {
            if(map.getName().equals(mapName) && map.hasGamemode(gamemode)) {
                previousMap = nextMap;
                nextMap = map;
                currentGamemode = gamemode;

                previousMap.unloadMap();
                nextMap.loadMap();

                return true;
            }
        }

        return false;
    }

    /**
     * Picks next map and gamemode randomly, will not be same map as previously.
     * But may be same gamemode.
     */
    public void pickNextMapAndGamemode() {
        Random random = new Random();

        while(nextMap.getName().equals(previousMap.getName())) {
            nextMap = maps.get(random.nextInt(maps.size()));
        }

        //Note: This does not work when there's only one map.
        previousMap.unloadMap();
        nextMap.loadMap();
        currentGamemode = nextMap.getGamemode();
    }

    /**
     * @return All gun configurations as a HashMap, with gun name as key and a WeaponConfiguration object as value
     */
    public List<GunConfiguration> getGunConfigurations() {
        return gunConfigurations;

    }

    /**
     * Start a game with the current settings for game and gamemode.
     */
    public void startGame() {
        if(game == null) {
            switch(currentGamemode) {
                case "tdm":
                    //Send the map in instead of locations etc
                    game = new TeamDeathMatch(nextMap, playerExtensions);
                    break;
                case "ctf":
                    game = new CaptureTheFlag(nextMap, plugin, playerExtensions);
                    break;
                case "ffa":
                    game = new FreeForAll(nextMap, playerExtensions);
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
            previousMap = nextMap;
            pickNextMapAndGamemode();
            //reloadMap(previousMapName);
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
                PlayerListMessage.setLobbyHeader(player, currentGamemode, nextMap.getName(),
                        nextMap.getMapCreators(), nextMap.getCreatorInformation());
            }
        } else  {
            game.updatePlayerList();
        }
    }
}
