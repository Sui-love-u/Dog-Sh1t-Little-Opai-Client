package com.amaya.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class ChatUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    public static void sendMessage(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(message));
        }
    }
}