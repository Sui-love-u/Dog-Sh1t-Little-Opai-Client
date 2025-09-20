package com.amaya.network;

import com.amaya.Amaya;
import com.amaya.module.impl.misc.IRC;
import com.amaya.network.packets.Packet;
import com.amaya.network.packets.PacketBuffer;
import com.amaya.network.packets.PacketManager;
import com.amaya.network.packets.PacketRegistry;
import com.amaya.network.packets.client.ClientGetRankPacket;
import com.amaya.network.packets.client.ClientHandshakePacket;
import com.amaya.network.packets.server.ServerHandshakePacket;
import com.amaya.utils.client.ChatUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.Socket;

/**
 * @author DiaoLing
 * @since 1/29/2024
 */
@Getter
@Setter
public class SocketClient {
    private Socket socket;
    private PacketBuffer packetBuffer;
    private PacketManager packetManager;
    private boolean isConnected = false;

    public void connect(String host, int port) throws IOException {
        try {
            socket = new Socket(host, port);
            isConnected = true;
            if (Amaya.Instance.getModuleManager().getModule(IRC.class).debug.getValue()) {
                ChatUtil.display("IRC", "Connected to server");
            }

            packetBuffer = new PacketBuffer(socket.getInputStream(), socket.getOutputStream());
            packetManager = new PacketManager();
            PacketRegistry packetRegistry = new PacketRegistry(packetManager);
            packetRegistry.register();

            process();
        } catch (IOException e) {
                if (Amaya.Instance.getModuleManager().getModule(IRC.class).debug.getValue()) {
                ChatUtil.display("IRC", "Failed to connect to the server: " + e.getMessage());
            }
        }finally {
            reconnect();
        }
    }
    private void reconnect() {
            if (Amaya.Instance.getModuleManager().getModule(IRC.class).debug.getValue()) {
            ChatUtil.display("IRC", "Attempting to reconnect...");
        }
        try {
            connect("222.187.227.134", 1667);
        } catch (IOException e) {
                if (Amaya.Instance.getModuleManager().getModule(IRC.class).debug.getValue()) {
                ChatUtil.display("IRC", "Reconnect failed: " + e.getMessage());
            }
        }
    }


    public void process() {
        try {
            packetManager.sendPacket(packetBuffer, new ClientHandshakePacket(1), 1);
            while (!socket.isClosed()) {
                if (!socket.isConnected() || socket.isInputShutdown()) {
                    break;
                }
                Packet packet = packetManager.processPacket(packetBuffer);
                if (packet == null) {
                    continue;
                }
                if (packet instanceof ServerHandshakePacket) {
                    if (((ServerHandshakePacket) packet).getStatus() == 1) {
                        ClientGetRankPacket getRankPacket = new ClientGetRankPacket(Amaya.Instance.userManager.getUser().getUsername());
                        packetManager.sendPacket(packetBuffer, getRankPacket, 2);
                    }
                }
            }
        } catch (IOException e) {
//            Client.logger.info("IO Exception occurred: " + e.getMessage());
                if (Amaya.Instance.getModuleManager().getModule(IRC.class).debug.getValue()) {
            ChatUtil.display("IRC", "Left the server due to IO Exception.");
            }
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        // 关闭socket和packetBuffer
        try {
            if (socket != null) {
                socket.close();
            }
            if (packetBuffer != null) {
                packetBuffer.close();
            }
        } catch (IOException e) {
//            Client.logger.info("Failed to close resources: " + e.getMessage());
        }
    }
    public void close() {
        try {
            if (packetBuffer != null) {
                packetBuffer.close();
            }
            if (socket != null) {
                socket.close();
                isConnected = false;
            }
        } catch (IOException e) {
                if (Amaya.Instance.getModuleManager().getModule(IRC.class).debug.getValue()) {
                ChatUtil.display("IRC", "Error closing client resources: " + e.getMessage());
            }
            Amaya.Instance.getModuleManager().getModule(IRC.class).setState(false);
        }
    }
}
