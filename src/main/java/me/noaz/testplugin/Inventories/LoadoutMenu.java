package me.noaz.testplugin.Inventories;

import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.GunConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LoadoutMenu {
    private static int inventorySize = 36;
    private static Material goBackArrow = Material.LAVA_BUCKET;

    /**
     * React to a player clicking on a slot in the loadout, select a weapon if a weapon is clicked etc.
     *
     * @param inventory The inventory that should be used
     * @param slot The slot that gets clicked on
     */
    public static void onItemClick(Inventory inventory, int slot, PlayerExtension player, List<GunConfiguration> gunConfigurations) {
        if(inventory.getViewers().get(0) instanceof Player) {
            String inventoryName = player.getPlayer().getOpenInventory().getTitle();

            String clickedItemName = inventory.getItem(slot).getItemMeta().getDisplayName();


            switch (inventoryName) {
                case "Loadout Selection":
                    switch (slot) {
                        case 10:
                            selectPrimaryScreen(player, gunConfigurations);
                            break;
                        case 11:
                            selectSecondary(player, gunConfigurations);
                            break;
                        default:
                            break;
                    }
                case "Select primary":
                    if (slot == 0) {
                        loadoutStartScreen(player);
                    } else {
                        for(GunConfiguration gun: gunConfigurations) {
                            if(gun.name.equals(clickedItemName)) {
                                if(player.getOwnedPrimaryGuns().contains(clickedItemName)) {
                                    player.changePrimaryGun(gun);
                                } else if(gun.costToBuy < player.getCredits()) {
                                    buyGun(player, gun);
                                    //Take player to other screen of buying gun
                                }
                            }
                        }
                    }
                    break;
                case "Select secondary":
                    if (slot == 0) {
                        loadoutStartScreen(player);
                    } else {
                        for(GunConfiguration gun: gunConfigurations) {
                            if(gun.name.equals(clickedItemName)) {
                                if(player.getOwnedPrimaryGuns().contains(clickedItemName)) {
                                    player.changePrimaryGun(gun);
                                } else if(gun.costToBuy < player.getCredits()) {
                                    //Take player to other screen of buying gun
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

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

        items[10] = player.getPrimaryGun().getMaterialAsItemStack();
        items[11] = player.getSecondaryGun().getMaterialAsItemStack();

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

    private static void buyGun(PlayerExtension player, GunConfiguration gunToBuy) {
        int inventorySize = 45;
        Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, "Buy gun");
        ItemStack[] items = new ItemStack[inventorySize];

        //Maybe no goback arrow?
        items[0] = new ItemStack(goBackArrow);

        for(int i = 10; i < 10+3*9; i+=9) {
            for(int j = 0; j < 3; j++) {
                items[i+j] = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            }
        }

        for(int i = 14; i < 14+3*9; i+=9) {
            for(int j = 0; j < 3; j++) {
                items[i+j] = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            }
        }

        items[22] = new ItemStack(gunToBuy.gunMaterial);

        inventory.setStorageContents(items);
        player.getPlayer().openInventory(inventory);
    }

    /**
     * Opens the loadout selector for primary guns.
     *
     * @param player The players playerExtension who should get the inventory
     */
    private static void selectPrimaryScreen(PlayerExtension player, List<GunConfiguration> configurations) {
        Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, "Select primary");
        ItemStack[] items = new ItemStack[inventorySize];

        items[0] = new ItemStack(goBackArrow);

        //Have: All unlocked guns
        //Want: All guns but unlocked in one way and other in other ways. O(n^2)?
        //Loop through unlocked once and make a list of locked ones?

        List<String> ownedPrimaryGuns = player.getOwnedPrimaryGuns();
        for(GunConfiguration gun : configurations) {
            if(!gun.weaponType.equals("Secondary")) {
                if (!ownedPrimaryGuns.contains(gun.name)) {
                    if(gun.unlockLevel > player.getLevel()) {
                    items[gun.loadoutSlot] = createLockedWeaponItem(gun);
                    } else if(gun.costToBuy > player.getCredits()) {
                        items[gun.loadoutSlot] = createLockedVisibleRedItem(gun);
                    } else {
                        items[gun.loadoutSlot] = createLockedVisibleGreenItem(gun);
                    }
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
    private static void selectSecondary(PlayerExtension player, List<GunConfiguration> configurations) {
        Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, "Select secondary");
        ItemStack[] items = new ItemStack[inventorySize];


        items[0] = new ItemStack(goBackArrow);

        List<String> ownedSecondaryGuns = player.getOwnedSecondaryGuns();
        for(GunConfiguration gun : configurations) {
            if(gun.weaponType.equals("Secondary")) {
                if (!ownedSecondaryGuns.contains(gun.name)) {
                    if(gun.unlockLevel > player.getLevel()) {
                        items[gun.loadoutSlot] = createLockedWeaponItem(gun);
                    } else if(gun.costToBuy > player.getCredits()) {
                        items[gun.loadoutSlot] = createLockedVisibleRedItem(gun);
                    } else {
                        items[gun.loadoutSlot] = createLockedVisibleGreenItem(gun);
                    }
                } else {
                    items[gun.loadoutSlot] = createUnlockedWeaponItem(gun);
                }
            }
        }

        inventory.setStorageContents(items);
        player.getPlayer().openInventory(inventory);
    }


    private static ItemStack createUnlockedWeaponItem(GunConfiguration configuration) {
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

    private static ItemStack createLockedWeaponItem(GunConfiguration configuration) {
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

    private static ItemStack createLockedVisibleGreenItem(GunConfiguration configuration) {
        Material material = configuration.gunMaterial;
        String name = configuration.name;
        List<String> lore = new ArrayList<>();
        lore.add("Buy by clicking");

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        meta.setUnbreakable(true);

        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES , ItemFlag.HIDE_DESTROYS);

        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack createLockedVisibleRedItem(GunConfiguration configuration) {
        Material material = configuration.gunMaterial;
        String name = configuration.name;
        List<String> lore = new ArrayList<>();
        lore.add("Not enough credits to buy");

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
