package me.noaz.testplugin.Inventories;

import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadoutMenu {
    private static int inventorySize = 36;
    private static Material goBackArrow = Material.LAVA_BUCKET;

    /**
     * Opens the start screen of the loadout selector
     *
     * @param player The player who opens the loadout
     */
    public static void loadoutStartScreen(PlayerExtension player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, "Loadout Selection");

        ItemStack[] items = new ItemStack[inventorySize];

        /*for(WeaponConfiguration configuration : configurations) {
            if(configuration.getUnlockLevel() > player.getLevel()) {
                items[configuration.getLoadoutSlot()] = createLockedWeaponItem(configuration);
            } else {
                items[configuration.getLoadoutSlot()] = createUnlockedWeaponItem(configuration);
            }
        }*/

        items[10] = player.getPrimaryWeapon().getMaterialAsItemStack();
        items[11] = player.getSecondaryWeapon().getMaterialAsItemStack();

        /*
        items[13] = lethal item
        items[14] = tactical item

        items[16] = perk

        items[28] = first ks
        items[29] = second ks
        items[30] = third ks (= always nuke so ignore?)
         */

        inventory.setStorageContents(items);
        player.getPlayer().openInventory(inventory);
    }

    /**
     * Opens the loadout selector for primary guns.
     *
     * @param player The players playerExtension who should get the inventory
     */
    public static void selectPrimaryScreen(PlayerExtension player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, "Select primary");
        ItemStack[] items = new ItemStack[inventorySize];

        HashMap<String, WeaponConfiguration> configurations = player.getWeaponConfigurations();

        items[0] = new ItemStack(goBackArrow);

        //Have: All unlocked guns
        //Want: All guns but unlocked in one way and other in other ways. O(n^2)?
        //Loop through unlocked once and make a list of locked ones?

        List<String> ownedPrimaryGuns = player.getOwnedPrimaryGuns();
        for(WeaponConfiguration gun : configurations.values()) {
            if(!gun.weaponType.equals("Secondary")) {
                if (!ownedPrimaryGuns.contains(gun.name)) {
                    //if(gun.getUnlockLevel() > player.getLevel()) {
                    items[gun.loadoutSlot] = createLockedWeaponItem(gun);
                    //} /*else if(gun.getCost() > player.getCredits) {
                        /*items[gun.getLoadoutSlot()] = createLockedVisibleRedItem(gun);
                    } else {
                        items[gun.getLoadoutSlot()] = createLockedVisibleGreenItem(gun);
                    }*/
                } else {
                    items[gun.loadoutSlot] = createUnlockedWeaponItem(gun);
                }
            }
        }

        inventory.setStorageContents(items);
        player.getPlayer().openInventory(inventory);
    }

    /**
     * Opens the loadout selection for secondary guns.
     *
     * @param player The players playerExtension who should get the inventory
     */
    public static void selectSecondary(PlayerExtension player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, "Select secondary");
        ItemStack[] items = new ItemStack[inventorySize];

        HashMap<String, WeaponConfiguration> configurations = player.getWeaponConfigurations();

        items[0] = new ItemStack(goBackArrow);

        List<String> ownedSecondaryGuns = player.getOwnedSecondaryGuns();
        for(WeaponConfiguration gun : configurations.values()) {
            if(gun.weaponType.equals("Secondary")) {
                if (!ownedSecondaryGuns.contains(gun.name)) {
                    //if(gun.getUnlockLevel() > player.getLevel()) {
                        items[gun.loadoutSlot] = createLockedWeaponItem(gun);
                    //} /*else if(gun.getCost() > player.getCredits) {
                        /*items[gun.getLoadoutSlot()] = createLockedVisibleRedItem(gun);
                    } else {
                        items[gun.getLoadoutSlot()] = createLockedVisibleGreenItem(gun);
                    }*/
                } else {
                    items[gun.loadoutSlot] = createUnlockedWeaponItem(gun);
                }
            }
        }

        inventory.setStorageContents(items);
        player.getPlayer().openInventory(inventory);
    }


    private static ItemStack createUnlockedWeaponItem(WeaponConfiguration configuration) {
        Material material = configuration.gunMaterial;
        String name = configuration.name;
        List<String> lore = configuration.weaponLore;

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
        Material material = Material.BARRIER;
        String name = configuration.name;
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
