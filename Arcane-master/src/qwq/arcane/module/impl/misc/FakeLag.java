package qwq.arcane.module.impl.misc;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.TickEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.combat.KillAura;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.rotation.RotationUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/misc/FakeLag.class */
public class FakeLag extends Module {
    public final BoolValue combat;
    public final BoolValue onlyMove;
    private final NumberValue startDelay;
    private final NumberValue lagDuration;
    public int sentC03Packets;
    private boolean shouldBlockPackets;
    private final TimerUtil delayTimer;
    private final ArrayList<Packet<?>> packets;

    public FakeLag() {
        super("FakeLag", Category.Misc);
        this.combat = new BoolValue("Combat", false);
        this.onlyMove = new BoolValue("Only Move", false);
        this.startDelay = new NumberValue("Start Delay", 300.0d, 0.0d, 1000.0d, 1.0d);
        this.lagDuration = new NumberValue("Lag Packets", 600.0d, 0.0d, 1000.0d, 1.0d);
        this.sentC03Packets = 0;
        this.delayTimer = new TimerUtil();
        this.packets = new ArrayList<>();
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.shouldBlockPackets = false;
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        resetPackets();
    }

    @EventTarget
    public void onTick(TickEvent event) {
        int count = 0;
        Iterator<Packet<?>> it = this.packets.iterator();
        while (it.hasNext()) {
            Packet<?> p = it.next();
            if (p instanceof C03PacketPlayer) {
                count++;
            }
        }
        this.sentC03Packets = count;
        if (this.combat.get().booleanValue()) {
            if (count > this.lagDuration.getValue().doubleValue() || ((Scaffold) getModule(Scaffold.class)).isEnabled()) {
                this.shouldBlockPackets = false;
            }
        } else if (count <= this.lagDuration.getValue().doubleValue() && !((Scaffold) getModule(Scaffold.class)).isEnabled()) {
            this.shouldBlockPackets = true;
        } else {
            this.shouldBlockPackets = false;
            resetPackets();
        }
        if (count <= this.lagDuration.getValue().doubleValue() && !((Scaffold) getModule(Scaffold.class)).isEnabled()) {
            if (!this.combat.get().booleanValue()) {
                this.shouldBlockPackets = true;
            }
        } else {
            this.shouldBlockPackets = false;
            resetPackets();
        }
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (this.combat.get().booleanValue()) {
            if (packet instanceof C02PacketUseEntity) {
                this.shouldBlockPackets = false;
                resetPackets();
            } else if ((packet instanceof C03PacketPlayer) && ((KillAura) getModule(KillAura.class)).isEnabled()) {
                if (KillAura.target != null) {
                    EntityLivingBase entityLivingBase = KillAura.target;
                    if (entityLivingBase instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) entityLivingBase;
                        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0f);
                        Vec3 positionEyesServer = mc.thePlayer.getSeverPosition().addVector(0.0d, mc.thePlayer.getEyeHeight(), 0.0d);
                        Vec3 bestHitVec = RotationUtil.getBestHitVec(player);
                        if (!this.shouldBlockPackets && player.hurtTime < 3 && positionEyes.distanceTo(bestHitVec) > 2.9d && positionEyes.distanceTo(bestHitVec) < 3.3d && positionEyes.distanceTo(bestHitVec) < positionEyesServer.distanceTo(bestHitVec)) {
                            this.shouldBlockPackets = true;
                        }
                    }
                }
            }
        }
        if (mc.theWorld != null && this.shouldBlockPackets && this.delayTimer.reached(this.startDelay.getValue().longValue())) {
            if (this.onlyMove.get().booleanValue()) {
                if ((packet instanceof C03PacketPlayer) && !this.packets.contains(packet)) {
                    this.packets.add(packet);
                    event.setCancelled(true);
                    return;
                }
                return;
            }
            if (!this.packets.contains(packet)) {
                this.packets.add(packet);
                event.setCancelled(true);
            }
        }
    }

    private void resetPackets() {
        if (mc.thePlayer != null) {
            if (!this.packets.isEmpty()) {
                this.packets.forEach(packet -> {
                    mc.thePlayer.sendQueue.addToSendQueueDirect(packet);
                });
                this.packets.clear();
                return;
            }
            return;
        }
        this.packets.clear();
    }
}
