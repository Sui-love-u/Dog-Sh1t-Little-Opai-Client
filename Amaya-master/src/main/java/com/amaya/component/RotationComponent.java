package com.amaya.component;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.*;
import com.amaya.utils.math.Rotation;
import com.amaya.utils.math.Vector2f;
import com.amaya.utils.player.Rise.MoveUtil;
import com.amaya.utils.player.Rise.MovementFix;
import com.amaya.utils.player.Rise.RotationRise;
import com.amaya.utils.player.Rise.RotationUtils;

import static com.amaya.utils.client.InstanceAccess.mc;

public final class RotationComponent {
    public static boolean active;
    private static boolean smoothed;
    public static Vector2f rotations;
    public static Vector2f lastRotations;
    public static Vector2f targetRotations;
    public static Vector2f lastServerRotations;
    private static double rotationSpeed;
    private static MovementFix correctMovement;

    /*
     * This method must be called on Pre Update Event to work correctly
     */
    public static void setRotations(final Vector2f rotations, final double rotationSpeed, final MovementFix correctMovement) {
        RotationComponent.targetRotations = rotations;
        RotationComponent.rotationSpeed = rotationSpeed * 18;
        RotationComponent.correctMovement = correctMovement;
        active = true;

        smooth();
    }

    @EventTarget
    public void onUpdate(UpdateEvent e){
        if (!RotationComponent.active || RotationComponent.rotations == null || RotationComponent.lastRotations == null || RotationComponent.targetRotations == null || RotationComponent.lastServerRotations == null) {
            RotationComponent.rotations = (RotationComponent.lastRotations = (RotationComponent.targetRotations = (RotationComponent.lastServerRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch))));
        }
        if (RotationComponent.active) {
            smooth();
        }
        if (RotationComponent.correctMovement == MovementFix.BACKWARDS_SPRINT && RotationComponent.active && Math.abs(RotationComponent.rotations.x - Math.toDegrees(MoveUtil.direction())) > 45.0) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
        }
    }

    @EventTarget
    public void onMoveInput(MoveInputEvent event) {
        if (active && correctMovement == MovementFix.NORMAL && rotations != null) {
            /*
             * Calculating movement fix
             */
            final float yaw = rotations.x;
            MoveUtil.fixMovement(event, yaw);
        }
    }

    @EventTarget
    public void onLook(LookEvent event) {
        if (active && rotations != null) {
            event.setRotation(rotations);
        }
    }

    @EventTarget
    public void onPlayerMoveUpdate(StrafeEvent a) {
        if (RotationComponent.active && (RotationComponent.correctMovement == MovementFix.NORMAL || RotationComponent.correctMovement == MovementFix.TRADITIONAL) && RotationComponent.rotations != null) {
            a.setYaw(RotationComponent.rotations.x);
        }
    }/////////////////////////

    @EventTarget
    public void onJumpFix(JumpEvent event) {
        if (RotationComponent.active && (RotationComponent.correctMovement == MovementFix.NORMAL || RotationComponent.correctMovement == MovementFix.TRADITIONAL || RotationComponent.correctMovement == MovementFix.BACKWARDS_SPRINT) && RotationComponent.rotations != null) {
            event.setYaw(RotationComponent.rotations.x);
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.isPre()) {
            if (active && rotations != null) {
                final float yaw = rotations.x;
                final float pitch = rotations.y;

                event.setYaw(yaw);
                event.setPitch(pitch);

                mc.thePlayer.renderYawOffset = yaw;
                mc.thePlayer.rotationYawHead = yaw;
                mc.thePlayer.renderPitchHead = pitch;

                lastServerRotations = new Vector2f(yaw, pitch);

                if (Math.abs((rotations.x - mc.thePlayer.rotationYaw) % 360) < 1 && Math.abs((rotations.y - mc.thePlayer.rotationPitch)) < 1) {
                    active = false;

                    this.correctDisabledRotations();
                }

                lastRotations = rotations;
            } else {
                lastRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            }

            targetRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            smoothed = false;
        }
    }

    private static void correctDisabledRotations() {
        if (mc.thePlayer == null) {
            return;
        }

        final Vector2f rotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        final Vector2f fixedRotations = RotationRise.resetRotation(RotationRise.applySensitivityPatchA(rotations, lastRotations));

        mc.thePlayer.rotationYaw = fixedRotations.x;
        mc.thePlayer.rotationPitch = fixedRotations.y;
    }

    public static void smooth() {
        if (!smoothed) {
            final float lastYaw = lastRotations.x;
            final float lastPitch = lastRotations.y;
            final float targetYaw = targetRotations.x;
            final float targetPitch = targetRotations.y;

            rotations = RotationRise.smooth(new Vector2f(lastYaw, lastPitch), new Vector2f(targetYaw, targetPitch),
                    rotationSpeed + Math.random());

            if (correctMovement == MovementFix.NORMAL || correctMovement == MovementFix.TRADITIONAL) {
                mc.thePlayer.movementYaw = rotations.x;
            }

            mc.thePlayer.velocityYaw = rotations.x;
        }

        smoothed = true;

        /*
         * Updating MouseOver
         */
        mc.entityRenderer.getMouseOver(1);
    }

    public static void stopRotation() {
        active = false;

        correctDisabledRotations();
    }
    public static double getRotationDifference(Rotation rotation) {
        return lastServerRotations == null ? 0.0D : getRotationDifference(rotation, lastServerRotations);
    }
    public static double getRotationDifference(Vector2f a ,Vector2f b2) {
        return Math.hypot(RotationUtils.getAngleDifference(a.getX(), b2.getX()), a.getY() - b2.getY());
    }

    public static double getRotationDifference(Rotation a, Vector2f b) {
        return Math.hypot((double)getAngleDifference(a.getYaw(), b.getX()), (double)(a.getPitch() - b.getY()));
    }


    public static float getAngleDifference(float a, float b) {
        return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
    }

}
