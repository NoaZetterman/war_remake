package me.noaz.testplugin.weapons;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class ThrowableItem implements Weapon {
    protected PlayerExtension playerExtension;
    protected GameMap map;
    protected TestPlugin plugin;

    protected final Material material;
    protected final float range;
    protected final float throwingSpeed;
    protected final int itemSlot;
    protected final int amount;
    protected final int cooldownTimeInTicks;

    protected boolean hasCooldown = false;

    public ThrowableItem(PlayerExtension playerExtension, GameMap map, TestPlugin plugin, Material material,
                         float range, float throwingSpeed, int itemSlot, int amount, int cooldownTimeInTicks) {
        this.playerExtension = playerExtension;
        this.map = map;
        this.plugin = plugin;
        this.material = material;
        this.range = range;
        this.throwingSpeed = throwingSpeed;
        this.itemSlot = itemSlot;
        this.amount = amount;
        this.cooldownTimeInTicks = cooldownTimeInTicks;
    }

    public void use() {
        if(!hasCooldown) {
            Vector velocity = playerExtension.getPlayer().getLocation().getDirection();

            velocity.normalize().multiply(throwingSpeed);
            ItemStack stack = playerExtension.getPlayer().getInventory().getItem(3);
            if (stack.getAmount() == 1) {
                playerExtension.getPlayer().getInventory().setItem(itemSlot, null);
            } else {
                stack.setAmount(stack.getAmount() - 1);
            }
            Item item = map.getWorld().dropItem(playerExtension.getPlayer().getEyeLocation(), stack);

            item.setPickupDelay(10000000);
            item.setVelocity(velocity);
            item.setMetadata("damage", new FixedMetadataValue(plugin, 30));

            startCooldown();

            flyUntilRemove(item);
        }
    }

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

    protected void flyUntilRemove(Item item) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Location itemLocation = item.getLocation();
                item.remove();

                activateItem(itemLocation);
            }
        }.runTaskLater(plugin, 40);
    }

    protected abstract void activateItem(Location itemLocation);
}
