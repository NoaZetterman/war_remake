package me.noaz.testplugin.Inventories;

import me.noaz.testplugin.player.PlayerExtension;
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


    /**
     * React to a player clicking on a slot in the loadout, select a weapon if a weapon is clicked etc.
     *
     * @param inventory The inventory that should be used
     * @param slot The slot that gets clicked on
     */
    public static void onItemClick(Inventory inventory, int slot, PlayerExtension player) {
        if(inventory.getViewers().get(0) instanceof Player) {

            //Here: get current inventory and then a switch thing with diff slots or smth idk
            System.out.println(player.getPlayer().getOpenInventory().getTitle());

            String inventoryName = player.getPlayer().getOpenInventory().getTitle();
            String clickedItemName = inventory.getItem(slot).getItemMeta().getDisplayName();


            switch(inventoryName) {
                case "Loadout Selection":
                    if(slot == 10) {
                        LoadoutMenu.selectPrimaryScreen(player);
                    }
                    break;
                case "Select primary":
                    for(String name : player.getOwnedWeaponNames()) {
                        if(clickedItemName.equals(name)) {
                            player.changeWeapon(clickedItemName);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

}

//Create a main loadout class that gets whats clicked on if its this inventory,
//then depending on whats clicked, send to some diff classes that creates the new inv
//https://bukkit.org/threads/icon-menu.108342/ nice.