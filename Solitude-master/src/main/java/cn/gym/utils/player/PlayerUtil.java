package cn.gym.utils.player;

import cn.gym.utils.Instance;
import cn.gym.utils.math.Vector2f;
import com.google.common.base.Predicate;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class PlayerUtil implements Instance {
    public static boolean scoreTeam(final EntityPlayer entityPlayer) {
        return mc.thePlayer.isOnSameTeam(entityPlayer);
    }
    public static boolean colorTeam(EntityPlayer entityPlayer) {
        String targetName = entityPlayer.getDisplayName().getFormattedText().replace("\u00a7r", "");
        String clientName = mc.thePlayer.getDisplayName().getFormattedText().replace("\u00a7r", "");
        return targetName.startsWith("\u00a7" + clientName.charAt(1));
    }public static boolean inLiquid() {
        return mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
    }
    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava/*, Blocks.sand*/, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web, Blocks.tnt);
    public static int getSpeedPotion() {
        return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
    }
    public static void stop() {
        Minecraft.getMinecraft().thePlayer.motionX = 0;
        Minecraft.getMinecraft().thePlayer.motionZ = 0;
    }
    public static boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0.0) {
            return false;
        } else {
            for (int offset = 0; offset < (int) mc.thePlayer.posY + 2; offset += 2) {
                AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0, (-offset), 0.0);
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
            }

            return false;
        }
    }
    public static boolean overVoid(double posX, double posY, double posZ) {
        for (int i = (int) posY; i > -1; i--) {
            if (!(mc.theWorld.getBlockState(new BlockPos(posX, i, posZ)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return true;
    }
    public static boolean isBlockUnder(double height) {
        return PlayerUtil.isBlockUnder(height, true);
    }
    public static boolean isBlockUnder(Entity ent) {
        return mc.theWorld.getBlockState(new BlockPos(ent.posX, ent.posY -1, ent.posZ)).getBlock() != Blocks.air && mc.theWorld.getBlockState(new BlockPos(ent.posX, ent.posY -1, ent.posZ)).getBlock().isFullBlock();
    }

    public static boolean isBlockUnder(double height, boolean boundingBox) {
        if (boundingBox) {
            int offset = 0;
            while ((double)offset < height) {
                AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0, -offset, 0.0);
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
                offset += 2;
            }
        } else {
            int offset = 0;
            while ((double)offset < height) {
                if (PlayerUtil.blockRelativeToPlayer(0.0, -offset, 0.0).isFullBlock()) {
                    return true;
                }
                ++offset;
            }
        }
        return false;
    }
    public float[] getRotationsToBlock(BlockPos blockPos, EnumFacing enumFacing) {
        // 计算目标位置（方块面的中心）
        double targetX = blockPos.getX() + 0.5 + enumFacing.getFrontOffsetX() * 0.5;
        double targetY = blockPos.getY() + 0.5 + enumFacing.getFrontOffsetY() * 0.5;
        double targetZ = blockPos.getZ() + 0.5 + enumFacing.getFrontOffsetZ() * 0.5;

        // 玩家眼睛位置
        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double playerZ = mc.thePlayer.posZ;

        // 计算差值
        double diffX = targetX - playerX;
        double diffY = targetY - playerY;
        double diffZ = targetZ - playerZ;

        // 计算水平距离
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        // 计算偏航角 (yaw)
        float yaw = (float)(MathHelper.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;

        // 计算俯仰角 (pitch)
        float pitch = (float)-(MathHelper.atan2(diffY, diffXZ) * 180.0D / Math.PI);

        return new float[] { yaw, pitch };
    }
    public int getBlockCount() {
        int n = 0;
        int i = 36;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = stack.getItem();
                if (stack.getItem() instanceof ItemBlock && isValid(item)) {
                    n += stack.stackSize;
                }
            }
            ++i;
        }
        return n;
    }
    private boolean isValid(final Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) (item)).getBlock());
    }

    public static Vec3 getVectorForRotation(Vector2f rotation) {
        float yawCos = (float)Math.cos(-rotation.x * ((float)Math.PI / 180) - (float)Math.PI);
        float yawSin = (float)Math.sin(-rotation.x * ((float)Math.PI / 180) - (float)Math.PI);
        float pitchCos = (float)(-Math.cos(-rotation.y * ((float)Math.PI / 180)));
        float pitchSin = (float)Math.sin(-rotation.y * ((float)Math.PI / 180));
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }
    public static int findTool(final BlockPos blockPos) {
        float bestSpeed = 1;
        int bestSlot = -1;

        final IBlockState blockState = mc.theWorld.getBlockState(blockPos);

        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack == null) {
                continue;
            }

            final float speed = itemStack.getStrVsBlock(blockState.getBlock());

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        return bestSlot;
    }
    public int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() || !(mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock))
                continue;
            return i;
        }
        return -1;
    }

    public static List<EntityPlayer> getLivingPlayers(Predicate<EntityPlayer> validator) {
        List<EntityPlayer> entities = new ArrayList<>();
        if(mc.theWorld == null) return entities;
        for (Entity entity : mc.theWorld.playerEntities) {
            if (entity instanceof EntityPlayer player) {
                if (validator.apply(player))
                    entities.add(player);
            }
        }
        return entities;
    }
    public static boolean nullCheck() {
        return mc.thePlayer != null && mc.theWorld != null;
    }
    public static boolean isMob(Entity entity) {
        return entity instanceof EntityMob
                || entity instanceof EntityVillager
                || entity instanceof EntitySlime
                || entity instanceof EntityGhast
                || entity instanceof EntityDragon;
    }
    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }
    public static boolean isAnimal(Entity entity) {
        return entity instanceof EntityAnimal
                || entity instanceof EntitySquid
                || entity instanceof EntityGolem
                || entity instanceof EntityBat;
    }
    public static boolean isBlockBlacklisted(Item item) {
        return item instanceof ItemAnvilBlock || item.getUnlocalizedName().contains("sand") || item.getUnlocalizedName().contains("gravel") || item.getUnlocalizedName().contains("ladder") || item.getUnlocalizedName().contains("tnt") || item.getUnlocalizedName().contains("chest") || item.getUnlocalizedName().contains("web");
    }
    public static boolean isInTeam(Entity entity) {
        if (mc.thePlayer.getDisplayName() != null && entity.getDisplayName() != null) {
            String targetName = entity.getDisplayName().getFormattedText().replace("§r", "");
            String clientName = mc.thePlayer.getDisplayName().getFormattedText().replace("§r", "");
            return targetName.startsWith("§" + clientName.charAt(1));
        }
        return false;
    }
    public static boolean armorTeam(final EntityPlayer entityPlayer) {
        if (mc.thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null) {
            final ItemStack myHead = mc.thePlayer.inventory.armorInventory[3];
            final ItemArmor myItemArmor = (ItemArmor)myHead.getItem();
            final ItemStack entityHead = entityPlayer.inventory.armorInventory[3];
            final ItemArmor entityItemArmor = (ItemArmor)entityHead.getItem();
            return String.valueOf(entityItemArmor.getColor(entityHead)).equals("10511680") || myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead);
        }
        return false;
    }

    public static boolean canEntityBeSeen(Entity e) {
        Vec3 vec1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3 vec2 = new Vec3(e.posX, e.posY + (e.getEyeHeight() / 1.32F), e.posZ);
        double minx = e.posX - 0.25;
        double maxx = e.posX + 0.25;
        double miny = e.posY;
        double maxy = e.posY + Math.abs(e.posY - box.maxY);
        double minz = e.posZ - 0.25;
        double maxz = e.posZ + 0.25;
        boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(minx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3(minx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;

        vec2 = new Vec3(maxx, maxy, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3(minx, maxy, minz);

        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(minx, maxy, maxz - 0.1);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, maxy, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        return see;
    }

    public Block block(final double x, final double y, final double z) {
        return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }
    public EnumFacingOffset getEnumFacing(final Vec3 position) {
        for (int x2 = -1; x2 <= 1; x2 += 2) {
            if (!(PlayerUtil.block(position.xCoord + x2, position.yCoord, position.zCoord) instanceof BlockAir)) {
                if (x2 > 0) {
                    return new EnumFacingOffset(EnumFacing.WEST, new Vec3(x2, 0, 0));
                } else {
                    return new EnumFacingOffset(EnumFacing.EAST, new Vec3(x2, 0, 0));
                }
            }
        }

        for (int y2 = -1; y2 <= 1; y2 += 2) {
            if (!(PlayerUtil.block(position.xCoord, position.yCoord + y2, position.zCoord) instanceof BlockAir)) {
                if (y2 < 0) {
                    return new EnumFacingOffset(EnumFacing.UP, new Vec3(0, y2, 0));
                }
            }
        }

        for (int z2 = -1; z2 <= 1; z2 += 2) {
            if (!(PlayerUtil.block(position.xCoord, position.yCoord, position.zCoord + z2) instanceof BlockAir)) {
                if (z2 < 0) {
                    return new EnumFacingOffset(EnumFacing.SOUTH, new Vec3(0, 0, z2));
                } else {
                    return new EnumFacingOffset(EnumFacing.NORTH, new Vec3(0, 0, z2));
                }
            }
        }

        return null;
    }


    public Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }

    //
    public class hyt {
        public static boolean isHoldingGodAxe(final EntityPlayer player) {
            final ItemStack holdingItem = player.getEquipmentInSlot(0);
            return isGodAxe(holdingItem);
        }

        public static boolean isGodAxe(final ItemStack stack) {
            if (stack == null) {
                return false;
            }
            if (stack.getItem() != Items.golden_axe) {
                return false;
            }
            final int durability = stack.getMaxDamage() - stack.getItemDamage();
            if (durability > 2) {
                return false;
            }
            final NBTTagList enchantmentTagList = stack.getEnchantmentTagList();
            if (enchantmentTagList == null) {
                return false;
            }
            for (int i = 0; i < enchantmentTagList.tagCount(); ++i) {
                final NBTTagCompound nbt = (NBTTagCompound) enchantmentTagList.get(i);
                if (nbt.hasKey("id") && nbt.hasKey("lvl") && nbt.getInteger("id") == 16) {
                    final int level = nbt.getInteger("lvl");
                    if (level >= 666) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static boolean isKBBall(final ItemStack stack) {
            if (stack == null) {
                return false;
            }
            if (stack.getItem() != Items.slime_ball) {
                return false;
            }
            final NBTTagList enchantmentTagList = stack.getEnchantmentTagList();
            if (enchantmentTagList == null) {
                return false;
            }
            for (int i = 0; i < enchantmentTagList.tagCount(); ++i) {
                final NBTTagCompound nbt = (NBTTagCompound) enchantmentTagList.get(i);
                if (nbt.hasKey("id") && nbt.hasKey("lvl") && nbt.getInteger("id") == 19) {
                    final int level = nbt.getInteger("lvl");
                    if (level >= 2) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static int hasEatenGoldenApple(final EntityPlayer player) {
            final PotionEffect regenPotion = player.getActivePotionEffect(Potion.regeneration);
            if (regenPotion == null) {
                return -1;
            }
            if (regenPotion.getAmplifier() < 4) {
                return -1;
            }
            return regenPotion.getDuration();
        }

        public static int isRegen(final EntityPlayer player) {
            final PotionEffect regenPotion = player.getActivePotionEffect(Potion.regeneration);
            if (regenPotion == null) {
                return -1;
            }
            return regenPotion.getDuration();
        }

        public static int isStrength(final EntityPlayer player) {
            final PotionEffect strengthPotion = player.getActivePotionEffect(Potion.damageBoost);
            if (strengthPotion == null) {
                return -1;
            }
            return strengthPotion.getDuration();
        }
    }
}
