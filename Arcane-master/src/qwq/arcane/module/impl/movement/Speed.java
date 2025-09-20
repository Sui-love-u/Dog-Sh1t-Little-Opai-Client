package qwq.arcane.module.impl.movement;

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
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketReceiveEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.combat.KillAura;
import qwq.arcane.module.impl.player.Blink;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.player.MovementUtil;
import qwq.arcane.utils.player.PlayerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/movement/Speed.class */
public class Speed extends Module {
    private final ModeValue mode;
    private final ModeValue watchdogmode;
    private final BoolValue lagbackcheck;
    public final BoolValue strafe;
    public final BoolValue strafe1;
    private final BoolValue scaffoldCheck;
    private final BoolValue blinkCheck;
    private int inAirTicks;

    public Speed() {
        super("Speed", Category.Movement);
        this.mode = new ModeValue("Mode", "Grim", new String[]{"Watchdog", "Grim", "AutoJump"});
        this.watchdogmode = new ModeValue("WatchDog Mode", () -> {
            return this.mode.is("Watchdog");
        }, "Ground", new String[]{"Ground", "Glide", "Glide2", "Test", "LowHop"});
        this.lagbackcheck = new BoolValue("LagBackCheck", true);
        this.strafe = new BoolValue("Grim-Strafe", () -> {
            return this.mode.is("Grim");
        }, false);
        this.strafe1 = new BoolValue("Strafe", () -> {
            return this.mode.is("Watchdog");
        }, false);
        this.scaffoldCheck = new BoolValue("Scaffold Check", false);
        this.blinkCheck = new BoolValue("Blink Check", false);
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.inAirTicks = 0;
        if (mc.thePlayer == null) {
            return;
        }
        super.onEnable();
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }

