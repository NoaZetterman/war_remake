package me.noaz.testplugin.weapons.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Messages.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;

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
            fireBullet();

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
