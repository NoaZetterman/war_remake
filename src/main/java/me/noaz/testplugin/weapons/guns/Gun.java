package me.noaz.testplugin.weapons.guns;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.messages.ActionBarMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerInformation;
import me.noaz.testplugin.weapons.Weapon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

/**
 * Main class that weapons are built from.
 *
 * @author Noa Zetterman
 * @version 2019-12-16
 */
public abstract class Gun implements Weapon {
    protected TestPlugin plugin;
    protected PlayerExtension player;
    protected PlayerInformation statistics;
    protected GunConfiguration config;

    protected int currentClip;
    protected int currentBullets;

    protected boolean isNextBulletReady = true;
    protected boolean isReloading = false;
    protected boolean isShooting = false;
    protected boolean justStartedReloading = false;

    protected BukkitRunnable reloadTask;
    protected BukkitRunnable burstDelayTask;

    protected int inventorySlot;

    /**
     * @param plugin this plugin
     * @param player The player that should use this weapon
     * @param statistics That players statistics
     * @param config The configuration of this weapon
     */
    protected Gun(TestPlugin plugin, PlayerExtension player, PlayerInformation statistics, GunConfiguration config) {
        this.plugin = plugin;
        this.player = player;
        this.statistics = statistics;
        this.config = config;
        this.currentClip = config.clipSize;
        this.currentBullets = config.startingBullets-currentClip;

        inventorySlot = config.gunType == GunType.SECONDARY ? 2 : 1;


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

        //TODO: Add so that accuracy gets worse over time when spraying
        //Do this by having a bukkitrunnable that has a counter that goes down by one
        //each tick, and add some nr to that counter with each bullet fired

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
            if(config.gunType != GunType.SHOTGUN) {
                currentClip--;
            }
        }

        if(config.gunType == GunType.SHOTGUN) {
            currentClip--;
        }

        statistics.addBulletsShot(config.bulletsPerClick);
        player.updateGameScoreboard();

        ActionBarMessage.ammunitionCurrentAndTotal(config.name, currentClip, currentBullets, player, inventorySlot);
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
            new Bullet(player.getPlayer(), plugin, calculateBulletDirection(accuracy), config.bulletSpeed,
                    config.range, config.bodyDamage, config.headDamage);
            player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));
            if(config.gunType != GunType.SHOTGUN) {
                currentClip--;
            }
        }

        if(config.gunType == GunType.SHOTGUN) {
            currentClip--;
        }

        statistics.addBulletsShot(config.bulletsPerClick);

        player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));

        ActionBarMessage.ammunitionCurrentAndTotal(config.name, currentClip, currentBullets, player, inventorySlot);
    }

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
                        int bulletsToReload = Math.min(config.clipSize-currentClip, currentBullets);
                        currentClip += bulletsToReload;
                        currentBullets -= bulletsToReload;

                        isReloading = false;
                        ActionBarMessage.ammunitionCurrentAndTotal(config.name, currentClip, currentBullets, player, inventorySlot);
                        cancel();
                    } else {
                        ActionBarMessage.reload(config.reloadTime, i, player, inventorySlot);
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
        /*
         * Finds two perpendicular vectors to the players direction such that
         * these two vectors are also perpendicular.
         *
         * Then adds a small, but random, length to a normalized vector in the players direction
         * to create an offset in the bullet direction.
         */

        Vector velocity = player.getLocation().getDirection();

        Vector perpendicular;
        //Use other unit vector to generate perpendicular vector if
        //the velocity is too close to the unit vector
        if(velocity.getX() < 0.1 && velocity.getY() < 0.1) {
            perpendicular = new Vector(1,0,0).getCrossProduct(velocity);
        } else {
            perpendicular = new Vector(0, 0, 1).getCrossProduct(velocity);
        }

        Vector orthogonalToVelocityAndPerpendicular = perpendicular.getCrossProduct(velocity);

        perpendicular.normalize();
        orthogonalToVelocityAndPerpendicular.normalize();


        Random random = new Random();
        double lengthPerpendicular = random.nextDouble()*accuracy-accuracy/2;
        double maxLengthOrthogonal = accuracy - Math.abs(lengthPerpendicular); //Max velocity in the other direction to make a circle

        double lengthOrthogonal = random.nextDouble()*maxLengthOrthogonal-maxLengthOrthogonal/2;

        velocity.add(perpendicular.multiply(lengthPerpendicular));
        velocity.add(orthogonalToVelocityAndPerpendicular.multiply(lengthOrthogonal));

        velocity.normalize();
        velocity.multiply(config.bulletSpeed);

        return velocity.clone();
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
        currentBullets = config.startingBullets - config.clipSize;
        currentClip = config.clipSize;

        ActionBarMessage.ammunitionCurrentAndTotal(config.name, currentClip, currentBullets, player, inventorySlot);
    }

    /**
     * Stops the gun from shooting
     *
     * Only used for guns that fires fast such as fully automatic.
     */
    public void stopShooting() {
        //??
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
        ActionBarMessage.ammunitionCurrentAndTotal(config.name, currentClip, currentBullets, player, inventorySlot);
    }

    public boolean justStartedReloading() {
        return justStartedReloading;
    }

    /**
     * @return The material type of this weapon
     */
    public Material getMaterial() {
        return config.gunMaterial;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    /**
     * @return The material of this gun as an item stack
     */
    public ItemStack getMaterialAsItemStack() {
        return new ItemStack(config.gunMaterial);
    }

    public int getStartingBullets() {
        return config.startingBullets;
    }

    public List<String> getLore() {
        return config.weaponLore;
    }

    public GunConfiguration getConfiguration() {
        return config;
    }

    @Override
    public String toString() {
        return config.name;
    }
}
