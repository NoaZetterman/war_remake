package me.noaz.testplugin.player;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.weapons.guns.Gun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Animation {

    public static void scopeAnimation(Player player, Gun gun, int slot, TestPlugin plugin) {
        int modelDataBaseValue = DefaultCustomModelData.SCOPE_START.getValue();
        int animations = gun.getConfiguration().getScopeAnimations();

        ItemStack item = player.getInventory().getItem(slot);
        ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    i++;
                    if(i-1 > animations) {
                        this.cancel();
                    } else {
                        meta.setCustomModelData(modelDataBaseValue+i);
                        item.setItemMeta(meta);
                        player.getInventory().setItem(slot, item);
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0, 1);
        }
    }

    public static void unscopeAnimation(Player player, Gun gun, int slot, TestPlugin plugin) {
        int modelDataBaseValue = DefaultCustomModelData.SCOPE_START.getValue();

        ItemStack item = player.getInventory().getItem(slot);
        ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            new BukkitRunnable() {
                int i = gun.getConfiguration().getScopeAnimations();

                @Override
                public void run() {
                    if(i < 0) {
                        this.cancel();
                    } else {
                        meta.setCustomModelData(modelDataBaseValue+i);
                        item.setItemMeta(meta);
                        player.getInventory().setItem(slot, item);
                    }

                    i--;
                }
            }.runTaskTimerAsynchronously(plugin, 0, 1);
        }
    }
}
