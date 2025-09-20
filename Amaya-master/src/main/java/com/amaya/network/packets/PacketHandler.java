package com.amaya.network.packets;

/**
 * @author IDeal_Studio
 * @since 8/8/2024
 */
public interface PacketHandler<T extends Packet> {
    void handle(T packet);
}
