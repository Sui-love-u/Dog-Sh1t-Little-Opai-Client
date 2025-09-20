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
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientGetRankPacket implements Packet {
    private String username;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.username = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(username);
    }
}