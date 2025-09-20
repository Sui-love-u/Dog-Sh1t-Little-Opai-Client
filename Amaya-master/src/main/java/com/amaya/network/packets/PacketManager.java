package com.amaya.network.packets;

import lombok.Getter;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author IDeal_Studio
 * @since 8/8/2024
 */
@Getter
public class PacketManager {
    Map<Integer, Supplier<Packet>> serverPacketRegistry = new HashMap<>();
    Map<Class<? extends Packet>, PacketHandler<?>> packetHandlers = new HashMap<>();

    public Packet processPacket(PacketBuffer buffer) throws IOException {
        try {
            int packetId = buffer.readInt();
            Supplier<Packet> packetSupplier = serverPacketRegistry.get(packetId);

            if (packetSupplier == null) {
                throw new IOException("Unregistered packet ID: " + packetId);
            }

            Packet packet = packetSupplier.get();
            packet.read(buffer);

            @SuppressWarnings("unchecked")
            PacketHandler<Packet> handler = (PacketHandler<Packet>) packetHandlers.get(packet.getClass());

            if (handler != null) {
                handler.handle(packet);
            } else {
                throw new IOException("No handler registered for packet: " + packet.getClass().getName());
            }

            return packet;
        } catch (EOFException e) {
            throw new IOException("Reached end of stream unexpectedly. Connection may have been lost.", e);
        } catch (SocketException se) {
            throw new IOException("Socket exception, connection might be lost", se);
        }
    }

    public void sendPacket(PacketBuffer buffer, Packet packet, int packetId) throws IOException {
        buffer.writeInt(packetId);
        packet.write(buffer);
        buffer.flush();
    }

    public void sendPacket(PacketBuffer buffer, Packet packet) throws IOException {
        Integer packetId = getPacketID(packet.getClass());
        if (packetId == null) {
            throw new IOException("PacketID not found for packet: " + packet.getClass().getName());
        }
        sendPacket(buffer, packet, packetId);
    }

    private Integer getPacketID(Class<? extends Packet> packetClass) {
        PacketID annotation = packetClass.getAnnotation(PacketID.class);
        return (annotation != null) ? annotation.value() : null;
    }

    public void registerServerPacket(int packetId, Supplier<Packet> packetSupplier) {
        serverPacketRegistry.put(packetId, packetSupplier);
    }

    public <T extends Packet> void registerPacketHandler(Class<T> packetClass, PacketHandler<T> handler) {
        packetHandlers.put(packetClass, handler);
    }
}