package me.noaz.testplugin.inventories;

import me.noaz.testplugin.killstreaks.Killstreak;
import me.noaz.testplugin.perk.Perk;
import me.noaz.testplugin.player.DefaultCustomModelData;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import me.noaz.testplugin.weapons.guns.GunType;
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
    private static int buyScreenInventorySize = 45;

    //TODO: Rename goBackArrow so it says GoBack
    private static Material goBackArrow = Material.LAVA_BUCKET;
    private final static String loadoutStartScreenTitle = "Loadout Selection";
    private final static String buyGunScreenTitle = "Buy gun";
    private final static String buyKillstreakScreenTitle = "Buy Killstreak";
    private final static String primaryGunScreenTitle = "Select primary";
    private final static String secondaryGunScreenTitle = "Select secondary";
    private final static String selectKillstreakScreen = "Select Killstreak";

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


            switch(inventoryName) {
                case loadoutStartScreenTitle:
                    switch (slot) {
                        case 10:
                            GunSelection.displayPrimaryScreen(player, gunConfigurations);
                            break;
                        case 11:
                            GunSelection.displaySecondaryScreen(player, gunConfigurations);
                            break;
                        case 29:
                            KillstreakSelection.selectKillstreakScreen(player);
                        default:
                            break;
                    }
                    break;
                case primaryGunScreenTitle:
                    if (slot == 0) {
                        loadoutStartScreen(player);
                    } else {
                        for(GunConfiguration gun: gunConfigurations) {
                            if(gun.name.equals(clickedItemName)) {
                                if(player.ownsPrimaryGun(clickedItemName)) {
                                    player.setSelectedPrimaryGun(clickedItemName);
                                } else if(gun.costToBuy <= player.getCredits()) {
                                    GunSelection.createBuyScreen(player, gun);
                                    //Take player to other screen of buying gun
                                }
                            }
                        }
                    }
                    break;
                case secondaryGunScreenTitle:
                    if (slot == 0) {
                        loadoutStartScreen(player);
                    } else {
                        for(GunConfiguration gun: gunConfigurations) {
                            if(gun.name.equals(clickedItemName)) {
                                if(player.ownsSecondaryGun(clickedItemName)) {
                                    player.setSelectedSecondaryGun(clickedItemName);
                                } else if(gun.costToBuy <= player.getCredits()) {
                                    GunSelection.createBuyScreen(player, gun);
                                }
                            }
                        }
                    }
                    break;
                case selectKillstreakScreen:
                    if(slot == 0) {
                        loadoutStartScreen(player);
                    } else {
                        Killstreak killstreak = Killstreak.valueOf(clickedItemName);
                        if(player.ownsKillstreak(killstreak)) {
                            player.setSelectedKillstreak(killstreak);
                        } else if(killstreak.getCostToBuy() <= player.getCredits()) {
                            KillstreakSelection.createBuyScreen(player, killstreak);
                        }
                    }
                    break;
                case buyGunScreenTitle:
                    if(clickedItemName.equals("Cancel")) {
                        loadoutStartScreen(player);
                    } else if(clickedItemName.equals("Buy")) {
                        String gunName = inventory.getItem(22).getItemMeta().getDisplayName();

                        player.buyGun(gunName, gunConfigurations);
                        loadoutStartScreen(player);
                    }
                    break;
                case buyKillstreakScreenTitle:
                    if(clickedItemName.equals("Cancel")) {
                        loadoutStartScreen(player);
                    } else if(clickedItemName.equals("Buy")) {
                        String killstreak = inventory.getItem(22).getItemMeta().getDisplayName();

                        player.buyKillstreak(Killstreak.valueOf(killstreak));
                        loadoutStartScreen(player);
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
        Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, loadoutStartScreenTitle);

        ItemStack[] items = new ItemStack[inventorySize];

        items[10] = GunSelection.createUnlockedItem(player.getPrimaryGunConfiguration());
        items[11] = GunSelection.createUnlockedItem(player.getSecondaryGunConfiguration());

        /*
        items[13] = lethal item
        items[14] = tactical item
        */

        items[16] = PerkSelection.createUnlockedPerk(player.getSelectedPerk());


        //items[28] = first ks
        items[29] = KillstreakSelection.createUnlockedItem(player.getSelectedKillstreak());
        //items[30] = third ks (= always nuke so ignore?)

        //Some knife stuff?


        inventory.setStorageContents(items);
        player.getPlayer().openInventory(inventory);
    }

    private static ItemStack[] createBuyScreenYesNoOptions() {
        ItemStack[] items = new ItemStack[buyScreenInventorySize];

        //Maybe hve a go back arrow?
        //items[0] = new ItemStack(goBackArrow);

        for(int i = 10; i < 10+3*9; i+=9) {
            for(int j = 0; j < 3; j++) {
                items[i+j] = createBuyNoOption();
            }
        }

        for(int i = 14; i < 14+3*9; i+=9) {
            for(int j = 0; j < 3; j++) {
                items[i+j] = createBuyYesOption();
            }
        }

        return items;
    }

    private static ItemStack createBuyYesOption() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("Buy");
        //TODO: Add some explanation as to what/how much it costs to buy this

        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES , ItemFlag.HIDE_DESTROYS);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createBuyNoOption() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("Cancel");
        //TODO: Add some extra explanation

        meta = hideAttributes(meta);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemMeta hideAttributes(ItemMeta meta) {
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES , ItemFlag.HIDE_DESTROYS);

        return meta;
    }


    private static class GunSelection {
        /**
         * Opens the loadout selector for primary guns.
         *
         * @param player The players playerExtension who should get the inventory
         */
        public static void displayPrimaryScreen(PlayerExtension player, List<GunConfiguration> configurations) {
            Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, primaryGunScreenTitle);
            ItemStack[] items = new ItemStack[inventorySize];

            items[0] = new ItemStack(goBackArrow);

            //Have: All unlocked guns
            //Want: All guns but unlocked in one way and other in other ways. O(n^2)?
            //Loop through unlocked once and make a list of locked ones?

            for(GunConfiguration gun : configurations) {
                if(gun.gunType != GunType.SECONDARY) {
                    if (!player.ownsPrimaryGun(gun.name)) {
                        if(gun.unlockLevel > player.getLevel()) {
                            items[gun.loadoutSlot] = createLockedItem(gun);
                        } else if(gun.costToBuy > player.getCredits()) {
                            items[gun.loadoutSlot] = createLockedVisibleRedItem(gun);
                        } else {
                            items[gun.loadoutSlot] = createLockedVisibleGreenItem(gun);
                        }
                    } else {
                        items[gun.loadoutSlot] = createUnlockedItem(gun);
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
        public static void displaySecondaryScreen(PlayerExtension player, List<GunConfiguration> configurations) {
            Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, secondaryGunScreenTitle);
            ItemStack[] items = new ItemStack[inventorySize];


            items[0] = new ItemStack(goBackArrow);

            for(GunConfiguration gun : configurations) {
                if(gun.gunType == GunType.SECONDARY) {
                    if (!player.ownsSecondaryGun(gun.name)) {
                        if(gun.unlockLevel > player.getLevel()) {
                            items[gun.loadoutSlot] = createLockedItem(gun);
                        } else if(gun.costToBuy > player.getCredits()) {
                            items[gun.loadoutSlot] = createLockedVisibleRedItem(gun);
                        } else {
                            items[gun.loadoutSlot] = createLockedVisibleGreenItem(gun);
                        }
                    } else {
                        items[gun.loadoutSlot] = createLockedItem(gun);
                    }
                }
            }

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }

        private static ItemStack createUnlockedItem(GunConfiguration configuration) {
            Material material = configuration.gunMaterial;
            String name = configuration.name;
            List<String> lore = configuration.weaponLore;

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(name);
            meta.setLore(lore);

            meta = hideAttributes(meta);

            meta.setCustomModelData(DefaultCustomModelData.DEFAULT_VALUE.getValue());

            item.setItemMeta(meta);

            return item;
        }

        public static ItemStack createLockedItem(GunConfiguration configuration) {
            Material material = Material.BARRIER;
            String name = configuration.name;
            List<String> lore = new ArrayList<>();
            lore.add("Locked");
            lore.add("Unlock level: " + configuration.unlockLevel);

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(name);
            meta.setLore(lore);

            meta = hideAttributes(meta);

            item.setItemMeta(meta);

            return item;
        }

        public static ItemStack createLockedVisibleGreenItem(GunConfiguration configuration) {
            Material material = Material.GREEN_STAINED_GLASS_PANE;
            String name = configuration.name;
            List<String> lore = new ArrayList<>();
            lore.add("Buy by clicking");
            lore.add("Cost: " + configuration.costToBuy);

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(name);
            meta.setLore(lore);

            meta = hideAttributes(meta);

            item.setItemMeta(meta);

            return item;
        }

        public static ItemStack createLockedVisibleRedItem(GunConfiguration configuration) {
            Material material = Material.RED_STAINED_GLASS_PANE;
            String name = configuration.name;
            List<String> lore = new ArrayList<>();
            lore.add("Not enough credits to buy");
            lore.add("Cost: " + configuration.costToBuy);

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(name);
            meta.setLore(lore);

            meta = hideAttributes(meta);

            item.setItemMeta(meta);

            return item;
        }

        private static void createBuyScreen(PlayerExtension player, GunConfiguration gunToBuy) {
            Inventory inventory = Bukkit.getServer().createInventory(null, buyScreenInventorySize, buyGunScreenTitle);

            ItemStack[] items = LoadoutMenu.createBuyScreenYesNoOptions();

            items[22] = createUnlockedItem(gunToBuy);

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }
    }

    private static class KillstreakSelection {
        public static void selectKillstreakScreen(PlayerExtension player) {
            Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, selectKillstreakScreen);
            ItemStack[] items = new ItemStack[inventorySize];


            items[0] = new ItemStack(goBackArrow);

            for(Killstreak killstreak : Killstreak.values()) {
                if(killstreak.getLoadoutMenuSlot() > 0) {
                    if (!player.ownsKillstreak(killstreak)) {
                        if (killstreak.getUnlockLevel() > player.getLevel()) {
                            items[killstreak.getLoadoutMenuSlot()] = createLockedItem(killstreak);
                        } else if (killstreak.getCostToBuy() > player.getCredits()) {
                            items[killstreak.getLoadoutMenuSlot()] = createLockedVisibleRedItem(killstreak);
                        } else {
                            items[killstreak.getLoadoutMenuSlot()] = createLockedVisibleGreenItem(killstreak);
                        }
                    } else {
                        items[killstreak.getLoadoutMenuSlot()] = createUnlockedItem(killstreak);
                    }
                }

            }

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }

        public static ItemStack createUnlockedItem(Killstreak killstreak) {
            ItemStack item = new ItemStack(killstreak.getMaterial());
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(killstreak.toString());
            //TODO: Add some extra explanation

            meta = hideAttributes(meta);

            item.setItemMeta(meta);
            return item;
        }

        public static ItemStack createLockedItem(Killstreak killstreak) {
            Material material = Material.BARRIER;
            String name = killstreak.toString();
            List<String> lore = new ArrayList<>();
            lore.add("Locked");
            lore.add("Unlock level: " + killstreak.getUnlockLevel());

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(name);
            meta.setLore(lore);

            meta = hideAttributes(meta);

            item.setItemMeta(meta);

            return item;
        }

        public static ItemStack createLockedVisibleGreenItem(Killstreak killstreak) {
            Material material = Material.GREEN_STAINED_GLASS_PANE;
            String name = killstreak.toString();
            List<String> lore = new ArrayList<>();
            lore.add("Buy by clicking");
            lore.add("Cost: " + killstreak.getCostToBuy());

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(name);
            meta.setLore(lore);

            meta = hideAttributes(meta);

            item.setItemMeta(meta);

            return item;
        }

        public static ItemStack createLockedVisibleRedItem(Killstreak killstreak) {
            Material material = Material.RED_STAINED_GLASS_PANE;
            String name = killstreak.name();
            List<String> lore = new ArrayList<>();
            lore.add("Not enough credits to buy");
            lore.add("Cost: " + killstreak.getCostToBuy());

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(name);
            meta.setLore(lore);

            meta = hideAttributes(meta);

            item.setItemMeta(meta);

            return item;
        }

        private static void createBuyScreen(PlayerExtension player, Killstreak killstreakToBuy) {
            Inventory inventory = Bukkit.getServer().createInventory(null, buyScreenInventorySize, buyKillstreakScreenTitle);

            ItemStack[] items = LoadoutMenu.createBuyScreenYesNoOptions();

            items[22] = createUnlockedItem(killstreakToBuy);

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }


    }

    private static class PerkSelection {
        public static ItemStack createUnlockedPerk(Perk perk) {
            ItemStack item = new ItemStack(perk.getMaterial());
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName("Perk");
            //TODO: Add some extra explanation

            meta = hideAttributes(meta);

            item.setItemMeta(meta);
            return item;
        }
    }
}
