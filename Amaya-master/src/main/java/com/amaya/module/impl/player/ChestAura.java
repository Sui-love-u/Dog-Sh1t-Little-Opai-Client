package com.amaya.module.impl.player;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.PlaceEvent;
import com.amaya.events.impl.player.PreUpdateEvent;
import com.amaya.events.impl.render.Render3DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.combat.KillAura;
import com.amaya.module.impl.world.Scaffold;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.math.Vector2f;
import com.amaya.utils.pack.PacketUtil;
import com.amaya.utils.player.Rise.MovementFix;
import com.amaya.component.RotationComponent;
import com.amaya.utils.player.Rise.RotationUtils;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.time.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "ChestAura", category = Category.Player)
public class ChestAura extends Module {
    private final NumberSetting range = new NumberSetting("Range", 4.0, 1.0, 15.0, 0.1);
    public TimerUtil waitBoxOpenTimer = new TimerUtil();
    public TimerUtil delayAfterOpenTimer = new TimerUtil(); // 新增计时器，用于控制打开箱子后的延迟
    public static boolean isWaitingOpen = false;
    private BlockPos globalPos;
    public static List<BlockPos> list = new ArrayList<BlockPos>();

    @Override
    public void onEnable() {
        list.clear();
    }

    @Override
    public void onDisable() {
        list.clear();
    }

    @EventTarget
    private void onPre(PreUpdateEvent event) {
        if (isGapple()) return;
        GuiScreen guiScreen = mc.currentScreen;

        if (mc.thePlayer.isOnLadder()) {
            return;
        }
        if (guiScreen instanceof GuiChest) {
            int a = 66;
        } else {
            if (KillAura.target != null || mc.thePlayer.isUsingItem() || Amaya.Instance.moduleManager.getModule(Scaffold.class).getState() || Amaya.Instance.moduleManager.getModule(Blink.class).getState()) {
                return;
            }

            // 检查是否在延迟时间内，如果是则不进行查找
            if (!delayAfterOpenTimer.hasTimeElapsed(500)) {
                return;
            }

            this.globalPos = null;
            if (mc.thePlayer.ticksExisted % 20 == 0 || KillAura.target != null || mc.currentScreen instanceof GuiContainer || mc.thePlayer.isUsingItem()) {
                return;
            }

            float radius = ((Double) range.getValue()).floatValue();
            for (float y = radius; y >= -radius; y -= 1.0f) {
                for (float x = -radius; x <= radius; x += 1.0f) {
                    for (float z = -radius; z <= radius; z += 1.0f) {
                        BlockPos pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                        Block block = mc.theWorld.getBlockState(pos).getBlock();
                        if (mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ()) < mc.playerController.getBlockReachDistance()
                                && (block instanceof BlockChest || block instanceof BlockFurnace || block instanceof BlockBrewingStand)
                                && !list.contains(pos)) {
                            float[] rotations = RotationUtils.getBlockRotations(pos.getX(), pos.getY(), pos.getZ());
                            RotationComponent.setRotations(new Vector2f(rotations[0], rotations[1]), 360.0f, MovementFix.NORMAL);
                            C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(pos, 1, mc.thePlayer.getCurrentEquippedItem(), 0.0f, 0.0f, 0.0f);
                            mc.thePlayer.sendQueue.addToSendQueue(packet);

                            PacketUtil.send(new C0APacketAnimation());
                            this.globalPos = pos;
                            delayAfterOpenTimer.reset(); // 打开箱子后重置延迟计时器
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onPostMotion(MotionEvent event) {
        if (isGapple()) return;
        if (event.isPost())return;
        GuiScreen guiScreen = mc.currentScreen;

        if (mc.thePlayer.isOnLadder()) {
            return;
        }
        if (guiScreen instanceof GuiChest) {
            int a = 66;
        } else {
            if (!event.isPost()) {
                return;
            }
            if (KillAura.target != null || mc.thePlayer.isUsingItem() || Amaya.Instance.moduleManager.getModule(Scaffold.class).getState() || Amaya.Instance.moduleManager.getModule(Blink.class).getState()) {
                return;
            }
            if (isWaitingOpen) {
                if (waitBoxOpenTimer.hasTimeElapsed(600.0)) {
                    isWaitingOpen = false;
                } else if (globalPos != null && mc.thePlayer.openContainer instanceof ContainerChest) {
                    list.add(globalPos);
                    globalPos = null;
                    isWaitingOpen = false;
                }
            }
        }
    }

    Entity entity;

    @EventTarget
    public void onPlace(PlaceEvent event) {
        if (KillAura.target != null) return;
        if (isGapple()) return;
        GuiScreen guiScreen = mc.currentScreen;
        if (mc.thePlayer.isOnLadder()) {
            return;
        }

        if (guiScreen instanceof GuiChest) {
            int a = 66;
        } else {
            if (!Amaya.Instance.moduleManager.getModule(ChestStealer.class).getState()) {
                return;
            }
            if (Amaya.Instance.moduleManager.getModule(Scaffold.class).getState()) {
                return;
            }
            if (globalPos != null && !(mc.currentScreen instanceof GuiContainer) && list.size() < 50 && !isWaitingOpen && !list.contains(globalPos)) {

                PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                event.setShouldRightClick(false);
                list.add(globalPos);
            }
        }
    }

    @EventTarget
    public void onRender(Render3DEvent event) {
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityBrewingStand || tileEntity instanceof TileEntityFurnace) {
                Color color = list.contains(tileEntity.getPos()) ? new Color(255, 0, 0, 60) : new Color(25, 255, 0, 120);
                if (mc.thePlayer.getDistance(tileEntity.getPos()) < 20.0) {
                    RenderUtil.drawBlockBox(tileEntity.getPos(), color, false);
                }
            }
        }
    }

    @EventTarget
    public void onWorld(WorldEvent e2) {
        list.clear();
    }
}
