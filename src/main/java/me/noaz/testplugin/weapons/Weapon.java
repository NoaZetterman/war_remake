package me.noaz.testplugin.weapons;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Utils.ActionBarMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Main class that weapons are built from.
 *
 * @author Noa Zetterman
 * @version 2019-12-16
 */
public abstract class Weapon {
    protected TestPlugin plugin;
    protected PlayerExtension player;
    protected PlayerStatistic statistics;
    protected WeaponConfiguration config;

    protected int currentClip;
    protected int currentBullets;

    protected boolean isNextBulletReady = true;
    protected boolean isReloading = false;
    protected boolean isShooting = false;

    protected BukkitRunnable reloadTask;
    protected BukkitRunnable burstDelayTask;

    protected int itemSlot;

    /**
     * @param plugin this plugin
     * @param player The player that should use this weapon
     * @param statistics That players statistics
     * @param config The configuration of this weapon
     */
    protected Weapon(TestPlugin plugin, PlayerExtension player, PlayerStatistic statistics, WeaponConfiguration config) {
        this.plugin = plugin;
        this.player = player;
        this.statistics = statistics;
        this.config = config;
        this.currentClip = config.getClipSize();
        this.currentBullets = config.getStartingBullets();

        itemSlot = config.getWeaponType().equals("Secondary") ? 2 : 1;


        //They have to be initialised now to not cause errors
        reloadTask = new BukkitRunnable() {
            @Override
            public void run() {
            }
        };
        burstDelayTask = new BukkitRunnable() {
            @Override
            public void run() {
            }
        };


    }

    /**
     * Tries to shoot one burst of bullets, does not shoot when player should not be able to shoot (eg reloading)
     */
    public abstract void shoot();
    //TODO: Add sounds

    /**
     * Reloads the gun
     */
    public void reload() {
        if(!isReloading && currentClip != config.getClipSize() && currentClip != currentBullets) {
            isReloading = true;

            reloadTask = new BukkitRunnable() {
                int i = 0;

                @Override
                public void run() {
                    i++;
                    if (i >= config.getReloadTime()) {
                        currentClip = Math.min(config.getClipSize(), currentBullets);
                        isReloading = false;
                        ActionBarMessage.ammunitionCurrentAndTotal(currentClip, currentBullets, player, itemSlot);
                        cancel();
                    } else {
                        ActionBarMessage.reload(config.getReloadTime(), i, player, itemSlot);
                    }

                }
            };

            reloadTask.runTaskTimerAsynchronously(plugin, 0L, 1L);
        }
    }

    /**
     * Stops the gun from being able to fire for config.getBurstDelay time.
     */
    protected void startBurstDelay() {
        isNextBulletReady = false;
        burstDelayTask = new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                i++;
                if(i >= config.getBurstDelay()) {
                    isNextBulletReady = true;
                    this.cancel();
                }
            }
        };

        burstDelayTask.runTaskTimerAsynchronously(plugin, 0L, 1L);
    }

    /**
     * Calculates the direction of the bullet with respect to accuracy
     * @param accuracy The accuracy of this gun
     * @return A vector containing direction of the bullet
     */
    protected Vector calculateBulletDirection(double accuracy) {
        Vector velocity = player.getLocation().getDirection();

        velocity.rotateAroundX(0.5*calculateAccuracy(accuracy));
        velocity.rotateAroundY(0.5*calculateAccuracy(accuracy));
        velocity.rotateAroundZ(0.5*calculateAccuracy(accuracy));

        velocity.normalize();
        velocity.multiply(config.getBulletSpeed());
        return velocity;
    }

    private double calculateAccuracy(double accuracy) {
        if(accuracy < 100) {
            return (Math.random() - 0.5) / accuracy;
        } else {
            return 0;
        }
    }

    /**
     * Resets the gun to the equivalent of a new gun, use this when a player dies.
     */
    public void reset() {
        if(isReloading)
            reloadTask.cancel();
        if(!isNextBulletReady)
            burstDelayTask.cancel();
        isReloading = false;
        isNextBulletReady = true;
        currentBullets = config.getStartingBullets();
        currentClip = config.getClipSize();

        ActionBarMessage.ammunitionCurrentAndTotal(currentClip, currentBullets, player, itemSlot);
    }

    /**
     * Stops the gun from shooting
     *
     * Only used for guns that fires fast such as fully automatic.
     */
    public void stopShooting() {
        //??
    }

    /**
     * Fires as many bullets as should get fired in one click/shot
     * If multiple bullets are fired at once they will be fired in the same direction
     * Do not use this for shotguns and alike.
     *
     * @param bulletDirection The bullets direction
     */
    protected void fireBullet(Vector bulletDirection) {
        playShootSound();

        for(int i = 0; i < config.getBulletsPerClick(); i++) {
            new Bullet(player.getPlayer(), plugin, bulletDirection, config.getBulletSpeed(),
                    config.getRange(), config.getBodyDamage(), config.getHeadDamage());
            player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));
        }

        currentClip--;
        currentBullets--;

        statistics.addBulletsShot(config.getBulletsPerClick());

        ActionBarMessage.ammunitionCurrentAndTotal(currentClip, currentBullets, player, itemSlot);
    }

    /**
     * Fires as many bullets as should get fired in one click/shot
     *
     * Each bullet is fired in a different direction, but more accurate when scoping.
     */
    protected void fireBullet() {
        playShootSound();
        double accuracy = player.isScoping() ? config.getAccuracyScoped() : config.getAccuracyNotScoped();

        for(int i = 0; i < config.getBulletsPerClick(); i++) {
            Vector velocity = calculateBulletDirection(accuracy);
            new Bullet(player.getPlayer(), plugin, velocity, config.getBulletSpeed(),
                    config.getRange(), config.getBodyDamage(), config.getHeadDamage());
            player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));
        }

        currentClip--;
        currentBullets--;
        statistics.addBulletsShot(config.getBulletsPerClick());

        player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));

        ActionBarMessage.ammunitionCurrentAndTotal(currentClip, currentBullets, player, itemSlot);
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

    public List<String> getLore() {
        return config.getWeaponLore();
    }

    @Override
    public String toString() {
        return config.getName();
    }

    protected void playShootSound() {
        player.getPlayer().getWorld().playSound(player.getLocation(), config.getSound(), 5, 1);
    }

    public void addBullets(int amount) {
        currentBullets += amount;
    }

    public int getStartingBullets() {
        return config.getStartingBullets();
    }
}
