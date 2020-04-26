package me.noaz.testplugin.Maps;

import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final String name;
    private final List<CustomLocation> locations;
    private final boolean hasTdm;
    private final boolean hasCtf;
    private final boolean hasFfa;
    private final boolean hasInfect;

    private final String mapCreator;
    private final String mediaOfCreator;

    //Remaker of the map, if that is a thing
    private final String mapRemaker;
    private final String mediaOfRemaker;

    private World world;

    public GameMap(String name, List<CustomLocation> locations, boolean hasTdm, boolean hasCtf, boolean hasFfa, boolean hasInfect,
                   String mapCreator, String mediaOfCreator, String mapremaker, String mediaOfRemaker) {
        this.name = name;
        this.locations = locations;
        this.hasTdm = hasTdm;
        this.hasCtf = hasCtf;
        this.hasFfa = hasFfa;
        this.hasInfect = hasInfect;

        this.mapCreator = mapCreator;
        this.mediaOfCreator = mediaOfCreator;
        this.mapRemaker = mapremaker;
        this.mediaOfRemaker = mediaOfRemaker;
        //Maybe create different lists of locations for different type of locations
    }

    public void loadMap() {
        if(world == null) {
            //Typ?
            world = Bukkit.getServer().createWorld(new WorldCreator(name));
            System.out.println(world.getDifficulty());
            world.setDifficulty(Difficulty.PEACEFUL);
            System.out.println("Map loaded: " + name);
        }
    }

    public void unloadMap() {
        if(world != null) {
            Bukkit.getServer().unloadWorld(world, false);
            System.out.println("Map unloaded: " + name);
            world = null;
        }
    }

    public boolean hasGamemode(String gamemode) {
        switch(gamemode) {
            case "tdm":
                    return hasTdm;
            case "ctf":
                    return hasCtf;
            case "ffa":
                    return hasFfa;
            case "infect":
                return hasInfect;
            default:
                return false;
        }
    }

    /**
     * @return Returns a random gamemode of the playable gamemodes.
     */
    public String getGamemode() {
        //TODO: Rework this to be random
        return "tdm";
    }

    public Location getRedFlagLocation() {
        return locations.get(1).getLocation(world);
    }

    public Location getBlueFlagLocation() {
        return locations.get(1).getLocation(world);
    }

    /**
     * Gets a type of locations, defined by pointName
     * @param pointName The type of location
     * @return A list of locations of type pointName
     */
    public List<Location> getLocationsByName(String pointName) {
        List<Location> spawnpoints = new ArrayList<>();

        for(CustomLocation location: locations) {
            if(location.getLocationType().equals(pointName)) {
                spawnpoints.add(location.getLocation(world));
            }
        }

        return spawnpoints;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    /*private void reloadMap(String mapName) {


        plugin.getServer().unloadWorld(plugin.getServer().getWorld(mapName), false);

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
                                case "redspawn":
                                case "ffaspawn":
                                case "redflag":
                                case "blueflag":
                                    break;
                                default:
                                    isConfigSign = false;
                                    break;
                            }

                            if (isConfigSign) {
                                signLocation.getBlock().setType(Material.AIR);
                            }

                        }
                    }
                }
            }
        }
    }

    */

    //More

}
