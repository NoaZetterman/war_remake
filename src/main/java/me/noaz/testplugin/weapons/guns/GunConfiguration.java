package me.noaz.testplugin.weapons.guns;

import me.noaz.testplugin.Buyable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
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
    private int gunId;
    private List<String> weaponLore;
    private GunType gunType;
    private FireType fireType;

    public double accuracyScoped;
    private double accuracyNotScoped;
    private double bodyDamage;
    private double headDamage;
    private double damageDropoffPerTick;
    private int damageDropoffStartAfterTick;
    //public double recoil; //Not added yet
    private double bulletSpeed;
    private int range;

    private int reloadTimeInTicks;
    private int burstDelayInTicks;

    //public int weight; //Not implemented

    private int bulletsPerClick;
    private int bulletsPerBurst;
    private int startingBullets;
    private int clipSize;

    private int scopeAnimations = 1; //Amount of animations in between normal and fully scoped
    private int scavengerAmmunition;
    private int maxResupplyAmmunition;

    private Sound fireBulletSound;
    private Sound fireWhileReloadingSound;
    private Sound fireWithoutAmmoSound;

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
     * @param reloadTimeInTicks The reload time this weapon should have in ms
     * @param burstDelayInTicks The delay in between bursts this weapon should have, delay between shots if there is no bursts
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
                            int reloadTimeInTicks, int burstDelayInTicks, int bulletsPerBurst, int bulletsPerClick, int startingBullets,
                            int clipSize, int loadoutMenuSlot, int unlockLevel, int costToBuy, int scavengerAmmunition, int maxResupplyAmmunition,
                            String fireBulletSound, String fireWhileReloadingSound, String fireWithoutAmmoSound) {
        super(name, StringUtils.replaceChars(name, '_',' '), unlockLevel, costToBuy, loadoutMenuSlot, Material.getMaterial(gunMaterial));

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
        this.reloadTimeInTicks = reloadTimeInTicks;
        this.burstDelayInTicks = burstDelayInTicks;
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

    public int getReloadTimeInTicks() {
        return reloadTimeInTicks;
    }

    public int getBulletsPerBurst() {
        return bulletsPerBurst;
    }

    public int getBulletsPerClick() {
        return bulletsPerClick;
    }

    public int getBurstDelayInTicks() {
        return burstDelayInTicks;
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

    public void setWeaponLore(List<String> weaponLore) {
        this.weaponLore = weaponLore;
    }

    public void setGunType(String gunType) {
        this.gunType = GunType.valueOf(gunType);
    }

    public void setFireType(String fireType) {
        this.fireType = FireType.valueOf(fireType);
    }

    public void setAccuracyScoped(double accuracyScoped) {
        this.accuracyScoped = accuracyScoped;
    }

    public void setAccuracyNotScoped(double accuracyNotScoped) {
        this.accuracyNotScoped = accuracyNotScoped;
    }

    public void setBodyDamage(double bodyDamage) {
        this.bodyDamage = bodyDamage;
    }

    public void setHeadDamage(double headDamage) {
        this.headDamage = headDamage;
    }

    public void setDamageDropoffPerTick(double damageDropoffPerTick) {
        this.damageDropoffPerTick = damageDropoffPerTick;
    }

    public void setDamageDropoffStartAfterTick(int damageDropoffStartAfterTick) {
        this.damageDropoffStartAfterTick = damageDropoffStartAfterTick;
    }

    public void setBulletSpeed(double bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setReloadTimeInTicks(int reloadTimeInTicks) {
        this.reloadTimeInTicks = reloadTimeInTicks;
    }

    public void setBurstDelayInTicks(int burstDelayInTicks) {
        this.burstDelayInTicks = burstDelayInTicks;
    }

    public void setBulletsPerClick(int bulletsPerClick) {
        this.bulletsPerClick = bulletsPerClick;
    }

    public void setBulletsPerBurst(int bulletsPerBurst) {
        this.bulletsPerBurst = bulletsPerBurst;
    }

    public void setStartingBullets(int startingBullets) {
        this.startingBullets = startingBullets;
    }

    public void setClipSize(int clipSize) {
        this.clipSize = clipSize;
    }

    public void setScopeAnimations(int scopeAnimations) {
        this.scopeAnimations = scopeAnimations;
    }

    public void setScavengerAmmunition(int scavengerAmmunition) {
        this.scavengerAmmunition = scavengerAmmunition;
    }

    public void setMaxResupplyAmmunition(int maxResupplyAmmunition) {
        this.maxResupplyAmmunition = maxResupplyAmmunition;
    }

    public void setFireBulletSound(String fireBulletSound) {
        this.fireBulletSound = Sound.valueOf(fireBulletSound);
    }

    public void setFireWhileReloadingSound(String fireWhileReloadingSound) {
        this.fireWhileReloadingSound = Sound.valueOf(fireWhileReloadingSound);
    }

    public void setFireWithoutAmmoSound(String fireWithoutAmmoSound) {
        this.fireWithoutAmmoSound = Sound.valueOf(fireWithoutAmmoSound);
    }

    /**
     * Set a field with name field and value value.
     * @param field The name of the field
     * @param value The value, as a string
     */
    public void setAttribute(String field, String value) throws NumberFormatException {
        //Better way?
        switch(field) {
            case "name":
                super.setName(StringUtils.replaceChars(value,' ', '_'));
            case "displayName":
                super.setDisplayName(StringUtils.replaceChars(value, '_',' '));
                break;
            case "unlockLevel":
                super.setUnlockLevel(Integer.parseInt(value));
                break;
            case "costToBuy":
                super.setCostToBuy(Integer.parseInt(value));
                break;
            case "loadoutMenuSlot":
                super.setLoadoutMenuSlot(Integer.parseInt(value));
                break;
            case "material":
                super.setMaterial(value);
                break;
            case "gunType":
                gunType = GunType.valueOf(value);
                break;
            case "fireType":
                fireType = FireType.valueOf(value);
                break;
            case "accuracyScoped":
                accuracyScoped = Double.parseDouble(value);
                break;
            case "accuracyNotScoped":
                accuracyNotScoped = Double.parseDouble(value);
                break;
            case "bodyDamage":
                bodyDamage = Double.parseDouble(value);
                break;
            case "headDamage":
                headDamage = Double.parseDouble(value);
                break;
            case "damageDropoffPerTick":
                damageDropoffPerTick = Double.parseDouble(value);
                break;
            case "damageDropoffStartAfterTick":
                damageDropoffStartAfterTick = Integer.parseInt(value);
                break;
            case "bulletSpeed":
                bulletSpeed = Integer.parseInt(value);
                break;
            case "range":
                range = Integer.parseInt(value);
                break;
            case "reloadTimeInTicks":
                reloadTimeInTicks = Integer.parseInt(value);
                break;
            case "burstDelayInTicks":
                burstDelayInTicks = Integer.parseInt(value);
                break;
            case "bulletsPerClick":
                bulletsPerClick = Integer.parseInt(value);
                break;
            case "bulletsPerBurst":
                bulletsPerBurst = Integer.parseInt(value);
                break;
            case "startingBullets":
                startingBullets = Integer.parseInt(value);
                break;
            case "clipSize":
                clipSize = Integer.parseInt(value);
                break;
            case "scavengerAmmunition":
                scavengerAmmunition = Integer.parseInt(value);
                break;
            case "maxResupplyAmmunition":
                maxResupplyAmmunition = Integer.parseInt(value);
                break;
            case "fireBulletSound":
                fireBulletSound = Sound.valueOf(value);
                break;
            case "fireWhileReloadingSound":
                fireWhileReloadingSound = Sound.valueOf(value);
                break;
            case "fireWithoutAmmoSound":
                fireWithoutAmmoSound = Sound.valueOf(value);
                break;
            default:
                Bukkit.getLogger().info("Invalid field");
        }

    }
}
