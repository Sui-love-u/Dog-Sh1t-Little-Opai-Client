package com.amaya.network.packets.server;

import com.amaya.network.packets.Packet;
import com.amaya.network.packets.PacketBuffer;
import com.amaya.network.packets.PacketID;
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
@PacketID(1)
public class ServerHandshakePacket implements Packet {
    private int status;
    private String message;

    public ServerHandshakePacket(int status) {
        this.status = status;
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.status = buffer.readInt();
        this.message = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(status);
        buffer.writeString(message);
    }
}