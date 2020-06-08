package me.noaz.testplugin.weapons.guns.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.messages.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.guns.Gun;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A gun that fires multiple bullets at once such as a shotgun, may be in bursts
 */
public class BuckGun extends Gun {
    private BukkitRunnable fireAsIfPlayerHoldsRightClick;

    /**
     * @param plugin     This plugin
     * @param player     The player that should use this weapon
     * @param statistics The players statistics
     * @param config     The configuration of this weapon
     */
    public BuckGun(TestPlugin plugin, PlayerExtension player, PlayerStatistic statistics, GunConfiguration config) {
        super(plugin, player, statistics, config);
    }

    /**
     * Fires the gun
     *
     * For buck guns, a number of bullets gets fired in different directions at the same time, but only removes
     * one bullet off the clip.
     */
    public synchronized void use() {
        if(currentClip != 0 && !isReloading) {
            if(isShooting) {
                fireAsIfPlayerHoldsRightClick.cancel();
            }

            fireAsIfPlayerHoldsRightClick = new FireAsIfPlayerHoldsRightClick();
            isShooting = true;
            fireAsIfPlayerHoldsRightClick.runTaskTimer(plugin, 0L, 1L);
        } else if(currentBullets == 0 && currentClip == 0) {
            playFireWithoutAmmoSound();
            ChatMessage.outOfAmmo(player);
        } else {
            playFireWhileReloadingSound();
        }
    }

    public void reset() {
        if(isShooting) {
            fireAsIfPlayerHoldsRightClick.cancel();
        }
        super.reset();
    }

    @Override
    public void stopShooting() {
        if(isShooting) {
            fireAsIfPlayerHoldsRightClick.cancel();
        }
    }

    /**
     * BukkitRunnable that fires the buck gun in the correct way.
     */
    private class FireAsIfPlayerHoldsRightClick extends BukkitRunnable {
        int i = 0;
        int bulletsInBurst = config.bulletsPerBurst;
        int firedBulletsInBurst = 0;

        @Override
        public void run() {
            i++;

            if(isNextBulletReady && firedBulletsInBurst >= bulletsInBurst) {
                firedBulletsInBurst = 0;
            }
            //FIX

            if(!isReloading && isNextBulletReady && firedBulletsInBurst < bulletsInBurst) {
                System.out.println(i);
                fireBullet();
                firedBulletsInBurst++;
            }

            if(currentClip <= 0) {
                reload();
                end();
            } else if(firedBulletsInBurst >= bulletsInBurst) {
                startBurstDelay();
                if(i >= 6) {
                    end();
                }
            }
        }

        private void end() {
            bulletsInBurst = config.bulletsPerBurst;
            isShooting = false;
            this.cancel();
        }
    }
}
