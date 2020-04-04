package me.noaz.testplugin.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a weapon, the same instance is used by all players who uses the same gun.
 *
 * @author Noa Zetterman
 * @version 2019-12-13
 */
public class WeaponConfiguration {
    private final String name;
    private final List<String> weaponLore;
    private final String weaponType;
    private final String fireType;

    private final double accuracyScoped;
    private final double accuracyNotScoped;
    private final double bodyDamage;
    private final double headDamage;
    private double recoil; //Not used yet (though it kinda is)
    private final double bulletSpeed;
    private final int range;

    private final Material gunMaterial;

    private final int reloadTime;
    private final int burstDelay;

    private int weight; //Not implemented

    private final int bulletsPerClick;
    private final int bulletsPerBurst;
    private final int startingBullets;
    private final int clipSize;

    /**
     * Configures a weapon
     *
     * @param name Name of the weapon
     * @param gunMaterial Material of the weapon
     * @param accuracyNotScoped The accuracy this weapon should have when not scoped
     * @param accuracyScoped The accuracy this weapon should have when scoped
     * @param bodyDamage The damage bullets fired with this weapon should do to the body of another player
     * @param headDamage The damage bullets fired with this weapon should do to the head of another player
     * @param bulletSpeed The bullet speed of the bullets fired with this weapon
     * @param range The range, in blocks, the bullets fired by this weapon should have.
     * @param reloadTimeInMs The reload time this weapon should have in ms
     * @param burstDelayInMs The delay in between bursts this weapon should have, delay between shots if there is no bursts
     * @param bulletsPerBurst The amount of bullets that should be fired when shooting (right clicking with the gun),
     *                        if this is 1 then it acts as a gun without bursts.
     * @param bulletsPerClick The amount of bullets that should be fired per shot, usually one but different for  ex:shotguns
     * @param startingBullets The amount of bullets this gun should start with
     * @param clipSize The amount of bullets that can be fired before reloading
     */
    public WeaponConfiguration(String name, Material gunMaterial, String weaponType, String fireType, double accuracyNotScoped,
                                double accuracyScoped, double bodyDamage, double headDamage, double bulletSpeed, int range,
                                int reloadTimeInMs, int burstDelayInMs, int bulletsPerBurst, int bulletsPerClick, int startingBullets, int clipSize) {
        this.name = name;
        this.gunMaterial = gunMaterial;
        this.weaponType = weaponType;
        this.fireType = fireType;
        this.accuracyNotScoped = accuracyNotScoped;
        this.accuracyScoped = accuracyScoped;
        this.bodyDamage = bodyDamage;
        this.headDamage = headDamage;
        this.bulletSpeed = bulletSpeed;
        this.range = range;
        this.reloadTime = convertToTicks(reloadTimeInMs);
        this.burstDelay = convertToTicks(burstDelayInMs);
        this.bulletsPerBurst = bulletsPerBurst;
        this.bulletsPerClick = bulletsPerClick;
        this.startingBullets = startingBullets;
        this.clipSize = clipSize;

        //Do some logic to show it in a more beautiful way
        weaponLore = new ArrayList<>();
        weaponLore.add(ChatColor.BLUE + "Type: " + weaponType.toLowerCase());
        weaponLore.add(ChatColor.BLUE + "Yeeters");
    }

    /**
     * Configures a weapon
     *
     * @param name Name of the weapon
     * @param gunMaterial Material of the weapon
     * @param accuracyNotScoped The accuracy this weapon should have when not scoped
     * @param accuracyScoped The accuracy this weapon should have when scoped
     * @param bodyDamage The damage bullets fired with this weapon should do to the body of another player
     * @param headDamage The damage bullets fired with this weapon should do to the head of another player
     * @param bulletSpeed The bullet speed of the bullets fired with this weapon
     * @param range The range, in blocks, the bullets fired by this weapon should have.
     * @param reloadTimeInMs The reload time this weapon should have in ms
     * @param burstDelayInMs The delay in between bursts this weapon should have, delay between shots if there is no bursts
     * @param bulletsPerBurst The amount of bullets that should be fired when shooting (right clicking with the gun),
     *                        if this is 1 then it acts as a gun without bursts.
     * @param startingBullets The amount of bullets this gun should start with
     * @param clipSize The amount of bullets that can be fired before reloading
     */
    public WeaponConfiguration(String name, Material gunMaterial, String weaponType, String fireType, double accuracyNotScoped,
                               double accuracyScoped, double bodyDamage, double headDamage, double bulletSpeed, int range,
                               int reloadTimeInMs, int burstDelayInMs, int bulletsPerBurst, int startingBullets, int clipSize) {
        this.name = name;
        this.gunMaterial = gunMaterial;
        this.weaponType = weaponType;
        this.fireType = fireType;
        this.accuracyNotScoped = accuracyNotScoped;
        this.accuracyScoped = accuracyScoped;
        this.bodyDamage = bodyDamage;
        this.headDamage = headDamage;
        this.bulletSpeed = bulletSpeed;
        this.range = range;
        this.reloadTime = convertToTicks(reloadTimeInMs);
        this.burstDelay = convertToTicks(burstDelayInMs);
        this.bulletsPerBurst = bulletsPerBurst;
        this.bulletsPerClick = 1;
        this.startingBullets = startingBullets;
        this.clipSize = clipSize;

        //Do some logic to show it in a more beautiful way
        weaponLore = new ArrayList<>();
        weaponLore.add(ChatColor.BLUE + "Type: " + weaponType.toLowerCase());
        weaponLore.add(ChatColor.BLUE + "Yeeters");
    }

    private int convertToTicks(int timeInMs) {
        return Math.max(timeInMs/50,1);
    }

    public String getName() {
        return name;
    }

    public List<String> getWeaponLore() {
        return weaponLore;
    }

    public String getWeaponType() {
        return weaponType;
    }

    public String getFireType() {
        return fireType;
    }

    public double getAccuracyScoped() {
        return accuracyScoped;
    }

    public double getAccuracyNotScoped() {
        return accuracyNotScoped;
    }

    public double getBodyDamage() {
        return bodyDamage;
    }

    public double getHeadDamage() {
        return headDamage;
    }

    /* Not implemented
    public double getRecoil() {
        return recoil;
    }*/

    public double getBulletSpeed() {
        return bulletSpeed;
    }

    public int getRange() {
        return range;
    }

    public Material getGunMaterial() {
        return gunMaterial;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public int getBurstDelay() {
        return burstDelay;
    }

    public int getBulletsPerClick() {
        return bulletsPerClick;
    }

    public int getBulletsPerBurst() { return bulletsPerBurst; }

    public int getStartingBullets() {
        return startingBullets;
    }

    public int getClipSize() {
        return clipSize;
    }
}
