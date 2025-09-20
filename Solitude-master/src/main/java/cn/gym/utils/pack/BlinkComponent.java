/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package cn.gym.utils.pack;

import cn.gym.events.annotations.EventPriority;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.WorldEvent;
import cn.gym.events.impl.packet.PacketSendEvent;
import cn.gym.utils.Instance;
import cn.gym.utils.time.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class BlinkComponent implements Instance {
    public static final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public static boolean blinking;
    public static ArrayList<Class<?>> exemptedPackets = new ArrayList<>();
    public static TimerUtil exemptionWatch = new TimerUtil();

    public static void setExempt(Class<?>... packets) {
        exemptedPackets = new ArrayList<>(Arrays.asList(packets));
        exemptionWatch.reset();
    }

    @EventTarget
    @EventPriority(-1)
    public void onPacketSend(PacketSendEvent event) {
        if (mc.thePlayer == null) {
            packets.clear();
            exemptedPackets.clear();
            return;
        }

        if (mc.thePlayer.isDead || mc.isSingleplayer() || !mc.getNetHandler().doneLoadingTerrain) {
            packets.forEach(PacketUtil::sendPacketNoEvent);
            packets.clear();
            blinking = false;
            exemptedPackets.clear();
            return;
        }

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart ||
                packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing ||
                packet instanceof C01PacketEncryptionResponse || packet instanceof C00PacketKeepAlive) {
            return;
        }

        if (blinking) {
            if (!event.isCancelled() && exemptedPackets.stream().noneMatch(packetClass ->
                    packetClass == packet.getClass())) {
                packets.add(packet);
                event.setCancelled(true);
            }
        }
    }

    public static void release(boolean clear) {
        if(!packets.isEmpty()) {
            packets.forEach(mc.getNetHandler()::addToSendQueueUnregistered);
            if(clear) {
                packets.clear();
                exemptedPackets.clear();
            }
        }
    }

    public static void dispatch(boolean releasePackets) {
        if (releasePackets) {
            release(true);
        }
        blinking = false;
    }

    public static void dispatch() {
        dispatch(true);
    }

    @EventTarget
    @EventPriority(-1)
    public void onWorld(WorldEvent event) {
        packets.clear();
        BlinkComponent.blinking = false;
    }

}
