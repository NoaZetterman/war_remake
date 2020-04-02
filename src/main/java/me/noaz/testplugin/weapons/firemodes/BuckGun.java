package me.noaz.testplugin.weapons.firemodes;

import de.Herbystar.TTA.TTA_Methods;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.Bullet;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class BuckGun extends Weapon {

    /**
     * @param plugin     This plugin
     * @param player     The player that should use this weapon
     * @param statistics The players statistics
     * @param config     The configuration of this weapon
     */
    public BuckGun(TestPlugin plugin, Player player, PlayerStatistic statistics, WeaponConfiguration config) {
        super(plugin, player, statistics, config);
    }

    /**
     * Fires the gun
     *
     * For buck guns, a number of bullets gets fired in different directions at the same time, but only removes
     * one bullet off the clip.
     */
    public void shoot() {
        if(!isReloading && currentBullets != 0 && isNextBulletReady) {
            int totalBulletsInCurrentBurst = config.getBulletsPerClick();
            double accuracy = player.hasPotionEffect(PotionEffectType.SLOW) ? config.getAccuracyScoped() : config.getAccuracyNotScoped();

            for(int i = 0; i < totalBulletsInCurrentBurst; i++) {
                Vector velocity = calculateBulletDirection(accuracy);
                new Bullet(player, plugin, velocity, config.getBulletSpeed(),
                        config.getRange(), config.getBodyDamage(), config.getHeadDamage());
            }
            player.setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));

            currentClip--;
            currentBullets--;

            statistics.addBulletsShot(totalBulletsInCurrentBurst);

            if(currentClip <= 0) {
                reload();
            } else {
                TTA_Methods.sendActionBar(player, currentBullets + " / " + currentClip);
                startBurstDelay();
            }
        } else if(currentBullets == 0){
            player.sendMessage("Out of ammo!");
        }
    }
}
