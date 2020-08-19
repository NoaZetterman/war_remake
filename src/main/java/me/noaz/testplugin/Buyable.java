package me.noaz.testplugin;

import org.bukkit.Material;

public class Buyable {
    String name;
    String displayName;
    int unlockLevel;
    int costToBuy;
    int loadoutMenuSlot;
    Material material;

    public Buyable(String name, String displayName, int unlockLevel, int costToBuy, int loadoutMenuSlot, Material material) {
        this.name = name;
        this.displayName = displayName;
        this.unlockLevel = unlockLevel;
        this.costToBuy = costToBuy;
        this.loadoutMenuSlot = loadoutMenuSlot;
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
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

    public Material getMaterial() {
        if(material == Material.AIR) {
            return Material.BARRIER;
        } else {
            return material;
        }
    }
}
