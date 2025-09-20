package com.amaya.network.packets.server.handlers;

import com.amaya.module.impl.misc.IRC;
import com.amaya.network.packets.PacketHandler;
import com.amaya.network.packets.server.ServerHandshakePacket;
import com.amaya.utils.client.ChatUtil;

/**
 * @author IDeal_Studio
 * @since 8/8/2024
 */
public class HandshakePacketHandler implements PacketHandler<ServerHandshakePacket> {

    @Override
    public void handle(ServerHandshakePacket packet) {
        if (packet.getStatus() == 1) {
            ChatUtil.display(IRC.debug.getValue(),"Handshake successful: " + packet.getMessage());
        } else {
            ChatUtil.display(IRC.debug.getValue(),"Handshake failed: " + packet.getMessage());
        }
    }
}
