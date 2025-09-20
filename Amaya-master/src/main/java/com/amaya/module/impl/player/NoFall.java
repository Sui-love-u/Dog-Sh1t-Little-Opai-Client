package com.amaya.module.impl.player;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.render.Render2DEvent;
import com.amaya.manager.BlinkManager;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.world.Scaffold;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.player.MovementUtils;
import com.amaya.utils.player.PlayerUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "NoFall", category = Category.Player)
public class NoFall extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", "NoGround",new String[]{"NoGround", "Blink", "Extra"});
    public final NumberSetting minDistance = new NumberSetting("Min Distance", 3, 0, 8, 1);
    private boolean blinked = false;
    private boolean prevOnGround = false;
    private double fallDistance = 0;
    private boolean timed = false;

    @Override
    public void onEnable() {
        if (PlayerUtil.nullCheck())
            this.fallDistance = mc.thePlayer.fallDistance;
    }

    @Override
    public void onDisable() {
        if (blinked) {
            BlinkManager.dispatch();
            blinked = false;
        }
        mc.timer.timerSpeed = 1f;
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
            setSuffix(mode.get());
        if (!PlayerUtil.nullCheck())
            return;

        if (event.isPost())
            return;

        if (mc.thePlayer.onGround)
            fallDistance = 0;
        else {
            fallDistance += (float) Math.max(mc.thePlayer.lastTickPosY - event.getY(), 0);

            fallDistance -= MovementUtils.predictedMotionY(mc.thePlayer.motionY, 1);
        }

        if (mc.thePlayer.capabilities.allowFlying) return;
        if (isVoid()) {
            if (blinked) {
                BlinkManager.dispatch();
                blinked = false;
            }
            return;
        }

        switch (mode.get()) {
            case "NoGround":
                event.setOnGround(false);
                break;

            case "Extra":
                if (fallDistance >= minDistance.get() && !Amaya.Instance.moduleManager.getModule(Scaffold.class).getState()) {
                    mc.timer.timerSpeed = (float) 0.5;
                    timed = true;
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                    fallDistance = 0;
                } else if (timed) {
                    mc.timer.timerSpeed = 1;
                    timed = false;
                }
                break;
            case "Blink":
                if (mc.thePlayer.onGround) {
                    if (blinked) {
                        BlinkManager.dispatch();
                        blinked = false;
                    }

                    this.prevOnGround = true;
                } else if (this.prevOnGround) {
                    if (shouldBlink()) {
                        if (!BlinkManager.blinking)
                            BlinkManager.blinking = true;
                        blinked = true;
                    }

                    prevOnGround = false;
                } else if (PlayerUtil.isBlockUnder() && BlinkManager.blinking && (this.fallDistance - mc.thePlayer.motionY) >= minDistance.get()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    this.fallDistance = 0.0F;
                }
                break;
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        if (mode.is("Blink")) {
            if (blinked)
                mc.fontRendererObj.drawStringWithShadow("Blinking: " + BlinkManager.packets.size(), (float) sr.getScaledWidth() / 2.0F - (float) mc.fontRendererObj.getStringWidth("Blinking: " + BlinkManager.packets.size()) / 2.0F, (float) sr.getScaledHeight() / 2.0F + 13.0F, -1);
        }
    }

    private boolean isVoid() {
        return PlayerUtil.overVoid(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    private boolean shouldBlink() {
        return !mc.thePlayer.onGround && !PlayerUtil.isBlockUnder((int) Math.floor(minDistance.get())) && PlayerUtil.isBlockUnder() && !Amaya.Instance.moduleManager.getModule(Scaffold.class).getState();
    }
}
