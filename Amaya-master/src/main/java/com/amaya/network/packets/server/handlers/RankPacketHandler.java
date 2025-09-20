package com.amaya.network.packets.server.handlers;

import com.amaya.Amaya;
import com.amaya.module.impl.misc.IRC;
import com.amaya.network.packets.PacketHandler;
import com.amaya.network.packets.server.ServerRankPacket;
import com.amaya.utils.client.ChatUtil;

/**
 * @author IDeal_Studio
 * @since 8/8/2024
 */
public class RankPacketHandler implements PacketHandler<ServerRankPacket> {
    @Override
    public void handle(ServerRankPacket packet) {
        ChatUtil.display(IRC.debug.getValue(),"Received rank: " + packet.getRank());
        Amaya.Instance.userManager.update(packet.getRank());
    }
}