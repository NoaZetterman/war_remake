package me.noaz.testplugin.weapons.lethals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NoLethal implements Lethal {
    public NoLethal() {
    }

    @Override
    public void use() {
    }

    @Override
    public ItemStack getMaterialAsItemStack() {
        return null;
    }

    @Override
    public Material getMaterial() {
        return null;
    }
}
