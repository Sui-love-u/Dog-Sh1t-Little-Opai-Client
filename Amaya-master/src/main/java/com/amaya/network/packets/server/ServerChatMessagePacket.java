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
@PacketID(3)
public class ServerChatMessagePacket implements Packet {
    private String message;
    private String username;
    private String rank;
    private long timestamp;
    private boolean isSystemMessage;

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.message = buffer.readString();
        this.username = buffer.readString();
        this.rank = buffer.readString();
        this.timestamp = buffer.readLong();
        this.isSystemMessage = buffer.readBoolean();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(message);
        buffer.writeString(username);
        buffer.writeString(rank);
        buffer.writeLong(timestamp);
        buffer.writeBoolean(isSystemMessage);
    }
}
