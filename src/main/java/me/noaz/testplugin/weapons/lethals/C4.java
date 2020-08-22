package me.noaz.testplugin.weapons.lethals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Switch;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
//Does no work properly, can "detonate" not yet placed c4s,
//Removes 2 items at once.:c

public class C4 implements Lethal {
    private PlayerExtension playerExtension;
    private TestPlugin plugin;
    private int cooldownTimeInTicks;
    private int placeRange = 4;
    private int explosionRange = 2;
    private boolean hasCooldown = false;

    private int additionalItemSlot = 7;

    private Material detonatorMaterial;
    private Material explosiveMaterial;

    private LinkedList<Block> placedExplosives = new LinkedList<>();

    public C4(PlayerExtension playerExtension, TestPlugin plugin, int cooldownTimeInTicks, Material explosiveMaterial,
              Material detonatorMaterial) {
        this.playerExtension = playerExtension;
        this.plugin = plugin;
        this.cooldownTimeInTicks = cooldownTimeInTicks;
        this.explosiveMaterial = explosiveMaterial;
        this.detonatorMaterial = detonatorMaterial;
    }

    @Override
    public synchronized void use() {
        if(!hasCooldown) {
            if (playerExtension.getPlayer().getInventory().getItemInMainHand().getType() == explosiveMaterial) {
                placeLever(playerExtension.getPlayer().getLastTwoTargetBlocks(null, placeRange));
            } else if (playerExtension.getPlayer().getInventory().getItemInMainHand().getType() == detonatorMaterial) {
                explode();
            }

            startCooldown();
        }
    }

    private void explode() {
        while(placedExplosives.peekLast() != null) {
            Block block = placedExplosives.removeLast();
            block.setType(Material.AIR);
            Location location = block.getLocation();
            location.getWorld().createExplosion(location, explosionRange,false,false, playerExtension.getPlayer());
        }

        playerExtension.getPlayer().getInventory().setItem(additionalItemSlot, null);

    }

    private void placeLever(List<Block> lastTwoTargetBlocksBlocks) {

        Block clickedBlock = lastTwoTargetBlocksBlocks.get(1);
        Block blockToChange = lastTwoTargetBlocksBlocks.get(0);

        BlockFace blockFace = clickedBlock.getFace(blockToChange);

        if(blockFace != null && isOkToPlaceOnBlock(clickedBlock, blockFace)) {

            if (blockToChange.getType() == Material.AIR) {
                blockToChange.setType(Material.LEVER);

                BlockData blockData = blockToChange.getBlockData();
                if (blockData instanceof Switch) {
                    switch (blockFace) {
                        case DOWN:
                            ((Switch) blockData).setFace(Switch.Face.CEILING);
                            break;
                        case UP:
                            ((Switch) blockData).setFace(Switch.Face.FLOOR);
                            break;
                        default:
                            ((Switch) blockData).setFace(Switch.Face.WALL);
                            ((Switch) blockData).setFacing(blockFace);
                            break;
                    }

                    blockToChange.setBlockData(blockData);
                }
                placedExplosives.add(blockToChange);

                ItemStack stack = playerExtension.getPlayer().getInventory().getItem(itemSlot);
                if (stack.getAmount() == 1) {
                    playerExtension.getPlayer().getInventory().setItem(itemSlot, null);
                } else {
                    stack.setAmount(stack.getAmount() - 1);
                }

                playerExtension.getPlayer().getInventory().setItem(additionalItemSlot, new ItemStack(detonatorMaterial));
            }
        }


    }

    private void startCooldown() {
        hasCooldown = true;
        new BukkitRunnable() {

            @Override
            public void run() {
                hasCooldown = false;
            }
        }.runTaskLater(plugin, cooldownTimeInTicks);
    }

    private boolean isOkToPlaceOnBlock(Block clickedBlock, BlockFace blockFace) {
        switch(clickedBlock.getType()) {
            case BARRIER:
                return false;
            case ACACIA_STAIRS:
            case ANDESITE_STAIRS:
            case BIRCH_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case DARK_OAK_STAIRS:
            case SMOOTH_QUARTZ_STAIRS:
            case DARK_PRISMARINE_STAIRS:
            case DIORITE_STAIRS:
            case END_STONE_BRICK_STAIRS:
            case GRANITE_STAIRS:
            case JUNGLE_STAIRS:
            case MOSSY_COBBLESTONE_STAIRS:
            case SMOOTH_RED_SANDSTONE_STAIRS:
            case MOSSY_STONE_BRICK_STAIRS:
            case NETHER_BRICK_STAIRS:
            case OAK_STAIRS:
            case POLISHED_ANDESITE_STAIRS:
            case POLISHED_DIORITE_STAIRS:
            case POLISHED_GRANITE_STAIRS:
            case PRISMARINE_BRICK_STAIRS:
            case PRISMARINE_STAIRS:
            case PURPUR_STAIRS:
            case QUARTZ_STAIRS:
            case RED_NETHER_BRICK_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case SANDSTONE_STAIRS:
            case STONE_BRICK_STAIRS:
            case STONE_STAIRS:
            case SMOOTH_SANDSTONE_STAIRS:
            case SPRUCE_STAIRS:
            case GLASS:
            case BLACK_STAINED_GLASS:
            case BLUE_STAINED_GLASS:
            case BROWN_STAINED_GLASS:
            case CYAN_STAINED_GLASS:
            case GRAY_STAINED_GLASS:
            case GREEN_STAINED_GLASS:
            case LIGHT_BLUE_STAINED_GLASS:
            case LIGHT_GRAY_STAINED_GLASS:
            case LIME_STAINED_GLASS:
            case MAGENTA_STAINED_GLASS:
            case ORANGE_STAINED_GLASS:
            case PINK_STAINED_GLASS:
            case PURPLE_STAINED_GLASS:
            case RED_STAINED_GLASS:
            case WHITE_STAINED_GLASS:
            case YELLOW_STAINED_GLASS:
            case IRON_DOOR:
            case GLOWSTONE:
                return true;
        }

        if(clickedBlock.getBlockData() instanceof Slab) {
            Slab slab = (Slab) clickedBlock.getBlockData();
            if(slab.getType() == Slab.Type.DOUBLE ||
                    slab.getType() == Slab.Type.TOP && blockFace == BlockFace.UP ||
                    slab.getType() == Slab.Type.BOTTOM && blockFace == BlockFace.DOWN) {
                return true;
            }

        }
        return clickedBlock.getType().isOccluding();
    }
}
