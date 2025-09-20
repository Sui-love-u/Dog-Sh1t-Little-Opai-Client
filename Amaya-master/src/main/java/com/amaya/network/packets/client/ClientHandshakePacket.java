package com.amaya.network.packets.client;

import com.amaya.network.packets.Packet;
import com.amaya.network.packets.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

/**
 * @author IDeal_Studio
 * @since 8/8/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientHandshakePacket implements Packet {
    private int protocolVersion;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.protocolVersion = buffer.readInt();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(protocolVersion);
    }
}