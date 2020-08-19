package me.noaz.testplugin.weapons;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class ThrowableItem implements Weapon {
    protected PlayerExtension playerExtension;
    protected World world;
    protected TestPlugin plugin;

    protected final float range;
    protected final float throwingSpeed;
    protected final int itemSlotInHotbar;
    protected final int cooldownTimeInTicks;
    protected final int damageOnImpact;

    protected boolean hasCooldown = false;

    public ThrowableItem(PlayerExtension playerExtension, World world, TestPlugin plugin,
                         float range, float throwingSpeed, int itemSlotInHotbar, int cooldownTimeInTicks, int damageOnImpact) {
        this.playerExtension = playerExtension;
        this.world = world;
        this.plugin = plugin;
        this.range = range;
        this.throwingSpeed = throwingSpeed;
        this.itemSlotInHotbar = itemSlotInHotbar;
        this.cooldownTimeInTicks = cooldownTimeInTicks;
        this.damageOnImpact = damageOnImpact;
    }

    public void use() {
        if(!hasCooldown) {
            Vector velocity = playerExtension.getPlayer().getLocation().getDirection();

            velocity.normalize().multiply(throwingSpeed);
            ItemStack stack = playerExtension.getPlayer().getInventory().getItem(itemSlotInHotbar);
            if (stack.getAmount() == 1) {
                playerExtension.getPlayer().getInventory().setItem(itemSlotInHotbar, null);
            } else {
                stack.setAmount(stack.getAmount() - 1);
            }
            Item item = world.dropItem(playerExtension.getPlayer().getEyeLocation(), stack);

            item.setPickupDelay(10000000);
            item.setVelocity(velocity);
            item.setMetadata("damage", new FixedMetadataValue(plugin, 0));

            startCooldown();

            flyUntilRemove(item);
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
