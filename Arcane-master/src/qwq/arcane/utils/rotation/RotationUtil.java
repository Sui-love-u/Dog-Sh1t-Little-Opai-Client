package qwq.arcane.utils.rotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.math.Vector2f;
import qwq.arcane.utils.math.Vector3d;
import qwq.arcane.utils.player.Rotation;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/rotation/RotationUtil.class */
public final class RotationUtil implements Instance {
    private static List<Double> xzPercents = Arrays.asList(Double.valueOf(0.5d), Double.valueOf(0.4d), Double.valueOf(0.3d), Double.valueOf(0.2d), Double.valueOf(0.1d), Double.valueOf(0.0d), Double.valueOf(-0.1d), Double.valueOf(-0.2d), Double.valueOf(-0.3d), Double.valueOf(-0.4d), Double.valueOf(-0.5d));

    private RotationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static float oppositeYaw(float yaw) {
        Vector2f from = new Vector2f((float) mc.thePlayer.lastTickPosX, (float) mc.thePlayer.lastTickPosZ);
        Vector2f to = new Vector2f((float) mc.thePlayer.posX, (float) mc.thePlayer.posZ);
        Vector2f difference = new Vector2f(to.x - from.x, to.y - from.y);
        float x = difference.x;
        float z = difference.y;
        if (x != 0.0f && z != 0.0f) {
            yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
        }
        return yaw - 180.0f;
    }

