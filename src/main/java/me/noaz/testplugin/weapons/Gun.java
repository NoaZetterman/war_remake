package me.noaz.testplugin.weapons;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Messages.ActionBarMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import org.bukkit.Material;
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
public abstract class Gun {
    protected TestPlugin plugin;
    protected PlayerExtension player;
    protected PlayerStatistic statistics;
    protected GunConfiguration config;

    protected int currentClip;
    protected int currentBullets;

    protected boolean isNextBulletReady = true;
    protected boolean isReloading = false;
    protected boolean isShooting = false;
    protected boolean justStartedReloading = false;

    protected BukkitRunnable reloadTask;
    protected BukkitRunnable burstDelayTask;

    protected int itemSlot;

    /**
     * @param plugin this plugin
     * @param player The player that should use this weapon
     * @param statistics That players statistics
     * @param config The configuration of this weapon
     */
    protected Gun(TestPlugin plugin, PlayerExtension player, PlayerStatistic statistics, GunConfiguration config) {
        this.plugin = plugin;
        this.player = player;
        this.statistics = statistics;
        this.config = config;
        this.currentClip = config.clipSize;
        this.currentBullets = config.startingBullets-currentClip;

        itemSlot = config.weaponType.equals("Secondary") ? 2 : 1;


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

    /**
     * Reloads the gun
     */
    public void reload() {
        justStartedReloading = true;
        if(!isReloading && currentClip != config.clipSize && currentBullets != 0) {
            isReloading = true;

            reloadTask = new BukkitRunnable() {
                int i = 0;

                @Override
                public void run() {
                    i++;
                    if (i >= config.reloadTime) {
                        currentClip = Math.min(config.clipSize, currentBullets);
                        currentBullets -= currentClip;

                        isReloading = false;
                        ActionBarMessage.ammunitionCurrentAndTotal(currentClip, currentBullets, player, itemSlot);
                        cancel();
                    } else {
                        ActionBarMessage.reload(config.reloadTime, i, player, itemSlot);
                    }

                }
            };

            reloadTask.runTaskTimerAsynchronously(plugin, 0L, 1L);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                justStartedReloading = false;
            }
        }.runTaskLater(plugin, 1L);
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
                if(i >= config.burstDelay) {
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
        velocity.multiply(config.bulletSpeed);
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
        currentBullets = config.startingBullets;
        currentClip = config.clipSize;

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
        playFireBulletSound();

        for(int i = 0; i < config.bulletsPerClick; i++) {
            new Bullet(player.getPlayer(), plugin, bulletDirection, config.bulletSpeed,
                    config.range, config.bodyDamage, config.headDamage);
            player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));
        }

        currentClip--;

        statistics.addBulletsShot(config.bulletsPerClick);

        ActionBarMessage.ammunitionCurrentAndTotal(currentClip, currentBullets, player, itemSlot);
    }

    /**
     * Fires as many bullets as should get fired in one click/shot
     *
     * Each bullet is fired in a different direction, but more accurate when scoping.
     */
    protected void fireBullet() {
        playFireBulletSound();
        double accuracy = player.isScoping() ? config.accuracyScoped : config.accuracyNotScoped;

        for(int i = 0; i < config.bulletsPerClick; i++) {
            Vector velocity = calculateBulletDirection(accuracy);
            new Bullet(player.getPlayer(), plugin, velocity, config.bulletSpeed,
                    config.range, config.bodyDamage, config.headDamage);
            player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));
        }

        currentClip--;
        statistics.addBulletsShot(config.bulletsPerClick);

        player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));

        ActionBarMessage.ammunitionCurrentAndTotal(currentClip, currentBullets, player, itemSlot);
    }

    /**
     * @return The material type of this weapon
     */
    public Material getMaterialType() {
        return config.gunMaterial;
    }

    /**
     * @return The material of this gun as an item stack
     */
    public ItemStack getMaterialAsItemStack() {
        return new ItemStack(config.gunMaterial);
    }

    public List<String> getLore() {
        return config.weaponLore;
    }

    @Override
    public String toString() {
        return config.name;
    }

    protected void playFireBulletSound() {
        player.getPlayer().getWorld().playSound(player.getLocation(), config.fireBulletSound, 1, 1);
    }

    protected void playFireWhileReloadingSound() {
        player.getPlayer().getWorld().playSound(player.getLocation(), config.fireWhileReloadingSound, 1, 1);
    }

    protected void playFireWithoutAmmoSound() {
        player.getPlayer().getWorld().playSound(player.getLocation(), config.fireWithoutAmmoSound, 1, 1);
    }

    public void addBullets(int amount) {
        currentBullets += amount;
    }

    public int getStartingBullets() {
        return config.startingBullets;
    }

    public boolean justStartedReloading() {
        return justStartedReloading;
    }

    public GunConfiguration getConfiguration() {
        return config;
    }
}
