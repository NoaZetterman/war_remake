package me.noaz.testplugin.tasks;

import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.gamemodes.CaptureTheFlag;
import me.noaz.testplugin.gamemodes.FreeForAll;
import me.noaz.testplugin.gamemodes.Game;
import me.noaz.testplugin.gamemodes.TeamDeathMatch;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Statement;
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
    private BukkitRunnable task;
    private BossBar bar;
    private List<String> mapNames = new ArrayList<>();
    private HashMap<String, HashMap<String, List<Location>>> maps = new HashMap<>(); //A bit ugly xd
    private HashMap<String, WeaponConfiguration> gunConfigurations = new HashMap<>();
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
    public GameController(TestPlugin plugin) {
        this.plugin = plugin;
        createGunConfigurations();
        loadMaps();
        runTimer();
    }

    private void createGunConfigurations() {
        //Should grab the information for all guns from database later on

        // http://puttyland.com/share/TTYWy1vN.txt
        gunConfigurations.put("Skullcrusher", new WeaponConfiguration("Skullcrusher", Material.GOLD_INGOT,
                "Automatic", "burst",
                2.0, 100.0, 5.0,7.5,4,
                100,4000, 400,3,72,24));

        gunConfigurations.put("Python", new WeaponConfiguration("Python", Material.GOLDEN_SHOVEL,
                "Secondary", "single",
                2.0, 100.0, 10.0, 20.0, 4,
                70, 3500, 333, 1, 50, 9));

        gunConfigurations.put("Dragunov", new WeaponConfiguration("Dragunov", Material.STONE_AXE,
                "Sniper", "single",
                2.0, 100.0, 11.4, 20.0, 4,
                400, 4250, 500, 1, 32, 7));

        gunConfigurations.put("L120 Isolator", new WeaponConfiguration("L120 Isolator", Material.BOWL,
                "Sniper", "single",
                2.0, 100.0, 21.4, 25.0, 4,
                400, 5000, 900, 1, 60, 4));

        gunConfigurations.put("AA12", new WeaponConfiguration("AA12", Material.IRON_PICKAXE,
                "Shotgun", "buck", 1.0, 3.0, 7,
                9, 4, 18, 1500, 450, 6,
                100, 20));
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
                    gameWorld.setWaterAnimalSpawnLimit(0);
                    gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
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
    private void runTimer() {
        createVisibleTimer();

        task = new BukkitRunnable() {
            public void run() {
            if (timeUntilNextGame == 0) {
                if(timeUntilGameEnds == -1) {
                    startGame();
                    timeUntilGameEnds = game.getLength();
                } else if(timeUntilGameEnds == 0) {
                    endGame();
                }
                timeUntilGameEnds--;
                bar.setTitle("Time until game ends: " + timeUntilGameEnds);
            } else {
                timeUntilNextGame--;
                bar.setTitle("Time until next game: " + timeUntilNextGame);
                if(timeUntilNextGame % 10 == 0) {
                    plugin.getServer().broadcastMessage(timeUntilNextGame + "s until game starts");
                } else if(timeUntilNextGame % 10 == 5) {
                    plugin.getServer().broadcastMessage("Next map: " + nextMapName + " Next gamemode:" + gamemode);
                }
            }
            }
        };

        task.runTaskTimer(plugin, 0, 20L);
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
            game.end(playerExtensions, true);
        }
        task.cancel();

        System.out.println("Resetting maps");

        File f = plugin.getServer().getWorldContainer();
        String[] mapNames = f.list();

        for(String mapName : mapNames) {
            System.out.println("Resetting " + mapName);
            plugin.getServer().unloadWorld(plugin.getServer().getWorld(mapName), false);
            plugin.getServer().getWorld(mapName);
        }
    }

    /**
     * Sets the next game to given input.
     * @param mapName The name of the map
     * @param gamemode The gamemode to use
     * @return True if the map and gamemode was set, false otherwise.
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
    public HashMap<String, WeaponConfiguration> getGunConfigurations() {
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

        while(!isValidGamemode(nextMapName, gamemode)) {
            gamemode = gamemodes[random.nextInt(gamemodes.length)];
        }
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

    public void endGame() {
        if(game != null) {
            plugin.getServer().broadcastMessage("Ending Game, new game in 60 sec!");
            //System.out.println("Ending Game, new game in 60 sec!"); //should be server message
            game.end(playerExtensions, false);
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

    public void addPlayer(TestPlugin plugin, Player player, ScoreManager scoreManager, Statement statement) {
        playerExtensions.put(player, new PlayerExtension(plugin, player, scoreManager, getGunConfigurations(), statement));
    }

    public void removePlayer(Player player) {
        if(game != null)
            getGame().leave(playerExtensions.get(player));
        playerExtensions.remove(player);
    }

    public PlayerExtension getPlayerExtension(Player player) {
        return playerExtensions.get(player);
    }
}
