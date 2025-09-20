package com.amaya.network.packets;




import com.amaya.network.packets.server.ServerChatMessagePacket;
import com.amaya.network.packets.server.ServerHandshakePacket;
import com.amaya.network.packets.server.ServerRankPacket;
import com.amaya.network.packets.server.handlers.ChatMessagePacketHandler;
import com.amaya.network.packets.server.handlers.HandshakePacketHandler;
import com.amaya.network.packets.server.handlers.RankPacketHandler;

import java.lang.reflect.InvocationTargetException;

/**
 * @author IDeal_Studio
 * @since 8/8/2024
 */
public class PacketRegistry {
    private final PacketManager packetManager;

    public PacketRegistry(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    public void register() {
        registerServerPackets();
        registerServerHandlers();
    }

    private void registerServerPackets() {
        /*packetManager.registerServerPacket(1, ServerHandshakePacket::new);
        packetManager.registerServerPacket(2, ServerRankPacket::new);
        packetManager.registerServerPacket(3, ServerChatMessagePacket::new);*/

        Class<?>[] packetClasses = {
                ServerHandshakePacket.class,
                ServerRankPacket.class,
                ServerChatMessagePacket.class,
        };

        for (Class<?> packetClass : packetClasses) {
            if (Packet.class.isAssignableFrom(packetClass)) {
                PacketID packetID = packetClass.getAnnotation(PacketID.class);
                if (packetID != null) {
                    packetManager.registerServerPacket(packetID.value(), () -> createPacketInstance((Class<? extends Packet>) packetClass));
                }
            }
        }
    }

    private Packet createPacketInstance(Class<? extends Packet> packetClass) {
        try {
            return packetClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error instantiating packet class: " + packetClass.getName(), e);
        }
    }

    private void registerServerHandlers() {
        packetManager.registerPacketHandler(ServerHandshakePacket.class, new HandshakePacketHandler());
        packetManager.registerPacketHandler(ServerRankPacket.class, new RankPacketHandler());
        packetManager.registerPacketHandler(ServerChatMessagePacket.class, new ChatMessagePacketHandler());
    }
}