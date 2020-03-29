package me.noaz.testplugin.events;

import me.noaz.testplugin.player.LoadoutGUI;
import me.noaz.testplugin.player.PlayerHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

//https://github.com/Belphemur/CustomEvent Might be useful

/**
 * Contains all events, may be broken up into multiple event classes later.
 */
public class Events implements Listener {
    /**
     * Disables players dropping items
     */
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * Fires a gun if the player is in game and has a gun to fire
     *
     * If player is in spawn then it may open the loadout GUI.
     */
    @EventHandler
    public void onClick(PlayerInteractEvent event)
    {
        PlayerHandler handler = (PlayerHandler) event.getPlayer().getMetadata("handler").get(0).value();

        Action action = event.getAction();

        if(handler.hasWeaponInMainHand() && (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
            handler.getWeaponInMainHand().shoot();
        } else if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_BLOCK)) {
            LoadoutGUI.loadoutStartScreen(event.getPlayer(), handler.getOwnedWeapons());
        }
    }

    /**
     * If the inventory was clicked on in game then do nothing, otherwise use the loadout GUI
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!((PlayerHandler)event.getWhoClicked().getMetadata("handler").get(0).value()).isPlayingGame())
            LoadoutGUI.onItemClick(event.getClickedInventory(), event.getSlot());
        event.setCancelled(true);
    }

    /**
     * Scopes/unscopes if the player is left clicking with a gun, otherwise nothing.
     */
    @EventHandler
    public void onHandSwingEvent(PlayerAnimationEvent event) {
        if(event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {
            PlayerHandler handler = (PlayerHandler) event.getPlayer().getMetadata("handler").get(0).value();

            if(handler.hasWeaponInMainHand()) {
                handler.changeScope();
            }
        }
    }

    //TODO: onBlockPlaceEvent, cancel.

    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        ChatColor color = ((PlayerHandler) event.getPlayer().getMetadata("handler").get(0).value()).getTeamChatColor();

        event.getPlayer().setDisplayName("Lvl " + event.getNewLevel() + " " + color + event.getPlayer().getName());
    }

    @EventHandler
    public void onChangeMainHand(PlayerItemHeldEvent event) {
        ((PlayerHandler) event.getPlayer().getMetadata("handler").get(0).value()).unScope();
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        //Reload when pressing Q
        PlayerHandler handler = (PlayerHandler) event.getPlayer().getMetadata("handler").get(0).value();
        if(handler.getPrimaryWeapon().getMaterialType().equals(event.getItemDrop().getItemStack().getType())) {
            handler.reloadWeapon(handler.getPrimaryWeapon());
        } else if (handler.getSecondaryWeapon().getMaterialType().equals(event.getItemDrop().getItemStack().getType())) {
            handler.reloadWeapon(handler.getSecondaryWeapon());
        }

        event.setCancelled(true);
        event.getItemDrop().remove();
    }

    @EventHandler
    public void onPlayerSwapHandItem(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        if(event.getEntity() instanceof Player) {
            ((Player) event.getEntity()).setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat("%s" + ChatColor.GOLD + " >" + ChatColor.WHITE + " %s");
    }
    @EventHandler
    public void onXpGain(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }
}
