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
 * Used for guns that fires one bullet at a time at any speed.
 */
public class FullyAutomaticGun extends Weapon {
    private BukkitRunnable fireAsIfPlayerHoldsRightClick;

    /**
     * @param plugin this plugin
     * @param player The player that should use this weapon
     * @param statistics That players statistics
     * @param config The configuration of this weapon
     */
    public FullyAutomaticGun(TestPlugin plugin, PlayerExtension player, PlayerStatistic statistics, WeaponConfiguration config) {
        super(plugin, player, statistics, config);
    }

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
     * Fires the automatic gun for 6 ticks
     */
    private class FireAsIfPlayerHoldsRightClick extends BukkitRunnable {
        int i = 0;

        @Override
        public void run() {
            i++;
            if(i >= 6 || currentClip <= 0) {
                if(currentClip <= 0) {
                    reload();
                } else {
                    player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentClip + " / " + currentBullets);
                    startBurstDelay();
                }
                isShooting = false;
                this.cancel();
            } else if(isNextBulletReady && !isReloading) {
                double accuracy = player.isScoping() ? config.getAccuracyScoped() : config.getAccuracyNotScoped();
                Vector velocity = calculateBulletDirection(accuracy);

                currentClip--;
                currentBullets--;
                statistics.addBulletsShot(1);

                new Bullet(player.getPlayer(), plugin, velocity, config.getBulletSpeed(), config.getRange(), config.getBodyDamage(),
                        config.getHeadDamage());
                player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));

                player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentBullets + " / " + currentClip);
                startBurstDelay();
            }
        }
    }
}
