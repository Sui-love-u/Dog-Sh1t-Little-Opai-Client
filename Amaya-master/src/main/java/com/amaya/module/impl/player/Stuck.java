package com.amaya.module.impl.player;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.utils.math.Rotation;
import com.amaya.utils.math.Vector2f;
import com.amaya.utils.pack.PacketUtil;
import com.amaya.utils.player.Rise.PlayerUtil;
import com.amaya.utils.player.Rise.RotationUtils;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.*;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "Stuck", category = Category.Player)
public class Stuck extends Module {
    public final BooleanSetting autoDisable = new BooleanSetting("AutoDisable", true);
    public final BooleanSetting rotationFirst = new BooleanSetting("Rotation First", true);
    private static Stuck INSTANCE;

    private boolean delayingC0F = false;

    public Stuck() {
        INSTANCE = this;
    }

    private double x, y, z, motionX, motionY, motionZ;
    private boolean enableAgain = false;
    private int count = 0, rotationCount = 0;
    private boolean onGround = false;
    public boolean thrown = false;
    private Rotation rotation;
    private boolean closing = false;
    private static boolean needS08 = false;

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;
        onGround = mc.thePlayer.onGround;
        x = mc.thePlayer.posX;
        y = mc.thePlayer.posY;
        z = mc.thePlayer.posZ;
        motionX = mc.thePlayer.motionX;
        motionY = mc.thePlayer.motionY;
        motionZ = mc.thePlayer.motionZ;
        rotation = RotationUtils.getPlayerRotation();
        rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
        delayingC0F = true;
        thrown = false;
        count = 20;
        rotationCount = 0;
        enableAgain = false;
        needS08 = false;
        return;
    }

    @Override
    public void onDisable() {
        if (!closing /* && !PlayerUtil.isBlockUnder(mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) */) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 1000, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            return;
        }
        delayingC0F = false;
        return;
    }

    public static void onS08() {
        if (needS08) return;
        INSTANCE.closing = true;
        INSTANCE.setState(false);
        INSTANCE.closing = false;
        if (INSTANCE.enableAgain) {
            INSTANCE.enableAgain = false;
            INSTANCE.setState(true);
        }
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
//            if (!(mc.thePlayer.getHeldItemMainhand().getItem() instanceof ItemEnderPearl)) return;
//            if (thrown) return;
//            thrown = true;
            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)
                return;
            updateRotation(event);
            return;
        }
        if (event.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging digging = (C07PacketPlayerDigging) event.getPacket();
            if (digging.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) {
                    updateRotation(event);
                    return;
                }
            }
        }
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (needS08 && ++count < 3) {
                return;
            }
            event.setCancelled(true);
            needS08 = false;
            return;
        }
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            if (needS08) return;
            event.setCancelled(true);
            return;
        }
        if (event.getPacket() instanceof C02PacketUseEntity) {
            event.setCancelled(true);
            return;
        }
    }

    private boolean updateRotation(PacketSendEvent event) {
        Rotation current = RotationUtils.getPlayerRotation();
        current.fixedSensitivity(mc.gameSettings.mouseSensitivity);
        if (rotation.yaw == current.yaw && rotation.pitch == current.pitch) {
            return false;
        }
        rotation = current;
        event.setCancelled(true);
        if (rotationCount++ > 19 && rotationFirst.getValue()) {
            count = 0;
            rotationCount = 0;
            needS08 = true;
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 1000, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            return false;
        }
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.yaw, current.pitch, onGround));
        PacketUtil.sendPacketNoEvent(event.getPacket());
        return true;
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        onS08();
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
//        mc.getConnection().sendPacketNoEvent(new CPacketSteerBoat(true, true));

//        mc.thePlayerStuckTicks += 1;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;
//        mc.thePlayer.setPosition(x, y, z);

        if (PlayerUtil.isBlockUnder(mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) && autoDisable.getValue()) {
            onS08();
        }
    }

    public static boolean isStuck() {
        return INSTANCE.getState();
    }

    public static boolean isDelayingC0F() {
        return INSTANCE.delayingC0F && INSTANCE.getState();
    }

    public static void throwPearl(Vector2f current) {
        if (!INSTANCE.getState()) {
            return;
        }
        Stuck.mc.thePlayer.rotationYaw = current.x;
        Stuck.mc.thePlayer.rotationPitch = current.y;
        float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        current.x -= current.x % gcd;
        current.y -= current.y % gcd;
        if (!Stuck.INSTANCE.rotation.equals(current)) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, Stuck.INSTANCE.onGround));
        }
        Stuck.INSTANCE.rotation = new Rotation(current.x, current.y);
        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(Stuck.mc.thePlayer.getHeldItem()));
    }
}