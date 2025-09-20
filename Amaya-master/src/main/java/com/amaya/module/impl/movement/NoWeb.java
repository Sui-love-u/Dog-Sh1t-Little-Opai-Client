package com.amaya.module.impl.movement;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.PreUpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.utils.misc.BlockUtil;
import com.amaya.utils.pack.PacketUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "NoWeb", category = Category.Movement)
public class NoWeb extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla",new String[]{"Vanilla","Grim","Intave","AAC","LowAAC","Rewind"});
    private List<BlockPos> pos = new ArrayList<>();
    private boolean pass = true;
    @EventTarget
    public void onMotion(MotionEvent e) {
        setSuffix(mode.get());
    }
    @EventTarget
    public void onUpdate(PreUpdateEvent event) {
        if (mc.thePlayer.isInWeb) {
            mc.thePlayer.isInWeb = false;
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        pos.clear();
    }

    @EventTarget
    public void onTick(TickEvent event) {
        switch (this.mode.getValue()) {
            case "Vanilla": {
                mc.thePlayer.isInWeb = false;
                break;
            }
            case "Grim": {
                for (int i = -2; i <= 2; ++i) {
                    for (int j = -2; j < 2; ++j) {
                        for (int k = -2; k < 2; ++k) {
                            BlockPos pos = mc.thePlayer.getPosition().add(i, j, k);
                            if (mc.theWorld.getBlockState(pos) == null || !(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockWeb) || this.pos.contains(pos))
                                continue;
                            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                            mc.theWorld.setBlockToAir(pos);
                            this.pass = true;
                        }
                    }
                }
                break;
            }
            case "Intave": {
                Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (!(NoWeb.mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb)) continue;
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block.getKey(), NoWeb.mc.objectMouseOver.sideHit));
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), NoWeb.mc.objectMouseOver.sideHit));
                }
                NoWeb.mc.thePlayer.isInWeb = false;
                break;
            }
            case "AAC": {
                Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (!(NoWeb.mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb)) continue;
                    NoWeb.mc.thePlayer.jumpMovementFactor = 0.59f;
                    if (NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) break;
                    NoWeb.mc.thePlayer.motionY = 0.0;
                }
                break;
            }
            case "LowAAC": {
                Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (!(NoWeb.mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb)) continue;
                    if (!NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        NoWeb.mc.thePlayer.motionY = 0.0;
                    }
                    if (!NoWeb.mc.thePlayer.onGround) break;
                    NoWeb.mc.thePlayer.jump();
                }
                break;
            }
            case "Rewind": {
                Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (!(NoWeb.mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb)) continue;
                    NoWeb.mc.thePlayer.jumpMovementFactor = 0.42f;
                    if (!NoWeb.mc.thePlayer.onGround) break;
                    NoWeb.mc.thePlayer.jump();
                }
                break;
            }
        }
    }
}
