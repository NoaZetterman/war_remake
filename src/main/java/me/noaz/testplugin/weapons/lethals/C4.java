package me.noaz.testplugin.weapons.lethals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Switch;

import java.util.List;


public class C4 implements Lethal {
    private PlayerExtension playerExtension;
    private TestPlugin plugin;
    private int cooldownTimeInTicks;
    private int placeRange = 4;

    public C4(PlayerExtension playerExtension, TestPlugin plugin, int cooldownTimeInTicks) {
        this.playerExtension = playerExtension;
        this.plugin = plugin;
        this.cooldownTimeInTicks = cooldownTimeInTicks;
    }

    @Override
    public void use() {
        placeLever(playerExtension.getPlayer().getLastTwoTargetBlocks(null, placeRange));
        //ONLY slab type top/bottom left (+ testing?)

        //Blows the C4
        //Block block = playerExtension.getPlayer().getLastTwoTargetBlocks(null,10).get(0);
        //block.setType(Material.LEVER);
        //Its a lever boi that gets placed where the player clicks to place it
    }

    private void placeLever(List<Block> lastTwoTargetBlocksBlocks) {

        Block clickedBlock = lastTwoTargetBlocksBlocks.get(1);
        Block blockToChange = lastTwoTargetBlocksBlocks.get(0);

        BlockFace blockFace = clickedBlock.getFace(blockToChange);
        System.out.println("BlockFace " + blockFace);

        if(blockFace != null && isOkToPlaceOnBlock(clickedBlock)) {

            System.out.println("Block to change type: " + blockToChange.getType());
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
            }
        }
    }

    private boolean isOkToPlaceOnBlock(Block clickedBlock) {
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
            if(slab.getType() == Slab.Type.DOUBLE) {
                return true;
            } else if(slab.getType() == Slab.Type.TOP /*+ blockface stuff*/) {

            }

        }
        return clickedBlock.getType().isOccluding();
    }
}
