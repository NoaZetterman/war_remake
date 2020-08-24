package me.noaz.testplugin.weapons.tacticals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class TacInsert implements Tactical {
    private PlayerExtension playerExtension;
    private TestPlugin plugin;
    private int cooldownTimeInTicks;

    private Location savedLocation = null;

    public TacInsert(PlayerExtension playerExtension, TestPlugin plugin, int cooldownTimeInTicks) {
        this.playerExtension = playerExtension;
        this.plugin = plugin;
        this.cooldownTimeInTicks = cooldownTimeInTicks;
    }

    @Override
    public void use() {
        playerExtension.getPlayer().getInventory().setItem(itemSlot, null);

        savedLocation = playerExtension.getLocation();
    }

    public boolean hasSavedLocation() {
        return savedLocation != null;
    }

    public Location getSavedLocation() {
        return savedLocation;
    }
}
