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
package cn.gym.module.impl.player;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.packet.PacketSendEvent;
import cn.gym.events.impl.player.TeleportEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.misc.Disabler;
import cn.gym.value.impl.ModeValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoRotate extends Module {
    public final ModeValue mode = new ModeValue("Mode", "Edit", new String[]{"Edit","Packet"});
    private float yaw, pitch;
    private boolean teleport;

    public NoRotate() {
        super("NoRotate",Category.Player);
    }

    @EventTarget
    public void onTeleport(TeleportEvent event) {
        if (getModule(Disabler.class).testTicks == -1) {

            switch (mode.get()) {
                case "Packet":
                    event.setYaw(mc.thePlayer.rotationYaw);
                    event.setPitch(mc.thePlayer.rotationPitch);
                    break;
                case "Edit":
                    this.yaw = event.getYaw();
                    this.pitch = event.getPitch();

                    event.setYaw(mc.thePlayer.rotationYaw);
                    event.setPitch(mc.thePlayer.rotationPitch);

                    this.teleport = true;
                    break;
            }
        }
    }

    @EventTarget
    private void onPacket(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();

        if (mode.is("Edit") && this.teleport && packet instanceof C03PacketPlayer.C06PacketPlayerPosLook c06PacketPlayerPosLook) {

            c06PacketPlayerPosLook.yaw = this.yaw;
            c06PacketPlayerPosLook.pitch = this.pitch;

            event.setPacket(c06PacketPlayerPosLook);

            this.teleport = false;
        }
    }
}
