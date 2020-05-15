package me.noaz.testplugin.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a weapon, the same instance is used by all players who uses the same gun.
 *
 * @author Noa Zetterman
 * @version 2019-12-13
 */
public class GunConfiguration {
    public final int gunId;
    public final String name;
    public final List<String> weaponLore;
    public final String gunType;
    public final String fireType;

    public final double accuracyScoped;
    public final double accuracyNotScoped;
    public final double bodyDamage;
    public final double headDamage;
    //public double recoil; //Not added yet
    public final double bulletSpeed;
    public final int range;

    public final Material gunMaterial;

    public final int reloadTime;
    public final int burstDelay;

    //public int weight; //Not implemented

    public final int bulletsPerClick;
    public final int bulletsPerBurst;
    public final int startingBullets;
    public final int clipSize;

    public final int unlockLevel;
    public final int loadoutSlot;
    public final int costToBuy;

    public final Sound fireBulletSound;
    public final Sound fireWhileReloadingSound;
    public final Sound fireWithoutAmmoSound;

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
     * @param loadoutSlot This guns slot in the loadout selector
     * @param unlockLevel The unlock level of this gun
     * @param costToBuy The cost, in credits, to buy this gun
     * @param fireBulletSound The sound this gun makes when it fires a bullet
     * @param fireWhileReloadingSound The sound this gun makes when trying to fire a bullet while reloading
     * @param fireWithoutAmmoSound The sound this gun maks when trying to fire a bullet without any ammo.
     */
    public GunConfiguration(int gunId, String name, String gunMaterial, String gunType, String fireType, double accuracyNotScoped,
                            double accuracyScoped, double bodyDamage, double headDamage, double bulletSpeed, int range,
                            int reloadTimeInMs, int burstDelayInMs, int bulletsPerBurst, int bulletsPerClick, int startingBullets,
                            int clipSize, int loadoutSlot, int unlockLevel, int costToBuy,
                            String fireBulletSound, String fireWhileReloadingSound, String fireWithoutAmmoSound) {
        this.gunId = gunId;
        this.name = name;
        this.gunMaterial = Material.getMaterial(gunMaterial);
        this.gunType = gunType;
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
        this.loadoutSlot = loadoutSlot;
        this.unlockLevel = unlockLevel;
        this.costToBuy = costToBuy;

        this.fireBulletSound = Sound.valueOf(fireBulletSound);
        this.fireWhileReloadingSound = Sound.valueOf(fireWhileReloadingSound);
        this.fireWithoutAmmoSound = Sound.valueOf(fireWithoutAmmoSound);


        //Do some logic to show it in a more beautiful way
        weaponLore = new ArrayList<>();
        weaponLore.add(ChatColor.BLUE + "Type: " + gunType.toLowerCase());
        weaponLore.add(ChatColor.BLUE + "Hello");
    }

    private int convertToTicks(int timeInMs) {
        return Math.max(timeInMs/50,1);
    }
}
