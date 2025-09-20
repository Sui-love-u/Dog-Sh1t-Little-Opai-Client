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
@PacketID(2)
public class ServerRankPacket implements Packet {
    private String rank;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.rank = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(rank);
    }
}