    public static Vec3 getBestHitVec(Entity entity) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0f);
        AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
        double ex = MathHelper.clamp_double(positionEyes.xCoord, entityBoundingBox.minX, entityBoundingBox.maxX);
        double ey = MathHelper.clamp_double(positionEyes.yCoord, entityBoundingBox.minY, entityBoundingBox.maxY);
        double ez = MathHelper.clamp_double(positionEyes.zCoord, entityBoundingBox.minZ, entityBoundingBox.maxZ);
        return new Vec3(ex, ey, ez);
    }

    public static float[] getAngles(Entity entity) {
        if (entity == null) {
            return null;
        }
        EntityPlayerSP thePlayer = mc.thePlayer;
        double diffX = entity.posX - thePlayer.posX;
        double diffY = (entity.posY + (entity.getEyeHeight() * 0.9d)) - (thePlayer.posY + thePlayer.getEyeHeight());
        double diffZ = entity.posZ - thePlayer.posZ;
        double dist = MathHelper.sqrt_double((diffX * diffX) + (diffZ * diffZ));
        float yaw = ((float) ((Math.atan2(diffZ, diffX) * 180.0d) / 3.141592653589793d)) - 90.0f;
        float pitch = (float) (-((Math.atan2(diffY, dist) * 180.0d) / 3.141592653589793d));
        return new float[]{thePlayer.rotationYaw + MathHelper.wrapDegrees(yaw - thePlayer.rotationYaw), thePlayer.rotationPitch + MathHelper.wrapDegrees(pitch - thePlayer.rotationPitch)};
    }

    public static float[] getBlockRotations(double x, double y, double z) {
        double var4 = (x - mc.thePlayer.posX) + 0.5d;
        double var6 = (z - mc.thePlayer.posZ) + 0.5d;
        double var8 = y - ((mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) - 1.0d);
        double var14 = MathHelper.sqrt_double((var4 * var4) + (var6 * var6));
        float var12 = ((float) ((Math.atan2(var6, var4) * 180.0d) / 3.141592653589793d)) - 90.0f;
        return new float[]{var12, (float) (((-Math.atan2(var8, var14)) * 180.0d) / 3.141592653589793d)};
    }

    public static double getRotationDifference(Entity entity) {
        Vector2f rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);
        return getRotationDifference(rotation, new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
    }

    public static Vec3 getCenter(AxisAlignedBB bb) {
        return new Vec3(bb.minX + ((bb.maxX - bb.minX) * 0.5d), bb.minY + ((bb.maxY - bb.minY) * 0.5d), bb.minZ + ((bb.maxZ - bb.minZ) * 0.5d));
    }

    public static double getRotationDifference(Vector2f a, Vector2f b2) {
        return Math.hypot(getAngleDifference(a.getX(), b2.getX()), a.getY() - b2.getY());
    }

    public static float[] getRotationsNeededBall(Entity entity) {
        if (entity == null) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        double xSize = entity.posX - mc.thePlayer.posX;
        double ySize = (entity.posY + (entity.getEyeHeight() / 2.0f)) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double zSize = entity.posZ - mc.thePlayer.posZ;
        double theta = MathHelper.sqrt_double((xSize * xSize) + (zSize * zSize));
        float yaw = ((float) Math.toDegrees(Math.atan2(zSize, xSize))) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(ySize, theta)));
        float playerYaw = mc.thePlayer.rotationYaw;
        float playerPitch = mc.thePlayer.rotationPitch;
        float deltaYaw = MathHelper.wrapAngleTo180_float(yaw - playerYaw);
        float deltaPitch = MathHelper.wrapAngleTo180_float(pitch - playerPitch);
        float newYaw = playerYaw + deltaYaw;
        float newPitch = playerPitch + deltaPitch;
        return new float[]{newYaw % 360.0f, newPitch % 360.0f};
    }

    public static MovingObjectPosition rayTrace(float[] rot, double blockReachDistance, float partialTicks) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 vec31 = mc.thePlayer.getLookCustom(rot[0], rot[1]);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, false);
    }

    public static Vector2f calculate(Vector3d from, Vector3d to) {
        Vector3d diff = to.subtract(from);
        double distance = Math.hypot(diff.getX(), diff.getZ());
        float yaw = ((float) (MathHelper.atan2(diff.getZ(), diff.getX()) * 57.2957763671875d)) - 90.0f;
        float pitch = (float) (-(MathHelper.atan2(diff.getY(), distance) * 57.2957763671875d));
        return new Vector2f(yaw, pitch);
    }

    public static float[] getRotations(Vec3 vec) {
        return getRotations(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static float[] getRotations(BlockPos blockPos) {
        return getRotations(blockPos.getX() + 0.5d, blockPos.getY() + 0.5d, blockPos.getZ() + 0.5d, mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        return getRotations(posX, posY, posZ, mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static float[] getRotations(double rotX, double rotY, double rotZ, double startX, double startY, double startZ) {
        double x = rotX - startX;
        double y = rotY - startY;
        double z = rotZ - startZ;
        double dist = MathHelper.sqrt_double((x * x) + (z * z));
        float yaw = ((float) ((Math.atan2(z, x) * 180.0d) / 3.141592653589793d)) - 90.0f;
        float pitch = (float) (-((Math.atan2(y, dist) * 180.0d) / 3.141592653589793d));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotations(BlockPos blockPos, EnumFacing enumFacing) {
        double d = ((blockPos.getX() + 0.5d) - mc.thePlayer.posX) + (enumFacing.getFrontOffsetX() * 0.25d);
        double d2 = ((blockPos.getZ() + 0.5d) - mc.thePlayer.posZ) + (enumFacing.getFrontOffsetZ() * 0.25d);
        double d3 = ((mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) - blockPos.getY()) - (enumFacing.getFrontOffsetY() * 0.25d);
        double d4 = MathHelper.sqrt_double((d * d) + (d2 * d2));
        float f = ((float) ((Math.atan2(d2, d) * 180.0d) / 3.141592653589793d)) - 90.0f;
        float f2 = (float) ((Math.atan2(d3, d4) * 180.0d) / 3.141592653589793d);
        return new float[]{MathHelper.wrapAngleTo180_float(f), f2};
    }

    public static float getAngleDifference(float a, float b) {
        return ((((a - b) % 360.0f) + 540.0f) % 360.0f) - 180.0f;
    }

    public static float[] getRotationBlock(BlockPos pos) {
        return getRotationsByVec(mc.thePlayer.getPositionVector().addVector(0.0d, mc.thePlayer.getEyeHeight(), 0.0d), new Vec3(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d));
    }

    public static Vec3 getVectorForRotation(Rotation rotation) {
        float yawCos = MathHelper.cos(((-rotation.getYaw()) * 0.017453292f) - 3.1415927f);
        float yawSin = MathHelper.sin(((-rotation.getYaw()) * 0.017453292f) - 3.1415927f);
        float pitchCos = -MathHelper.cos((-rotation.getPitch()) * 0.017453292f);
        float pitchSin = MathHelper.sin((-rotation.getPitch()) * 0.017453292f);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static Vec3 getVectorForRotations(Rotation rotation) {
        float yawCos = MathHelper.cos(((-rotation.getYaw()) * 0.017453292f) - 3.1415927f);
        float yawSin = MathHelper.sin(((-rotation.getYaw()) * 0.017453292f) - 3.1415927f);
        float pitchCos = -MathHelper.cos((-rotation.getPitch()) * 0.017453292f);
        float pitchSin = MathHelper.sin((-rotation.getPitch()) * 0.017453292f);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static float[] getRotationBlock2(BlockPos pos) {
        return getRotationsByVec(mc.thePlayer.getPositionVector().addVector(0.0d, mc.thePlayer.getEyeHeight(), 0.0d), new Vec3(pos.getX() + 0.51d, pos.getY() + 0.51d, pos.getZ() + 0.51d));
    }

    private static float[] getRotationsByVec(Vec3 origin, Vec3 position) {
        Vec3 difference = position.subtract(origin);
        double distance = difference.flat().lengthVector();
        float yaw = ((float) Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord))) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new float[]{yaw, pitch};
    }

    public static void setVisualRotations(float yaw, float pitch) {
        EntityPlayerSP entityPlayerSP = mc.thePlayer;
        mc.thePlayer.renderYawOffset = yaw;
        entityPlayerSP.rotationYawHead = yaw;
        mc.thePlayer.renderPitchHead = pitch;
    }

    public static Vector2f toRotation(Vec3 vec, boolean predict) {
        Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        if (predict) {
            eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
        }
        double diffX = vec.xCoord - eyesPos.xCoord;
        double diffY = vec.yCoord - eyesPos.yCoord;
        double diffZ = vec.zCoord - eyesPos.zCoord;
        return new Vector2f(MathHelper.wrapAngleTo180_float(((float) Math.toDegrees(Math.atan2(diffZ, diffX))) - 90.0f), MathHelper.wrapAngleTo180_float((float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt((diffX * diffX) + (diffZ * diffZ)))))));
    }

    public static Vector2f calculateSimple(Entity entity, double range, double wallRange) {
        AxisAlignedBB aabb = entity.getEntityBoundingBox().contract(-0.05d, -0.05d, -0.05d).contract(0.05d, 0.05d, 0.05d);
        double range2 = range + 0.05d;
        double wallRange2 = wallRange + 0.05d;
        Vec3 eyePos = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 nearest = new Vec3(MathUtils.clamp(eyePos.xCoord, aabb.minX, aabb.maxX), MathUtils.clamp(eyePos.yCoord, aabb.minY, aabb.maxY), MathUtils.clamp(eyePos.zCoord, aabb.minZ, aabb.maxZ));
        Vector2f rotation = toRotation(nearest, false);
        if (nearest.subtract(eyePos).lengthSquared() <= wallRange2 * wallRange2) {
            return rotation;
        }
        MovingObjectPosition result = RayCastUtil.rayCast(rotation, range2, 0.0f, false);
        double maxRange = Math.max(wallRange2, range2);
        if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && result.entityHit == entity && result.hitVec.subtract(eyePos).lengthSquared() <= maxRange * maxRange) {
            return rotation;
        }
        return null;
    }

    public static Vector2f calculate(Entity entity) {
        return calculate(entity.getCustomPositionVector().add(0.0d, Math.max(0.0d, Math.min((mc.thePlayer.posY - entity.posY) + mc.thePlayer.getEyeHeight(), (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.9d)), 0.0d));
    }

    public static Vector2f calculate(Entity entity, boolean adaptive, double range, double wallRange, boolean predict, boolean randomCenter) {
        MovingObjectPosition result;
        MovingObjectPosition result2;
        MovingObjectPosition normalResult;
        if (mc.thePlayer == null) {
            return null;
        }
        double rangeSq = range * range;
        double wallRangeSq = wallRange * wallRange;
        Vector2f simpleRotation = calculateSimple(entity, range, wallRange);
        if (simpleRotation != null) {
            return simpleRotation;
        }
        Vector2f normalRotations = toRotation(getVec(entity), predict);
        if (!randomCenter && (normalResult = RayCastUtil.rayCast(normalRotations, range, 0.0f, false)) != null && normalResult.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            return normalRotations;
        }
        double yStart = 1.0d;
        double yEnd = 0.0d;
        double yStep = -0.5d;
        if (randomCenter && MathUtils.secureRandom.nextBoolean()) {
            yStart = 0.0d;
            yEnd = 1.0d;
            yStep = 0.5d;
        }
        double d = yStart;
        while (true) {
            double yPercent = d;
            if (Math.abs(yEnd - yPercent) > 0.001d) {
                if (randomCenter) {
                    Collections.shuffle(xzPercents);
                }
                Iterator<Double> it = xzPercents.iterator();
                while (it.hasNext()) {
                    double xzPercent = it.next().doubleValue();
                    for (int side = 0; side <= 3; side++) {
                        double xPercent = 0.0d;
                        double zPercent = 0.0d;
                        switch (side) {
                            case 0:
                                xPercent = xzPercent;
                                zPercent = 0.5d;
                                break;
                            case 1:
                                xPercent = xzPercent;
                                zPercent = -0.5d;
                                break;
                            case 2:
                                xPercent = 0.5d;
                                zPercent = xzPercent;
                                break;
                            case 3:
                                xPercent = -0.5d;
                                zPercent = xzPercent;
                                break;
                        }
                        Vec3 Vec3 = getVec(entity).add(new Vec3((entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent, (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent, (entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent));
                        double distanceSq = Vec3.squareDistanceTo(mc.thePlayer.getPositionEyes(1.0f));
                        Rotation rotation = toRotationRot(Vec3, predict);
                        rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
                        rotation.distanceSq = distanceSq;
                        if (distanceSq <= wallRangeSq && (result2 = RayCastUtil.rayCast(rotation.toVec2f(), wallRange, 0.0f, true)) != null && result2.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                            return rotation.toVec2f();
                        }
                        if (distanceSq <= rangeSq && (result = RayCastUtil.rayCast(rotation.toVec2f(), range, 0.0f, false)) != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                            return rotation.toVec2f();
                        }
                    }
                }
                d = yPercent + yStep;
            } else {
                return null;
            }
        }
    }

    public static Vec3 getVec(Entity entity) {
        return new Vec3(entity.posX, entity.posY, entity.posZ);
    }

    public static Rotation toRotationRot(Vec3 vec, boolean predict) {
        Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        if (predict) {
            eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
        }
        double diffX = vec.xCoord - eyesPos.xCoord;
        double diffY = vec.yCoord - eyesPos.yCoord;
        double diffZ = vec.zCoord - eyesPos.zCoord;
        return new Rotation(MathHelper.wrapAngleTo180_float(((float) Math.toDegrees(Math.atan2(diffZ, diffX))) - 90.0f), MathHelper.wrapAngleTo180_float((float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt((diffX * diffX) + (diffZ * diffZ)))))));
    }

    public static float[] getHVHRotation(Entity entity, double maxRange) {
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        Vec3 BestPos = EntityPlayerSP.getNearestPointBB(mc.thePlayer.getPositionEyes(1.0f), entity.getEntityBoundingBox());
        Location myEyePos = new Location(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        double diffY = BestPos.yCoord - myEyePos.getY();
        double dist = MathHelper.sqrt_double((diffX * diffX) + (diffZ * diffZ));
        float yaw = ((float) ((Math.atan2(diffZ, diffX) * 180.0d) / 3.141592653589793d)) - 90.0f;
        float pitch = (float) (-((Math.atan2(diffY, dist) * 180.0d) / 3.141592653589793d));
        return new float[]{yaw, pitch};
    }

    public static Vector2f getNewRotation(Entity target) {
        Vec3 vec3;
        double yDist = target.posY - mc.thePlayer.posY;
        if (yDist >= 1.7d) {
            vec3 = new Vec3(target.posX, target.posY, target.posZ);
        } else {
            vec3 = yDist <= -1.7d ? new Vec3(target.posX, target.posY + target.getEyeHeight(), target.posZ) : new Vec3(target.posX, target.posY + (target.getEyeHeight() / 2.0f), target.posZ);
        }
        Vec3 pos = vec3;
        Vec3 vec = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        double xDist = pos.xCoord - vec.xCoord;
        double yDist2 = pos.yCoord - vec.yCoord;
        double zDist = pos.zCoord - vec.zCoord;
        float yaw = ((float) Math.toDegrees(Math.atan2(zDist, xDist))) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(yDist2, Math.sqrt((xDist * xDist) + (zDist * zDist)))));
        return new Vector2f(yaw, Math.min(Math.max(pitch, -90.0f), 90.0f));
    }

    public static Vector2f calculate(Entity entity, boolean adaptive, double range) {
        Vector2f normalRotations = calculate(entity);
        if (!adaptive || RayCastUtil.rayCast(normalRotations, range).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            return normalRotations;
        }
        double d = 1.0d;
        while (true) {
            double yPercent = d;
            if (yPercent >= 0.0d) {
                double d2 = 1.0d;
                while (true) {
                    double xPercent = d2;
                    if (xPercent >= -0.5d) {
                        double d3 = 1.0d;
                        while (true) {
                            double zPercent = d3;
                            if (zPercent >= -0.5d) {
                                Vector2f adaptiveRotations = calculate(entity.getCustomPositionVector().add((entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent, (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent, (entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent));
                                if (RayCastUtil.rayCast(adaptiveRotations, range).typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                                    d3 = zPercent - 0.5d;
                                } else {
                                    return adaptiveRotations;
                                }
                            }
                        }
                    }
                    d2 = xPercent - 0.5d;
                }
            } else {
                return normalRotations;
            }
        }
    }

    public static Vector2f calculate(Vec3 to, EnumFacing enumFacing) {
        return calculate(new Vector3d(to.xCoord, to.yCoord, to.zCoord), enumFacing);
    }

    public static Vector2f calculate(Vec3 to) {
        return calculate(mc.thePlayer.getCustomPositionVector().add(0.0d, mc.thePlayer.getEyeHeight(), 0.0d), new Vector3d(to.xCoord, to.yCoord, to.zCoord));
    }

    public static Vector2f calculate(Vector3d to) {
        return calculate(mc.thePlayer.getCustomPositionVector().add(0.0d, mc.thePlayer.getEyeHeight(), 0.0d), to);
    }

    public static Vector2f calculate(Vector3d position, EnumFacing enumFacing) {
        double x = position.getX() + 0.5d;
        double y = position.getY() + 0.5d;
        double z = position.getZ() + 0.5d;
        return calculate(new Vector3d(x + (enumFacing.getDirectionVec().getX() * 0.5d), y + (enumFacing.getDirectionVec().getY() * 0.5d), z + (enumFacing.getDirectionVec().getZ() * 0.5d)));
    }

    public static Vector2f applySensitivityPatch(Vector2f rotation) {
        Vector2f previousRotation = mc.thePlayer.getPreviousRotation();
        float mouseSensitivity = (float) ((mc.gameSettings.mouseSensitivity * (1.0d + (Math.random() / 1.0E7d)) * 0.6000000238418579d) + 0.20000000298023224d);
        double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f * 0.15d;
        float yaw = previousRotation.x + ((float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier));
        float pitch = previousRotation.y + ((float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier));
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f));
    }

    public static Vector2f applySensitivityPatch(Vector2f rotation, Vector2f previousRotation) {
        float mouseSensitivity = (float) ((mc.gameSettings.mouseSensitivity * (1.0d + (Math.random() / 1.0E7d)) * 0.6000000238418579d) + 0.20000000298023224d);
        double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f * 0.15d;
        float yaw = previousRotation.x + ((float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier));
        float pitch = previousRotation.y + ((float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier));
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f));
    }

    public static float[] applyGCDFix(float[] prevRotation, float[] currentRotation) {
        float f = (float) ((mc.gameSettings.mouseSensitivity * (1.0d + (Math.random() / 100000.0d)) * 0.6000000238418579d) + 0.20000000298023224d);
        double gcd = f * f * f * 8.0f * 0.15d;
        float yaw = prevRotation[0] + ((float) (Math.round((currentRotation[0] - prevRotation[0]) / gcd) * gcd));
        float pitch = prevRotation[1] + ((float) (Math.round((currentRotation[1] - prevRotation[1]) / gcd) * gcd));
        return new float[]{yaw, pitch};
    }

    public static Vector2f relateToPlayerRotation(Vector2f rotation) {
        Vector2f previousRotation = mc.thePlayer.getPreviousRotation();
        float yaw = previousRotation.x + MathHelper.wrapAngleTo180_float(rotation.x - previousRotation.x);
        float pitch = MathHelper.clamp_float(rotation.y, -90.0f, 90.0f);
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f resetRotation(Vector2f rotation) {
        if (rotation == null) {
            return null;
        }
        float yaw = rotation.x + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - rotation.x);
        float pitch = mc.thePlayer.rotationPitch;
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f smooth(Vector2f lastRotation, Vector2f targetRotation, double speed) {
        float yaw = targetRotation.x;
        float pitch = targetRotation.y;
        float lastYaw = lastRotation.x;
        float lastPitch = lastRotation.y;
        if (speed != 0.0d) {
            float rotationSpeed = (float) speed;
            double deltaYaw = MathHelper.wrapAngleTo180_float(targetRotation.x - lastRotation.x);
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
            for (int i = 1; i <= ((int) ((Minecraft.getDebugFPS() / 20.0f) + (Math.random() * 10.0d))); i++) {
                if (Math.abs(moveYaw) + Math.abs(movePitch) > 1.0f) {
                    yaw = (float) (yaw + ((Math.random() - 0.5d) / 1000.0d));
                    pitch = (float) (pitch - (Math.random() / 200.0d));
                }
                Vector2f rotations = new Vector2f(yaw, pitch);
                Vector2f fixedRotations = applySensitivityPatch(rotations);
                yaw = fixedRotations.x;
                pitch = Math.max(-90.0f, Math.min(90.0f, fixedRotations.y));
            }
        }
        return new Vector2f(yaw, pitch);
    }
}
