package qwq.arcane.utils.chats;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import qwq.arcane.module.Mine;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/chats/ChatUtils.class */
public class ChatUtils {
    private static final Mine mc = Mine.getMinecraft();

    public static void sendMessage(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[Arcane] " + EnumChatFormatting.RESET + message));
        }
    }
}
