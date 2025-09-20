package qwq.arcane.module.impl.misc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketReceiveEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/misc/ClientSpoofer.class */
public class ClientSpoofer extends Module {
    public final ModeValue mode;

    public ClientSpoofer() {
        super("ClientSpoofer", Category.Misc);
        this.mode = new ModeValue("Mode", "Lunar", new String[]{"Lunar", "Feather"});
    }

    @EventTarget
    public void onPacket(PacketReceiveEvent packetEvent) {
        String str;
        setsuffix(this.mode.getValue());
        Packet<?> packet = packetEvent.getPacket();
        if (packet instanceof C17PacketCustomPayload) {
            C17PacketCustomPayload packet2 = (C17PacketCustomPayload) packet;
            switch (this.mode.getValue()) {
                case "Lunar":
                    str = "lunarclient:v2.14.5-2411";
                    break;
                case "Feather":
                    str = "Feather Forge";
                    break;
                default:
                    str = "";
                    break;
            }
            String data = str;
            ByteBuf byteBuf = Unpooled.wrappedBuffer(data.getBytes());
            PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(byteBuf));
            packet2.setData(buffer);
        }
    }
}
