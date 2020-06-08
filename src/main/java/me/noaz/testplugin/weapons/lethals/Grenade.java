package me.noaz.testplugin.weapons.lethals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Grenade implements Weapon {
    private PlayerExtension playerExtension;
    private TestPlugin plugin;
    private GameMap map;

    private final Material material = Material.APPLE;
    private final float power = 3;
    private final float throwingSpeed = 1.3f;
    private final int itemSlot = 3;
    private final int amount = 2;
    private final int cooldownTimeInTicks = 20;

    private boolean hasCooldown = false;

    public Grenade(TestPlugin plugin, PlayerExtension playerExtension, GameMap map) {
        this.playerExtension = playerExtension;
        this.plugin = plugin;
        this.map = map;
    }

    @Override
    public synchronized void use() {
        if(!hasCooldown) {
            Vector velocity = playerExtension.getPlayer().getLocation().getDirection();

            velocity.normalize().multiply(throwingSpeed);

            ItemStack stack = playerExtension.getPlayer().getInventory().getItem(3);
            if(stack.getAmount() == 1) {
                playerExtension.getPlayer().getInventory().setItem(itemSlot, null);
            } else {
                stack.setAmount(stack.getAmount() - 1);
            }

            Item item = map.getWorld().dropItem(playerExtension.getPlayer().getEyeLocation(), stack);

            item.setPickupDelay(10000000);
            item.setVelocity(velocity);
            item.setMetadata("damage", new FixedMetadataValue(plugin, 30));

            startCooldown();

            new BukkitRunnable() {

                @Override
                public void run() {
                    Location itemLocation = item.getLocation();
                    item.remove();

                    map.getWorld().createExplosion(itemLocation, power,false,false, playerExtension.getPlayer());
                }
            }.runTaskLater(plugin, 40);
        }
    }

    @Override
    public ItemStack getMaterialAsItemStack() {
        return new ItemStack(material, amount);
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

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }
}
