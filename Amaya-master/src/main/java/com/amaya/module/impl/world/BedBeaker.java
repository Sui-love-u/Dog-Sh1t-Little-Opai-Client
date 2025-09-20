package com.amaya.module.impl.world;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.combat.KillAura;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.math.Vector2f;
import com.amaya.utils.misc.BlockUtil;
import com.amaya.utils.player.Rise.MovementFix;
import com.amaya.component.RotationComponent;
import com.amaya.utils.player.Rise.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "BedBeaker",category = Category.World)
public class BedBeaker extends com.amaya.module.Module {
    public BooleanSetting auto = new BooleanSetting("Auto", false);
    public BooleanSetting silent = new BooleanSetting("Silent", false);
    public NumberSetting range = new NumberSetting("Range", 6.0, 1.0, 6.0, 0.1);
    private BlockPos bedPos;

    @Override
    public void onEnable() {
        bedPos = null;
    }

    @Override
    public void onDisable() {
        bedPos = null;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (isGapple()) return;
        for (int i = -3; i < 3; i++) {
            for (int i2 = -12; i2 < 12; i2++) {
                final BlockPos playerPos = new BlockPos(mc.thePlayer);
                BlockPos[] blockPoses = new BlockPos[]{playerPos.add(i2, i, 7), playerPos.add(i2, i, -7), playerPos.add(7, i, i2), playerPos.add(-7, i, i2)};
                for (BlockPos blockPos : blockPoses) {
                    final IBlockState blockState = mc.theWorld.getBlockState(blockPos);
                    final Block block = blockState.getBlock();

                    if (block instanceof BlockBed) {
                        bedPos = blockPos;
                        System.out.println("BedBeaker: " + blockPos + " " + block.getUnlocalizedName());
                    }
                }
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (isGapple()) return;
        if (bedPos != null) {
            if (BlockUtil.isAirBlock(bedPos)) {
                bedPos = null;
                return;
            }
            if (KillAura.target != null) {
                return;
            }
            float[] rotation = RotationUtils.getBlockPosRotation(bedPos);
            if (silent.getValue()) {
                RotationComponent.setRotations(new Vector2f(rotation[0], rotation[1]), 360f, MovementFix.NORMAL);
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bedPos, EnumFacing.UP));
            } else {
                mc.thePlayer.rotationYaw = rotation[0];
                mc.thePlayer.rotationPitch = rotation[1];
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bedPos, EnumFacing.UP));
            }
        }
    }
}
