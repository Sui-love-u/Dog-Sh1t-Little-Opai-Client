package com.amaya.utils.client;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static com.amaya.module.Module.mc;

public class ChatUtil {
    public static void display(boolean prefix, String message) {
        if (mc.thePlayer != null) {
            if (prefix) message = EnumChatFormatting.GREEN + "[IRC] " + message;
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }
    public static void display(String prefix, String message) {
        if (mc.thePlayer != null) {
            message = EnumChatFormatting.GREEN + "[" + prefix + "]" + message;
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }
}
