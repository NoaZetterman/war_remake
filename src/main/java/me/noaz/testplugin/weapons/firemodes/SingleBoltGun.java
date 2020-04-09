package me.noaz.testplugin.weapons.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Utils.ActionBarMessage;
import me.noaz.testplugin.Utils.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.Bullet;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.util.Vector;

/**
 * A gun that does not take into account holding down rightclick, but may fire faster when clicking the mouse fast
 */
public class SingleBoltGun extends Weapon {
    /**
     * @param plugin     this plugin
     * @param player     The player that should use this weapon
     * @param statistics That players statistics
     * @param config     The configuration of this weapon
     */
    public SingleBoltGun(TestPlugin plugin, PlayerExtension player, PlayerStatistic statistics, WeaponConfiguration config) {
        super(plugin, player, statistics, config);
    }

    public void shoot() {
        if(!isReloading && currentClip != 0 && isNextBulletReady) {

            double accuracy = player.isScoping() ? config.getAccuracyScoped() : config.getAccuracyNotScoped();
            Vector velocity = calculateBulletDirection(accuracy);

            playShootSound();

            new Bullet(player.getPlayer(), plugin, velocity, config.getBulletSpeed(),
                    config.getRange(), config.getBodyDamage(), config.getHeadDamage());
            player.getPlayer().setVelocity(player.getLocation().getDirection().multiply(-0.08).setY(-0.1));

            currentClip--;
            currentBullets--;

            statistics.addBulletsShot(1);

            ActionBarMessage.ammunitionCurrentAndTotal(currentClip, currentBullets, player);

            if(currentClip <= 0) {
                reload();
            } else {
                startBurstDelay();
            }

        } else if(currentBullets == 0){
            ChatMessage.outOfAmmo(player);
        }
    }
}
