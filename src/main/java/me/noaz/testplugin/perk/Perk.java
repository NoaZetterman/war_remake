package me.noaz.testplugin.perk;

import me.noaz.testplugin.Buyable;
import org.bukkit.Material;

public enum Perk {
    SCAVENGER("Scavenger", Material.APPLE, "Gives ammo back on kill", 0, 0, 9),
    SLEIGHT_OF_HAND("Sleight of Hand", Material.APPLE, "Decreases reload time", 0, 0, 10),
    LIGHTWEIGHT("Lightweight", Material.APPLE, "Increases Speed", 0, 0, 11),
    BANDOILER("Bandoiler", Material.APPLE, "Increases spawning ammunition", 0, 0, 12),
    HARDLINE("Hardline", Material.APPLE, "Killstreaks given one kill earlier", 0,0,13);

    String displayName;
    Material material;
    String description;
    int unlockLevel;
    int costToBuy;
    int loadoutMenuSlot;

    Perk(String displayName, Material material, String description, int unlockLevel, int costToBuy, int loadoutMenuSlot) {
        this.displayName = displayName;
        this.material = material;
        this.description = description;
        this.unlockLevel = unlockLevel;
        this.costToBuy = costToBuy;
        this.loadoutMenuSlot = loadoutMenuSlot;
    }

    public Material getMaterial() {
        return material;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }

    public int getCostToBuy() {
        return costToBuy;
    }

    public int getLoadoutMenuSlot() {
        return loadoutMenuSlot;
    }

    public Buyable getAsBuyable() {
        return new Buyable(name(), displayName, unlockLevel, costToBuy, loadoutMenuSlot, material);
    }
}