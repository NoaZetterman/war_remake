package me.noaz.testplugin.perk;

import org.bukkit.Material;

public enum Perk {
    SCAVENGER(Material.APPLE, "Gives ammo back on kill", 0, 0, 9),
    SLEIGHT_OF_HAND(Material.APPLE, "Decreases reload time", 0, 0, 10),
    LIGHTWEIGHT(Material.APPLE, "Increases Speed", 0, 0, 11),
    BANDOILER(Material.APPLE, "Increases spawning ammunition", 0, 0, 12),
    HARDLINE(Material.APPLE, "Killstreaks given one kill earlier", 0,0,13);

    Material material;
    String description;
    int unlockLevel;
    int costToBuy;
    int loadoutMenuSlot;

    Perk(Material material, String description, int unlockLevel, int costToBuy, int loadoutMenuSlot) {
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
}