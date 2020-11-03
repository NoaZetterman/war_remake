package me.noaz.testplugin.dao;

import me.noaz.testplugin.weapons.guns.GunConfiguration;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GunDao {
    private static Connection connection;

    public GunDao(Connection connection) {
        GunDao.connection = connection;
    }

    public static List<GunConfiguration> getAll() {
        List<GunConfiguration> gunConfigurations = new ArrayList<>();

        try {
            PreparedStatement getGunConfigurationsFromDatabase = connection.prepareStatement("SELECT * FROM gun_configuration");
            ResultSet result = getGunConfigurationsFromDatabase.executeQuery();

            while(result.next()) {
                int gunId = result.getInt("gun_id");
                String name = result.getString("gun_name");
                String gunMaterial = result.getString("gun_material");
                String weaponType = result.getString("gun_type");
                String fireType = result.getString("fire_type");
                float accuracyNotScoped = result.getFloat("accuracy_not_scoped");
                float accuracyScoped = result.getFloat("accuracy_scoped");
                float bodyDamage = result.getFloat("body_damage");
                float headDamage = result.getFloat("head_damage");
                float damageDropoffPerTick = result.getFloat("damage_dropoff_per_tick");
                int damageDropoffStartAfterTick = result.getInt("damage_dropoff_start_after_tick");
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
                int scavengerAmmunition = result.getInt("scavenger_ammunition");
                int maxResupplyAmmunition = result.getInt("max_resupply_ammunition");
                String fireBulletSound = result.getString("fire_bullet_sound");
                String fireWhileReloadingSound = result.getString("fire_while_reloading_sound");
                String fireWithoutAmmoSound = result.getString("fire_without_ammo_sound");

                gunConfigurations.add(new GunConfiguration(gunId, name, gunMaterial, weaponType,
                        fireType, accuracyNotScoped, accuracyScoped, bodyDamage, headDamage,
                        damageDropoffPerTick, damageDropoffStartAfterTick,
                        bulletSpeed, gunRange, reloadTimeInMs, burstDelayInMs, bulletsPerBurst,
                        bulletsPerClick, startingBullets, clipSize, loadoutSlot, unlockLevel,
                        costToBuy, scavengerAmmunition, maxResupplyAmmunition, fireBulletSound, fireWhileReloadingSound, fireWithoutAmmoSound));
            }

            result.close();
            getGunConfigurationsFromDatabase.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gunConfigurations;
    }

    /**
     * Updates this gunConfiguration to sync with the values in the database
     *
     * @param gunConfiguration The GunConfiguration to update.
     */
    public static void updateGunConfiguration(GunConfiguration gunConfiguration) {
        try {
            PreparedStatement getGunConfigurationsFromDatabase = connection.prepareStatement("SELECT * FROM gun_configuration " +
                    "WHERE gun_id=?");
            getGunConfigurationsFromDatabase.setInt(1, gunConfiguration.getGunId());
            ResultSet result = getGunConfigurationsFromDatabase.executeQuery();

            while(result.next()) {
                gunConfiguration.setName(result.getString("gun_name"));
                gunConfiguration.setDisplayName(result.getString("gun_name"));
                gunConfiguration.setMaterial(result.getString("gun_material"));
                gunConfiguration.setGunType(result.getString("gun_type"));
                gunConfiguration.setFireType(result.getString("fire_type"));
                gunConfiguration.setAccuracyNotScoped(result.getFloat("accuracy_not_scoped"));
                gunConfiguration.setAccuracyScoped(result.getFloat("accuracy_scoped"));
                gunConfiguration.setBodyDamage(result.getFloat("body_damage"));
                gunConfiguration.setHeadDamage(result.getFloat("head_damage"));
                gunConfiguration.setDamageDropoffPerTick(result.getFloat("damage_dropoff_per_tick"));
                gunConfiguration.setDamageDropoffStartAfterTick(result.getInt("damage_dropoff_start_after_tick"));
                gunConfiguration.setBulletSpeed(result.getFloat("bullet_speed"));
                gunConfiguration.setRange(result.getInt("gun_range"));
                gunConfiguration.setReloadTime(result.getInt("reload_time_in_ms"));
                gunConfiguration.setBurstDelay(result.getInt("burst_delay_in_ms"));
                gunConfiguration.setBulletsPerBurst(result.getInt("bullets_per_burst"));
                gunConfiguration.setBulletsPerClick(result.getInt("bullets_per_click"));
                gunConfiguration.setStartingBullets(result.getInt("starting_bullets"));
                gunConfiguration.setClipSize(result.getInt("clip_size"));
                gunConfiguration.setLoadoutMenuSlot(result.getInt("loadout_slot"));
                gunConfiguration.setUnlockLevel(result.getInt("unlock_level"));
                gunConfiguration.setCostToBuy(result.getInt("cost_to_buy"));
                gunConfiguration.setScavengerAmmunition(result.getInt("scavenger_ammunition"));
                gunConfiguration.setMaxResupplyAmmunition(result.getInt("max_resupply_ammunition"));
                gunConfiguration.setFireBulletSound(result.getString("fire_bullet_sound"));
                gunConfiguration.setFireWhileReloadingSound(result.getString("fire_while_reloading_sound"));
                gunConfiguration.setFireWithoutAmmoSound(result.getString("fire_without_ammo_sound"));

            }

            result.close();
            getGunConfigurationsFromDatabase.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean createNewGunConfiguration(String name, List<GunConfiguration> gunConfigurations) {
        name = StringUtils.replaceChars(name, ' ', '_');

        try {
            PreparedStatement createNewGun = connection.prepareStatement("INSERT INTO gun_configuration (gun_name) VALUES (?)");
            createNewGun.setString(1, name);
            createNewGun.execute();
            createNewGun.closeOnCompletion();

            PreparedStatement getGunConfiguration = connection.prepareStatement("SELECT * FROM gun_configuration WHERE gun_name=?");
            getGunConfiguration.setString(1, name);
            ResultSet resultSet = getGunConfiguration.executeQuery();
            while(resultSet.next()) {
                int gunId = resultSet.getInt("gun_id");
                String gunMaterial = resultSet.getString("gun_material");
                String weaponType = resultSet.getString("gun_type");
                String fireType = resultSet.getString("fire_type");
                float accuracyNotScoped = resultSet.getFloat("accuracy_not_scoped");
                float accuracyScoped = resultSet.getFloat("accuracy_scoped");
                float bodyDamage = resultSet.getFloat("body_damage");
                float headDamage = resultSet.getFloat("head_damage");
                float damageDropoffPerTick = resultSet.getFloat("damage_dropoff_per_tick");
                int damageDropoffStartAfterTick = resultSet.getInt("damage_dropoff_start_after_tick");
                float bulletSpeed = resultSet.getFloat("bullet_speed");
                int gunRange = resultSet.getInt("gun_range");
                int reloadTimeInMs = resultSet.getInt("reload_time_in_ms");
                int burstDelayInMs = resultSet.getInt("burst_delay_in_ms");
                int bulletsPerBurst = resultSet.getInt("bullets_per_burst");
                int bulletsPerClick = resultSet.getInt("bullets_per_click");
                int startingBullets = resultSet.getInt("starting_bullets");
                int clipSize = resultSet.getInt("clip_size");
                int loadoutSlot = resultSet.getInt("loadout_slot");
                int unlockLevel = resultSet.getInt("unlock_level");
                int costToBuy = resultSet.getInt("cost_to_buy");
                int scavengerAmmunition = resultSet.getInt("scavenger_ammunition");
                int maxResupplyAmmunition = resultSet.getInt("max_resupply_ammunition");
                String fireBulletSound = resultSet.getString("fire_bullet_sound");
                String fireWhileReloadingSound = resultSet.getString("fire_while_reloading_sound");
                String fireWithoutAmmoSound = resultSet.getString("fire_without_ammo_sound");

                gunConfigurations.add(new GunConfiguration(gunId, name, gunMaterial, weaponType,
                        fireType, accuracyNotScoped, accuracyScoped, bodyDamage, headDamage,
                        damageDropoffPerTick, damageDropoffStartAfterTick,
                        bulletSpeed, gunRange, reloadTimeInMs, burstDelayInMs, bulletsPerBurst,
                        bulletsPerClick, startingBullets, clipSize, loadoutSlot, unlockLevel,
                        costToBuy, scavengerAmmunition, maxResupplyAmmunition, fireBulletSound, fireWhileReloadingSound, fireWithoutAmmoSound));
            }

            resultSet.close();
            getGunConfiguration.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean deleteGunConfiguration(String name) {
        name = StringUtils.replaceChars(name, ' ', '_');

        try {
            PreparedStatement getGunConfigurationsFromDatabase = connection.prepareStatement("DELETE FROM gun_configuration " +
                    "WHERE gun_name=?");
            getGunConfigurationsFromDatabase.setString(1, name);
            getGunConfigurationsFromDatabase.execute();

            getGunConfigurationsFromDatabase.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
