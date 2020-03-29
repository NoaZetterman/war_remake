package me.noaz.testplugin.weapons;

import de.Herbystar.TTA.TTA_Methods;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerStatistic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.List;

/**
 * Main class that weapons are built from, may be inherited for special guns, only used to shoot and for setup.
 * Right now a demo gun
 *
 * @author Noa Zetterman
 * @version 2019-12-16
 */
public abstract class Weapon {
    protected TestPlugin plugin;
    protected Player player;
    protected PlayerStatistic statistics;
    protected WeaponConfiguration config;

    protected int currentClip;
    protected int currentBullets;
    protected long reloadEnd;
    protected long nextBurst;

    protected boolean isReloading = false;


    /**
     * @param plugin this plugin
     * @param player The player that should use this weapon
     * @param statistics That players statistics
     * @param config The configuration of this weapon
     */
    protected Weapon(TestPlugin plugin, Player player, PlayerStatistic statistics, WeaponConfiguration config) {
        this.plugin = plugin;
        this.player = player;
        this.statistics = statistics;
        this.config = config;
        this.currentClip = config.getClipSize();
        this.currentBullets = config.getStartingBullets();
    }

    /**
     * Tries to shoot one burst of bullets, does not shoot when player should not be able to shoot (eg reloading)
     */
    public abstract void shoot();

    /**
     * Starts reloading the weapon
     */
    public void reload() {
        if(currentClip != config.getClipSize()) {
            reloadEnd = System.currentTimeMillis() + config.getReloadTime();
            currentClip = Math.min(config.getClipSize(), currentBullets);
            isReloading = true;
            TTA_Methods.sendActionBar(player, ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "Reloading");
            //Differently later
        }
    }


    //Maybe change reloading sys to work with PlayerHandler and drawing out stuff showing reload
    protected boolean isReloading() {
        if(isReloading && reloadEnd <= System.currentTimeMillis()) {
            isReloading = false;
        }
        return isReloading;
    }

    /**
     * Calculates the direction of the bullet with respect to accuracy
     * @param accuracy The accuracy of this gun
     * @return A vector containing direction of the bullet
     */
    protected Vector calculateBulletDirection(double accuracy) {
        Vector velocity = player.getLocation().toVector();
        velocity.normalize();

        velocity.rotateAroundX(0.5*calculateAccuracy(accuracy));
        velocity.rotateAroundY(0.5*calculateAccuracy(accuracy));
        velocity.rotateAroundZ(0.5*calculateAccuracy(accuracy));

        velocity.multiply(config.getBulletSpeed());
        return velocity;
    }

    private double calculateAccuracy(double accuracy) {
        if(accuracy <= 100) {
            return (Math.random() - 0.5) / accuracy;
        } else {
            return 1;
        }
    }

    /**
     * Resets the gun to the equivalent of a new gun, use this when a player dies.
     */
    public void reset() {
        isReloading = false;
        currentBullets = config.getStartingBullets();
        currentClip = config.getClipSize();
    }

    private int getCurrentBullets() {
        int bulletsLeft = 0;
        //Not used anymore
        ItemStack[] inventoryContent = player.getInventory().getContents();
        for(ItemStack i : inventoryContent) {
            if(i.getType().equals(config.getGunMaterial())) {
                bulletsLeft += i.getAmount();
            }
        }
        return bulletsLeft;
    }

    /**
     * @return The material type of this weapon
     */
    public Material getMaterialType() {
        return config.getGunMaterial();
    }

    /**
     * @return The material of this gun as an item stack
     */
    public ItemStack getMaterialAsItemStack() {
        return new ItemStack(config.getGunMaterial());
    }

    /**
     * @return The amount of bullets this gun starts with
     */
    public int getStartingBullets() {
        return config.getStartingBullets();
    }

    public List<String> getLore() {
        return config.getWeaponLore();
    }

    @Override
    public String toString() {
        return config.getName();
    }
}
