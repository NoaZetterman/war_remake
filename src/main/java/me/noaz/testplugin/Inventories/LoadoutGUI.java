package me.noaz.testplugin.Inventories;

import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.GunConfiguration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

/**
 * Takes care of the loadout GUI in the lobby, lets a player change guns, perks etc.
 *
 * @author Noa Zetterman
 * @version 2020-03-06
 */
public class LoadoutGUI {


    /**
     * React to a player clicking on a slot in the loadout, select a weapon if a weapon is clicked etc.
     *
     * @param inventory The inventory that should be used
     * @param slot The slot that gets clicked on
     */
    public static void onItemClick(Inventory inventory, int slot, PlayerExtension player, List<GunConfiguration> configurations) {
        if(inventory.getViewers().get(0) instanceof Player) {
            String inventoryName = player.getPlayer().getOpenInventory().getTitle();

            String clickedItemName = inventory.getItem(slot).getItemMeta().getDisplayName();


            switch (inventoryName) {
                case "Loadout Selection":
                    switch (slot) {
                        case 10:
                            LoadoutMenu.selectPrimaryScreen(player, configurations);
                            break;
                        case 11:
                            LoadoutMenu.selectSecondary(player, configurations);
                            break;
                        default:
                            break;
                    }
                case "Select primary":
                    if (slot == 0) {
                        LoadoutMenu.loadoutStartScreen(player);
                    } else {
                        player.changePrimaryGun(clickedItemName);
                    }
                    break;
                case "Select secondary":
                    if (slot == 0) {
                        LoadoutMenu.loadoutStartScreen(player);
                    } else {
                        player.changeSecondaryGun(clickedItemName);
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