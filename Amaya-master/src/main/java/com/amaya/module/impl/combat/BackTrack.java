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
package com.amaya.module.impl.combat;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.player.AttackEvent;
import com.amaya.events.impl.render.Render3DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.render.RenderUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.Vec3;

import java.awt.*;

@ModuleInfo(name = "BackTrack",category = Category.Combat)
public class BackTrack extends Module {
    public static EntityLivingBase target;
    private final NumberSetting amount = new NumberSetting("Amount", 1.0D, 1.0D, 3.0D, 0.1D);
    private final NumberSetting range = new NumberSetting("Range", 2.0D, 2.0, 8.0D, 0.1D);
    private final NumberSetting interval = new NumberSetting("IntervalTick", 1.0D, 0.0d, 10.0D, 1.0D);
    private final BooleanSetting esp = new BooleanSetting("Esp", false);
    private Vec3 realTargetPosition = new Vec3(0.0D, 0.0D, 0.0D);
    public static double realX;
    public static double realY;
    public static double realZ;
    int tick = 0;

    @EventTarget
    public void onAttack(AttackEvent e) {
        target = (EntityLivingBase)e.getTargetEntity();
    }

    @EventTarget
    public void onTick(TickEvent e) {
        if ((double)this.tick <= this.interval.getValue()) {
            ++this.tick;
        }

        if (target != null && (double)mc.thePlayer.getDistanceToEntity(target) <= this.range.getValue() && (new Vec3(target.posX, target.posY, target.posZ)).distanceTo(this.realTargetPosition) < (Double)this.amount.getValue() && (double)this.tick > (Double)this.interval.getValue()) {
            target.posX = target.lastTickPosX;
            target.posY = target.lastTickPosY;
            target.posZ = target.lastTickPosZ;
            this.tick = 0;
        }

    }

    @EventTarget
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S18PacketEntityTeleport s18) {
            if (s18.getEntityId() == target.getEntityId()) {
                this.realTargetPosition = new Vec3((double)s18.getX() / 32.0D, (double)s18.getY() / 32.0D, (double)s18.getZ() / 32.0D);
                realX = (double)s18.getX() / 32.0D;
                realY = (double)s18.getY() / 32.0D;
                realZ = (double)s18.getZ() / 32.0D;
            }
        }

    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (this.esp.get()) {
            RenderUtil.renderBoundingBox(target, Color.red, 100.0F);
            RenderUtil.resetColor();
        }

    }

    @Override
    public void onDisable() {
        target = null;
        this.tick = 0;
    }
}
