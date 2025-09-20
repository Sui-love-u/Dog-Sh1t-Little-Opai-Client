package qwq.arcane.utils.pack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import qwq.arcane.event.annotations.EventPriority;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.time.TimerUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/pack/BlinkComponent.class */
public class BlinkComponent {
    public static boolean blinking;
    public static boolean dispatch;
    public static final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public static ArrayList<Class<?>> exemptedPackets = new ArrayList<>();
    public static TimerUtil exemptionWatch = new TimerUtil();

    public static void setExempt(Class<?>... packets2) {
        exemptedPackets = new ArrayList<>(Arrays.asList(packets2));
        exemptionWatch.reset();
    }

    @EventPriority(-1)
    @EventTarget
    public void onPacketSend(PacketSendEvent event) {
        if (Instance.mc.thePlayer == null) {
            packets.clear();
            exemptedPackets.clear();
            return;
        }
        if (Instance.mc.thePlayer.isDead || Instance.mc.isSingleplayer() || !Instance.mc.getNetHandler().doneLoadingTerrain) {
            packets.forEach(PacketUtil::sendPacketNoEvent);
            packets.clear();
            blinking = false;
            exemptedPackets.clear();
            return;
        }
        Packet<?> packet = event.getPacket();
        if ((packet instanceof C00Handshake) || (packet instanceof C00PacketLoginStart) || (packet instanceof C00PacketServerQuery) || (packet instanceof C01PacketPing) || (packet instanceof C01PacketEncryptionResponse)) {
            return;
        }
        if (blinking && !dispatch) {
            if (exemptionWatch.hasTimeElapsed(100L)) {
                exemptionWatch.reset();
                exemptedPackets.clear();
            }
            if (!event.isCancelled() && exemptedPackets.stream().noneMatch(packetClass -> {
                return packetClass == packet.getClass();
            })) {
                packets.add(packet);
                event.setCancelled(true);
                return;
            }
            return;
        }
        if (packet instanceof C03PacketPlayer) {
            packets.forEach(PacketUtil::sendPacketNoEvent);
            packets.clear();
            dispatch = false;
        }
    }

    public static void dispatch() {
        dispatch = true;
    }

    @EventPriority(-1)
    @EventTarget
    public void onWorld(WorldLoadEvent event) {
        packets.clear();
        blinking = false;
    }
}
