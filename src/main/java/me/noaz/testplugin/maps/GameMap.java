package me.noaz.testplugin.maps;

import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMap {
    private final String name;
    private final List<CustomLocation> locations;
    private final boolean hasTeamDeathMatch;
    private final boolean hasCaptureTheFlag;
    private final boolean hasFreeForAll;
    private final boolean hasInfect;

    private final String mapCreators;

    private World world;

    public GameMap(String name, List<CustomLocation> locations, boolean hasTeamDeathMatch, boolean hasCaptureTheFlag, boolean hasFreeForAll, boolean hasInfect,
                   String mapCreators) {
        this.name = name;
        this.locations = locations;
        this.hasTeamDeathMatch = hasTeamDeathMatch;
        this.hasCaptureTheFlag = hasCaptureTheFlag;
        this.hasFreeForAll = hasFreeForAll;
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
            Bukkit.getServer().unloadWorld(world, false);
            System.out.println("Map unloaded: " + name);
            world = null;
        }
    }

    public boolean hasGamemode(String gamemodeString) {
        Gamemode gamemode = Gamemode.valueOf(gamemodeString);

        switch(gamemode) {
            case TEAM_DEATH_MATCH:
                return hasTeamDeathMatch;
            case CAPTURE_THE_FLAG:
                return hasCaptureTheFlag;
            case FREE_FOR_ALL:
                return hasFreeForAll;
            case INFECT:
                return hasInfect;
            default:
                return false;
        }
    }

    /**
     * @return Returns a random gamemode of the playable gamemodes.
     */
    public Gamemode getRandomGamemode(int playercount) {
        Random random = new Random();

        while(true) {
            switch(Gamemode.values()[random.nextInt(Gamemode.values().length)]) {
                case TEAM_DEATH_MATCH:
                    if(hasTeamDeathMatch) {
                        return Gamemode.TEAM_DEATH_MATCH;
                    }
                    break;
                case CAPTURE_THE_FLAG:
                    if(hasCaptureTheFlag) {
                        return Gamemode.CAPTURE_THE_FLAG;
                    }
                    break;
                case INFECT:
                    if(hasInfect && playercount >= 2) {
                        return Gamemode.INFECT;
                    }
                    break;
                case FREE_FOR_ALL:
                    if(hasFreeForAll) {
                        return Gamemode.FREE_FOR_ALL;
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
