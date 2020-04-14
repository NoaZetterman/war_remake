package me.noaz.testplugin.weapons.firemodes;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.Messages.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.weapons.Weapon;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

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
            isShooting = true;
            fireAsIfPlayerHoldsRightClick.runTaskTimer(plugin, 0L, 1L);
        } else {
            ChatMessage.outOfAmmo(player);
        }
    }

    @Override
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
     * Fires the automatic gun for 6 ticks (300ms)
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
                    startBurstDelay();
                }
                isShooting = false;
                this.cancel();
            } else if(isNextBulletReady && !isReloading) {
                fireBullet();
                startBurstDelay();
            }
        }
    }
}
