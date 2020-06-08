package me.noaz.testplugin.weapons;

import org.bukkit.inventory.ItemStack;

public interface Weapon {

    void use();

    ItemStack getMaterialAsItemStack();
}
