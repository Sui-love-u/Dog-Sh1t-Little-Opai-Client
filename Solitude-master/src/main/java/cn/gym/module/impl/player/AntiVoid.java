package cn.gym.module.impl.player;

import cn.gym.Solitude;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.packet.PacketSendEvent;
import cn.gym.events.impl.player.MotionEvent;
import cn.gym.events.impl.player.UpdateEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.misc.Disabler;
import cn.gym.module.impl.movement.Scaffold;
import cn.gym.utils.pack.BlinkComponent;
import cn.gym.utils.pack.PacketUtil;
import cn.gym.value.impl.ModeValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 15:56
 */
public class AntiVoid extends Module {
    public final ModeValue mode = new ModeValue("Mode", "Blink",new String[]{"Blink"});
    private double groundX = 0.0;
    private double groundY = 0.0;
    private double groundZ = 0.0;
    private boolean universalStarted = false;
    private boolean universalFlag = false;

    public AntiVoid() {
        super("AntiVoid", Category.Player);
    }

    @Override
    public void onEnable() {
        universalStarted = false;
    }

    @Override
    public void onDisable() {
        BlinkComponent.dispatch();
    }
    @EventTarget
    private void onMotion(MotionEvent event) {
        setSuffix(mode.get());
    }
    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (getModule(Disabler.class).disabled) return;
        if (Solitude.Instance.getModuleManager().getModule(Scaffold.class).getState()) {
            return;
        }
        switch (mode.get()) {
            case "Blink":
                if (universalStarted) {
                    if (mc.thePlayer.onGround || mc.thePlayer.fallDistance > 8f) {
                        BlinkComponent.dispatch();
                        universalStarted = false;
                        universalFlag = false;
                    } else if (mc.thePlayer.fallDistance > 6f && !universalFlag) {
                        universalFlag = true;
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(groundX, groundY + 1, groundZ, false));
                    }
                } else if (mc.thePlayer.fallDistance > 0f && !mc.thePlayer.onGround && mc.thePlayer.motionY < 0) {
                    if (isOverVoid()) {
                        universalStarted = true;
                        universalFlag = false;
                        BlinkComponent.blinking = true;
                        groundX = mc.thePlayer.posX;
                        groundY = mc.thePlayer.posY;
                        groundZ = mc.thePlayer.posZ;
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        if (getModule(Disabler.class).disabled) return;
        if (Solitude.Instance.getModuleManager().getModule(Scaffold.class).getState()) {
            return;
        }
        if (mode.is("Blink")) {
            S08PacketPlayerPosLook s08PacketPlayerPosLook = new S08PacketPlayerPosLook();
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                if (s08PacketPlayerPosLook.getX() == groundX && s08PacketPlayerPosLook.getY() == groundY && s08PacketPlayerPosLook.getZ() == groundZ) {
                    BlinkComponent.blinking = false;
                    mc.thePlayer.setPosition(groundX, groundY, groundZ);
                    universalFlag = false;
                    universalStarted = false;
                }
            }
        }
    }

    private boolean isOverVoid() {
        return mc.theWorld.rayTraceBlocks(
                new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
                new Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 40, mc.thePlayer.posZ),
                true, true, false) == null;
    }
}
