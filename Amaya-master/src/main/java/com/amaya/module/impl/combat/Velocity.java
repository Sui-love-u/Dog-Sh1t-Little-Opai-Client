package com.amaya.module.impl.combat;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.utils.math.Vector3d;
import com.amaya.utils.pack.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.viamcp.fixes.AttackOrder;

/**
 * @Author: Guyuemang
 * 2025/4/26
 */
@ModuleInfo(name = "Velocity",category = Category.Combat)
public class Velocity extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "GrimAC", new String[]{"GrimAC", "Watchdog"});

    private boolean absorbedVelocity;

    public boolean shouldVelo;
    Entity target;

    @EventTarget
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        setSuffix(mode.get());
        if (mode.is("GrimAC")) {
            if (event.getPacket() instanceof S12PacketEntityVelocity s12) {
                double strength = new Vector3d(s12.getMotionX(), s12.getMotionY(), s12.getMotionZ()).length();
                if (s12.getEntityID() == mc.thePlayer.getEntityId() && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWeb) {
                    target = getNearTarget();
                    if (target == null) return;
                    if (mc.thePlayer.getDistanceToEntity(target) > 3.3F) {
                        reset();
                        return;
                    }
                    shouldVelo = true;
                }
            }
        }
        if (event.getPacket() instanceof S12PacketEntityVelocity s12) {
            if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
                if (mode.is("Watchdog")) {
                    if (!mc.thePlayer.onGround) {
                        if (!absorbedVelocity) {
                            event.setCancelled(true);
                            absorbedVelocity = true;
                            return;
                        }
                    }
                    s12.motionX = (int) (mc.thePlayer.motionX * 8000);
                    s12.motionZ = (int) (mc.thePlayer.motionZ * 8000);
                }
            }
        }
    }

    public void onWorldEvent(WorldEvent event) {
        if (mode.is("GrimAC")) {
            reset();
        }
    }

    private Entity getNearTarget() {
        Entity target = null;
        EntityLivingBase clientTarget = KillAura.target;
        if (clientTarget != null) {
            target = clientTarget;
            return target;
        } else {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (!entity.equals(mc.thePlayer) && !entity.isDead) {
                    if (entity instanceof EntityArrow entityArrow) {
                        if (entityArrow.getTicksInGround() <= 0) {
                            target = entityArrow;
                        }
                    }

                    if (entity instanceof EntitySnowball) {
                        target = entity;
                    }

                    if (entity instanceof EntityEgg) {
                        target = entity;
                    }

                    if (entity instanceof EntityTNTPrimed) {
                        target = entity;
                    }

                    if (entity instanceof EntityFishHook) {
                        target = entity;
                    }
                }
            }
        }

        return target;
    }

    @EventTarget
    public void onUpdateEvent(UpdateEvent event) {
        if (mode.is("GrimAC")) {
            if (shouldVelo) {
                if (mc.thePlayer.getDistanceToEntity(target) > 3.0) {
                    reset();
                    return;
                }
                if (!mc.thePlayer.serverSprintState) {
                    PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                }
                for (int i = 0; i < 5; i++) {
                    AttackOrder.sendFixedAttackByPacket(target);
                }
                if (!mc.thePlayer.serverSprintState) {
                    PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                }
                mc.thePlayer.motionX *= 0.07776;
                mc.thePlayer.motionZ *= 0.07776;
                shouldVelo = false;
            }
        }
        if (mode.is("Watchdog")) {
            if (mc.thePlayer.onGround) {
                absorbedVelocity = false;
            }
        }
    }

    private void reset() {
        shouldVelo = false;
        target = null;
    }
}