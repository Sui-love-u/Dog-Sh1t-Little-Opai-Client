package com.amaya.module.impl.player;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.manager.BlinkManager;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.world.Scaffold;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.utils.pack.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "AntiVoid", category = Category.Player)
public class AntiVoid extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", "Blink",new String[]{"Blink"});
    private double groundX = 0.0;
    private double groundY = 0.0;
    private double groundZ = 0.0;
    private boolean universalStarted = false;
    private boolean universalFlag = false;

    @Override
    public void onEnable() {
        universalStarted = false;
    }

    @Override
    public void onDisable() {
        BlinkManager.dispatch();
    }
    @EventTarget
    private void onMotion(MotionEvent event) {
        setSuffix(mode.get());
    }
    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (Amaya.Instance.moduleManager.getModule(Scaffold.class).getState()) {
            return;
        }
        switch (mode.get()) {
            case "Blink":
                if (universalStarted) {
                    if (mc.thePlayer.onGround || mc.thePlayer.fallDistance > 8f) {
                        BlinkManager.dispatch();
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
                        BlinkManager.blinking = true;
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
        if (Amaya.Instance.moduleManager.getModule(Scaffold.class).getState()) {
            return;
        }
        if (mode.is("Blink")) {
            S08PacketPlayerPosLook s08PacketPlayerPosLook = new S08PacketPlayerPosLook();
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                if (s08PacketPlayerPosLook.getX() == groundX && s08PacketPlayerPosLook.getY() == groundY && s08PacketPlayerPosLook.getZ() == groundZ) {
                    BlinkManager.blinking = false;
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
