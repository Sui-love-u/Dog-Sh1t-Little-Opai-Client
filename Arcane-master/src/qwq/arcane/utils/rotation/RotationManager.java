package qwq.arcane.utils.rotation;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import qwq.arcane.event.annotations.EventPriority;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.JumpEvent;
import qwq.arcane.event.impl.events.player.LookEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.MoveInputEvent;
import qwq.arcane.event.impl.events.player.StrafeEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.math.Vector2f;
import qwq.arcane.utils.player.MovementUtil;
import qwq.arcane.utils.player.Rotation;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/rotation/RotationManager.class */
public final class RotationManager implements Instance {
    public static Vector2f rotation;
    public Vector2f lastRotation;
    public Vector2f targetRotation;
    public Vector2f lastServerRotation;
    private float rotationSpeed;
    private boolean modify;
    private boolean smoothed;
    private boolean movementFix;
    private boolean strict;
    private boolean useRandomOffset = false;
    private float randomOffsetRange = 0.0f;

    public RotationManager() {
        rotation = new Vector2f(0.0f, 0.0f);
    }

    public Vector2f getRotation() {
        return rotation;
    }

    public void setRandomOffset(boolean enabled, float range) {
        this.useRandomOffset = enabled;
        this.randomOffsetRange = range;
    }

