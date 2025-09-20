package qwq.arcane.utils.render;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.Instance;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/BlockUtil.class */
public class BlockUtil {
    public static boolean isAirBlock(BlockPos blockPos) {
        Block block = Mine.getMinecraft().theWorld.getBlockState(blockPos).getBlock();
        return block instanceof BlockAir;
    }

    public static boolean isValidBock(BlockPos blockPos) {
        Block block = Mine.getMinecraft().theWorld.getBlockState(blockPos).getBlock();
        return ((block instanceof BlockLiquid) || (block instanceof BlockAir) || (block instanceof BlockChest) || (block instanceof BlockFurnace) || (block instanceof BlockLadder) || (block instanceof BlockTNT)) ? false : true;
    }

    public static BlockPos getBlockCorner(BlockPos start, BlockPos end) {
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    BlockPos pos = new BlockPos(end.getX() + x, end.getY() + y, end.getZ() + z);
                    if (!isBlockBetween(start, pos)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isBlockBetween(BlockPos start, BlockPos end) {
        int startX = start.getX();
        int startY = start.getY();
        int startZ = start.getZ();
        int endX = end.getX();
        int endY = end.getY();
        int endZ = end.getZ();
        double diffX = endX - startX;
        double diffY = endY - startY;
        double diffZ = endZ - startZ;
        double x = startX;
        double y = startY;
        double z = startZ;
        int STEPS = ((int) Math.max(Math.abs(diffX), Math.max(Math.abs(diffY), Math.abs(diffZ)))) * 4;
        for (int i = 0; i < STEPS - 1; i++) {
            x += diffX / STEPS;
            y += diffY / STEPS;
            z += diffZ / STEPS;
            if (x != endX || y != endY || z != endZ) {
                BlockPos pos = new BlockPos(x, y, z);
                Block block = Instance.mc.theWorld.getBlockState(pos).getBlock();
                if (block.getMaterial() != Material.air && block.getMaterial() != Material.water && !(block instanceof BlockVine) && !(block instanceof BlockLadder)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Block getBlock(BlockPos blockPos) {
        return Instance.mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static Map<BlockPos, Block> searchBlocks(int radius) {
        Map<BlockPos, Block> blocks = new HashMap<>();
        for (int x = radius; x > (-radius); x--) {
            for (int y = radius; y > (-radius); y--) {
                for (int z = radius; z > (-radius); z--) {
                    BlockPos blockPos = new BlockPos(Instance.mc.thePlayer.lastTickPosX + x, Instance.mc.thePlayer.lastTickPosY + y, Instance.mc.thePlayer.lastTickPosZ + z);
                    Block block = getBlock(blockPos);
                    blocks.put(blockPos, block);
                }
            }
        }
        return blocks;
    }
}
