package qwq.arcane.module.impl.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.TickEvent;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.packet.HigherPacketEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/player/Blink.class */
public class Blink extends Module {
    private final LinkedList<List<Packet<?>>> packets;
    public EntityOtherPlayerMP fakePlayer;
    public int ticks;
    public ModeValue modeValue;
    public final NumberValue Release_Value;
    public BoolValue AutoClose;
    public BoolValue auraValue;
    public BoolValue render;
    public ModeValue renderMode;

    public Blink() {
        super("Blink", Category.Player);
        this.packets = new LinkedList<>();
        this.modeValue = new ModeValue("Mode", "Slow Release", new String[]{"Simple", "Slow Release", "Delay"});
        this.Release_Value = new NumberValue("Delay", () -> {
            return this.modeValue.is("Slow Release");
        }, 100.0d, 50.0d, 200.0d, 1.0d);
        this.AutoClose = new BoolValue("AutoClose", false);
        this.auraValue = new BoolValue("Aura Support", true);
        this.render = new BoolValue("Render", true);
        this.renderMode = new ModeValue("RenderMode", () -> {
            return this.render.getValue().booleanValue();
        }, "Naven", new String[]{"Naven"});
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        if (mc.thePlayer == null) {
            return;
        }
        this.packets.clear();
        this.packets.add(new ArrayList());
        this.ticks = 0;
        this.fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        this.fakePlayer.clonePlayer(mc.thePlayer, true);
        this.fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        this.fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        this.fakePlayer.noClip = true;
        mc.theWorld.addEntityToWorld(-1337, this.fakePlayer);
    }

    @EventTarget
    public void onWorld(WorldLoadEvent event) {
        setState(false);
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        this.packets.forEach(this::sendTick);
        this.packets.clear();
        try {
            if (this.fakePlayer != null) {
                mc.theWorld.removeEntity(this.fakePlayer);
            }
        } catch (Exception e) {
        }
    }

    @EventTarget
    public void onPacket(HigherPacketEvent event) {
        Packet packet = event.getPacket();
        if (PacketUtil.isCPacket(packet)) {
            mc.addScheduledTask(() -> {
                return Boolean.valueOf(this.packets.getLast().add(packet));
            });
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        this.ticks++;
        this.packets.add(new ArrayList());
        switch (this.modeValue.getValue()) {
            case "Delay":
                if (this.packets.size() > 100) {
                    poll();
                    break;
                }
                break;
            case "Slow Release":
                if (this.packets.size() > this.Release_Value.getValue().doubleValue() || this.ticks % 5 == 0) {
                    poll();
                    break;
                }
                break;
        }
    }

    private void poll() {
        if (this.packets.isEmpty()) {
            return;
        }
        sendTick(this.packets.getFirst());
        this.packets.removeFirst();
    }

    @Override // qwq.arcane.module.Module
    public String getSuffix() {
        return this.modeValue.getValue();
    }

    private void sendTick(List<Packet<?>> tick) {
        tick.forEach(packet -> {
            mc.getNetHandler().getNetworkManager().sendUnregisteredPacket(packet);
            handleFakePlayerPacket(packet);
        });
    }

    private void handleFakePlayerPacket(Packet<?> packet) {
        if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
            C03PacketPlayer.C04PacketPlayerPosition position = (C03PacketPlayer.C04PacketPlayerPosition) packet;
            this.fakePlayer.setPositionAndRotation2(position.x, position.y, position.z, this.fakePlayer.rotationYaw, this.fakePlayer.rotationPitch, 3, true);
            this.fakePlayer.onGround = position.isOnGround();
            return;
        }
        if (packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
            C03PacketPlayer.C05PacketPlayerLook rotation = (C03PacketPlayer.C05PacketPlayerLook) packet;
            this.fakePlayer.setPositionAndRotation2(this.fakePlayer.posX, this.fakePlayer.posY, this.fakePlayer.posZ, rotation.getYaw(), rotation.getPitch(), 3, true);
            this.fakePlayer.onGround = rotation.isOnGround();
            this.fakePlayer.rotationYawHead = rotation.getYaw();
            this.fakePlayer.rotationYaw = rotation.getYaw();
            this.fakePlayer.rotationPitch = rotation.getPitch();
            return;
        }
        if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            C03PacketPlayer.C06PacketPlayerPosLook positionRotation = (C03PacketPlayer.C06PacketPlayerPosLook) packet;
            this.fakePlayer.setPositionAndRotation2(positionRotation.x, positionRotation.y, positionRotation.z, positionRotation.getYaw(), positionRotation.getPitch(), 3, true);
            this.fakePlayer.onGround = positionRotation.isOnGround();
            this.fakePlayer.rotationYawHead = positionRotation.getYaw();
            this.fakePlayer.rotationYaw = positionRotation.getYaw();
            this.fakePlayer.rotationPitch = positionRotation.getPitch();
            return;
        }
        if (!(packet instanceof C0BPacketEntityAction)) {
            if (packet instanceof C0APacketAnimation) {
                this.fakePlayer.swingItem();
                return;
            }
            return;
        }
        C0BPacketEntityAction action = (C0BPacketEntityAction) packet;
        if (action.getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
            this.fakePlayer.setSprinting(true);
            return;
        }
        if (action.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
            this.fakePlayer.setSprinting(false);
        } else if (action.getAction() == C0BPacketEntityAction.Action.START_SNEAKING) {
            this.fakePlayer.setSneaking(true);
        } else if (action.getAction() == C0BPacketEntityAction.Action.STOP_SNEAKING) {
            this.fakePlayer.setSneaking(false);
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setsuffix(this.modeValue.get());
        if (mc.thePlayer.hurtTime > 0 && this.AutoClose.getValue().booleanValue()) {
            setState(false);
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (this.render.getValue().booleanValue()) {
            switch (this.renderMode.getValue()) {
                case "Naven":
                    ScaledResolution scaledResolution = new ScaledResolution(mc);
                    int screenWidth = scaledResolution.getScaledWidth();
                    int screenHeight = scaledResolution.getScaledHeight();
                    float progress = this.ticks < 100 ? (float) (this.ticks * 0.8d) : 80.0f;
                    int x = (int) ((screenWidth / 2.0f) - (80.0f / 2.0f));
                    int y = (screenHeight / 2) - 15;
                    RoundedUtil.drawRound(x, y, 80.0f, 3.0f, 2.0f, new Color(0, 0, 0, 150));
                    RoundedUtil.drawRound(x, y, progress, 3.0f, 2.0f, new Color(143, 49, 46, 220));
                    break;
            }
        }
    }
}
