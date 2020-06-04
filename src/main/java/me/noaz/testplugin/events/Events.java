package me.noaz.testplugin.events;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.inventories.LoadoutMenu;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

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
        PlayerExtension player = data.getPlayerExtension(event.getPlayer());
        Action action = event.getAction();

        if(player.hasWeaponInMainHand() && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            player.getWeaponInMainHand().shoot();
        } else if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_BLOCK)) {
            LoadoutMenu.loadoutStartScreen(player);
        }

        if(action == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            if(player.hasWeaponInMainHand()) {
                changeScopeWhenArmswingIsActivatedByRightclick(event.getClickedBlock().getType(), player);
            }

            cancelClickActions(event);
        }
    }

    private void changeScopeWhenArmswingIsActivatedByRightclick(Material clickedMaterial, PlayerExtension player) {
        if(player.hasWeaponInMainHand()) {
            switch (clickedMaterial) {
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
                case FLOWER_POT:
                case ACACIA_BUTTON:
                case BIRCH_BUTTON:
                case DARK_OAK_BUTTON:
                case JUNGLE_BUTTON:
                case OAK_BUTTON:
                case SPRUCE_BUTTON:
                case STONE_BUTTON:
                case LEVER:
                case ACACIA_SIGN:
                case ACACIA_WALL_SIGN:
                case BIRCH_SIGN:
                case BIRCH_WALL_SIGN:
                case DARK_OAK_SIGN:
                case DARK_OAK_WALL_SIGN:
                case JUNGLE_SIGN:
                case JUNGLE_WALL_SIGN:
                case OAK_SIGN:
                case OAK_WALL_SIGN:
                case SPRUCE_SIGN:
                case SPRUCE_WALL_SIGN:
                case ANVIL:
                case CHIPPED_ANVIL:
                case DAMAGED_ANVIL:
                case ACACIA_TRAPDOOR:
                case BIRCH_TRAPDOOR:
                case DARK_OAK_TRAPDOOR:
                case JUNGLE_TRAPDOOR:
                case OAK_TRAPDOOR:
                case SPRUCE_TRAPDOOR:
                case ACACIA_FENCE:
                case BIRCH_FENCE:
                case JUNGLE_FENCE:
                case OAK_FENCE:
                case SPRUCE_FENCE:
                case NETHER_BRICK_FENCE:
                case ACACIA_FENCE_GATE:
                case DARK_OAK_FENCE:
                case BIRCH_FENCE_GATE:
                case DARK_OAK_FENCE_GATE:
                case JUNGLE_FENCE_GATE:
                case OAK_FENCE_GATE:
                case SPRUCE_FENCE_GATE:
                case DARK_OAK_DOOR:
                case ACACIA_DOOR:
                case BIRCH_DOOR:
                case IRON_DOOR:
                case JUNGLE_DOOR:
                case OAK_DOOR:
                case SPRUCE_DOOR:
                case CRAFTING_TABLE:
                case CAULDRON:
                case CHEST:
                case ENDER_CHEST:
                case TRAPPED_CHEST:
                case CHEST_MINECART:
                case NOTE_BLOCK:
                case JUKEBOX:
                case BREWING_STAND:
                    player.changeScope();
                    break;
            }
        }
    }

    private void cancelClickActions(PlayerInteractEvent event) {
        switch(event.getClickedBlock().getType()) {
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
            case FLOWER_POT:
            case ACACIA_BUTTON:
            case BIRCH_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
            case STONE_BUTTON:
            case LEVER:
            case CRAFTING_TABLE:
            case FURNACE:
            case FURNACE_MINECART:
            case CHEST:
            case CHEST_MINECART:
            case ENDER_CHEST:
            case TRAPPED_CHEST:
            case NOTE_BLOCK:
            case JUKEBOX:
            case HOPPER:
            case HOPPER_MINECART:
            case ITEM_FRAME:
            case PAINTING:
            case ANVIL:
            case DAMAGED_ANVIL:
            case CHIPPED_ANVIL:
            case BREWING_STAND:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof ItemFrame) {
            data.getPlayerExtension(event.getPlayer()).getWeaponInMainHand().shoot();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() instanceof ArmorStand) {
            PlayerExtension player = data.getPlayerExtension(event.getPlayer());
            if(player.hasWeaponInMainHand()) {
                player.getWeaponInMainHand().shoot();
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractEntityEvent(EntityInteractEvent event) {
        if(event.getBlock().getType() == Material.ITEM_FRAME) {
            event.setCancelled(true);
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
        if(event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            PlayerExtension player = data.getPlayerExtension(event.getPlayer());
            if(player.hasWeaponInMainHand()) {
                player.changeScope();
            }
        }
    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        event.getItemDrop().remove();
        event.setCancelled(true);

        PlayerExtension player = data.getPlayerExtension(event.getPlayer());
        player.reloadIfGun(event.getItemDrop().getItemStack().getType());
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

        PlayerExtension player = data.getPlayerExtension(event.getPlayer());
        if(event.getMainHandItem() != null) {
            player.reloadIfGun(event.getMainHandItem().getType());
        }
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
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingEntityBreakEvent(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHangEntityEvent(HangingPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onManipulateArmorstandEvent(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTakeLecternBookEvent(PlayerTakeLecternBookEvent event) {
        event.setCancelled(true);
    }
}
