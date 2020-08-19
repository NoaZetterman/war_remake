package me.noaz.testplugin.weapons.lethals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.CustomDamageType;
import me.noaz.testplugin.weapons.guns.Bullet;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Tomahawk implements Lethal {
    private PlayerExtension playerExtension;
    private TestPlugin plugin;
    private Material material = Material.APPLE;
    private boolean hasCooldown = false;
    private int itemSlot = 3;
    private int cooldownTimeInTicks = 20;
    private int amount = 10;

    public Tomahawk(PlayerExtension playerExtension, TestPlugin plugin) {
        this.playerExtension = playerExtension;
        this.plugin = plugin;
    }


    @Override
    public Material getMaterial() {
        return material;
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
                    15, 20, 0, 0, CustomDamageType.TOMAHAWK);

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


    @Override
    public ItemStack getMaterialAsItemStack() {
        return new ItemStack(material, amount);
    }
}
