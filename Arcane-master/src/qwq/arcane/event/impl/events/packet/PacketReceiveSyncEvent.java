package qwq.arcane.event.impl.events.packet;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/packet/PacketReceiveSyncEvent.class */
public class PacketReceiveSyncEvent extends CancellableEvent {
    private Packet<?> packet;
    private NetworkManager networkManager;

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public PacketReceiveSyncEvent(Packet<?> packet) {
        this.packet = packet;
    }
}
