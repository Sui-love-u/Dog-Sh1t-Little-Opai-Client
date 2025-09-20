package cn.gym.utils.client;

import net.minecraft.client.Minecraft;

/**
 * @Author: Guyuemang
 * 2025/5/25
 */
public class ChatUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendMessage(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(message));
        }
    }
}
