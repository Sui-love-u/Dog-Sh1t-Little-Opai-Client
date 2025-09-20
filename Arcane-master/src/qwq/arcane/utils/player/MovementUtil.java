package qwq.arcane.utils.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import qwq.arcane.event.impl.events.player.MoveEvent;
import qwq.arcane.event.impl.events.player.MoveInputEvent;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.math.Vector2f;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/MovementUtil.class */
public class MovementUtil implements Instance {
    public static boolean isMoving() {
        return isMoving(mc.thePlayer);
    }

    public static boolean isMoving(EntityLivingBase player) {
        return (player == null || (player.moveForward == 0.0f && player.moveStrafing == 0.0f)) ? false : true;
    }

    public static double predictedMotion(double motion, int ticks) {
        if (ticks == 0) {
            return motion;
        }
        double predicted = motion;
        for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08d) * 0.9800000190734863d;
        }
        return predicted;
    }

    public static float getDirection() {
        if (mc.thePlayer == null) {
            return 0.0f;
        }
        float yaw = mc.thePlayer.rotationYaw;
        boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean back = mc.gameSettings.keyBindBack.isKeyDown();
        boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean right = mc.gameSettings.keyBindRight.isKeyDown();
        float result = 0.0f;
        if (forward) {
            result = (!left || right) ? (!right || left) ? 0.0f : 45.0f : -45.0f;
        } else if (back) {
            result = (!left || right) ? (!right || left) ? 180.0f : 135.0f : -135.0f;
        } else if (left && !right) {
            result = -90.0f;
        } else if (right && !left) {
            result = 90.0f;
        }
        float direction = yaw + result;
        return ((direction % 360.0f) + 360.0f) % 360.0f;
    }

    public static int getSpeedEffect() {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;
    }

    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0d) {
            if (strafe > 0.0d) {
                yaw += forward > 0.0d ? -45 : 45;
            } else if (strafe < 0.0d) {
                yaw += forward > 0.0d ? 45 : -45;
            }
            strafe = 0.0d;
            if (forward > 0.0d) {
                forward = 1.0d;
            } else if (forward < 0.0d) {
                forward = -1.0d;
            }
        }
        if (strafe > 0.0d) {
            strafe = 1.0d;
        } else if (strafe < 0.0d) {
            strafe = -1.0d;
        }
        double mx = Math.cos(Math.toRadians(yaw + 90.0f));
        double mz = Math.sin(Math.toRadians(yaw + 90.0f));
        mc.thePlayer.motionX = (forward * moveSpeed * mx) + (strafe * moveSpeed * mz);
        mc.thePlayer.motionZ = ((forward * moveSpeed) * mz) - ((strafe * moveSpeed) * mx);
    }

    public static void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.getMoveStrafe(), mc.thePlayer.movementInput.getMoveForward());
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = mc.thePlayer.capabilities.getWalkSpeed() * 2.873d;
        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            baseSpeed /= 1.0d + (0.2d * (mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1));
        }
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0d + (0.2d * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
        }
        return baseSpeed;
    }

    public static double strafe(double d) {
        if (!isMoving()) {
            return mc.thePlayer.rotationYaw;
        }
        double yaw = getDirection1();
        mc.thePlayer.motionX = (-MathHelper.sin((float) yaw)) * d;
        mc.thePlayer.motionZ = MathHelper.cos((float) yaw) * d;
        return yaw;
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0d, -height, 0.0d)).isEmpty();
    }

    public static float getBindsDirection(float rotationYaw) {
        int moveForward = 0;
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindForward)) {
            moveForward = 0 + 1;
        }
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindBack)) {
            moveForward--;
        }
        int moveStrafing = 0;
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) {
            moveStrafing = 0 + 1;
        }
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) {
            moveStrafing--;
        }
        boolean reversed = moveForward < 0;
        double strafingYaw = 90.0d * (moveForward > 0 ? 0.5d : reversed ? -0.5d : 1.0d);
        if (reversed) {
            rotationYaw += 180.0f;
        }
        if (moveStrafing > 0) {
            rotationYaw = (float) (rotationYaw + strafingYaw);
        } else if (moveStrafing < 0) {
            rotationYaw = (float) (rotationYaw - strafingYaw);
        }
        return rotationYaw;
    }

    public static boolean isMovingStraight() {
        float direction = getRawDirection() + 180.0f;
        float movingYaw = Math.round(direction / 45.0f) * 45;
        return movingYaw % 90.0f == 0.0f;
    }

    public static void setMotion2(double d, float f) {
        mc.thePlayer.motionX = (-Math.sin(Math.toRadians(f))) * d;
        mc.thePlayer.motionZ = Math.cos(Math.toRadians(f)) * d;
    }

    public static float getMoveYaw(float yaw) {
        Vector2f from = new Vector2f((float) mc.thePlayer.lastTickPosX, (float) mc.thePlayer.lastTickPosZ);
        Vector2f to = new Vector2f((float) mc.thePlayer.posX, (float) mc.thePlayer.posZ);
        Vector2f diff = new Vector2f(to.x - from.x, to.y - from.y);
        double x = diff.x;
        double z = diff.y;
        if (x != 0.0d && z != 0.0d) {
            yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
        }
        return yaw;
    }

    public static double speed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static double getDirection1() {
        float yaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0f) {
            yaw += 180.0f;
        }
        float forward = 1.0f;
        if (mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (mc.thePlayer.moveStrafing > 0.0f) {
            yaw -= 90.0f * forward;
        } else if (mc.thePlayer.moveStrafing < 0.0f) {
            yaw += 90.0f * forward;
        }
        return Math.toRadians(yaw);
    }

    public static void stop() {
        mc.thePlayer.motionX = 0.0d;
        mc.thePlayer.motionZ = 0.0d;
    }

    public static float getDirection(float yaw) {
        return getDirection(yaw, mc.thePlayer.movementInput.moveForward, mc.thePlayer.movementInput.moveStrafe);
    }

    public static float getDirection(float yaw, float forward, float strafe) {
        if (forward != 0.0f) {
            if (strafe < 0.0f) {
                yaw += forward < 0.0f ? 135.0f : 45.0f;
            } else if (strafe > 0.0f) {
                yaw -= forward < 0.0f ? 135.0f : 45.0f;
            } else if (strafe == 0.0f && forward < 0.0f) {
                yaw -= 180.0f;
            }
        } else if (strafe < 0.0f) {
            yaw += 90.0f;
        } else if (strafe > 0.0f) {
            yaw -= 90.0f;
        }
        return yaw;
    }

    public static boolean canSprint(boolean legit) {
        if (legit) {
            return (mc.thePlayer.moveForward < 0.8f || mc.thePlayer.isCollidedHorizontally || (mc.thePlayer.getFoodStats().getFoodLevel() <= 6 && !mc.thePlayer.capabilities.allowFlying) || mc.thePlayer.isPotionActive(Potion.blindness) || mc.thePlayer.isSneaking()) ? false : true;
        }
        return enoughMovementForSprinting();
    }

    public static boolean enoughMovementForSprinting() {
        return Math.abs(mc.thePlayer.moveForward) >= 0.8f || Math.abs(mc.thePlayer.moveStrafing) >= 0.8f;
    }

    public static void stopXZ() {
        EntityPlayerSP entityPlayerSP = mc.thePlayer;
        mc.thePlayer.motionZ = 0.0d;
        entityPlayerSP.motionX = 0.0d;
    }

    public static void fixMovement(MoveInputEvent event, float yaw) {
        float forward = event.getForward();
        float strafe = event.getStrafe();
        double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(mc.thePlayer.rotationYaw, forward, strafe)));
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
                            double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
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

    public static double direction(float rotationYaw, double moveForward, double moveStrafing) {
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

    public static double direction() {
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 70.0f * forward;
        }
        if (mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 70.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static void strafe(double speed, double yaw) {
        if (!isMoving()) {
            return;
        }
        mc.thePlayer.motionX = (-Math.sin(yaw)) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static void strafe(MoveEvent moveEvent, double speed, double direction) {
        if (isMoving()) {
            EntityPlayerSP entityPlayerSP = mc.thePlayer;
            double d = (-Math.sin(direction)) * speed;
            entityPlayerSP.motionX = d;
            moveEvent.setX(d);
            EntityPlayerSP entityPlayerSP2 = mc.thePlayer;
            double dCos = Math.cos(direction) * speed;
            entityPlayerSP2.motionZ = dCos;
            moveEvent.setZ(dCos);
        }
    }

    public static float getRawDirection() {
        return getRawDirectionRotation(mc.thePlayer.rotationYaw, mc.thePlayer.keyMovementInput.moveStrafe, mc.thePlayer.keyMovementInput.moveForward);
    }

    public static float getRawDirectionRotation(float yaw, float pStrafe, float pForward) {
        float rotationYaw = yaw;
        if (pForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (pForward < 0.0f) {
            forward = -0.5f;
        } else if (pForward > 0.0f) {
            forward = 0.5f;
        }
        if (pStrafe > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (pStrafe < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return rotationYaw;
    }
}
