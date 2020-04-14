package me.noaz.testplugin.Inventories;

import me.noaz.testplugin.weapons.Weapon;
import org.bukkit.Material;
import org.bukkit.Color;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

/**
 * Contains a set of default inventories that the player may access at any time.
 *
 * @author Noa Zetterman
 * @version 2020-03-06
 */
public class DefaultInventories {
    /**
     * Changes a players inventory to the default lobby inventory
     * @param inventory The players inventory
     */
    public static void setDefaultLobbyInventory(PlayerInventory inventory) {
        inventory.clear();
        setArmor(inventory, Color.BLUE);
        inventory.setItem(0, new ItemStack(Material.DIAMOND_BLOCK));

        //Give some like a something that can be used to change guns etc

    }

    public static void giveEmptyInventory(PlayerInventory inventory) {
        inventory.clear();
    }

    /**
     * Gives the player the correct loadout for a game.
     *
     * @param inventory The players inventory
     * @param teamColor The color of the players team
     * @param primaryWeapon The players primary weapon
     * @param secondaryWeapon The playrs secondary weapon
     */
    public static void giveDefaultInGameInventory(PlayerInventory inventory, Color teamColor, Weapon primaryWeapon, Weapon secondaryWeapon) {
        inventory.clear();
        setArmor(inventory, teamColor);

        // Yikes... gotta be able to set some stuff like dia armor and all types of grenades etc. Ok
        //That shouldn't be hard, just make a class leathal or tactial.
        inventory.setItem(0, new ItemStack(Material.DIAMOND_SWORD));
        inventory.setItem(1, customItemName(primaryWeapon.getMaterialAsItemStack(), primaryWeapon.toString(), primaryWeapon.getLore()));
        inventory.setItem(2, customItemName(secondaryWeapon.getMaterialAsItemStack(), secondaryWeapon.toString(), secondaryWeapon.getLore()));
    }

    public static void setArmor(PlayerInventory inventory, Color teamColor) {
        /*ItemStack helmet;
        if(teamColor == Color.RED) {
            helmet = new ItemStack(Material.RED_WOOL);
        } else {
            helmet = new ItemStack(Material.BLUE_WOOL);
        }

        inventory.setHelmet(helmet);*/

        ItemStack[] armor = new ItemStack[4];
        armor[3] = new ItemStack(Material.LEATHER_HELMET);
        armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        armor[0] = new ItemStack(Material.LEATHER_BOOTS);
        for(int i = 0; i < armor.length; i++) {
            armor[i] = colorArmor(armor[i], teamColor);
        }
        inventory.setArmorContents(armor);
    }

    public static void setHelmet(PlayerInventory inventory, Color teamColor) {

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);

        helmet = colorArmor(helmet, teamColor);
        inventory.setHelmet(helmet);
    }

    private static ItemStack colorArmor(ItemStack armor, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        meta.setUnbreakable(true);
        meta.setColor(color);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        armor.setItemMeta(meta);
        return armor;
    }

    private static ItemStack customItemName(ItemStack item, String name, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setUnbreakable(true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);
        return item;
    }
}
