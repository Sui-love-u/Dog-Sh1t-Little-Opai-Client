package com.amaya.module.impl.world;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModuleInfo(name = "FastPlace", category = Category.World)
public class FastPlace extends Module {
    
    // 设置项
    public final NumberSetting delay = new NumberSetting("Delay", 0.0, 0.0, 4.0, 1.0);
    public final BooleanSetting blocksOnly = new BooleanSetting("Blocks Only", true);
    public final BooleanSetting checkAir = new BooleanSetting("Check Air", true);
    public final BooleanSetting rotate = new BooleanSetting("Rotate", false);
    
    private int placeDelay;
    
    @Override
    public void onEnable() {
        placeDelay = 0;
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.isPre()) {
            // 减少放置延迟计时器
            if (placeDelay > 0) {
                placeDelay--;
            }

            // 如果右键按下且可以放置
            if (mc.gameSettings.keyBindUseItem.isKeyDown() && canPlace()) {
                // 获取准星指向的方块
                MovingObjectPosition objectMouseOver = mc.objectMouseOver;

                if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    BlockPos blockPos = objectMouseOver.getBlockPos();
                    EnumFacing enumFacing = objectMouseOver.sideHit;

                    // 检查是否可以放置
                    if (canPlaceAt(blockPos, enumFacing)) {
                        // 如果需要旋转
                        if (rotate.get()) {
                            float[] rotations = PlayerUtil.getRotationsToBlock(blockPos, enumFacing);
                            event.setYaw(rotations[0]);
                            event.setPitch(rotations[1]);
                        }

                        // 放置方块
                        placeBlock(blockPos, enumFacing);
                    }
                }
            }
        }
    }
    
    // 检查是否可以放置
    private boolean canPlace() {
        EntityPlayerSP player = mc.thePlayer;
        
        // 检查延迟
        if (placeDelay > 0) {
            return false;
        }
        
        // 检查手中物品
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null) {
            return false;
        }
        
        // 如果只允许方块
        if (blocksOnly.get() && !(heldItem.getItem() instanceof net.minecraft.item.ItemBlock)) {
            return false;
        }
        
        return true;
    }
    
    // 检查是否可以在指定位置放置
    private boolean canPlaceAt(BlockPos blockPos, EnumFacing enumFacing) {
        // 检查空气
        if (checkAir.get() && !(mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockAir)) {
            return false;
        }
        
        // 检查距离
        double distance = mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        if (distance > 4.5) {
            return false;
        }
        
        return true;
    }
    
    // 放置方块
    private void placeBlock(BlockPos blockPos, EnumFacing enumFacing) {
        // 获取点击位置向量
        Vec3 hitVec = new Vec3(
            blockPos.getX() + 0.5 + enumFacing.getFrontOffsetX() * 0.5,
            blockPos.getY() + 0.5 + enumFacing.getFrontOffsetY() * 0.5,
            blockPos.getZ() + 0.5 + enumFacing.getFrontOffsetZ() * 0.5
        );
        
        // 右键点击放置方块
        mc.playerController.onPlayerRightClick(
            mc.thePlayer, 
            mc.theWorld, 
            mc.thePlayer.getHeldItem(), 
            blockPos, 
            enumFacing, 
            hitVec
        );
        
        // 播放动画
        mc.thePlayer.swingItem();
        
        // 重置延迟
        placeDelay = delay.get().intValue();
    }
}