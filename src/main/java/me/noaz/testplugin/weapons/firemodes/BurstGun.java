package me.noaz.testplugin.weapons.firemodes;

import de.Herbystar.TTA.TTA_Methods;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.Bullet;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BurstGun extends Weapon {
    /**
     * @param plugin     this plugin
     * @param player     The player that should use this weapon
     * @param statistics That players statistics
     * @param config     The configuration of this weapon
     */
    public BurstGun(TestPlugin plugin, Player player, PlayerStatistic statistics, WeaponConfiguration config) {
        super(plugin, player, statistics, config);
    }

    public void shoot() {
        if(!isReloading() && currentBullets != 0 && isNextBulletReady) {
            int totalBulletsInCurrentBurst = Math.min(currentClip, config.getBulletsPerClick());

            double accuracy = player.hasPotionEffect(PotionEffectType.SLOW) ? config.getAccuracyScoped() : config.getAccuracyNotScoped();
            Vector velocity = calculateBulletDirection(accuracy);

            BukkitRunnable task = new BukkitRunnable() {
                private int i = 0;

                public void run() {
                    if (i >= totalBulletsInCurrentBurst) {
                        this.cancel();
                    } else {
                        new Bullet(player, plugin, velocity, config.getBulletSpeed(),
                                config.getRange(), config.getBodyDamage(), config.getHeadDamage());
                        player.setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));
                        i++;
                    }

                    //Make another bullet constructor so that all bullets fires in same direction in same burst

                }
            };

            task.runTaskTimer(plugin, 0L, 1L);
            currentClip -= totalBulletsInCurrentBurst;
            currentBullets -= totalBulletsInCurrentBurst;

            statistics.addBulletsShot(totalBulletsInCurrentBurst);

            startBurstDelay();
            TTA_Methods.sendActionBar(player, ChatColor.DARK_RED + "" + ChatColor.BOLD + config.getClipSize() + " / " + currentClip);

            if(currentClip <= 0) {
                reload();
            }
        } else if(currentBullets == 0){
            player.sendMessage("Out of ammo!");
        }
    }
}
