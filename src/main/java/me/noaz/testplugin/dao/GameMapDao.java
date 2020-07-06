package me.noaz.testplugin.dao;

import me.noaz.testplugin.maps.CustomLocation;
import me.noaz.testplugin.maps.GameMap;
import org.bukkit.*;
import org.bukkit.block.Sign;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameMapDao {
    private static final String pathToNewMaps = "C:/Users/Noa/MinecraftBukkitServer/newMaps";
    private static final String pathToSavedMapsWithSigns = "C:/Users/Noa/MinecraftBukkitServer/mapsWithLocationSigns";
    private static final String pathToPlayableMaps = "C:/Users/Noa/MinecraftBukkitServer/maps";
    private static Connection connection;

    public GameMapDao(Connection connection) {
        GameMapDao.connection = connection;
    }

    public static List<GameMap> getAll() {
        List<GameMap> maps = new ArrayList<>();

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

                PreparedStatement getMapLocations = connection.prepareStatement("SELECT * FROM test.map_location " +
                        "WHERE map_id=?");
                getMapLocations.setInt(1, id);

                ResultSet mapLocations = getMapLocations.executeQuery();

                List<CustomLocation> locations = new ArrayList<>();

                while(mapLocations.next()) {
                    locations.add(new CustomLocation(mapLocations.getString("location_type"),
                            mapLocations.getDouble("x_location"),
                            mapLocations.getDouble("y_location"),
                            mapLocations.getDouble("z_location")));
                }

                maps.add(new GameMap(name, locations, hasTdm, hasCtf, hasFfa, hasInfect, mapCreator));

                System.out.println("Successfully configured map: " + name);

            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return maps;
    }

    /**
     * Copes given file/directory and all underdirectories
     * @param src The source file
     * @param target The target file
     */
    private static void copyRecursive(File src, File target) throws IOException {
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

    private static void deleteFile(Path directory) throws IOException {
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

    public static void addNewMaps(Server server) {
        File newMapFile = new File(pathToNewMaps);

        String[] newMaps = newMapFile.list();

        for(String mapName : newMaps) {
            addMap(mapName, server);
        }
    }

    private static void addMap(String mapName, Server server) {
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
        World gameWorld = server.createWorld(creator);


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
        server.unloadWorld(gameWorld, true);
        System.out.println("Map saved: " + mapName);
    }
}
