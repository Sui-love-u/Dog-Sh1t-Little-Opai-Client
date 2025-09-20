package com.amaya.network.packets.server.handlers;


import com.amaya.module.impl.misc.IRC;
import com.amaya.network.packets.PacketHandler;
import com.amaya.network.packets.server.ServerChatMessagePacket;
import com.amaya.utils.client.ChatUtil;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author IDeal_Studio
 * @since 8/8/2024
 */
public class ChatMessagePacketHandler implements PacketHandler<ServerChatMessagePacket> {
    @Override
    public void handle(ServerChatMessagePacket packet) {
        String formattedMessage = buildMessage(packet.getRank(), packet.getUsername(), packet.getMessage());
        ChatUtil.display(IRC.debug.getValue(), formattedMessage);
    }

    private String buildMessage(String rank, String username, String message) {
        EnumChatFormatting rankColor = getRankColor(rank);
        return rankColor + "[" + rank + "] " + EnumChatFormatting.WHITE + username + EnumChatFormatting.GRAY + ": " + message;
    }

    private EnumChatFormatting getRankColor(String rank) {
        EnumChatFormatting enumChatFormatting;
        switch (rank.toLowerCase()) {
            case "owner":
                enumChatFormatting = EnumChatFormatting.DARK_RED;
                break;
            case "dev":
                enumChatFormatting = EnumChatFormatting.DARK_PURPLE;
                break;
            case "admin":
                enumChatFormatting = EnumChatFormatting.RED;
                break;
            case "user":
                enumChatFormatting = EnumChatFormatting.GREEN;
                break;
            default:
                enumChatFormatting = EnumChatFormatting.YELLOW;
                break;
        };
        return enumChatFormatting;
    }
}
