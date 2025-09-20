package com.amaya.events.impl.packet;

import com.amaya.events.events.callables.EventCancellable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

/**
 * @Author: Guyuemang
 * 2025/5/11
 */

@Getter
@Setter
public class PacketReceiveSyncEvent extends EventCancellable {
    private Packet<?> packet;
    private NetworkManager networkManager;

    public PacketReceiveSyncEvent(Packet<?> packet) {
        this.packet = packet;
    }
}
