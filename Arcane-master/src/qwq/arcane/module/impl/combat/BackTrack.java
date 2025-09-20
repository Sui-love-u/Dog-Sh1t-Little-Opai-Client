package qwq.arcane.module.impl.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.CancellableEvent;
import qwq.arcane.event.impl.events.packet.PacketReceiveEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/combat/BackTrack.class */
public class BackTrack extends Module {
    public BoolValue legitProperty;
    public BoolValue releaseOnHitProperty;
    public NumberValue hitRangeProperty;
    public BoolValue onlyIfNeedProperty;
    public double lastRealX;
    public double lastRealY;
    public double lastRealZ;
    private WorldClient lastWorld;
    private EntityLivingBase entity;
    public TimerUtil timer;
    private final Map<UUID, Deque<Vec3>> backtrackPositions;
    public static NumberValue delayProperty = new NumberValue("Delayed Position Time", 400.0d, 0.0d, 1000.0d, 10.0d);
    public static final ArrayList<Packet> incomingPackets = new ArrayList<>();
    public static final ArrayList<Packet> outgoingPackets = new ArrayList<>();

    public BackTrack() {
        super("BackTrack", Category.Combat);
        this.legitProperty = new BoolValue("Legit", false);
        BoolValue boolValue = this.legitProperty;
        Objects.requireNonNull(boolValue);
        this.releaseOnHitProperty = new BoolValue("Release Upon Hit", boolValue::isAvailable, true);
        this.hitRangeProperty = new NumberValue("Hit Range", 3.0d, 0.0d, 10.0d, 0.1d);
        this.onlyIfNeedProperty = new BoolValue("Only If Needed", true);
        this.timer = new TimerUtil();
        this.backtrackPositions = new HashMap();
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        incomingPackets.clear();
        outgoingPackets.clear();
    }

