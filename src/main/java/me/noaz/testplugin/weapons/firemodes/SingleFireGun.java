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
    public SingleFireGun(TestPlugin plugin, Player player, PlayerStatistic statistics, WeaponConfiguration config) {
        super(plugin, player, statistics, config);
    }

    public void shoot() {
        if(!isReloading && currentBullets != 0 && isNextBulletReady) {
            int totalBulletsInCurrentBurst = Math.min(currentClip, config.getBulletsPerClick());
            double accuracy = player.hasPotionEffect(PotionEffectType.SLOW) ? config.getAccuracyScoped() : config.getAccuracyNotScoped();
            Vector velocity = calculateBulletDirection(accuracy);

            if(totalBulletsInCurrentBurst != 0) {
                new Bullet(player, plugin, velocity, config.getBulletSpeed(), config.getRange(), config.getBodyDamage(),
                        config.getHeadDamage());
                player.setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));
            }

            currentClip -= totalBulletsInCurrentBurst;
            currentBullets -= totalBulletsInCurrentBurst;
            statistics.addBulletsShot(totalBulletsInCurrentBurst);

            if(currentClip <= 0) {
                reload();
            } else {
                TTA_Methods.sendActionBar(player, ChatColor.DARK_RED + "" + ChatColor.BOLD + currentBullets + " / " + currentClip);
                startBurstDelay();
            }
        } else if(currentBullets == 0){
            player.sendMessage("Out of ammo!");
        }
    }
}
