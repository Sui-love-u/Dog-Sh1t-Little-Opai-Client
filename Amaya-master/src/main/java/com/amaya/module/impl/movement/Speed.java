package com.amaya.module.impl.movement;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.combat.KillAura;
import com.amaya.module.impl.player.Blink;
import com.amaya.module.impl.world.Scaffold;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.utils.player.PlayerUtil;
import com.amaya.utils.player.Rise.MoveUtil;
import com.amaya.component.RotationComponent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;

import java.util.Iterator;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "Speed", category = Category.Movement)
public class Speed extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim",new String[]{"Watchdog", "Grim", "AutoJump"});
    private final ModeSetting watchdogmode = new ModeSetting("WatchDog Mode", () -> this.mode.is("Watchdog"),"Ground",new String[]{"Ground", "Glide", "Glide2", "Test","LowHop"});
    private final BooleanSetting lagbackcheck = new BooleanSetting("LagBackCheck", true);
    public final BooleanSetting strafe = new BooleanSetting("Grim-Strafe", () -> this.mode.is("Grim"),false);
    public final BooleanSetting strafe1 = new BooleanSetting("Strafe", () -> this.mode.is("Watchdog"),false);
    private final BooleanSetting scaffoldCheck = new BooleanSetting("Scaffold Check", false);
    private final BooleanSetting blinkCheck = new BooleanSetting("Blink Check", false);

    private int inAirTicks;

    @Override
    public void onEnable() {
        inAirTicks = 0;
        if (mc.thePlayer == null) return;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }

    @EventTarget
    private void onPacketReceive(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S08PacketPlayerPosLook) {
            if (this.lagbackcheck.getValue()) {
                this.setState(false);
            }
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if ((blinkCheck.getValue() && Amaya.Instance.moduleManager.getModule(Blink.class).getState()) || (scaffoldCheck.getValue() && Amaya.Instance.moduleManager.getModule(Scaffold.class).getState())) {
            return;
        }
        if (mode.is("Watchdog")) {
            if (watchdogmode.is("Ground")) {
                if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
                    mc.thePlayer.jump();
                    MoveUtil.strafe(0.45);
                }
            } else if (watchdogmode.is("Glide")) {
                if (MoveUtil.isMoving()) {
                    if ((mc.thePlayer.offGroundTicks == 10) && MoveUtil.isOnGround(0.769)) {
                        mc.thePlayer.motionY = 0;
                    }

                    if (MoveUtil.isOnGround(0.769) && mc.thePlayer.offGroundTicks >= 9) {
                        MoveUtil.strafe(0.29);
                    }

                    if (mc.thePlayer.onGround) {
                        if (mc.gameSettings.keyBindForward.isPressed()) MoveUtil.strafe(0.28);
                        else MoveUtil.strafe(0.45);
                        mc.thePlayer.jump();
                    }
                }
            } else if (watchdogmode.is("Glide2")) {
                if (MoveUtil.isMoving()) {
                    if ((mc.thePlayer.offGroundTicks == 10 || mc.thePlayer.offGroundTicks == 11) && MoveUtil.isOnGround(0.769)) {
                        mc.thePlayer.motionY = 0;
                        MoveUtil.strafe(0.15);
                    }

                    if (mc.thePlayer.onGround) {
                        if (mc.gameSettings.keyBindForward.pressed) MoveUtil.strafe(0.28);
                        else MoveUtil.strafe(0.45);
                        mc.thePlayer.jump();
                    }
                }
            } else if (watchdogmode.is("LowHop")) {
                if (MoveUtil.isMoving()) {
                    if (PlayerUtil.isBlockUnder(mc.thePlayer)) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                        }
                        if (mc.thePlayer.offGroundTicks == 7 && strafe.getValue()) {
                            mc.thePlayer.motionY = -0.06;
                            MoveUtil.strafe(0.3F);
                        } else {
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                                MoveUtil.strafe(0.476 + MoveUtil.getSpeedEffect() * 0.04);
                            }
                        }
                    } else {
                        switch (mc.thePlayer.offGroundTicks) {
                            case 0:
                                mc.thePlayer.jump();
                                MoveUtil.strafe(0.485);
                                break;
                            case 5:
                                if (strafe1.getValue())
                                    MoveUtil.strafe(0.315);
                                mc.thePlayer.motionY = MoveUtil.predictedMotion(mc.thePlayer.motionY, 0);
                                break;
                            case 6:
                                if (strafe1.getValue())
                                    MoveUtil.strafe();
                                break;
                        }
                    }
                }
            }
        } else if (watchdogmode.is("Test")) {
            if (mc.thePlayer.onGround) {
                if (MoveUtil.isMoving()) {
                    mc.thePlayer.jump();
                    MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() * 1.6);
                    this.inAirTicks = 0;
                }
            } else {
                ++this.inAirTicks;
                if (this.inAirTicks == 1) {
                    MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() * 1.16);
                }
            }

        } else if (mode.is("AutoJump")) {
            if (MoveUtil.isMoving() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                mc.thePlayer.jump();
            }
        }
    }


    @EventTarget
    private void onMotion(MotionEvent event) {
        setSuffix(mode.get());
        if ((blinkCheck.getValue() && Amaya.Instance.moduleManager.getModule(Blink.class).getState()) || (scaffoldCheck.getValue() && Amaya.Instance.moduleManager.getModule(Scaffold.class).getState())) {
            return;
        }
        if (isGapple()) return;
        if (mode.is("Grim")) {
            AxisAlignedBB playerBox = mc.thePlayer.boundingBox.expand(1.0D, 1.0D, 1.0D);
            int c = 0;
            Iterator<Entity> entitys = mc.theWorld.loadedEntityList.iterator();

            while(true) {
                Entity entity;
                do {
                    if (!entitys.hasNext()) {
                        if (c > 0 && MoveUtil.isMoving()) {
                            double strafeOffset = (double)Math.min(c, 3) * 0.03D;
                            float yaw = this.getMoveYaw();
                            double mx = -Math.sin(Math.toRadians(yaw));
                            double mz = Math.cos(Math.toRadians(yaw));
                            mc.thePlayer.addVelocity(mx * strafeOffset, 0.0D, mz * strafeOffset);
                            if (c < 4 && KillAura.target != null && this.shouldFollow()) {
                                mc.gameSettings.keyBindLeft.pressed = true;
                            } else {
                                mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
                            }
                            return;
                        } else {
                            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
                            return;
                        }
                    }

                    entity = entitys.next();
                } while(!(entity instanceof EntityLivingBase) && !(entity instanceof EntityBoat) && !(entity instanceof EntityMinecart) && !(entity instanceof EntityFishHook));

                if (!(entity instanceof EntityArmorStand) && entity.getEntityId() != mc.thePlayer.getEntityId() && playerBox.intersectsWith(entity.boundingBox) && entity.getEntityId() != -8 && entity.getEntityId() != -1337 && !(Amaya.Instance.moduleManager.getModule(Blink.class)).getState()) {
                    ++c;
                }
            }
        }
    }

    public boolean shouldFollow() {
        return this.getState() && mc.gameSettings.keyBindJump.isKeyDown();
    }

    private float getMoveYaw() {
        EntityPlayerSP thePlayer = mc.thePlayer;
        float moveYaw = thePlayer.rotationYaw;
        if (thePlayer.moveForward != 0.0F && thePlayer.moveStrafing == 0.0F) {
            moveYaw += thePlayer.moveForward > 0.0F ? 0.0F : 180.0F;
        } else if (thePlayer.moveForward != 0.0F) {
            if (thePlayer.moveForward > 0.0F) {
                moveYaw += thePlayer.moveStrafing > 0.0F ? -45.0F : 45.0F;
            } else {
                moveYaw -= thePlayer.moveStrafing > 0.0F ? -45.0F : 45.0F;
            }

            moveYaw += thePlayer.moveForward > 0.0F ? 0.0F : 180.0F;
        } else if (thePlayer.moveStrafing != 0.0F) {
            moveYaw += thePlayer.moveStrafing > 0.0F ? -70.0F : 70.0F;
        }

        if (KillAura.target != null && mc.gameSettings.keyBindJump.isKeyDown()) {
            moveYaw = RotationComponent.rotations.x;
        }

        return moveYaw;
    }
}
