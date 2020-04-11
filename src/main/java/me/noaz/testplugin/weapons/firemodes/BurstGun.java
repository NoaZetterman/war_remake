package me.noaz.testplugin.weapons.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Utils.ActionBarMessage;
import me.noaz.testplugin.Utils.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.Bullet;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BurstGun extends Weapon {
    /**
     * @param plugin     this plugin
     * @param player     The player that should use this weapon
     * @param statistics That players statistics
     * @param config     The configuration of this weapon
     */
    public BurstGun(TestPlugin plugin, PlayerExtension player, PlayerStatistic statistics, WeaponConfiguration config) {
        super(plugin, player, statistics, config);
    }

    public void shoot() {
        if(!isReloading && currentBullets != 0 && isNextBulletReady && !isShooting) {
            isShooting = true;
            int totalBulletsInCurrentBurst = Math.min(currentClip, config.getBulletsPerBurst());

            double accuracy = player.isScoping() ? config.getAccuracyScoped() : config.getAccuracyNotScoped();
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

        } else if(currentBullets == 0){
            ChatMessage.outOfAmmo(player);
        }
    }
}
