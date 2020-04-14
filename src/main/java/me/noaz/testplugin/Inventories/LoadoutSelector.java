package me.noaz.testplugin.Inventories;

import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LoadoutSelector {
    private static int inventorySize = 36;
    private static ItemStack[] items = new ItemStack[inventorySize];
    private static Material goBackArrow = Material.LAVA_BUCKET;

    /**
     * Opens the start screen of the loadout selector
     *
     * @param player The player who opens the loadout
     */
    public static void loadoutStartScreen(Player player, WeaponConfiguration[] configurations) {
        Inventory loadoutInventory = Bukkit.getServer().createInventory(null, inventorySize, "Loadout Selection");


        items[0] = new ItemStack(goBackArrow);

        for(WeaponConfiguration configuration : configurations) {
            if(configuration.getUnlockLevel() > player.getLevel()) {
                items[configuration.getLoadoutSlot()] = createLockedWeaponItem(configuration);
            } else {
                items[configuration.getLoadoutSlot()] = createUnlockedWeaponItem(configuration);
            }
        }

        loadoutInventory.setStorageContents(items);

        player.openInventory(loadoutInventory);
    }


    private static ItemStack createUnlockedWeaponItem(WeaponConfiguration configuration) {
        Material material = configuration.getGunMaterial();
        String name = configuration.getName();
        List<String> lore = configuration.getWeaponLore();

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        meta.setUnbreakable(true);

        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES , ItemFlag.HIDE_DESTROYS);

        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack createLockedWeaponItem(WeaponConfiguration configuration) {
        Material material = configuration.getGunMaterial();
        String name = configuration.getName();
        List<String> lore = new ArrayList<>();
        lore.add("Locked");

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        meta.setUnbreakable(true);

        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES , ItemFlag.HIDE_DESTROYS);

        item.setItemMeta(meta);

        return item;
    }
}
