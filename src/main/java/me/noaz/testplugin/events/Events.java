package me.noaz.testplugin.events;

import me.noaz.testplugin.Inventories.LoadoutMenu;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.tasks.GameController;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

//https://github.com/Belphemur/CustomEvent Might be useful

/**
 * Contains all events, may be broken up into multiple event classes later.
 */
public class Events implements Listener {
    GameController gameController;

    public Events(GameController gameController) {
        this.gameController = gameController;
    }
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
        //event.setCancelled(true);

        //event.getItem()

        PlayerExtension player = gameController.getPlayerExtension(event.getPlayer());
        Action action = event.getAction();


        if(player.hasWeaponInMainHand() && (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
            player.getWeaponInMainHand().shoot();
        } else if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_BLOCK)) {
            LoadoutMenu.loadoutStartScreen(player);
            //player.getOwnedWeapons();
        } else if(action.equals(Action.RIGHT_CLICK_BLOCK)) {
            switch(event.getClickedBlock().getType()) {
                case CRAFTING_TABLE:
                case FLOWER_POT:
                case FURNACE:
                case FURNACE_MINECART:
                case CHEST:
                case CHEST_MINECART:
                case ENDER_CHEST:
                case TRAPPED_CHEST:
                case NOTE_BLOCK:
                case CAULDRON: {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    /**
     * If the inventory was clicked on in game then do nothing, otherwise use the loadout GUI
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!gameController.getPlayerExtension((Player) event.getWhoClicked()).isPlayingGame() &&
                event.getClickedInventory() != null && event.getAction() != InventoryAction.NOTHING)
            LoadoutMenu.onItemClick(event.getClickedInventory(), event.getSlot(), gameController.getPlayerExtension((Player) event.getWhoClicked()),
                    gameController.getGunConfigurations());
        event.setCancelled(true);
    }

    @EventHandler
    public void onHandSwingEvent(PlayerAnimationEvent event) {

        if(event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {
            PlayerExtension player = gameController.getPlayerExtension(event.getPlayer());

            if(player.hasWeaponInMainHand() && !player.getWeaponInMainHand().justStartedReloading()) {
                player.changeScope();
            }
        }
    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        //Reload when pressing drop button (Q)
        PlayerExtension player = gameController.getPlayerExtension(event.getPlayer());
        if(player.getPrimaryGun().getMaterialType().equals(event.getItemDrop().getItemStack().getType())) {
            player.reloadWeapon(player.getPrimaryGun());
        } else if (player.getSecondaryGun().getMaterialType().equals(event.getItemDrop().getItemStack().getType())) {
            player.reloadWeapon(player.getSecondaryGun());
        }

        event.setCancelled(true);
        event.getItemDrop().remove();
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        ChatColor color = gameController.getPlayerExtension(event.getPlayer()).getTeamChatColor();

        event.getPlayer().setDisplayName("Lvl " + event.getNewLevel() + " " + color + event.getPlayer().getName());
    }

    @EventHandler
    public void onChangeMainHand(PlayerItemHeldEvent event) {
        PlayerExtension player = gameController.getPlayerExtension(event.getPlayer());

        if(player.isPlayingGame()) {
            player.getPrimaryGun().stopShooting();
            player.getSecondaryGun().stopShooting();
            gameController.getPlayerExtension(event.getPlayer()).unScope();
        }
        player.updateActionBar();
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
        //TODO: Change this, maybe put in ChatMessages?
        event.setFormat("%s" + ChatColor.GOLD + " >" + ChatColor.WHITE + " %s");
    }
    @EventHandler
    public void onXpGain(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }
}
