package com.amaya.module.impl.player;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.pack.PacketUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "Blink", category = Category.Player)
public class Blink extends Module {
    private final LinkedList<List<Packet<?>>> packets = new LinkedList<>();
    public static EntityOtherPlayerMP fakePlayer;
    public int ticks;
    public ModeSetting modeValue = new ModeSetting("Mode", "Slow Release", new String[]{"Simple", "Slow Release", "Delay"});
    public final NumberSetting Release_Value = new NumberSetting("Delay",() -> modeValue.is("Slow Release"),100.0,50.0,200.0,1.0);
    public BooleanSetting AutoClose = new BooleanSetting("AutoClose", false);

    public static EntityOtherPlayerMP getFakePlayer() {
        return fakePlayer;
    }
    @Override
    public void onEnable() {
        if (Blink.mc.thePlayer == null) {
            return;
        }
        this.packets.clear();
        this.packets.add(new ArrayList<>());
        this.ticks = 0;
        this.fakePlayer = new EntityOtherPlayerMP(Blink.mc.theWorld, Blink.mc.thePlayer.getGameProfile());
        this.fakePlayer.clonePlayer(Blink.mc.thePlayer, true);
        this.fakePlayer.copyLocationAndAnglesFrom(Blink.mc.thePlayer);
        this.fakePlayer.rotationYawHead = Blink.mc.thePlayer.rotationYawHead;
        this.fakePlayer.noClip = true;//移除碰撞
        Blink.mc.theWorld.addEntityToWorld(-1337, this.fakePlayer);
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        this.setState(false);
    }

    @Override
    public void onDisable() {
        this.packets.forEach(this::sendTick);
        this.packets.clear();
        try {
            if (this.fakePlayer != null) {
                Blink.mc.theWorld.removeEntity(this.fakePlayer);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        Packet packet = event.getPacket();
        if (PacketUtil.isCPacket(packet)) {
            mc.addScheduledTask(() -> this.packets.getLast().add(packet));
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        ++this.ticks;
        this.packets.add(new ArrayList<>());
        switch (this.modeValue.getValue()) {
            case "Delay": {
                if (this.packets.size() <= 100) break;
                this.poll();
                break;
            }
            case "Slow Release": {
                if (this.packets.size() <= Release_Value.getValue() && this.ticks % 5 != 0) break;
                this.poll();
            }
        }
    }

    private void poll() {
        if (this.packets.isEmpty()) {
            return;
        }
        this.sendTick(this.packets.getFirst());
        this.packets.removeFirst();
    }

    private void sendTick(List<Packet<?>> tick) {
        tick.forEach(packet -> {
            mc.getNetHandler().getNetworkManager().sendPacketWithoutHigherPacket(packet);
            this.handleFakePlayerPacket(packet);
        });
    }

    private void handleFakePlayerPacket(Packet<?> packet) {
        if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
            C03PacketPlayer.C04PacketPlayerPosition position = (C03PacketPlayer.C04PacketPlayerPosition) packet;
            this.fakePlayer.setPositionAndRotation2(position.x, position.y, position.z, this.fakePlayer.rotationYaw, this.fakePlayer.rotationPitch, 3, true);
            this.fakePlayer.onGround = position.isOnGround();
        } else if (packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
            C03PacketPlayer.C05PacketPlayerLook rotation = (C03PacketPlayer.C05PacketPlayerLook) packet;
            this.fakePlayer.setPositionAndRotation2(this.fakePlayer.posX, this.fakePlayer.posY, this.fakePlayer.posZ, rotation.getYaw(), rotation.getPitch(), 3, true);
            this.fakePlayer.onGround = rotation.isOnGround();
            this.fakePlayer.rotationYawHead = rotation.getYaw();
            this.fakePlayer.rotationYaw = rotation.getYaw();
            this.fakePlayer.rotationPitch = rotation.getPitch();
        } else if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            C03PacketPlayer.C06PacketPlayerPosLook positionRotation = (C03PacketPlayer.C06PacketPlayerPosLook) packet;
            this.fakePlayer.setPositionAndRotation2(positionRotation.x, positionRotation.y, positionRotation.z, positionRotation.getYaw(), positionRotation.getPitch(), 3, true);
            this.fakePlayer.onGround = positionRotation.isOnGround();
            this.fakePlayer.rotationYawHead = positionRotation.getYaw();
            this.fakePlayer.rotationYaw = positionRotation.getYaw();
            this.fakePlayer.rotationPitch = positionRotation.getPitch();
        } else if (packet instanceof C0BPacketEntityAction) {
            C0BPacketEntityAction action = (C0BPacketEntityAction) packet;
            if (action.getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                this.fakePlayer.setSprinting(true);
            } else if (action.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                this.fakePlayer.setSprinting(false);
            } else if (action.getAction() == C0BPacketEntityAction.Action.START_SNEAKING) {
                this.fakePlayer.setSneaking(true);
            } else if (action.getAction() == C0BPacketEntityAction.Action.STOP_SNEAKING) {
                this.fakePlayer.setSneaking(false);
            }
        } else if (packet instanceof C0APacketAnimation) {
            this.fakePlayer.swingItem();
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.hurtTime > 0 && this.AutoClose.getValue()) {
            this.setState(false);
        }
    }
}
