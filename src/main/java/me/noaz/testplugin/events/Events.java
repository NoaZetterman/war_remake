package me.noaz.testplugin.events;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.inventories.LoadoutMenu;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

//https://github.com/Belphemur/CustomEvent Might be useful

/**
 * Contains all events, may be broken up into multiple event classes later.
 */
public class Events implements Listener {
    GameData data;

    public Events(GameData data) {
        this.data = data;
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

        PlayerExtension player = data.getPlayerExtension(event.getPlayer());
        Action action = event.getAction();


        if(player.hasWeaponInMainHand() && (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
            player.getWeaponInMainHand().shoot();
        } else if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_BLOCK)) {
            LoadoutMenu.loadoutStartScreen(player);
            //player.getOwnedWeapons();
        }

        if(action.equals(Action.RIGHT_CLICK_BLOCK)) {
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
                case POTTED_ACACIA_SAPLING:
                case POTTED_ORANGE_TULIP:
                case POTTED_ALLIUM:
                case POTTED_AZURE_BLUET:
                case POTTED_BAMBOO:
                case POTTED_BIRCH_SAPLING:
                case POTTED_BLUE_ORCHID:
                case POTTED_BROWN_MUSHROOM:
                case POTTED_CACTUS:
                case POTTED_CORNFLOWER:
                case POTTED_DANDELION:
                case POTTED_DARK_OAK_SAPLING:
                case POTTED_DEAD_BUSH:
                case POTTED_FERN:
                case POTTED_JUNGLE_SAPLING:
                case POTTED_LILY_OF_THE_VALLEY:
                case POTTED_OAK_SAPLING:
                case POTTED_OXEYE_DAISY:
                case POTTED_PINK_TULIP:
                case POTTED_POPPY:
                case POTTED_RED_MUSHROOM:
                case POTTED_RED_TULIP:
                case POTTED_SPRUCE_SAPLING:
                case POTTED_WHITE_TULIP:
                case POTTED_WITHER_ROSE:
                case HOPPER:
                case HOPPER_MINECART:
                case ANVIL:
                case LEVER:
                case CHIPPED_ANVIL:
                case DAMAGED_ANVIL:
                    event.setCancelled(true);
                    break;
            }
        }
    }

    /**
     * If the inventory was clicked on in game then do nothing, otherwise use the loadout GUI
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!data.getPlayerExtension((Player) event.getWhoClicked()).isPlayingGame() &&
                event.getClickedInventory() != null && event.getAction() != InventoryAction.NOTHING)
            LoadoutMenu.onItemClick(event.getClickedInventory(), event.getSlot(), data.getPlayerExtension((Player) event.getWhoClicked()),
                    data.getGunConfigurations());
        event.setCancelled(true);
    }

    @EventHandler
    public void onHandSwingEvent(PlayerAnimationEvent event) {

        if(event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {
            PlayerExtension player = data.getPlayerExtension(event.getPlayer());

            if(player.hasWeaponInMainHand() && !player.getWeaponInMainHand().justStartedReloading()) {
                player.changeScope();
            }
        }
    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        //Reload when pressing drop button (Q)
        PlayerExtension player = data.getPlayerExtension(event.getPlayer());
        if(player.getPrimaryGun().getMaterialType().equals(event.getItemDrop().getItemStack().getType())) {
            player.reloadWeapon(player.getPrimaryGun());
        } else if (player.getSecondaryGun().getMaterialType().equals(event.getItemDrop().getItemStack().getType())) {
            player.reloadWeapon(player.getSecondaryGun());
        }

        event.getItemDrop().remove();
        event.setCancelled(true);
        //@SuppressWarnings()
        //event.getPlayer().updateInventory();
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        ChatColor color = data.getPlayerExtension(event.getPlayer()).getTeamChatColor();

        event.getPlayer().setDisplayName("Lvl " + event.getNewLevel() + " " + color + event.getPlayer().getName());
    }

    @EventHandler
    public void onChangeMainHand(PlayerItemHeldEvent event) {
        PlayerExtension player = data.getPlayerExtension(event.getPlayer());

        if(player.isPlayingGame()) {
            player.getPrimaryGun().stopShooting();
            player.getSecondaryGun().stopShooting();
            data.getPlayerExtension(event.getPlayer()).unScope();
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

    @EventHandler
    public void onPlayerSwimEvent(EntityToggleSwimEvent event) {
        //event.setCancelled(true);
    }
}
