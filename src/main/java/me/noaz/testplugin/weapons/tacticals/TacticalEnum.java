package me.noaz.testplugin.weapons.tacticals;

import me.noaz.testplugin.Buyable;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum TacticalEnum {
    FLASHBANG(11, Material.DIAMOND_ORE, null, 10, 20),
    CONCUSSION_GRENADE(12, Material.DIAMOND_ORE, null, 10, 20),
    SENSOR_GRENADE(13, Material.DIAMOND_ORE, null, 10, 20),
    NONE(-1, Material.AIR, null,0, 0);

    int loadoutMenuSlot;
    Material material;
    Material additionalMaterial;
    int amount;
    int cooldownTimeInTicks;

    TacticalEnum(int loadoutMenuSlot, Material material, Material additionalMaterial,int amount, int cooldownTimeInTicks) {
        this.loadoutMenuSlot = loadoutMenuSlot;
        this.material = material;
        this.additionalMaterial = additionalMaterial;
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
    public Tactical getAsWeapon(PlayerExtension playerExtension, TestPlugin plugin) {
        switch(this) {
            case FLASHBANG:
                return new Flashbang(playerExtension, plugin, cooldownTimeInTicks);
            case CONCUSSION_GRENADE:
                return new ConcussionGrenade(playerExtension, plugin, cooldownTimeInTicks);
            case SENSOR_GRENADE:
                return new SensorGrenade(playerExtension, plugin, cooldownTimeInTicks);
            case NONE:
                return new NoTactical();
            default:
                return null;
        }

    }

    public Material getMaterial() {
        return material;
    }

    /**
     * May be null
     * @return Returns an additional material that belongs to a Lethal. Such as C4 Detonator.
     */
    public Material getAdditionalMaterial() {
        return additionalMaterial;
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
