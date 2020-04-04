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
 * A gun that fires multiple bullets at once such as a shotgun.
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

            //Pull shot u to weapon? And then implement a runnable thingy?
            fireAsIfPlayerHoldsRightClick = new FireAsIfPlayerHoldsRightClick();
            fireAsIfPlayerHoldsRightClick.runTaskTimer(plugin, 0L, 1L);
            isShooting = true;
        } else {
            player.getPlayer().sendMessage("Out of ammo!");
        }
    }

    private class FireAsIfPlayerHoldsRightClick extends BukkitRunnable {
        int i = 0;

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
                int totalBulletsInCurrentBurst = Math.min(currentClip, config.getBulletsPerBurst());
                double accuracy = player.isScoping() ? config.getAccuracyScoped() : config.getAccuracyNotScoped();

                currentClip -= totalBulletsInCurrentBurst;
                currentBullets -= totalBulletsInCurrentBurst;
                statistics.addBulletsShot(totalBulletsInCurrentBurst*config.getBulletsPerClick());

                //Runnable within a runnable yikes?
                BukkitRunnable task = new BukkitRunnable() {
                    private int i = 0;

                    public void run() {
                        if (i >= totalBulletsInCurrentBurst) {
                            currentClip -= totalBulletsInCurrentBurst;
                            currentBullets -= totalBulletsInCurrentBurst;

                            statistics.addBulletsShot(totalBulletsInCurrentBurst);

                            if(currentClip <= 0) {
                                reload();
                            } else {
                                player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentBullets + " / " + currentClip);
                                startBurstDelay();
                            }
                            this.cancel();
                        } else {
                            for(int i = 0; i < config.getBulletsPerClick(); i++) {
                                Vector velocity = calculateBulletDirection(accuracy);
                                new Bullet(player.getPlayer(), plugin, velocity, config.getBulletSpeed(),
                                        config.getRange(), config.getBodyDamage(), config.getHeadDamage());
                            }
                        }
                    }
                };

                task.runTaskTimer(plugin, 0L, 1L);

                player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));

                player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentBullets + " / " + currentClip);
                startBurstDelay();
            }
        }
    }
}
