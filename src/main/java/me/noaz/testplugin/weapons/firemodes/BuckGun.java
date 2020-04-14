package me.noaz.testplugin.weapons.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Messages.ChatMessage;
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
        int firedBulletsInBurst = 0;

        @Override
        public void run() {
            i++;

            if(!isReloading && isNextBulletReady && firedBulletsInBurst < bulletsInBurst) {
                fireBullet();
                firedBulletsInBurst++;
            }

            if(currentClip <= 0) {
                reload();
                end();
            } else if(firedBulletsInBurst == bulletsInBurst) {
                startBurstDelay();
                firedBulletsInBurst = 0;
                if(i >= 6) {
                    end();
                }
            }


            /*if(firedBulletsInBurst < bulletsInBurst && currentClip > 0 && isNextBulletReady && !isReloading) {
                fireBullet();
                firedBulletsInBurst++;
            } else {
                if(currentClip <= 0) {
                    reload();
                } else {
                    startBurstDelay();
                }

                //Dont stop mid burst if it goes over time, but then what if .cancel()?
                //Stop if going to reload
                //Stop if time is over and not mid burst
                //Othwerwise keep shooting

                firedBulletsInBurst = 0;

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
        private void end() {
            bulletsInBurst = config.getBulletsPerBurst();
            isShooting = false;
            this.cancel();
        }
    }
}
