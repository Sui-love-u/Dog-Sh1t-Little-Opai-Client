package qwq.arcane.event.impl.events.packet;

import net.minecraft.network.Packet;
import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/packet/HigherPacketEvent.class */
public class HigherPacketEvent extends CancellableEvent {
    private Packet<?> packet;

    public HigherPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }
}
