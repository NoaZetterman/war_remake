package me.noaz.testplugin;

import me.noaz.testplugin.dao.GameMapDao;
import me.noaz.testplugin.dao.GunDao;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.*;

/**
 * This class contains data from the database that is used frequently.
 *
 * @author Noa Zetterman
 * @version 2020-03-01
 */
public class GameData {

    private List<GameMap> maps;
    private List<GunConfiguration> gunConfigurations;
    private HashMap<Player, PlayerExtension> playerExtensions = new HashMap<>();

    /**
     * Starts the timer.
     * @param plugin This plugin
     */
    public GameData(TestPlugin plugin) {
        gunConfigurations = GunDao.getAll();
        GameMapDao.addNewMaps(plugin.getServer());
        maps = GameMapDao.getAll();
    }

    /**
     * Sets the next game to given input.
     * @param mapName The name of the map
     * @param gamemode The gamemode to use
     * @return True if the map and gamemode was set, False otherwise.
     */
    public boolean gameAndGamemodeExists(String mapName, String gamemode) {
        for(GameMap map : maps) {
            if(map.getName().equals(mapName) && map.hasGamemode(gamemode)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Picks next map and gamemode randomly, will not be same map as previously.
     * But may be same gamemode.
     * @param previousMap The previous map, this will not be selected
     */
    public GameMap getNewGameMap(GameMap previousMap) {
        Random random = new Random();

        GameMap nextMap = previousMap;
        if(previousMap != null) {

            while(nextMap.getName().equals(previousMap.getName())) {
                nextMap = maps.get(random.nextInt(maps.size()));
            }
        } else {
            nextMap = maps.get(random.nextInt(maps.size()));
        }

        return nextMap;
    }

    public GameMap getGameMap(String mapName) {
        for(GameMap map : maps) {
            if(map.getName().equals(mapName)) {
                return map;
            }
        }
        return null;
    }

    public void addPlayer(TestPlugin plugin, Player player, ScoreManager scoreManager) {
        playerExtensions.put(player, new PlayerExtension(plugin, player, scoreManager, getGunConfigurations()));
    }

    public void removePlayer(Player player) {
        playerExtensions.get(player).leaveGame();
        playerExtensions.remove(player);
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
     * @return All gun configurations as a HashMap, with gun name as key and a WeaponConfiguration object as value
     */
    public List<GunConfiguration> getGunConfigurations() {
        return gunConfigurations;

    }

    public List<String> getGunNames() {
        List<String> gunNames = new ArrayList<>();
        for(GunConfiguration configuration: gunConfigurations) {
            gunNames.add(configuration.getDisplayName());
        }

        return gunNames;
    }

    public PlayerExtension getPlayerExtension(Player player) {
        return playerExtensions.get(player);
    }

    public Set<Player> getPlayers() {
        return playerExtensions.keySet();
    }

    public HashMap<Player,PlayerExtension> getPlayerExtensionHashMap() {
        return playerExtensions;
    }

    public Collection<PlayerExtension> getPlayerExtensions() {
        return playerExtensions.values();
    }

    //Not rly the playercount?
    public int getPlayercount() {
        return playerExtensions.size();
    }
}