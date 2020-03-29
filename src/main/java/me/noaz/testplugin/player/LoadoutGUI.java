package me.noaz.testplugin.player;

import me.noaz.testplugin.weapons.WeaponConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Takes care of the loadout GUI in the lobby, lets a player change guns, perks etc.
 *
 * @author Noa Zetterman
 * @version 2020-03-06
 */
public class LoadoutGUI {
    private static int inventorySize = 27;
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

        //Might throw error if items 10+i is out of range, fix later
        for(int i = 0; i < configurations.length; i++) {
            WeaponConfiguration configuration = configurations[i];
            items[10+i] = createCustomItem(configuration.getGunMaterial(), configuration.getName(), configuration.getWeaponLore());
        }

        loadoutInventory.setStorageContents(items);

        player.openInventory(loadoutInventory);
    }

    /**
     * React to a player clicking on a slot in the loadout, select a weapon if a weapon is clicked etc.
     *
     * @param inventory The inventory that should be used
     * @param slot The slot that gets clicked on
     */
    public static void onItemClick(Inventory inventory, int slot) {
        if(inventory.getViewers().get(0) instanceof Player && inventory.getItem(0).getType().equals(goBackArrow)) {
            Player player = (Player) inventory.getViewers().get(0);
            PlayerHandler handler = (PlayerHandler) player.getMetadata("handler").get(0).value();
            
            String clickedItemName = inventory.getItem(slot).getItemMeta().getDisplayName();
            for(String name : handler.getOwnedWeaponNames()) {
                if(clickedItemName.equals(name)) {
                    handler.changeWeapon(clickedItemName);

                }
            }
        }
    }

    private static ItemStack createCustomItem(Material material, String name, List<String> lore) {
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
