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
    MOLOTOV(10, Material.APPLE),
    GRENADE(11, Material.APPLE),
    TOMAHAWK(12, Material.APPLE),
    NONE(-1, Material.AIR);

    int loadoutMenuSlot;
    Material material;

    LethalEnum(int loadoutMenuSlot, Material material) {
        this.loadoutMenuSlot = loadoutMenuSlot;
        this.material = material;

    }

    public int getLoadoutMenuSlot() {
        return loadoutMenuSlot;
    }

    public Lethal getAsWeapon(PlayerExtension playerExtension, TestPlugin plugin) {
        switch(this) {
            case TOMAHAWK:
                return new Tomahawk(playerExtension, plugin);
            case GRENADE:
                return new Grenade(playerExtension, plugin);
            case MOLOTOV:
                return new Molotov(playerExtension, plugin);
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
        return new ItemStack(material);
    }

    public Buyable getAsBuyable() {
        return new Buyable(name(), name(), getUnlockLevel(), getCostToBuy(), loadoutMenuSlot, material);
    }
}
