package cn.gym.module.impl.combat;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.packet.PacketReceiveEvent;
import cn.gym.events.impl.packet.PacketSendEvent;
import cn.gym.events.impl.player.UpdateEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.value.impl.ModeValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 14:18
 */
public class Velocity extends Module {
    private final ModeValue mode = new ModeValue("Mode","Watchdog", new String[]{"Watchdog"});
    private boolean absorbedVelocity;

    public Velocity() {
        super("Velocity",Category.Combat);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setSuffix(mode.get());

        switch (mode.get()) {
            case "Watchdog":
                if (mc.thePlayer.onGround) {
                    absorbedVelocity = false;
                }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S12PacketEntityVelocity s12 && s12.getEntityID() == mc.thePlayer.getEntityId()) {
            switch (mode.get()) {
                case "Watchdog":
                    if (!mc.thePlayer.onGround) {
                        if (!absorbedVelocity) {
                            event.setCancelled(true);
                            absorbedVelocity = true;
                            return;
                        }
                    }
                    s12.motionX = (int) (mc.thePlayer.motionX * 8000);
                    s12.motionZ = (int) (mc.thePlayer.motionZ * 8000);
                    break;
            }
        }
    }
}
