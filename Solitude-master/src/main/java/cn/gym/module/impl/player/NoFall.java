package cn.gym.module.impl.player;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.player.MotionEvent;
import cn.gym.events.impl.render.Render2DEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.combat.KillAura;
import cn.gym.module.impl.movement.Scaffold;
import cn.gym.utils.pack.BlinkComponent;
import cn.gym.utils.player.MovementUtils;
import cn.gym.utils.player.PlayerUtil;
import cn.gym.value.impl.ModeValue;
import cn.gym.value.impl.NumberValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 15:56
 */
public class NoFall extends Module {
    public final ModeValue mode = new ModeValue("Mode", "NoGround", new String[]{"NoGround", "Blink", "Extra"});
    public final NumberValue minDistance = new NumberValue("Min Distance", () -> !mode.is("NoGround"), 3, 0, 8, 1);
    private boolean blinked = false;
    private boolean prevOnGround = false;
    private double fallDistance = 0;
    private boolean timed = false;

    public NoFall() {
        super("NoFall",Category.Player);
    }

    @Override
    public void onEnable() {
        if (PlayerUtil.nullCheck())
            this.fallDistance = mc.thePlayer.fallDistance;
    }

    @Override
    public void onDisable() {
        if (blinked) {
            BlinkComponent.dispatch();
            blinked = false;
        }
        mc.timer.timerSpeed = 1f;
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setSuffix(mode.get());
        if (KillAura.target != null) return;
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
                BlinkComponent.dispatch();
                blinked = false;
            }
            return;
        }

        switch (mode.get()) {
            case "NoGround":
                event.setOnGround(false);
                break;

            case "Extra":
                if (fallDistance >= minDistance.get() && !isEnabled(Scaffold.class)) {
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
                        BlinkComponent.dispatch();
                        blinked = false;
                    }

                    this.prevOnGround = true;
                } else if (this.prevOnGround) {
                    if (shouldBlink()) {
                        if (!BlinkComponent.blinking)
                            BlinkComponent.blinking = true;
                        blinked = true;
                    }

                    prevOnGround = false;
                } else if (PlayerUtil.isBlockUnder() && BlinkComponent.blinking && (this.fallDistance - mc.thePlayer.motionY) >= minDistance.get()) {
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
                mc.fontRendererObj.drawStringWithShadow("Blinking: " + BlinkComponent.packets.size(), (float) sr.getScaledWidth() / 2.0F - (float) mc.fontRendererObj.getStringWidth("Blinking: " + BlinkComponent.packets.size()) / 2.0F, (float) sr.getScaledHeight() / 2.0F + 13.0F, -1);
        }
    }

    private boolean isVoid() {
        return PlayerUtil.overVoid(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    private boolean shouldBlink() {
        return !mc.thePlayer.onGround && !PlayerUtil.isBlockUnder((int) Math.floor(minDistance.get())) && PlayerUtil.isBlockUnder() && !getModule(Scaffold.class).isEnabled();
    }
}
