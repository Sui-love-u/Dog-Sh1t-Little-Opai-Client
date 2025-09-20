package com.amaya.module.impl.misc;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.ChatEvent;
import com.amaya.firend.FriendManager;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.network.SocketClient;
import com.amaya.network.packets.Packet;
import com.amaya.network.packets.client.ClientChatMessagePacket;
import com.amaya.network.user.User;
import com.amaya.utils.client.ChatUtil;

import java.io.IOException;

@ModuleInfo(name = "IRC",category = Category.Misc)
public class IRC extends Module {
    public IRC() {
        client = new SocketClient();
        setState(true);
    }
    public static BooleanSetting debug = new BooleanSetting("Debug", true);
    private String ircPrefix = "@";
    private String friendPrefix = "+";
    private SocketClient client;
    private Thread thread;

    @Override
    public void onEnable() {
        thread = new Thread(() -> {
            try {
                client.connect("222.187.227.134", 1667);
            } catch (IOException e) {
                ChatUtil.display("IRC", "Error connecting server: " + e.getMessage());
            }
        });
        thread.start();
    }

    @Override
    public void onDisable() {
        if (client != null) {
            client.close();
        }
    }

    @EventTarget
    public void onChatInput(ChatEvent event) {
        String message = event.getMessage();
        // 处理好友命令
        if (message.startsWith(friendPrefix)) {
            event.setCancelled(true);
            handleFriendCommand(message.substring(friendPrefix.length()).trim());
            return;
        }

        // 处理IRC消息
        if (message.startsWith(ircPrefix)) {
            event.setCancelled(true);
            String msg = message.substring(ircPrefix.length());
            User user = Amaya.Instance.userManager.getUser();
            sendPacket(new ClientChatMessagePacket(msg, user.getUsername(), user.getRank()));
        }
    }
    private void handleFriendCommand(String command) {
        String[] parts = command.split(" ");
        if (parts.length < 1) return;

        String action = parts[0].toLowerCase();
        if (parts.length < 2) {
            ChatUtil.display("IRC", "Usage: +add <username> or +remove <username>");
            return;
        }

        String username = parts[1];
        FriendManager fm = Amaya.Instance.userManager.getFriendManager();

        switch (action) {
            case "add":
                fm.addFriend(username);
                ChatUtil.display("IRC", "Added " + username + " to friends list");
                break;
            case "remove":
                fm.removeFriend(username);
                ChatUtil.display("IRC", "Removed " + username + " from friends list");
                break;
            default:
                ChatUtil.display("IRC", "Unknown friend command: " + action);
        }
    }

    public void sendPacket(Packet packet) {
        try {
            client.getPacketManager().sendPacket(client.getPacketBuffer(), packet, 3);
        } catch (IOException e) {
            ChatUtil.display("IRC", "Error sending packet: " + e.getMessage());
        }
    }
}
