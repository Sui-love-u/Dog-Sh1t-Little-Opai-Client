package qwq.arcane.utils.pack;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.exception.CancelException;
import java.util.Arrays;
import net.minecraft.network.Packet;
import qwq.arcane.utils.Instance;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/pack/PacketUtil.class */
public class PacketUtil implements Instance {
    public static void sendPacket(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void queue(Packet packet) {
        if (packet == null) {
            System.out.println("Packet is null");
        } else if (isClientPacket(packet)) {
            mc.getNetHandler().addToSendQueueUnregistered(packet);
        } else {
            packet.processPacket(mc.getNetHandler().getNetworkManager().getNetHandler());
        }
    }

    public static boolean isCPacket(Packet<?> packet) {
        return packet.getClass().getSimpleName().startsWith("C");
    }

    public static boolean isClientPacket(Packet<?> packet) {
        return Arrays.stream(NetworkAPI.serverbound).anyMatch(clazz -> {
            return clazz == packet.getClass();
        });
    }

    public static void sendPacketNoEvent(Packet packet) {
        mc.getNetHandler().addToSendQueueUnregistered(packet);
    }
}
