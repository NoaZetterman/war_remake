package me.noaz.testplugin.weapons.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.Bullet;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

/**
 * Right now the default weapon works like automatics.
 */
public class SingleFireGun extends Weapon {

    /**
     * @param plugin this plugin
     * @param player The player that should use this weapon
     * @param statistics That players statistics
     * @param config The configuration of this weapon
     */
    public SingleFireGun(TestPlugin plugin, PlayerExtension player, PlayerStatistic statistics, WeaponConfiguration config) {
        super(plugin, player, statistics, config);
    }

    public void shoot() {
        if(!isReloading && currentBullets != 0 && isNextBulletReady) {
            int totalBulletsInCurrentBurst = Math.min(currentClip, config.getBulletsPerClick());
            double accuracy = player.isScoping() ? config.getAccuracyScoped() : config.getAccuracyNotScoped();
            Vector velocity = calculateBulletDirection(accuracy);

            if(totalBulletsInCurrentBurst != 0) {
                new Bullet(player.getPlayer(), plugin, velocity, config.getBulletSpeed(), config.getRange(), config.getBodyDamage(),
                        config.getHeadDamage());
                player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));
            }

            currentClip -= totalBulletsInCurrentBurst;
            currentBullets -= totalBulletsInCurrentBurst;
            statistics.addBulletsShot(totalBulletsInCurrentBurst);

            if(currentClip <= 0) {
                reload();
            } else {
                player.setActionBar(ChatColor.DARK_RED + "" + ChatColor.BOLD + currentBullets + " / " + currentClip);
                startBurstDelay();
            }
        } else if(currentBullets == 0){
            player.getPlayer().sendMessage("Out of ammo!");
        }
    }
}
