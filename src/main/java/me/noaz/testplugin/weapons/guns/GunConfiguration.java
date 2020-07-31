package me.noaz.testplugin.weapons.guns;

import me.noaz.testplugin.Buyable;
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
public class GunConfiguration extends Buyable {
    private final int gunId;
    private final List<String> weaponLore;
    private final GunType gunType;
    private final FireType fireType;

    private final double accuracyScoped;
    private final double accuracyNotScoped;
    private final double bodyDamage;
    private final double headDamage;
    private final double damageDropoffPerTick;
    private final int damageDropoffStartAfterTick;
    //public double recoil; //Not added yet
    private final double bulletSpeed;
    private final int range;

    private final int reloadTime;
    private final int burstDelay;

    //public int weight; //Not implemented

    private final int bulletsPerClick;
    private final int bulletsPerBurst;
    private final int startingBullets;
    private final int clipSize;

    private final int scopeAnimations = 1; //Amount of animations in between normal and fully scoped
    private final int scavengerAmmunition;
    private final int maxResupplyAmmunition;

    private final Sound fireBulletSound;
    private final Sound fireWhileReloadingSound;
    private final Sound fireWithoutAmmoSound;

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
     * @param loadoutMenuSlot This guns slot in the loadout selector
     * @param unlockLevel The unlock level of this gun
     * @param costToBuy The cost, in credits, to buy this gun
     * @param fireBulletSound The sound this gun makes when it fires a bullet
     * @param fireWhileReloadingSound The sound this gun makes when trying to fire a bullet while reloading
     * @param fireWithoutAmmoSound The sound this gun maks when trying to fire a bullet without any ammo.
     */
    public GunConfiguration(int gunId, String name, String gunMaterial, String gunType, String firemode, double accuracyNotScoped,
                            double accuracyScoped, double bodyDamage, double headDamage, double damageDropoffPerTick,
                            int damageDropoffStartAfterTick, double bulletSpeed, int range,
                            int reloadTimeInMs, int burstDelayInMs, int bulletsPerBurst, int bulletsPerClick, int startingBullets,
                            int clipSize, int loadoutMenuSlot, int unlockLevel, int costToBuy, int scavengerAmmunition, int maxResupplyAmmunition,
                            String fireBulletSound, String fireWhileReloadingSound, String fireWithoutAmmoSound) {
        super(name, name, unlockLevel, costToBuy, loadoutMenuSlot, Material.getMaterial(gunMaterial));
        this.gunId = gunId;
        this.gunType = GunType.valueOf(gunType);
        this.fireType = FireType.valueOf(firemode);
        this.accuracyNotScoped = accuracyNotScoped;
        this.accuracyScoped = accuracyScoped;
        this.bodyDamage = bodyDamage;
        this.headDamage = headDamage;
        this.damageDropoffPerTick = damageDropoffPerTick;
        this.damageDropoffStartAfterTick = damageDropoffStartAfterTick;

        this.bulletSpeed = bulletSpeed;
        this.range = range;
        this.reloadTime = convertToTicks(reloadTimeInMs);
        this.burstDelay = convertToTicks(burstDelayInMs);
        this.bulletsPerBurst = bulletsPerBurst;
        this.bulletsPerClick = bulletsPerClick;
        this.startingBullets = startingBullets;
        this.clipSize = clipSize;

        this.scavengerAmmunition = scavengerAmmunition;
        this.maxResupplyAmmunition = maxResupplyAmmunition;

        this.fireBulletSound = Sound.valueOf(fireBulletSound);
        this.fireWhileReloadingSound = Sound.valueOf(fireWhileReloadingSound);
        this.fireWithoutAmmoSound = Sound.valueOf(fireWithoutAmmoSound);

        //Do some logic to show it in a more beautiful way
        weaponLore = new ArrayList<>();
        weaponLore.add(ChatColor.BLUE + "Type: " + gunType.toLowerCase());
        weaponLore.add(ChatColor.BLUE + "Hello");
    }

    public double getHeadDamage() {
        return headDamage;
    }

    public double getBodyDamage() {
        return bodyDamage;
    }

    public int getRange() {
        return range;
    }

    public double getAccuracyScoped() {
        return accuracyScoped;
    }

    public double getAccuracyNotScoped() {
        return accuracyNotScoped;
    }

    public double getBulletSpeed() {
        return bulletSpeed;
    }

    public double getDamageDropoffPerTick() {
        return damageDropoffPerTick;
    }

    public FireType getFireType() {
        return fireType;
    }

    public GunType getGunType() {
        return gunType;
    }

    public int getDamageDropoffStartAfterTick() {
        return damageDropoffStartAfterTick;
    }

    public int getGunId() {
        return gunId;
    }

    public List<String> getWeaponLore() {
        return weaponLore;
    }

    public int getStartingBullets() {
        return startingBullets;
    }

    public int getClipSize() {
        return clipSize;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public int getBulletsPerBurst() {
        return bulletsPerBurst;
    }

    public int getBulletsPerClick() {
        return bulletsPerClick;
    }

    public int getBurstDelay() {
        return burstDelay;
    }

    public int getMaxResupplyAmmunition() {
        return maxResupplyAmmunition;
    }

    public int getScavengerAmmunition() {
        return scavengerAmmunition;
    }

    public int getScopeAnimations() {
        return scopeAnimations;
    }

    public Sound getFireBulletSound() {
        return fireBulletSound;
    }

    public Sound getFireWhileReloadingSound() {
        return fireWhileReloadingSound;
    }

    public Sound getFireWithoutAmmoSound() {
        return fireWithoutAmmoSound;
    }

    private int convertToTicks(int timeInMs) {
        return Math.max(timeInMs/50,1);
    }
}
