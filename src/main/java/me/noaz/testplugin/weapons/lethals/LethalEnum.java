package me.noaz.testplugin.weapons.lethals;

import me.noaz.testplugin.Buyable;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Individual Lethal properties are defined within various classes. This Enum only defines which ones exists,
 * and common factors between different lethals.
 */
public enum LethalEnum {
    MOLOTOV(10, Material.APPLE, 2, 20),
    GRENADE(11, Material.APPLE, 2, 20),
    TOMAHAWK(12, Material.APPLE, 1, 20),
    NONE(-1, Material.AIR, 0, 0);

    int loadoutMenuSlot;
    Material material;
    int amount;
    int cooldownTimeInTicks;

    LethalEnum(int loadoutMenuSlot, Material material, int amount, int cooldownTimeInTicks) {
        this.loadoutMenuSlot = loadoutMenuSlot;
        this.material = material;
        this.amount = amount;
        this.cooldownTimeInTicks = cooldownTimeInTicks;

    }

    public int getLoadoutMenuSlot() {
        return loadoutMenuSlot;
    }

    /**
     * Get the object to activating this enum.
     *
     * @param playerExtension The players playerExtension
     * @param plugin This plugin
     * @return An object that can trigger an action of this enum type.
     */
    public Lethal getAsWeapon(PlayerExtension playerExtension, TestPlugin plugin) {
        switch(this) {
            case TOMAHAWK:
                return new Tomahawk(playerExtension, plugin, cooldownTimeInTicks);
            case GRENADE:
                return new Grenade(playerExtension, plugin, cooldownTimeInTicks);
            case MOLOTOV:
                return new Molotov(playerExtension, plugin, cooldownTimeInTicks);
            case NONE:
                return new NoLethal();
            default:
                return null;
        }

    }

    public Material getMaterial() {
        return material;
    }

    public int getUnlockLevel() {
        return 0;
    }

    public int getCostToBuy() {
        return 0;
    }

    public ItemStack getMaterialAsItemStack() {
        return new ItemStack(material, amount);
    }

    public Buyable getAsBuyable() {
        return new Buyable(name(), name(), getUnlockLevel(), getCostToBuy(), loadoutMenuSlot, material);
    }
}