    @EventTarget
    private void onPacketReceive(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if ((packet instanceof S08PacketPlayerPosLook) && this.lagbackcheck.getValue().booleanValue()) {
            setState(false);
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        setsuffix(this.mode.getValue());
        if (this.blinkCheck.getValue().booleanValue() && ((Blink) Client.Instance.getModuleManager().getModule(Blink.class)).getState()) {
            return;
        }
        if (this.scaffoldCheck.getValue().booleanValue() && ((Scaffold) Client.Instance.getModuleManager().getModule(Scaffold.class)).getState()) {
            return;
        }
        if (this.mode.is("Watchdog")) {
            if (this.watchdogmode.is("Ground")) {
                if (mc.thePlayer.onGround && MovementUtil.isMoving()) {
                    mc.thePlayer.jump();
                    MovementUtil.strafe(0.45d);
                    return;
                }
                return;
            }
            if (this.watchdogmode.is("Glide")) {
                if (MovementUtil.isMoving()) {
                    if (mc.thePlayer.offGroundTicks == 10 && MovementUtil.isOnGround(0.769d)) {
                        mc.thePlayer.motionY = 0.0d;
                    }
                    if (MovementUtil.isOnGround(0.769d) && mc.thePlayer.offGroundTicks >= 9) {
                        MovementUtil.strafe(0.29d);
                    }
                    if (mc.thePlayer.onGround) {
                        if (mc.gameSettings.keyBindForward.isPressed()) {
                            MovementUtil.strafe(0.28d);
                        } else {
                            MovementUtil.strafe(0.45d);
                        }
                        mc.thePlayer.jump();
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.watchdogmode.is("Glide2")) {
                if (MovementUtil.isMoving()) {
                    if ((mc.thePlayer.offGroundTicks == 10 || mc.thePlayer.offGroundTicks == 11) && MovementUtil.isOnGround(0.769d)) {
                        mc.thePlayer.motionY = 0.0d;
                        MovementUtil.strafe(0.15d);
                    }
                    if (mc.thePlayer.onGround) {
                        if (mc.gameSettings.keyBindForward.pressed) {
                            MovementUtil.strafe(0.28d);
                        } else {
                            MovementUtil.strafe(0.45d);
                        }
                        mc.thePlayer.jump();
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.watchdogmode.is("LowHop") && MovementUtil.isMoving()) {
                if (PlayerUtil.isBlockUnder(mc.thePlayer)) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    if (mc.thePlayer.offGroundTicks == 9 && this.strafe.getValue().booleanValue()) {
                        mc.thePlayer.motionY = -0.06d;
                        MovementUtil.strafe(0.30000001192092896d);
                        return;
                    } else {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            MovementUtil.strafe(0.476d + (MovementUtil.getSpeedEffect() * 0.04d));
                            return;
                        }
                        return;
                    }
                }
                switch (mc.thePlayer.offGroundTicks) {
                    case 0:
                        mc.thePlayer.jump();
                        MovementUtil.strafe(0.485d);
                        break;
                    case 5:
                        if (this.strafe1.getValue().booleanValue()) {
                            MovementUtil.strafe(0.315d);
                        }
                        mc.thePlayer.motionY = MovementUtil.predictedMotion(mc.thePlayer.motionY, 2);
                        break;
                    case 6:
                        if (this.strafe1.getValue().booleanValue()) {
                            MovementUtil.strafe(0.415d);
                            break;
                        }
                        break;
                }
                return;
            }
            return;
        }
        if (this.watchdogmode.is("Test")) {
            if (mc.thePlayer.onGround) {
                if (MovementUtil.isMoving()) {
                    mc.thePlayer.jump();
                    MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 1.6d);
                    this.inAirTicks = 0;
                    return;
                }
                return;
            }
            this.inAirTicks++;
            if (this.inAirTicks == 1) {
                MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 1.16d);
                return;
            }
            return;
        }
        if (this.mode.is("AutoJump") && MovementUtil.isMoving() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
            mc.thePlayer.jump();
        }
    }

    @EventTarget
    private void onMotion(MotionEvent event) {
        if (this.blinkCheck.getValue().booleanValue() && ((Blink) Client.Instance.getModuleManager().getModule(Blink.class)).getState()) {
            return;
        }
        if ((!this.scaffoldCheck.getValue().booleanValue() || !((Scaffold) Client.Instance.getModuleManager().getModule(Scaffold.class)).getState()) && this.mode.is("Grim")) {
            AxisAlignedBB playerBox = mc.thePlayer.boundingBox.expand(1.0d, 1.0d, 1.0d);
            int c = 0;
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if ((entity instanceof EntityLivingBase) || (entity instanceof EntityBoat) || (entity instanceof EntityMinecart) || (entity instanceof EntityFishHook)) {
                    if (!(entity instanceof EntityArmorStand) && entity.getEntityId() != mc.thePlayer.getEntityId() && playerBox.intersectsWith(entity.boundingBox) && entity.getEntityId() != -8 && entity.getEntityId() != -1337 && !((Blink) Client.Instance.getModuleManager().getModule(Blink.class)).getState()) {
                        c++;
                    }
                }
            }
            if (c > 0 && MovementUtil.isMoving()) {
                double strafeOffset = Math.min(c, 3) * 0.03d;
                float yaw = getMoveYaw();
                double mx = -Math.sin(Math.toRadians(yaw));
                double mz = Math.cos(Math.toRadians(yaw));
                mc.thePlayer.addVelocity(mx * strafeOffset, 0.0d, mz * strafeOffset);
                if (c < 4 && KillAura.target != null && shouldFollow()) {
                    mc.gameSettings.keyBindLeft.pressed = true;
                    return;
                } else {
                    mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
                    return;
                }
            }
            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
        }
    }

    public boolean shouldFollow() {
        return getState() && mc.gameSettings.keyBindJump.isKeyDown();
    }

    private float getMoveYaw() {
        float moveYaw;
        EntityPlayerSP thePlayer = mc.thePlayer;
        float moveYaw2 = thePlayer.rotationYaw;
        if (thePlayer.moveForward != 0.0f && thePlayer.moveStrafing == 0.0f) {
            moveYaw2 += thePlayer.moveForward > 0.0f ? 0.0f : 180.0f;
        } else if (thePlayer.moveForward != 0.0f) {
            if (thePlayer.moveForward > 0.0f) {
                moveYaw = moveYaw2 + (thePlayer.moveStrafing > 0.0f ? -45.0f : 45.0f);
            } else {
                moveYaw = moveYaw2 - (thePlayer.moveStrafing > 0.0f ? -45.0f : 45.0f);
            }
            moveYaw2 = moveYaw + (thePlayer.moveForward > 0.0f ? 0.0f : 180.0f);
        } else if (thePlayer.moveStrafing != 0.0f) {
            moveYaw2 += thePlayer.moveStrafing > 0.0f ? -70.0f : 70.0f;
        }
        if (KillAura.target != null && mc.gameSettings.keyBindJump.isKeyDown()) {
            moveYaw2 = Client.Instance.getRotationManager().lastRotation.x;
        }
        return moveYaw2;
    }
}
