package me.noaz.testplugin.weapons.guns.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.messages.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.guns.Gun;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BurstGun extends Gun {
    /**
     * @param plugin     this plugin
     * @param player     The player that should use this weapon
     * @param statistics That players statistics
     * @param config     The configuration of this weapon
     */
    public BurstGun(TestPlugin plugin, PlayerExtension player, PlayerStatistic statistics, GunConfiguration config) {
        super(plugin, player, statistics, config);
    }

    public synchronized void use() {
        if(!isReloading && currentClip != 0 && isNextBulletReady && !isShooting) {
            isShooting = true;
            int totalBulletsInCurrentBurst = Math.min(currentClip, config.bulletsPerBurst);

            double accuracy = player.isScoping() ? config.accuracyScoped : config.accuracyNotScoped;
            Vector velocity = calculateBulletDirection(accuracy);

            BukkitRunnable task = new BukkitRunnable() {
                private int i = 0;

                public void run() {
                    i++;
                    if (i <= totalBulletsInCurrentBurst) {
                        fireBullet(velocity);
                    } else {
                        if(currentClip <= 0) {
                            reload();
                        } else {
                            startBurstDelay();
                        }

                        isShooting = false;
                        this.cancel();
                    }
                }
            };

            task.runTaskTimer(plugin, 0L, 1L);

        } else if(currentBullets == 0 && currentClip == 0){
            playFireWithoutAmmoSound();
            ChatMessage.outOfAmmo(player);
        } else if(isReloading) {
            playFireWhileReloadingSound();
        }
    }
}
