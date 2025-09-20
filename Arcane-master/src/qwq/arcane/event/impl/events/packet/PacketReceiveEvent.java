package qwq.arcane.event.impl.events.packet;

import net.minecraft.network.Packet;
import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/packet/PacketReceiveEvent.class */
public class PacketReceiveEvent extends CancellableEvent {
    private Packet<?> packet;

    public PacketReceiveEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }
}