    public MovingObjectPosition rayTrace(double blockReachDistance, float partialTicks) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 vec31 = mc.thePlayer.getLookCustom(this.lastServerRotation.getX(), this.lastServerRotation.getY());
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, false);
    }

    public float snapToHypYaw(float yaw, boolean slowdown) {
        float lowerOffset;
        float upperOffset;
        float snappedBase = Math.round(yaw / 45.0f) * 45.0f;
        if (Math.abs(snappedBase % 90.0f) < 0.001f) {
            lowerOffset = 111.0f;
            upperOffset = 111.0f;
        } else {
            lowerOffset = 137.0f;
            upperOffset = 137.0f;
            if (slowdown) {
                MovementUtil.strafe(0.009999999776482582d);
            }
        }
        float lowerCandidate = snappedBase - lowerOffset;
        float upperCandidate = snappedBase + upperOffset;
        return Math.abs(yaw - lowerCandidate) <= Math.abs(upperCandidate - yaw) ? lowerCandidate : upperCandidate;
    }

    public void setRotation(Vector2f rotation2, float rotationSpeed, boolean movementFix, boolean strict) {
        this.targetRotation = applyRandomOffset(rotation2);
        this.rotationSpeed = rotationSpeed;
        this.movementFix = movementFix;
        this.modify = true;
        this.strict = strict;
        smoothRotation();
    }

    private Vector2f applyRandomOffset(Vector2f rotation2) {
        if (!this.useRandomOffset || this.randomOffsetRange <= 0.0f) {
            return rotation2;
        }
        float randomYaw = (float) (((Math.random() * 2.0d) - 1.0d) * this.randomOffsetRange);
        float randomPitch = (float) (((Math.random() * 2.0d) - 1.0d) * this.randomOffsetRange);
        return new Vector2f(rotation2.getX() + randomYaw, MathHelper.clamp_float(rotation2.getY() + randomPitch, -90.0f, 90.0f));
    }

    public void setRotation(Vector2f rotation2, float rotationSpeed, boolean movementFix) {
        this.targetRotation = rotation2;
        this.rotationSpeed = rotationSpeed;
        this.movementFix = movementFix;
        this.modify = true;
        this.strict = false;
        smoothRotation();
    }

    public Vector2f getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double((x * x) + (z * z));
        float yaw = ((float) ((Math.atan2(z, x) * 180.0d) / 3.141592653589793d)) - 90.0f;
        float pitch = (float) (-((Math.atan2(y, dist) * 180.0d) / 3.141592653589793d));
        return new Vector2f(yaw, pitch);
    }

    public void setRotation(Rotation rotation2, float rotationSpeed, boolean movementFix) {
        this.targetRotation = rotation2.toVec2f();
        this.rotationSpeed = rotationSpeed;
        this.movementFix = movementFix;
        this.modify = true;
        this.strict = false;
        smoothRotation();
    }

    public void setTargetRotation(Rotation rotation2, int keepLength) {
        this.targetRotation = new Vector2f(rotation2.yaw, rotation2.pitch);
        this.movementFix = true;
        this.modify = true;
        this.strict = false;
    }

    public double getRotationDifference(Rotation rotation2) {
        if (this.lastServerRotation == null) {
            return 0.0d;
        }
        return getRotationDifference(rotation2, this.lastServerRotation);
    }

    public float getAngleDifference(float a, float b) {
        return ((((a - b) % 360.0f) + 540.0f) % 360.0f) - 180.0f;
    }

    public double getRotationDifference(Rotation a, Vector2f b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getX()), a.getPitch() - b.getY());
    }

    @EventPriority(8888)
    @EventTarget
    public void onMotion(UpdateEvent event) {
        if (!this.modify || rotation == null || this.lastRotation == null || this.targetRotation == null) {
            Vector2f vector2f = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            this.targetRotation = vector2f;
            this.lastServerRotation = vector2f;
            this.lastRotation = vector2f;
            rotation = vector2f;
        }
        if (this.modify) {
            smoothRotation();
        }
    }

    @EventPriority(8888)
    @EventTarget
    public void onMovementInput(MoveInputEvent event) {
        if (this.modify && this.movementFix && !this.strict) {
            float yaw = rotation.getX();
            float forward = event.getForward();
            float strafe = event.getStrafe();
            double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(getDirection(mc.thePlayer.rotationYaw, forward, strafe)));
            if (forward == 0.0f && strafe == 0.0f) {
                return;
            }
            float closestForward = 0.0f;
            float closestStrafe = 0.0f;
            float closestDifference = Float.MAX_VALUE;
            float f = -1.0f;
            while (true) {
                float predictedForward = f;
                if (predictedForward <= 1.0f) {
                    float f2 = -1.0f;
                    while (true) {
                        float predictedStrafe = f2;
                        if (predictedStrafe <= 1.0f) {
                            if (predictedStrafe != 0.0f || predictedForward != 0.0f) {
                                double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(getDirection(yaw, predictedForward, predictedStrafe)));
                                double difference = Math.abs(angle - predictedAngle);
                                if (difference < closestDifference) {
                                    closestDifference = (float) difference;
                                    closestForward = predictedForward;
                                    closestStrafe = predictedStrafe;
                                }
                            }
                            f2 = predictedStrafe + 1.0f;
                        }
                    }
                } else {
                    event.setForward(closestForward);
                    event.setStrafe(closestStrafe);
                    return;
                }
            }
        }
    }

    public static double getDirection(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0d) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (moveForward < 0.0d) {
            forward = -0.5f;
        } else if (moveForward > 0.0d) {
            forward = 0.5f;
        }
        if (moveStrafing > 0.0d) {
            rotationYaw -= 90.0f * forward;
        }
        if (moveStrafing < 0.0d) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    @EventPriority(8888)
    @EventTarget
    public void onLook(LookEvent event) {
        if (this.modify) {
            event.setRotation(rotation);
        }
    }

    @EventPriority(8888)
    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if (this.modify && this.movementFix) {
            event.setYaw(rotation.getX());
        }
    }

    @EventPriority(8888)
    @EventTarget
    public void onJump(JumpEvent event) {
        if (this.modify && this.movementFix) {
            event.setYaw(rotation.getX());
        }
    }

    @EventPriority(8888)
    @EventTarget
    public void onUpdate(MotionEvent event) {
        if (event.isPre()) {
            if (this.modify) {
                event.setYaw(rotation.getX());
                event.setPitch(rotation.getY());
                mc.thePlayer.renderYawOffset = rotation.getX();
                mc.thePlayer.rotationYawHead = rotation.getX();
                mc.thePlayer.renderPitchHead = rotation.getY();
                this.lastServerRotation = new Vector2f(rotation.getX(), rotation.getY());
                if (Math.abs((rotation.getX() - mc.thePlayer.rotationYaw) % 360.0f) < 1.0f && Math.abs(rotation.getY() - mc.thePlayer.rotationPitch) < 1.0f) {
                    this.modify = false;
                    correctDisabledRotations();
                }
                this.lastRotation = rotation;
            } else {
                this.lastRotation = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            }
            this.targetRotation = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            this.smoothed = false;
        }
    }

    private void correctDisabledRotations() {
        Vector2f rotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        Vector2f fixedRotations = resetRotation(applySensitivityPatch(rotations, this.lastRotation));
        mc.thePlayer.rotationYaw = fixedRotations.getX();
        mc.thePlayer.rotationPitch = fixedRotations.getY();
    }

    public Vector2f resetRotation(Vector2f rotation2) {
        if (rotation2 == null) {
            return null;
        }
        float yaw = rotation2.getX() + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - rotation2.getX());
        float pitch = mc.thePlayer.rotationPitch;
        return new Vector2f(yaw, pitch);
    }

    public Vector2f applySensitivityPatch(Vector2f rotation2, Vector2f previousRotation) {
        float mouseSensitivity = (float) ((mc.gameSettings.mouseSensitivity * (1.0d + (Math.random() / 1.0E7d)) * 0.6000000238418579d) + 0.20000000298023224d);
        double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f * 0.15d;
        float yaw = previousRotation.getX() + ((float) (Math.round((rotation2.getX() - previousRotation.getX()) / multiplier) * multiplier));
        float pitch = previousRotation.getY() + ((float) (Math.round((rotation2.getY() - previousRotation.getY()) / multiplier) * multiplier));
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f));
    }

    private void smoothRotation() {
        if (!this.smoothed) {
            float lastYaw = this.lastRotation.getX();
            float lastPitch = this.lastRotation.getY();
            float targetYaw = this.targetRotation.getX();
            float targetPitch = this.targetRotation.getY();
            rotation = getSmoothRotation(new Vector2f(lastYaw, lastPitch), applyRandomOffset(new Vector2f(targetYaw, targetPitch)), this.rotationSpeed + Math.random());
            rotation = getSmoothRotation(new Vector2f(lastYaw, lastPitch), new Vector2f(targetYaw, targetPitch), this.rotationSpeed + Math.random());
            if (this.movementFix) {
                mc.thePlayer.movementYaw = rotation.getX();
            }
            mc.thePlayer.velocityYaw = rotation.getX();
        }
        this.smoothed = true;
        mc.entityRenderer.getMouseOver(1.0f);
    }

    public Vector2f getSmoothRotation(Vector2f lastRotation, Vector2f targetRotation, double speed) {
        float yaw = targetRotation.getX();
        float pitch = targetRotation.getY();
        float lastYaw = lastRotation.getX();
        float lastPitch = lastRotation.getY();
        if (speed != 0.0d) {
            float rotationSpeed = (float) speed;
            double deltaYaw = MathHelper.wrapAngleTo180_float(targetRotation.getX() - lastRotation.getX());
            double deltaPitch = pitch - lastPitch;
            double distance = Math.sqrt((deltaYaw * deltaYaw) + (deltaPitch * deltaPitch));
            double distributionYaw = Math.abs(deltaYaw / distance);
            double distributionPitch = Math.abs(deltaPitch / distance);
            double maxYaw = rotationSpeed * distributionYaw;
            double maxPitch = rotationSpeed * distributionPitch;
            float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);
            yaw = lastYaw + moveYaw;
            pitch = lastPitch + movePitch;
        }
        boolean randomise = Math.random() > 0.8d;
        for (int i = 1; i <= ((int) (2.0d + (Math.random() * 2.0d))); i++) {
            if (randomise) {
                yaw += (float) ((Math.random() - 0.5d) / 1.0E8d);
                pitch -= (float) (Math.random() / 2.0E8d);
            }
            Vector2f rotations = new Vector2f(yaw, pitch);
            Vector2f fixedRotations = applySensitivityPatch(rotations);
            yaw = fixedRotations.getX();
            pitch = Math.max(-90.0f, Math.min(90.0f, fixedRotations.getY()));
        }
        return new Vector2f(yaw, pitch);
    }

    public Vector2f applySensitivityPatch(Vector2f rotation2) {
        Vector2f previousRotation = new Vector2f(mc.thePlayer.lastReportedYaw, mc.thePlayer.lastReportedPitch);
        float mouseSensitivity = (float) ((mc.gameSettings.mouseSensitivity * (1.0d + (Math.random() / 1.0E7d)) * 0.6000000238418579d) + 0.20000000298023224d);
        double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f * 0.15d;
        float yaw = previousRotation.getX() + ((float) (Math.round((rotation2.getX() - previousRotation.getX()) / multiplier) * multiplier));
        float pitch = previousRotation.getY() + ((float) (Math.round((rotation2.getY() - previousRotation.getY()) / multiplier) * multiplier));
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f));
    }
}
