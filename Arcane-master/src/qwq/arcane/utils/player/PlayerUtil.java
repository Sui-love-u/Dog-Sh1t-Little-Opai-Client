package qwq.arcane.utils.player;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.StringUtils;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.rotation.RotationUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/PlayerUtil.class */
public class PlayerUtil implements Instance {
    private static final Int2IntMap GOOD_POTIONS = new Int2IntOpenHashMap() { // from class: qwq.arcane.utils.player.PlayerUtil.1
        {
            put(6, 1);
            put(10, 2);
            put(11, 3);
            put(21, 4);
            put(22, 5);
            put(23, 6);
            put(5, 7);
            put(1, 8);
            put(12, 9);
            put(14, 10);
            put(3, 11);
            put(13, 12);
        }
    };

    public static boolean scoreTeam(EntityPlayer entityPlayer) {
        return mc.thePlayer.isOnSameTeam(entityPlayer);
    }

    public static boolean colorTeam(EntityPlayer sb) {
        String targetName = StringUtils.replace(sb.getDisplayName().getFormattedText(), "§r", "");
        String clientName = mc.thePlayer.getDisplayName().getFormattedText().replace("§r", "");
        return targetName.startsWith("§" + clientName.charAt(1));
    }

    public static boolean armorTeam(EntityPlayer entityPlayer) {
        if (mc.thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null) {
            ItemStack myHead = mc.thePlayer.inventory.armorInventory[3];
            ItemArmor myItemArmor = (ItemArmor) myHead.getItem();
            ItemStack entityHead = entityPlayer.inventory.armorInventory[3];
            ItemArmor entityItemArmor = (ItemArmor) entityHead.getItem();
            return String.valueOf(entityItemArmor.getColor(entityHead)).equals("10511680") || myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead);
        }
        return false;
    }

    public static boolean isBlockUnder(Entity ent) {
        return mc.theWorld.getBlockState(new BlockPos(ent.posX, ent.posY - 1.0d, ent.posZ)).getBlock() != Blocks.air && mc.theWorld.getBlockState(new BlockPos(ent.posX, ent.posY - 1.0d, ent.posZ)).getBlock().isFullBlock();
    }

    public static boolean isBlockUnder(double height, boolean boundingBox) {
        if (boundingBox) {
            for (int offset = 0; offset < height; offset += 2) {
                AxisAlignedBB bb = RotationUtil.mc.thePlayer.getEntityBoundingBox().offset(0.0d, -offset, 0.0d);
                if (!RotationUtil.mc.theWorld.getCollidingBoundingBoxes(RotationUtil.mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        for (int offset2 = 0; offset2 < height; offset2++) {
            if (blockRelativeToPlayer(0.0d, -offset2, 0.0d).isFullBlock()) {
                return true;
            }
        }
        return false;
    }

    public static int findTool(BlockPos blockPos) {
        float bestSpeed = 1.0f;
        int bestSlot = -1;
        IBlockState blockState = mc.theWorld.getBlockState(blockPos);
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null) {
                float speed = itemStack.getStrVsBlock(blockState.getBlock());
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }
        }
        return bestSlot;
    }

    public static boolean isInTeam(Entity entity) {
        if (mc.thePlayer.getDisplayName() != null && entity.getDisplayName() != null) {
            String targetName = entity.getDisplayName().getFormattedText().replace("§r", "");
            String clientName = mc.thePlayer.getDisplayName().getFormattedText().replace("§r", "");
            return targetName.startsWith("§" + clientName.charAt(1));
        }
        return false;
    }

    public static int potionRanking(int id) {
        return GOOD_POTIONS.getOrDefault(id, -1);
    }

    public static Vec3 getPredictedPos(float forward, float strafe) {
        float friction;
        float strafe2 = strafe * 0.98f;
        float forward2 = forward * 0.98f;
        float f4 = 0.91f;
        double motionX = mc.thePlayer.motionX;
        double motionZ = mc.thePlayer.motionZ;
        double motionY = mc.thePlayer.motionY;
        boolean isSprinting = mc.thePlayer.isSprinting();
        if (mc.thePlayer.isJumping && mc.thePlayer.onGround) {
            motionY = mc.thePlayer.getJumpUpwardsMotion();
            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                motionY += (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
            }
            if (isSprinting) {
                float f = mc.thePlayer.rotationYaw * 0.017453292f;
                motionX -= MathHelper.sin(f) * 0.2f;
                motionZ += MathHelper.cos(f) * 0.2f;
            }
        }
        if (mc.thePlayer.onGround) {
            f4 = mc.thePlayer.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ))).getBlock().slipperiness * 0.91f;
        }
        float f3 = 0.16277136f / ((f4 * f4) * f4);
        if (mc.thePlayer.onGround) {
            friction = mc.thePlayer.getAIMoveSpeed() * f3;
            if (mc.thePlayer == Mine.getMinecraft().thePlayer && mc.thePlayer.isSprinting()) {
                friction = 0.12999998f;
            }
        } else {
            friction = mc.thePlayer.jumpMovementFactor;
        }
        float f2 = (strafe2 * strafe2) + (forward2 * forward2);
        if (f2 >= 1.0E-4f) {
            float f5 = MathHelper.sqrt_float(f2);
            if (f5 < 1.0f) {
                f5 = 1.0f;
            }
            float f6 = friction / f5;
            float strafe3 = strafe2 * f6;
            float forward3 = forward2 * f6;
            float f1 = MathHelper.sin((mc.thePlayer.rotationYaw * 3.1415927f) / 180.0f);
            float f22 = MathHelper.cos((mc.thePlayer.rotationYaw * 3.1415927f) / 180.0f);
            motionX += (strafe3 * f22) - (forward3 * f1);
            motionZ += (forward3 * f22) + (strafe3 * f1);
        }
        float f42 = 0.91f;
        if (mc.thePlayer.onGround) {
            f42 = mc.thePlayer.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ))).getBlock().slipperiness * 0.91f;
        }
        return new Vec3(motionX * f42, motionY * 0.9800000190734863d, motionZ * f42);
    }

    public static Block blockRelativeToPlayer(double d, double d2, double d3) {
        return block(mc.thePlayer.posX + d, mc.thePlayer.posY + d2, mc.thePlayer.posZ + d3);
    }

    public static Block block(double d, double d2, double d3) {
        return mc.theWorld.getBlockState(new BlockPos(d, d2, d3)).getBlock();
    }

    public static boolean isMob(Entity entity) {
        return (entity instanceof EntityMob) || (entity instanceof EntityVillager) || (entity instanceof EntitySlime) || (entity instanceof EntityGhast) || (entity instanceof EntityDragon);
    }

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static boolean isAnimal(Entity entity) {
        return (entity instanceof EntityAnimal) || (entity instanceof EntitySquid) || (entity instanceof EntityGolem) || (entity instanceof EntityBat);
    }
}
