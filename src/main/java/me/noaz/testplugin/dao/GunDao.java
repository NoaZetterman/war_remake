package me.noaz.testplugin.dao;

import me.noaz.testplugin.weapons.guns.GunConfiguration;

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
                String weaponType = result.getString("weapon_type");
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gunConfigurations;
    }

}
