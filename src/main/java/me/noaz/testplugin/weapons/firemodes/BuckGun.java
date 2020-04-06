package me.noaz.testplugin.weapons.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.Bullet;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
        if(currentBullets != 0) {
            if(isShooting) {
                fireAsIfPlayerHoldsRightClick.cancel();
            }

            fireAsIfPlayerHoldsRightClick = new FireAsIfPlayerHoldsRightClick();
            fireAsIfPlayerHoldsRightClick.runTaskTimer(plugin, 0L, 1L);
            isShooting = true;
        } else {
            player.getPlayer().sendMessage("Out of ammo!");
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
        int bulletsInBurst = Math.min(currentClip, config.getBulletsPerBurst());

        @Override
        public void run() {
            i++;
            if(i >= 6 || currentClip <= 0) {
                if(currentClip <= 0) {
                    reload();
                } else {
                    player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentBullets + " / " + currentClip);
                    startBurstDelay();
                }
                isShooting = false;
                this.cancel();
            } else if(isNextBulletReady && !isReloading) {
                double accuracy = player.isScoping() ? config.getAccuracyScoped() : config.getAccuracyNotScoped();

                currentClip--;
                currentBullets--;
                statistics.addBulletsShot(config.getBulletsPerClick());


                if (bulletsInBurst > 0) {
                    bulletsInBurst--;
                    for(int j = 0; j < config.getBulletsPerClick(); j++) {
                        Vector velocity = calculateBulletDirection(accuracy);
                        new Bullet(player.getPlayer(), plugin, velocity, config.getBulletSpeed(),
                                config.getRange(), config.getBodyDamage(), config.getHeadDamage());
                    }
                }

                if(bulletsInBurst <= 0) {
                    if(currentClip <= 0) {
                        reload();
                    } else {
                        player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentBullets + " / " + currentClip);
                        startBurstDelay();
                    }
                    bulletsInBurst = 0;
                }


                player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));

                player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentBullets + " / " + currentClip);
            }
        }
    }
}
