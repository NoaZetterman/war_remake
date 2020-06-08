package me.noaz.testplugin.weapons.guns;

import me.noaz.testplugin.TestPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * Handles the physics of a bullet. Fires it and stops it after given range.
 *
 * @author Noa Zetterman
 * @version 2019-12-11
 */
public class Bullet {
    /**
     * Creates a bullet that fires from players face in the direction player is looking at
     * for a specified range wih specified accuracy
     *
     * @param player The player to shoot the bullet
     * @param plugin This plugin
     * @param bulletSpeed The speed in blocks per sec
     * @param range The length the bullet travels before stopping
     * @param bodyDamage The damage this bullet should do when it hits a players body (everything except head)
     * @param headDamage The damage this bullet should do when it hits the players head
     */
    public Bullet(Player player, TestPlugin plugin, Vector velocity, double bulletSpeed, int range, double bodyDamage, double headDamage) {

        Entity bullet = player.launchProjectile(Snowball.class);

        bullet.setGravity(false);
        bullet.setVelocity(velocity);

        bullet.setMetadata("bodyDamage", new FixedMetadataValue(plugin, bodyDamage));
        bullet.setMetadata("headDamage", new FixedMetadataValue(plugin, headDamage));

        Bukkit.getServer().getScheduler().runTaskLater(plugin, new ActivateGravity(bullet), (int) Math.ceil(range/bulletSpeed)); //Delay = range with respect to speed (time=dist/speed)
    }

    /**
     * Subclass to bullet for activating gravity after bullet a certain distance (time)
     */
    private class ActivateGravity implements Runnable {
        Entity bullet;

        public ActivateGravity(Entity bullet) {
            this.bullet = bullet;
        }

        @Override
        public void run() {
            bullet.setGravity(true);
            bullet.setVelocity(bullet.getVelocity().normalize());
        }
    }

}
