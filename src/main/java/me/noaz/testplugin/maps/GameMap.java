package me.noaz.testplugin.maps;

import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMap {
    private final String name;
    private final List<CustomLocation> locations;
    private final boolean hasTdm;
    private final boolean hasCtf;
    private final boolean hasFfa;
    private final boolean hasInfect;

    private final String mapCreators;

    private World world;

    public GameMap(String name, List<CustomLocation> locations, boolean hasTdm, boolean hasCtf, boolean hasFfa, boolean hasInfect,
                   String mapCreators) {
        this.name = name;
        this.locations = locations;
        this.hasTdm = hasTdm;
        this.hasCtf = hasCtf;
        this.hasFfa = hasFfa;
        this.hasInfect = hasInfect;

        if(mapCreators == null) {
            this.mapCreators = "";
        } else {
            this.mapCreators = mapCreators;
        }
    }

    public void loadMap() {
        //TODO: Load the map on a different thread to prevent lagspike
        if(world == null) {
            //Typ?
            /*Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("TestPlugin"), () -> {
                world = Bukkit.getServer().createWorld(new WorldCreator(name));
                world.setDifficulty(Difficulty.PEACEFUL);
                System.out.println("Map loaded: " + name);
            });*/
            world = Bukkit.getServer().createWorld(new WorldCreator(name));
            world.setDifficulty(Difficulty.PEACEFUL);
            System.out.println("Map loaded: " + name);
        }
    }

    public void unloadMap() {
        if(world != null) {
            /*Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("TestPlugin"), () -> {
                Bukkit.getServer().unloadWorld(world, false);
                System.out.println("Map unloaded: " + name);
                world = null;
            });*/
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
    public String getRandomGamemode() {
        Random random = new Random();

        //Make this better
        while(true) {
            random.nextInt(4);
            switch(random.nextInt(4)) {
                case 0:
                    if(hasTdm) {
                        return "tdm";
                    }
                    break;
                case 1:
                    if(hasCtf) {
                        return "ctf";
                    }
                    break;
                case 2:
                    if(hasInfect) {
                        return "infect";
                    }
                    break;
                case 3:
                    if(hasFfa) {
                        return "ffa";
                    }
                    break;
            }
        }
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

    public String getMapCreators() {
        return mapCreators;
    }
}
