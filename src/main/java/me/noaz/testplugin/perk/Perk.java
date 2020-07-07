package me.noaz.testplugin.perk;

import org.bukkit.Material;

public enum Perk {
    SCAVENGER(Material.APPLE);

    Material material;

    Perk(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void use() {
        //Create a perk class of some type depending on enum and use it for perk activation(maybe)
    }
}
