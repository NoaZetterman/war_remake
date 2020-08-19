package me.noaz.testplugin.weapons.lethals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.CustomDamageType;
import me.noaz.testplugin.weapons.guns.Bullet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Tomahawk implements Lethal {
    private PlayerExtension playerExtension;
    private TestPlugin plugin;
    private boolean hasCooldown = false;
    private final int cooldownTimeInTicks;

    public Tomahawk(PlayerExtension playerExtension, TestPlugin plugin, int cooldownTimeInTicks) {
        this.playerExtension = playerExtension;
        this.plugin = plugin;
        this.cooldownTimeInTicks = cooldownTimeInTicks;
    }

    @Override
    public void use() {
        if(!hasCooldown) {
            ItemStack stack = playerExtension.getPlayer().getInventory().getItem(itemSlot);
            if (stack.getAmount() == 1) {
                playerExtension.getPlayer().getInventory().setItem(itemSlot, null);
            } else {
                stack.setAmount(stack.getAmount() - 1);
            }

            new Bullet(playerExtension.getPlayer(), plugin, playerExtension.getPlayer().getLocation().getDirection().multiply(0.9),
                    20, 20, 0, 0, CustomDamageType.TOMAHAWK);

            startCooldown();
        }
    }

    private void startCooldown() {
        hasCooldown = true;
        new BukkitRunnable() {

            @Override
            public void run() {
                hasCooldown = false;
            }
        }.runTaskLater(plugin, cooldownTimeInTicks);
    }
}
