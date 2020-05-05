package me.noaz.testplugin.Maps;

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
    private final String creatorInformation;

    private World world;

    public GameMap(String name, List<CustomLocation> locations, boolean hasTdm, boolean hasCtf, boolean hasFfa, boolean hasInfect,
                   String mapCreators, String creatorInformation) {
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

        if(creatorInformation == null) {
            this.creatorInformation = "";
        } else {
            this.creatorInformation = creatorInformation;
        }
        //Maybe create different lists of locations for different type of locations
    }

    public void loadMap() {
        //TODO: Load the map on a different thread to prevent lagspike
        if(world == null) {
            //Typ?
            world = Bukkit.getServer().createWorld(new WorldCreator(name));
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

    public String getMapCreators() {
        return mapCreators;
    }

    public String getCreatorInformation() {
        return creatorInformation;
    }
}
