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
            PreparedStatement getGunConfigurationsFromDatabase = connection.prepareStatement("SELECT * FROM test.gun_configuration");
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
            PreparedStatement getGunConfigurationsFromDatabase = connection.prepareStatement("SELECT * FROM test.gun_configuration " +
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

    /**
     * Gets the names of all attributes in the gun_configuration table.
     * @return A list containing all columns
     */
    public static List<String> getAttributeNames() {
        List<String> gunAttributeNames = new ArrayList<>();

        try {
            PreparedStatement getAttributeNamesFromDatabase = connection.prepareStatement("SELECT COLUMN_NAME FROM " +
                    "information_schema.columns WHERE TABLE_SCHEMA = 'test' AND TABLE_NAME = 'gun_configuration';");
            ResultSet result = getAttributeNamesFromDatabase.executeQuery();

            while(result.next()) {
                gunAttributeNames.add(result.getString("COLUMN_NAME"));
            }

            result.close();
            getAttributeNamesFromDatabase.closeOnCompletion();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return gunAttributeNames;
    }

    /**
     * Updates
     * @param gunName
     * @param attribute
     * @param data
     * @return True if it was successful, false otherwise
     */
    public static boolean setAttributeName(String gunName, String attribute, String data) {
        try {
            //Get the datatype of the data to insert
            PreparedStatement getAttributeDatatype = connection.prepareStatement("SELECT DATA_TYPE FROM information_schema.columns " +
                    "WHERE TABLE_SCHEMA = 'test' AND TABLE_NAME = 'gun_configuration' AND COLUMN_NAME = ?;");
            getAttributeDatatype.setString(1, attribute);

            ResultSet datatypeResult = getAttributeDatatype.executeQuery();

            String datatype = "";

            while(datatypeResult.next()) {
                datatype = datatypeResult.getString("DATA_TYPE");
            }

            //Convert and insert data into gun_configuration using that information
            PreparedStatement updateAttribute = connection.prepareStatement("UPDATE gun_configuration SET " +
                    " ?=? WHERE gun_name=?");
            updateAttribute.setString(1,attribute);

            switch(datatype) {
                case "float":
                    updateAttribute.setFloat(2,Float.parseFloat(data));
                    break;
                case "int":
                    updateAttribute.setInt(2, Integer.parseInt(data));
                    break;
                case "varchar":
                    updateAttribute.setString(2,data);
                    break;
                default:
                    //This instead? - Converts from java object to correct value(?)
                    updateAttribute.setObject(2,0);
                    break;
            }

            updateAttribute.setString(3,gunName);

            updateAttribute.executeUpdate();
            updateAttribute.closeOnCompletion();




        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
