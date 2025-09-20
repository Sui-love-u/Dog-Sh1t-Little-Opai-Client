package qwq.arcane.utils.player;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import qwq.arcane.Client;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.math.MathUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/ScaffoldUtil.class */
public class ScaffoldUtil implements Instance {
    private static int lastSelectedSlot = -1;
    private static long lastSwitchTime = 0;
    static final List<Block> blacklistedBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.wooden_slab, Blocks.chest, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.skull, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence, Blocks.activator_rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.redstone_torch, Blocks.acacia_stairs, Blocks.birch_stairs, Blocks.brick_stairs, Blocks.dark_oak_stairs, Blocks.jungle_stairs, Blocks.nether_brick_stairs, Blocks.oak_stairs, Blocks.quartz_stairs, Blocks.red_sandstone_stairs, Blocks.sandstone_stairs, Blocks.spruce_stairs, Blocks.stone_brick_stairs, Blocks.stone_stairs, Blocks.double_wooden_slab, Blocks.stone_slab, Blocks.double_stone_slab, Blocks.stone_slab2, Blocks.double_stone_slab2, Blocks.web, Blocks.gravel, Blocks.daylight_detector_inverted, Blocks.daylight_detector, Blocks.soul_sand, Blocks.piston, Blocks.piston_extension, Blocks.piston_head, Blocks.sticky_piston, Blocks.iron_trapdoor, Blocks.ender_chest, Blocks.end_portal, Blocks.end_portal_frame, Blocks.standing_banner, Blocks.wall_banner, Blocks.deadbush, Blocks.slime_block, Blocks.acacia_fence_gate, Blocks.birch_fence_gate, Blocks.dark_oak_fence_gate, Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate);

    public static boolean canBePlacedOn(BlockPos blockPos) {
        Material material = mc.theWorld.getBlockState(blockPos).getBlock().getMaterial();
        return material.blocksMovement() && material.isSolid() && !(PlayerUtil.getBlock(blockPos) instanceof BlockAir);
    }

    public static Vec3 getHitVecOptimized(BlockPos blockPos, EnumFacing facing) {
        Vec3 eyes = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        return MathUtils.closestPointOnFace(new AxisAlignedBB(blockPos, blockPos.add(1, 1, 1)), facing, eyes);
    }

    private static boolean isBlockValid(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemBlock)) {
            return false;
        }
        Block block = ((ItemBlock) stack.getItem()).getBlock();
        return !blacklistedBlocks.contains(block);
    }

    public static int getBlockCount() {
        int blockCount = 0;
        for (int i = 36; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if ((is.getItem() instanceof ItemBlock) && !blacklistedBlocks.contains(((ItemBlock) is.getItem()).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
        }
        return blockCount;
    }

    public static PlaceData getPlaceData(BlockPos pos) {
        EnumFacing[] facings = {EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP};
        BlockPos[] offsets = {new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};
        Scaffold module = (Scaffold) Client.INSTANCE.getModuleManager().getModule(Scaffold.class);
        if (module.previousBlock != null && module.previousBlock.getY() > mc.thePlayer.posY) {
            module.previousBlock = null;
        }
        for (int i = 0; i < offsets.length; i++) {
            BlockPos blockPos = pos.add(offsets[i]);
            if (canBePlacedOn(blockPos)) {
                return new PlaceData(blockPos, facings[i]);
            }
        }
        BlockPos posBelow = pos.add(0, -1, 0);
        if (canBePlacedOn(posBelow)) {
            return new PlaceData(posBelow, EnumFacing.UP);
        }
        for (BlockPos offset : offsets) {
            BlockPos blockPos2 = pos.add(offset);
            for (int i2 = 0; i2 < offsets.length; i2++) {
                BlockPos blockPos1 = blockPos2.add(offsets[i2]);
                if (canBePlacedOn(blockPos1)) {
                    return new PlaceData(blockPos1, facings[i2]);
                }
            }
        }
        for (int lastCheck = 0; lastCheck < 2; lastCheck++) {
            for (int i3 = 0; i3 < offsets.length; i3++) {
                BlockPos newPos = pos.add(offsets[i3]);
                Block block = mc.theWorld.getBlockState(newPos).getBlock();
                if (newPos.equals(module.previousBlock)) {
                    return new PlaceData(newPos, facings[i3]);
                }
                if (lastCheck != 0 && !block.getMaterial().isReplaceable() && isInteractable(block)) {
                    return new PlaceData(newPos, facings[i3]);
                }
            }
        }
        return null;
    }

    private static boolean isInteractable(Block block) {
        return ((block instanceof BlockFurnace) || (block instanceof BlockFenceGate) || (block instanceof BlockChest) || (block instanceof BlockEnderChest) || (block instanceof BlockEnchantmentTable) || (block instanceof BlockBrewingStand) || (block instanceof BlockBed) || (block instanceof BlockDispenser) || (block instanceof BlockHopper) || (block instanceof BlockAnvil) || block == Blocks.crafting_table) ? false : true;
    }
}
