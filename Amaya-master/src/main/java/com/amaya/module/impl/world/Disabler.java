package com.amaya.module.impl.world;

import com.amaya.Amaya;
import com.amaya.events.EventManager;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.events.impl.packet.PacketSendHigherEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.PreUpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.combat.Gapple;
import com.amaya.module.impl.player.Blink;
import com.amaya.module.impl.player.Stuck;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.utils.math.MathUtils;
import com.amaya.utils.pack.PacketUtil;
import com.amaya.component.RotationComponent;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "Disabler", category = Category.World)
public class Disabler extends Module {
    private static final ModeSetting mode = new ModeSetting("Mode","GrimAC", new String[]{"GrimAC"});
    private final BooleanSetting badPacketsA = new BooleanSetting("BadPacketA", true);
    private final BooleanSetting badPacketsF = new BooleanSetting("BadPacketF", false);
    private final BooleanSetting test = new BooleanSetting("Test", false);
    private final BooleanSetting higherVersion = new BooleanSetting("Move 1.17+", false);
    private final BooleanSetting fastBreak = new BooleanSetting("Fast Break", false);
    private static boolean lastResult;
    int lastSlot;
    boolean lastSprinting , c03Check;
    private static boolean S08 = false;

    public static List<Packet<INetHandler>> storedPackets;
    public static ConcurrentLinkedDeque<Integer> pingPackets;
    @EventTarget
    public void onWorld(WorldEvent event){
        this.lastSlot = -1;
        this.lastSprinting = false;
        this.c03Check = false;
    }
    @EventTarget
    public void onUpdate(PreUpdateEvent event) {
        if (!getGrimPost()) {
            processPackets();
        }
    }
    @EventTarget
    public void onMotion(MotionEvent event) {
        setSuffix(mode.get());
    }
    @EventTarget(0)
    public void onSend(PacketSendEvent event){
        Packet<?> packet = event.getPacket();
        if (mode.is("GrimAC")) {
            if (badPacketsF.get() && packet instanceof C0BPacketEntityAction c0b) {
                if (c0b.getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                    if (this.lastSprinting) {
                        event.setCancelled(true);
                    }
                    this.lastSprinting = true;
                    c03Check = true;
                }
                if (c0b.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                    if (!this.lastSprinting) {
                        event.setCancelled(true);
                    }
                    this.lastSprinting = false;
                }
            }
            if (test.get() && event.getPacket() instanceof C0EPacketClickWindow pkt) {
                if (pkt.getWindowId() <= 0 || pkt.getSlotId() >= 100 || pkt.getUsedButton() < 0) {
                    event.setCancelled(true);
                }
            }
            if (higherVersion.get() && packet instanceof C03PacketPlayer wrapped && !(packet instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
                if (wrapped.isMoving()) {
                    mc.getNetHandler().addToSendQueueUnregistered(new C03PacketPlayer.C06PacketPlayerPosLook(
                            wrapped.getX(),
                            wrapped.getY(),
                            wrapped.getZ(),
                            RotationComponent.rotations.x,
                            RotationComponent.rotations.y,
                            wrapped.isOnGround()
                    ));
                } else if (wrapped.rotating) {
                    mc.getNetHandler().addToSendQueueUnregistered(new C03PacketPlayer.C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            wrapped.getYaw(),
                            wrapped.getPitch(),
                            wrapped.isOnGround()
                    ));
                } else {
                    mc.getNetHandler().addToSendQueueUnregistered(new C03PacketPlayer.C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            RotationComponent.rotations.x,
                            RotationComponent.rotations.y,
                            wrapped.isOnGround()
                    ));
                }
            }
            if (packet instanceof C09PacketHeldItemChange c09) {
                int slot = c09.getSlotId();
                if (slot == this.lastSlot && slot != -1) {
                    event.setCancelled(true);
                }
                this.lastSlot = c09.getSlotId();
            }
            if (packet instanceof C02PacketUseEntity c02) {
                if (c02.getAction() == C02PacketUseEntity.Action.INTERACT) {
                    if (c02.getEntityFromWorld(mc.theWorld) instanceof EntityPlayer) {
                        event.setCancelled(true);
                    }
                }
                if (c02.getAction() == C02PacketUseEntity.Action.INTERACT_AT) {
                    if (c02.getEntityFromWorld(mc.theWorld) instanceof EntityPlayer) {
                        event.setCancelled(true);
                    }
                }
            }


            if (fastBreak.get() && event.getPacket() instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)event.getPacket()).getStatus() == net.minecraft.network.play.client.C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                BlockPos blockPos = ((C07PacketPlayerDigging)event.getPacket()).getPosition();
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
            }

            Scaffold scaffold = Amaya.Instance.moduleManager.getModule(Scaffold.class);
            if (scaffold.getState() && packet instanceof C03PacketPlayer c03) {
                c03.setYaw(getRandomYaw(c03.getYaw()));
            }

        }
        if (this.S08) {
            Stuck.onS08();
            this.S08 = false;
        }
    }
    @EventTarget
    public void onHigher(PacketSendHigherEvent event) {
        if (badPacketsA.get()) {
            if (event.getPacket() instanceof C09PacketHeldItemChange c09) {
                int slot = c09.getSlotId();
                if (slot == this.lastSlot && slot != -1) {
                    event.setCancelled(true);
                }
                this.lastSlot = c09.getSlotId();
            }
        }
    }

    private float getRandomYaw(float requestedYaw){
        int rand = MathUtils.getRandomInRange(1,200);
        return requestedYaw + (360 * rand);
    }


    public static boolean getGrimPost() {
        Disabler dis = Amaya.Instance.moduleManager.getModule(Disabler.class);
        boolean result = mode.is("GrimAC") && dis.getState() && mc.thePlayer != null && !(mc.currentScreen instanceof GuiDownloadTerrain);
        if (lastResult && !result) {
            lastResult = false;
            mc.addScheduledTask(Disabler::processPackets);
        }
        return lastResult = result;
    }


    public static boolean grimPostDelay(final Packet<?> packet) {
        if (mc.thePlayer == null) {
            return false;
        }
        if (mc.currentScreen instanceof GuiDownloadTerrain) {
            return false;
        }
        if (packet instanceof S00PacketServerInfo) {
            return false;
        }
        if (packet instanceof S01PacketEncryptionRequest) {
            return false;
        }
        if (packet instanceof S38PacketPlayerListItem) {
            return false;
        }
        if (packet instanceof S00PacketDisconnect) {
            return false;
        }
        if (packet instanceof S40PacketDisconnect) {
            return false;
        }
        if (packet instanceof S21PacketChunkData) {
            return false;
        }
        if (packet instanceof S01PacketPong) {
            return false;
        }
        if (packet instanceof S44PacketWorldBorder) {
            return false;
        }
        if (packet instanceof S01PacketJoinGame) {
            return false;
        }
        if (packet instanceof S19PacketEntityHeadLook) {
            return false;
        }
        if (packet instanceof S3EPacketTeams) {
            return false;
        }
        if (packet instanceof S02PacketChat) {
            return false;
        }
        if (packet instanceof S2FPacketSetSlot) {
            return false;
        }
        if (packet instanceof S1CPacketEntityMetadata) {
            return false;
        }
        if (packet instanceof S20PacketEntityProperties) {
            return false;
        }
        if (packet instanceof S35PacketUpdateTileEntity) {
            return false;
        }
        if (packet instanceof S03PacketTimeUpdate) {
            return false;
        }
        if (packet instanceof S47PacketPlayerListHeaderFooter) {
            return false;
        }
        if (packet instanceof S12PacketEntityVelocity sPacketEntityVelocity) {
            return sPacketEntityVelocity.getEntityID() == mc.thePlayer.getEntityId();
        }
        return packet instanceof S27PacketExplosion || packet instanceof S32PacketConfirmTransaction || packet instanceof S08PacketPlayerPosLook || packet instanceof S18PacketEntityTeleport || packet instanceof S19PacketEntityStatus || packet instanceof S04PacketEntityEquipment || packet instanceof S23PacketBlockChange || packet instanceof S22PacketMultiBlockChange || packet instanceof S13PacketDestroyEntities || packet instanceof S00PacketKeepAlive || packet instanceof S06PacketUpdateHealth || packet instanceof S14PacketEntity || packet instanceof S0FPacketSpawnMob || packet instanceof S2DPacketOpenWindow || packet instanceof S30PacketWindowItems || packet instanceof S3FPacketCustomPayload || packet instanceof S2EPacketCloseWindow;
    }


    public static void processPackets() {
        if (!storedPackets.isEmpty()) {
            for (Packet<INetHandler> packet : storedPackets) {
                PacketReceiveEvent event = new PacketReceiveEvent(packet);
                EventManager.call(event);
                if (event.isCancelled()) {
                    continue;
                }
                if ((Packet<?>)packet instanceof S32PacketConfirmTransaction || ((Packet<?>)packet instanceof S12PacketEntityVelocity s12 && s12.getEntityID() == mc.thePlayer.getEntityId())) {
                    if (noS32andS12()) {
                        storedPackets.remove(packet);
                        continue;
                    }
                }
                if (mc.getNetHandler() != null) {
                    packet.processPacket(mc.getNetHandler());
                }
            }
            storedPackets.clear();
        }
    }
    private static boolean noS32andS12() {
        return Amaya.Instance.moduleManager.getModule(Gapple.class).getState() || Amaya.Instance.moduleManager.getModule(Blink.class).getState();
    }

    public static void fixC0F(C0FPacketConfirmTransaction packet) {
        int id = packet.getUid();
        if (id >= 0 || pingPackets.isEmpty()) {
            PacketUtil.sendPacketNoEvent(packet);
        }
        else {
            do {
                int current = pingPackets.getFirst();
                PacketUtil.sendPacketNoEvent(new C0FPacketConfirmTransaction(packet.getWindowId(), (short)current, true));
                pingPackets.pollFirst();
                if (current == id) {
                    break;
                }
            } while (!pingPackets.isEmpty());
        }
    }
    public static void onS08() {
        Disabler.S08 = true;
    }
    static {
        lastResult = false;
        storedPackets = new CopyOnWriteArrayList<>();
        pingPackets = new ConcurrentLinkedDeque<>();
    }
}
