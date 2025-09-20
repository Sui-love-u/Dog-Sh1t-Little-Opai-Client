/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package cn.gym.utils.pack;

import cn.gym.utils.Instance;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 14:18
 */
public class PacketUtil implements Instance {
    public static void sendPacket(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }
    public static void sendC0F(){
        PacketUtil.sendPacket(new C0FPacketConfirmTransaction(114514, (short) 191981000, true));
    }
    public static void sendPacketNoEvent(Packet<?> packet) {
        mc.getNetHandler().sendPacketNoEvent(packet);
    }

}
