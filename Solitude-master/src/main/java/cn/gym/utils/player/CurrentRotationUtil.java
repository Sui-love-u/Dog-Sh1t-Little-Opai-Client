package cn.gym.utils.player;

import cn.gym.utils.Instance;
import cn.gym.utils.math.Vector2f;
import net.minecraft.network.play.client.C03PacketPlayer;

public class CurrentRotationUtil implements Instance {
    public static Vector2f currentRotation = new Vector2f(0, 0);
    public static void getCurrentRotation(C03PacketPlayer rotationPacket) {
        if (rotationPacket.rotating) {
            currentRotation = new Vector2f(rotationPacket.getYaw(), rotationPacket.getPitch());
        }
    }
}