    @EventTarget
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (mc.thePlayer == null || mc.theWorld == null || !((KillAura) Client.INSTANCE.getModuleManager().getModule(KillAura.class)).isEnabled() || mc.getNetHandler().getNetworkManager().getNetHandler() == null) {
            incomingPackets.clear();
            return;
        }
        if (((Scaffold) Client.INSTANCE.getModuleManager().getModule(Scaffold.class)).isEnabled()) {
            incomingPackets.clear();
            return;
        }
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            incomingPackets.clear();
            return;
        }
        this.entity = KillAura.target;
        if (e.getPacket() instanceof S14PacketEntity) {
            S14PacketEntity packet = (S14PacketEntity) e.getPacket();
            Entity packetEntity = mc.theWorld.getEntityByID(packet.entityId);
            if (packetEntity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) packetEntity;
                entityLivingBase.realPosX += packet.func_149062_c();
                entityLivingBase.realPosY += packet.func_149061_d();
                entityLivingBase.realPosZ += packet.func_149064_e();
            }
        }
        if (e.getPacket() instanceof S18PacketEntityTeleport) {
            S18PacketEntityTeleport packet2 = (S18PacketEntityTeleport) e.getPacket();
            Entity packetEntity2 = mc.theWorld.getEntityByID(packet2.getEntityId());
            if (packetEntity2 instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase2 = (EntityLivingBase) packetEntity2;
                entityLivingBase2.realPosX = packet2.getX();
                entityLivingBase2.realPosY = packet2.getY();
                entityLivingBase2.realPosZ = packet2.getZ();
            }
        }
        if (mc.theWorld != null && this.lastWorld != mc.theWorld) {
            resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
            this.lastWorld = mc.theWorld;
        } else if (this.entity == null || (this.onlyIfNeedProperty.getValue().booleanValue() && mc.thePlayer.getDistanceToEntity(this.entity) < 3.0f)) {
            resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
        } else {
            addIncomingPackets(e.getPacket(), e);
        }
    }

    @EventTarget
    public void onPacketSendEvent(PacketSendEvent e) {
        if (mc.thePlayer == null || mc.theWorld == null || !((KillAura) Client.INSTANCE.getModuleManager().getModule(KillAura.class)).isEnabled() || mc.getNetHandler().getNetworkManager().getNetHandler() == null) {
            outgoingPackets.clear();
            return;
        }
        if (((Scaffold) Client.INSTANCE.getModuleManager().getModule(Scaffold.class)).isEnabled()) {
            outgoingPackets.clear();
            return;
        }
        this.entity = KillAura.target;
        if (mc.theWorld != null && this.lastWorld != mc.theWorld) {
            resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
            this.lastWorld = mc.theWorld;
        } else if (this.entity == null || (this.onlyIfNeedProperty.getValue().booleanValue() && mc.thePlayer.getDistanceToEntity(this.entity) < 3.0f)) {
            resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
        } else {
            addOutgoingPackets(e.getPacket(), e);
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setsuffix(delayProperty.get().toString());
        if (this.entity != null && this.entity.getEntityBoundingBox() != null && mc.thePlayer != null && mc.theWorld != null && this.entity.realPosX != 0.0d && this.entity.realPosY != 0.0d && this.entity.realPosZ != 0.0d && this.entity.width != 0.0f && this.entity.height != 0.0f && this.entity.posX != 0.0d && this.entity.posY != 0.0d && this.entity.posZ != 0.0d) {
            double realX = this.entity.realPosX / 32.0d;
            double realY = this.entity.realPosY / 32.0d;
            double realZ = this.entity.realPosZ / 32.0d;
            if (this.onlyIfNeedProperty.getValue().booleanValue()) {
                if (mc.thePlayer.getDistance(this.entity.posX, this.entity.posY, this.entity.posZ) >= mc.thePlayer.getDistance(realX, realY, realZ) || mc.thePlayer.getDistance(realX, realY, realZ) < mc.thePlayer.getDistance(this.lastRealX, this.lastRealY, this.lastRealZ)) {
                    resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                    resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                }
            } else if (mc.thePlayer.getDistanceToEntity(this.entity) > 3.0f && mc.thePlayer.getDistance(this.entity.posX, this.entity.posY, this.entity.posZ) >= mc.thePlayer.getDistance(realX, realY, realZ)) {
                resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
            }
            if (this.legitProperty.getValue().booleanValue() && this.releaseOnHitProperty.getValue().booleanValue() && this.entity.hurtTime <= 1) {
                resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
            }
            if (mc.thePlayer.getDistance(realX, realY, realZ) > this.hitRangeProperty.getValue().doubleValue() || this.timer.hasTimeElapsed(delayProperty.getValue().intValue(), true)) {
                resetIncomingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                resetOutgoingPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
            }
            this.lastRealX = realX;
            this.lastRealY = realY;
            this.lastRealZ = realZ;
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (this.entity == null || this.entity.getEntityBoundingBox() == null || mc.thePlayer == null || mc.theWorld == null || this.entity.realPosX == 0.0d || this.entity.realPosY == 0.0d || this.entity.realPosZ == 0.0d || this.entity.width == 0.0f || this.entity.height == 0.0f || this.entity.posX == 0.0d || this.entity.posY == 0.0d || this.entity.posZ == 0.0d) {
            return;
        }
        boolean render = true;
        double realX = this.entity.realPosX / 32.0d;
        double realY = this.entity.realPosY / 32.0d;
        double realZ = this.entity.realPosZ / 32.0d;
        if (this.onlyIfNeedProperty.getValue().booleanValue()) {
            if (mc.thePlayer.getDistance(this.entity.posX, this.entity.posY, this.entity.posZ) >= mc.thePlayer.getDistance(realX, realY, realZ) || mc.thePlayer.getDistance(realX, realY, realZ) < mc.thePlayer.getDistance(this.lastRealX, this.lastRealY, this.lastRealZ)) {
                render = false;
            }
        } else if (mc.thePlayer.getDistanceToEntity(this.entity) > 3.0f && mc.thePlayer.getDistance(this.entity.posX, this.entity.posY, this.entity.posZ) >= mc.thePlayer.getDistance(realX, realY, realZ)) {
            render = false;
        }
        if (this.legitProperty.getValue().booleanValue() && this.releaseOnHitProperty.getValue().booleanValue() && this.entity.hurtTime <= 1) {
            render = false;
        }
        if (mc.thePlayer.getDistance(realX, realY, realZ) > this.hitRangeProperty.getValue().doubleValue() || this.timer.hasTimeElapsed(delayProperty.getValue().intValue(), false)) {
            render = false;
        }
        if (this.entity == null || this.entity == mc.thePlayer || this.entity.isInvisible() || !render || this.entity.width == 0.0f || this.entity.height == 0.0f) {
            return;
        }
        Color color = Color.WHITE;
        double x = (this.entity.realPosX / 32.0d) - RenderManager.renderPosX;
        double y = (this.entity.realPosY / 32.0d) - RenderManager.renderPosY;
        double z = (this.entity.realPosZ / 32.0d) - RenderManager.renderPosZ;
        GlStateManager.pushMatrix();
        RenderUtil.start3D();
        RenderUtil.renderBoundingBox(new AxisAlignedBB(x - (this.entity.width / 2.0f), y, z - (this.entity.width / 2.0f), x + (this.entity.width / 2.0f), y + this.entity.height, z + (this.entity.width / 2.0f)), color, 145);
        RenderUtil.stop3D();
        GlStateManager.popMatrix();
    }

    private void resetIncomingPackets(INetHandler netHandler) {
        if (!incomingPackets.isEmpty()) {
            while (!incomingPackets.isEmpty()) {
                Packet packet = incomingPackets.get(0);
                try {
                    packet.processPacket(netHandler);
                } catch (ThreadQuickExitException e) {
                }
                incomingPackets.remove(0);
            }
        }
        this.timer.reset();
    }

    private void addIncomingPackets(Packet packet, CancellableEvent event) {
        if (event != null && packet != null) {
            synchronized (incomingPackets) {
                if (blockPacketIncoming(packet)) {
                    incomingPackets.add(packet);
                    event.setCancelled(true);
                }
            }
        }
    }

    private void resetOutgoingPackets(INetHandler netHandler) {
        if (!outgoingPackets.isEmpty()) {
            while (!outgoingPackets.isEmpty()) {
                Packet packet = outgoingPackets.get(0);
                try {
                    packet.processPacket(netHandler);
                } catch (ThreadQuickExitException e) {
                }
                outgoingPackets.remove(0);
            }
        }
        this.timer.reset();
    }

    private void addOutgoingPackets(Packet packet, CancellableEvent event) {
        if (event != null && packet != null) {
            synchronized (outgoingPackets) {
                if (blockPacketsOutgoing(packet)) {
                    outgoingPackets.add(packet);
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isEntityPacket(Packet packet) {
        return (packet instanceof S14PacketEntity) || (packet instanceof S19PacketEntityHeadLook) || (packet instanceof S18PacketEntityTeleport) || (packet instanceof S0FPacketSpawnMob);
    }

    private boolean blockPacketIncoming(Packet packet) {
        return (packet instanceof S03PacketTimeUpdate) || (packet instanceof S00PacketKeepAlive) || (packet instanceof S12PacketEntityVelocity) || (packet instanceof S27PacketExplosion) || (packet instanceof S32PacketConfirmTransaction) || (packet instanceof S08PacketPlayerPosLook) || (packet instanceof S01PacketPong) || isEntityPacket(packet);
    }

    private boolean blockPacketsOutgoing(Packet packet) {
        if (this.legitProperty.getValue().booleanValue()) {
            return (packet instanceof C03PacketPlayer) || (packet instanceof C02PacketUseEntity) || (packet instanceof C0FPacketConfirmTransaction) || (packet instanceof C08PacketPlayerBlockPlacement) || (packet instanceof C09PacketHeldItemChange) || (packet instanceof C07PacketPlayerDigging) || (packet instanceof C0APacketAnimation) || (packet instanceof C01PacketPing) || (packet instanceof C00PacketKeepAlive) || (packet instanceof C0BPacketEntityAction);
        }
        return false;
    }
}
