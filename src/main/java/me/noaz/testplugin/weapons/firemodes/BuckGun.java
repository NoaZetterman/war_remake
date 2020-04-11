package me.noaz.testplugin.weapons.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Utils.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A gun that fires multiple bullets at once such as a shotgun, may be in bursts
 */
public class BuckGun extends Weapon {
    private BukkitRunnable fireAsIfPlayerHoldsRightClick;

    /**
     * @param plugin     This plugin
     * @param player     The player that should use this weapon
     * @param statistics The players statistics
     * @param config     The configuration of this weapon
     */
    public BuckGun(TestPlugin plugin, PlayerExtension player, PlayerStatistic statistics, WeaponConfiguration config) {
        super(plugin, player, statistics, config);
    }

    /**
     * Fires the gun
     *
     * For buck guns, a number of bullets gets fired in different directions at the same time, but only removes
     * one bullet off the clip.
     */
    public void shoot() {
        if(currentBullets != 0 && !isReloading) {
            if(isShooting) {
                fireAsIfPlayerHoldsRightClick.cancel();
            }

            fireAsIfPlayerHoldsRightClick = new FireAsIfPlayerHoldsRightClick();
            isShooting = true;
            fireAsIfPlayerHoldsRightClick.runTaskTimer(plugin, 0L, 1L);
        } else if(currentBullets <= 0) {
            ChatMessage.outOfAmmo(player);
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
        int bulletsInBurst = config.getBulletsPerBurst();

        @Override
        public void run() {
            i++;

            if(isNextBulletReady && !isReloading && bulletsInBurst > 0) {
                bulletsInBurst--;
                fireBullet();
            }

            if(bulletsInBurst <= 0 || currentClip <= 0) {
                if (currentClip <= 0) {
                    reload();
                } else {
                    startBurstDelay();
                }

                bulletsInBurst = config.getBulletsPerBurst();

                if(i >= 6) {
                    isShooting = false;
                    this.cancel();
                }
            }

            //Is this going to work? No.
            /*if((i >= 6 && bulletsInBurst <= 0) || currentClip <= 0 || bulletsInBurst <= 0) {
                if (currentClip <= 0) {
                    reload();
                } else {
                    startBurstDelay();
                }
                isShooting = false;
                this.cancel();
            }*/
        }
    }
}
