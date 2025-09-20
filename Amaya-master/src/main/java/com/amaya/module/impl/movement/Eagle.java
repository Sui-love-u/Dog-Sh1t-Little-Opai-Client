package com.amaya.module.impl.movement;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.PreUpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

@ModuleInfo(name = "Eagle", category = Category.Movement)
public class Eagle extends Module {
    // 设置项
    public final BooleanSetting onSneak = new BooleanSetting("Only on Sneak", false);
    public final NumberSetting distance = new NumberSetting("Distance", 1.0, 0.1, 3.0, 0.1);
    public final BooleanSetting blocksOnly = new BooleanSetting("Blocks Only", true);
    public final BooleanSetting liquidsCheck = new BooleanSetting("Check Liquids", false);

    private boolean shouldEagle = false;

    @EventTarget
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        shouldEagle = checkEagleCondition();

        if (shouldEagle) {
            mc.gameSettings.keyBindSneak.pressed = true;
        } else if (!onSneak.get()) {
            mc.gameSettings.keyBindSneak.pressed = false;
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (shouldEagle) {
            event.setOnGround(true);
        }
    }

    private boolean checkEagleCondition() {
        if (onSneak.get() && !mc.thePlayer.isSneaking()) {
            return false;
        }

        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
        BlockPos blockBelow = playerPos.down();

        // 修改后的方块检测方式
        Block blockBelowBlock = mc.theWorld.getBlockState(blockBelow).getBlock();
        if (!(blockBelowBlock instanceof BlockAir)) {
            return false;
        }

        Vec3 lookVec = mc.thePlayer.getLookVec();
        Vec3 targetPos = new Vec3(
                mc.thePlayer.posX + lookVec.xCoord * distance.get().floatValue(),
                mc.thePlayer.posY - 0.5,
                mc.thePlayer.posZ + lookVec.zCoord * distance.get().floatValue()
        );

        BlockPos targetBlockPos = new BlockPos(targetPos.xCoord, targetPos.yCoord, targetPos.zCoord);
        Block targetBlock = mc.theWorld.getBlockState(targetBlockPos).getBlock();

        // 替换isBlockSolid的检测逻辑
        if (blocksOnly.get()) {
            // 使用更简单的固体方块检测
            if (targetBlock instanceof BlockAir ||
                    targetBlock instanceof BlockLiquid ||
                    !targetBlock.isFullBlock()) {
                return false;
            }
        } else {
            if (targetBlock instanceof BlockAir) {
                return false;
            }
        }

        if (liquidsCheck.get() && targetBlock instanceof BlockLiquid) {
            return false;
        }

        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    @Override
    public void onDisable() {
        if (!onSneak.get()) {
            mc.gameSettings.keyBindSneak.pressed = false;
        }
        shouldEagle = false;
    }
}