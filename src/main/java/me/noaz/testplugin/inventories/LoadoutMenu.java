package me.noaz.testplugin.inventories;

import me.noaz.testplugin.Buyable;
import me.noaz.testplugin.killstreaks.Killstreak;
import me.noaz.testplugin.perk.Perk;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import me.noaz.testplugin.weapons.guns.GunType;
import me.noaz.testplugin.weapons.lethals.LethalEnum;
import me.noaz.testplugin.weapons.tacticals.TacticalEnum;
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
    private final static String buyPerkScreenTitle = "Buy Perk";
    private final static String buyLethalScreenTitle = "Buy Lethal";
    private final static String buyTacticalScreenTitle = "Buy Tactical";

    private final static String primaryGunScreenTitle = "Select primary";
    private final static String secondaryGunScreenTitle = "Select secondary";
    private final static String selectKillstreakScreen = "Select Killstreak";
    private final static String selectPerkScreen = "Select Perk";
    private final static String selectLethalScreen = "Select Lethal";
    private final static String selectTacticalScreen = "Select Tactical";
    private final static String cancelOption = "Cancel";
    private final static String buyOption = "Buy";


    /**
     * React to a player clicking on a slot in the loadout, select a weapon if a weapon is clicked etc.
     *
     * @param inventory The inventory that should be used
     * @param slot The slot that gets clicked on
     */
    public static void onItemClick(Inventory inventory, int slot, PlayerExtension player, List<GunConfiguration> gunConfigurations) {
        if(inventory.getViewers().get(0) instanceof Player) {
            String inventoryName = player.getPlayer().getOpenInventory().getTitle();

            String clickedItemName = inventory.getItem(slot).getItemMeta().getLocalizedName();


            switch(inventoryName) {
                case loadoutStartScreenTitle:
                    switch (slot) {
                        case 10:
                            GunSelection.displayPrimaryScreen(player, gunConfigurations);
                            break;
                        case 11:
                            GunSelection.displaySecondaryScreen(player, gunConfigurations);
                            break;
                        case 13:
                            LethalSelection.selectLethalScreen(player);
                            break;
                        case 14:
                            TacticalSelection.selectTacticalScreen(player);
                            break;
                        case 16:
                            PerkSelection.selectPerkScreen(player);
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
                            if(gun.getDisplayName().equals(clickedItemName)) {
                                if(player.ownsPrimaryGun(clickedItemName)) {
                                    player.setSelectedPrimaryGun(clickedItemName);
                                } else if(gun.getCostToBuy() <= player.getCredits()) {
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
                            if(gun.getDisplayName().equals(clickedItemName)) {
                                if(player.ownsSecondaryGun(clickedItemName)) {
                                    player.setSelectedSecondaryGun(clickedItemName);
                                } else if(gun.getCostToBuy() <= player.getCredits()) {
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
                case selectPerkScreen:
                    if(slot == 0) {
                        loadoutStartScreen(player);
                    } else {
                        Perk perk = Perk.valueOf(clickedItemName);
                        if(player.ownsPerk(perk)) {
                            player.setSelectedPerk(perk);
                        } else if(perk.getCostToBuy() <= player.getCredits()) {
                            PerkSelection.createBuyScreen(player, perk);
                        }
                    }
                    break;
                case selectLethalScreen:
                    if(slot == 0) {
                        loadoutStartScreen(player);
                    } else {
                        LethalEnum lethal = LethalEnum.valueOf(clickedItemName);
                        if(player.ownsLethal(lethal)) {
                            player.setSelectedLethal(lethal);
                        } else if(lethal.getCostToBuy() <= player.getCredits()) {
                            LethalSelection.createBuyScreen(player, lethal);
                        }
                    }
                    break;
                case selectTacticalScreen:
                    if(slot == 0) {
                        loadoutStartScreen(player);
                    } else {
                        TacticalEnum tactical = TacticalEnum.valueOf(clickedItemName);
                        if(player.ownsTactical(tactical)) {
                            player.setSelectedTactical(tactical);
                        } else if(tactical.getCostToBuy() <= player.getCredits()) {
                            TacticalSelection.createBuyScreen(player, tactical);
                        }
                    }
                    break;
                case buyGunScreenTitle:
                    if(clickedItemName.equals(cancelOption)) {
                        loadoutStartScreen(player);
                    } else if(clickedItemName.equals(buyOption)) {
                        String gunName = inventory.getItem(22).getItemMeta().getLocalizedName();

                        player.buyGun(gunName, gunConfigurations);
                        loadoutStartScreen(player);
                    }
                    break;
                case buyKillstreakScreenTitle:
                    if(clickedItemName.equals(cancelOption)) {
                        loadoutStartScreen(player);
                    } else if(clickedItemName.equals(buyOption)) {
                        String killstreak = inventory.getItem(22).getItemMeta().getLocalizedName();

                        player.buyKillstreak(Killstreak.valueOf(killstreak));
                        loadoutStartScreen(player);
                    }
                    break;
                case buyPerkScreenTitle:
                    if(clickedItemName.equals(cancelOption)) {
                        loadoutStartScreen(player);
                    } else if(clickedItemName.equals(buyOption)) {
                        String perk = inventory.getItem(22).getItemMeta().getLocalizedName();

                        player.buyPerk(Perk.valueOf(perk));
                        loadoutStartScreen(player);
                    }
                    break;
                case buyLethalScreenTitle:
                    if(clickedItemName.equals(cancelOption)) {
                        loadoutStartScreen(player);
                    } else if(clickedItemName.equals(buyOption)) {
                        String lethal = inventory.getItem(22).getItemMeta().getLocalizedName();

                        player.buyLethal(LethalEnum.valueOf(lethal));
                        loadoutStartScreen(player);
                    }
                    break;
                case buyTacticalScreenTitle:
                    if(clickedItemName.equals(cancelOption)) {
                        loadoutStartScreen(player);
                    } else if(clickedItemName.equals(buyOption)) {
                        String tactical = inventory.getItem(22).getItemMeta().getLocalizedName();

                        player.buyTactical(TacticalEnum.valueOf(tactical));
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

        items[10] = BuyableSelection.createUnlockedItem(player.getPrimaryGunConfiguration());
        items[11] = BuyableSelection.createUnlockedItem(player.getSecondaryGunConfiguration());


        items[13] = BuyableSelection.createUnlockedItem(player.getSelectedLethal().getAsBuyable());
        items[14] = BuyableSelection.createUnlockedItem(player.getSelectedTactical().getAsBuyable());

        items[16] = BuyableSelection.createUnlockedItem(player.getSelectedPerk().getAsBuyable());


        //items[28] = first ks
        items[29] = BuyableSelection.createUnlockedItem(player.getSelectedKillstreak().getAsBuyable());
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

        meta.setDisplayName(buyOption);
        meta.setLocalizedName(buyOption);
        //TODO: Add some explanation as to what/how much it costs to buy this

        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES , ItemFlag.HIDE_DESTROYS);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createBuyNoOption() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(cancelOption);
        meta.setLocalizedName(cancelOption);
        //TODO: Add some extra explanation

        hideAttributes(meta);

        item.setItemMeta(meta);
        return item;
    }

    private static void hideAttributes(ItemMeta meta) {
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES , ItemFlag.HIDE_DESTROYS);
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
                if(gun.getGunType() != GunType.SECONDARY) {
                    if (!player.ownsPrimaryGun(gun.getDisplayName())) {
                        if(gun.getUnlockLevel() > player.getLevel()) {
                            items[gun.getLoadoutMenuSlot()] = BuyableSelection.createLockedItem(gun);
                        } else if(gun.getCostToBuy() > player.getCredits()) {
                            items[gun.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleRedItem(gun);
                        } else {
                            items[gun.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleGreenItem(gun);
                        }
                    } else {
                        items[gun.getLoadoutMenuSlot()] = BuyableSelection.createUnlockedItem(gun);
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
                if(gun.getGunType() == GunType.SECONDARY) {
                    if (!player.ownsSecondaryGun(gun.getDisplayName())) {
                        if(gun.getUnlockLevel() > player.getLevel()) {
                            items[gun.getLoadoutMenuSlot()] = BuyableSelection.createLockedItem(gun);
                        } else if(gun.getCostToBuy() > player.getCredits()) {
                            items[gun.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleRedItem(gun);
                        } else {
                            items[gun.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleGreenItem(gun);
                        }
                    } else {
                        items[gun.getLoadoutMenuSlot()] = BuyableSelection.createUnlockedItem(gun);
                    }
                }
            }

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }

        private static void createBuyScreen(PlayerExtension player, GunConfiguration gunToBuy) {
            Inventory inventory = Bukkit.getServer().createInventory(null, buyScreenInventorySize, buyGunScreenTitle);

            BuyableSelection.createBuyScreen(player, gunToBuy, inventory);
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
                            items[killstreak.getLoadoutMenuSlot()] = BuyableSelection.createLockedItem(killstreak.getAsBuyable());
                        } else if (killstreak.getCostToBuy() > player.getCredits()) {
                            items[killstreak.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleRedItem(killstreak.getAsBuyable());
                        } else {
                            items[killstreak.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleGreenItem(killstreak.getAsBuyable());
                        }
                    } else {
                        items[killstreak.getLoadoutMenuSlot()] = BuyableSelection.createUnlockedItem(killstreak.getAsBuyable());
                    }
                }

            }

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }

        private static void createBuyScreen(PlayerExtension player, Killstreak killstreakToBuy) {
            Inventory inventory = Bukkit.getServer().createInventory(null, buyScreenInventorySize, buyKillstreakScreenTitle);

            BuyableSelection.createBuyScreen(player, killstreakToBuy.getAsBuyable(), inventory);
        }
    }

    private static class PerkSelection {
        public static void selectPerkScreen(PlayerExtension player) {
            Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, selectPerkScreen);
            ItemStack[] items = new ItemStack[inventorySize];


            items[0] = new ItemStack(goBackArrow);

            for(Perk perk : Perk.values()) {
                if(perk.getLoadoutMenuSlot() > 0) {
                    if (!player.ownsPerk(perk)) {
                        if (perk.getUnlockLevel() > player.getLevel()) {
                            items[perk.getLoadoutMenuSlot()] = BuyableSelection.createLockedItem(perk.getAsBuyable());
                        } else if (perk.getCostToBuy() > player.getCredits()) {
                            items[perk.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleRedItem(perk.getAsBuyable());
                        } else {
                            items[perk.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleGreenItem(perk.getAsBuyable());
                        }
                    } else {
                        items[perk.getLoadoutMenuSlot()] = BuyableSelection.createUnlockedItem(perk.getAsBuyable());
                    }
                }

            }

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }

        private static void createBuyScreen(PlayerExtension player, Perk perkToBuy) {
            Inventory inventory = Bukkit.getServer().createInventory(null, buyScreenInventorySize, buyPerkScreenTitle);

            BuyableSelection.createBuyScreen(player, perkToBuy.getAsBuyable(), inventory);
        }
    }

    private static class LethalSelection {
        public static void selectLethalScreen(PlayerExtension player) {
            Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, selectLethalScreen);
            ItemStack[] items = new ItemStack[inventorySize];


            items[0] = new ItemStack(goBackArrow);

            for(LethalEnum lethal : LethalEnum.values()) {
                if(lethal.getLoadoutMenuSlot() > 0) {
                    if (!player.ownsLethal(lethal)) {
                        if (lethal.getUnlockLevel() > player.getLevel()) {
                            items[lethal.getLoadoutMenuSlot()] = BuyableSelection.createLockedItem(lethal.getAsBuyable());
                        } else if (lethal.getCostToBuy() > player.getCredits()) {
                            items[lethal.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleRedItem(lethal.getAsBuyable());
                        } else {
                            items[lethal.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleGreenItem(lethal.getAsBuyable());
                        }
                    } else {
                        items[lethal.getLoadoutMenuSlot()] = BuyableSelection.createUnlockedItem(lethal.getAsBuyable());
                    }
                }

            }

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }

        private static void createBuyScreen(PlayerExtension player, LethalEnum lethalToBuy) {
            Inventory inventory = Bukkit.getServer().createInventory(null, buyScreenInventorySize, buyLethalScreenTitle);

            BuyableSelection.createBuyScreen(player, lethalToBuy.getAsBuyable(), inventory);
        }
    }

    private static class TacticalSelection {
        public static void selectTacticalScreen(PlayerExtension player) {
            Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize, selectTacticalScreen);
            ItemStack[] items = new ItemStack[inventorySize];


            items[0] = new ItemStack(goBackArrow);

            for(TacticalEnum tactical : TacticalEnum.values()) {
                if(tactical.getLoadoutMenuSlot() > 0) {
                    if (!player.ownsTactical(tactical)) {
                        if (tactical.getUnlockLevel() > player.getLevel()) {
                            items[tactical.getLoadoutMenuSlot()] = BuyableSelection.createLockedItem(tactical.getAsBuyable());
                        } else if (tactical.getCostToBuy() > player.getCredits()) {
                            items[tactical.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleRedItem(tactical.getAsBuyable());
                        } else {
                            items[tactical.getLoadoutMenuSlot()] = BuyableSelection.createLockedVisibleGreenItem(tactical.getAsBuyable());
                        }
                    } else {
                        items[tactical.getLoadoutMenuSlot()] = BuyableSelection.createUnlockedItem(tactical.getAsBuyable());
                    }
                }

            }

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }

        private static void createBuyScreen(PlayerExtension player, TacticalEnum tacticalToBuy) {
            Inventory inventory = Bukkit.getServer().createInventory(null, buyScreenInventorySize, buyTacticalScreenTitle);

            BuyableSelection.createBuyScreen(player, tacticalToBuy.getAsBuyable(), inventory);
        }
    }

    private static class BuyableSelection {
        public static ItemStack createUnlockedItem(Buyable item) {
            ItemStack itemStack = new ItemStack(item.getMaterial());
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(item.getDisplayName());
            itemMeta.setLocalizedName(item.getName());

            hideAttributes(itemMeta);

            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        public static ItemStack createLockedItem(Buyable item) {
            Material material = Material.BARRIER;
            String name = item.getDisplayName();
            List<String> lore = new ArrayList<>();
            lore.add("Locked");
            lore.add("Unlock level: " + item.getUnlockLevel());

            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(name);
            itemMeta.setLocalizedName(item.getName());
            itemMeta.setLore(lore);

            hideAttributes(itemMeta);

            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        public static ItemStack createLockedVisibleGreenItem(Buyable item) {
            Material material = Material.GREEN_STAINED_GLASS_PANE;
            String name = item.getDisplayName();
            List<String> lore = new ArrayList<>();
            lore.add("Buy by clicking");
            lore.add("Cost: " + item.getCostToBuy());

            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(name);
            itemMeta.setLocalizedName(item.getName());
            itemMeta.setLore(lore);

            hideAttributes(itemMeta);

            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        public static ItemStack createLockedVisibleRedItem(Buyable item) {
            Material material = Material.RED_STAINED_GLASS_PANE;
            String name = item.getDisplayName();
            List<String> lore = new ArrayList<>();
            lore.add("Not enough credits to buy");
            lore.add("Cost: " + item.getCostToBuy());

            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(name);
            itemMeta.setLocalizedName(item.getName());
            itemMeta.setLore(lore);

            hideAttributes(itemMeta);

            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        private static void createBuyScreen(PlayerExtension player, Buyable buyableToBuy, Inventory inventory) {
            ItemStack[] items = LoadoutMenu.createBuyScreenYesNoOptions();

            items[22] = createUnlockedItem(buyableToBuy);

            inventory.setStorageContents(items);
            player.getPlayer().openInventory(inventory);
        }
    }
}
