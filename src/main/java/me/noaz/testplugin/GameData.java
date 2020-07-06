package me.noaz.testplugin;

import me.noaz.testplugin.dao.GameMapDao;
import me.noaz.testplugin.maps.CustomLocation;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import org.bukkit.*;
import org.bukkit.block.Sign;
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
import java.util.*;

/**
 * This class contains data from the database that is used frequently.
 *
 * @author Noa Zetterman
 * @version 2020-03-01
 */
public class GameData {
    private TestPlugin plugin;

    private List<GameMap> maps;
    private List<GunConfiguration> gunConfigurations = new ArrayList<>();
    private HashMap<Player, PlayerExtension> playerExtensions = new HashMap<>();

    /**
     * Starts the timer.
     * @param plugin This plugin
     */
    public GameData(TestPlugin plugin, Connection connection) {
        this.plugin = plugin;
        createGunConfigurations(connection);
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

    public void addPlayer(TestPlugin plugin, Player player, ScoreManager scoreManager, Connection connection) {
        playerExtensions.put(player, new PlayerExtension(plugin, player, scoreManager, getGunConfigurations(), connection));
    }

    public void removePlayer(Player player) {
        playerExtensions.get(player).saveCurrentLoadout(false);
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
            gunNames.add(configuration.name);
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

    public int getPlayercount() {
        return playerExtensions.size();
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
}