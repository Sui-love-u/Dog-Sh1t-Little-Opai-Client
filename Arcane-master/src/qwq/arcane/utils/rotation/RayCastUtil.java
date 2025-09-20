package qwq.arcane.utils.rotation;

import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.optifine.reflect.Reflector;
import qwq.arcane.Client;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.math.Vector2f;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/rotation/RayCastUtil.class */
public final class RayCastUtil implements Instance {

    /* loaded from: Arcane 8.10.jar:qwq/arcane/utils/rotation/RayCastUtil$IEntityFilter.class */
    public interface IEntityFilter {
        boolean canRaycast(Entity entity);
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range) {
        return rayCast(rotation, range, 0.0f);
    }

    public static Entity raycastEntity(double range, IEntityFilter entityFilter) {
        return raycastEntity(range, Client.Instance.getRotationManager().lastRotation.x, Client.Instance.getRotationManager().lastRotation.y, entityFilter);
    }

    public static Entity raycastEntity(double range, float yaw, float pitch, IEntityFilter entityFilter) {
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (renderViewEntity != null && mc.theWorld != null) {
            double blockReachDistance = range;
            Vec3 eyePosition = renderViewEntity.getPositionEyes(1.0f);
            float yawCos = MathHelper.cos(((-yaw) * 0.017453292f) - 3.1415927f);
            float yawSin = MathHelper.sin(((-yaw) * 0.017453292f) - 3.1415927f);
            float pitchCos = -MathHelper.cos((-pitch) * 0.017453292f);
            float pitchSin = MathHelper.sin((-pitch) * 0.017453292f);
            Vec3 entityLook = new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
            Vec3 vector = eyePosition.addVector(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance);
            List<Entity> entityList = mc.theWorld.getEntitiesInAABBexcluding(renderViewEntity, renderViewEntity.getEntityBoundingBox().addCoord(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance).expand(1.0d, 1.0d, 1.0d), Predicates.and(EntitySelectors.NOT_SPECTATING, (v0) -> {
                return v0.canBeCollidedWith();
            }));
            Entity pointedEntity = null;
            for (Entity entity : entityList) {
                if (entityFilter.canRaycast(entity)) {
                    float collisionBorderSize = entity.getCollisionBorderSize();
                    AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                    MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(eyePosition, vector);
                    if (axisAlignedBB.isVecInside(eyePosition)) {
                        if (blockReachDistance >= 0.0d) {
                            pointedEntity = entity;
                            blockReachDistance = 0.0d;
                        }
                    } else if (movingObjectPosition != null) {
                        double eyeDistance = eyePosition.distanceTo(movingObjectPosition.hitVec);
                        if (eyeDistance < blockReachDistance || blockReachDistance == 0.0d) {
                            if (entity != renderViewEntity.ridingEntity || Reflector.callBoolean(entity, Reflector.ForgeEntity_canRiderInteract, new Object[0])) {
                                pointedEntity = entity;
                                blockReachDistance = eyeDistance;
                            } else if (blockReachDistance == 0.0d) {
                                pointedEntity = entity;
                            }
                        }
                    }
                }
            }
            return pointedEntity;
        }
        return null;
    }

    private static MovingObjectPosition rayTraceBlocks(float yaw, float pitch, float reach) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 vec31 = Entity.getVectorForRotation(pitch, yaw);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach);
        return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, false);
    }

    public static boolean isOnBlock(EnumFacing facing, BlockPos position, boolean strict, float reach, float yaw, float pitch) {
        MovingObjectPosition blockHitResult = rayTraceBlocks(yaw, pitch, reach);
        if (blockHitResult != null && blockHitResult.getBlockPos().getX() == position.getX() && blockHitResult.getBlockPos().getY() == position.getY() && blockHitResult.getBlockPos().getZ() == position.getZ()) {
            if (strict) {
                mc.objectMouseOver = blockHitResult;
                return blockHitResult.sideHit == facing;
            }
            mc.objectMouseOver = blockHitResult;
            return true;
        }
        return false;
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand) {
        return rayCast(rotation, range, expand, mc.thePlayer);
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand, boolean throughWall) {
        return rayCast(rotation, range, expand, mc.thePlayer, throughWall);
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand, Entity entity, boolean throughWall) {
        float f = mc.timer.renderPartialTicks;
        if (entity != null && mc.theWorld != null) {
            MovingObjectPosition objectMouseOver = entity.rayTraceCustom(range, rotation.x, rotation.y);
            double d1 = range;
            Vec3 vec3 = entity.getPositionEyes(2.0f);
            if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && !throughWall) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }
            EntityPlayerSP entityPlayerSP = mc.thePlayer;
            Vec3 vec31 = EntityPlayerSP.getVectorForRotation(rotation.y, rotation.x);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(1.0d, 1.0d, 1.0d), Predicates.and(EntitySelectors.NOT_SPECTATING, (v0) -> {
                return v0.canBeCollidedWith();
            }));
            double d2 = d1;
            for (Entity entity1 : list) {
                float f1 = entity1.getCollisionBorderSize() + expand;
                AxisAlignedBB original = entity1.getEntityBoundingBox().offset(entity.posX - entity.prevPosX, entity.posY - entity.prevPosY, entity.posZ - entity.prevPosZ);
                AxisAlignedBB axisalignedbb = f1 >= 0.0f ? original.expand(f1, f1, f1).expand(-f1, -f1, -f1) : original.contract(f1, f1, f1).contract(-f1, -f1, -f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0d) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0d;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0d) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }
            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }
            return objectMouseOver;
        }
        return null;
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand, Entity entity) {
        float partialTicks = mc.timer.renderPartialTicks;
        if (entity != null && mc.theWorld != null) {
            MovingObjectPosition objectMouseOver = entity.rayTraceCustom(range, rotation.x, rotation.y);
            double d1 = range;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }
            EntityPlayerSP entityPlayerSP = mc.thePlayer;
            Vec3 vec31 = EntityPlayerSP.getVectorForRotation(rotation.y, rotation.x);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(1.0d, 1.0d, 1.0d), Predicates.and(EntitySelectors.NOT_SPECTATING, (v0) -> {
                return v0.canBeCollidedWith();
            }));
            double d2 = d1;
            for (Entity entity1 : list) {
                float f1 = entity1.getCollisionBorderSize() + expand;
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0d) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0d;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0d) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }
            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }
            return objectMouseOver;
        }
        return null;
    }

    public static boolean overBlock(Vector2f rotation, EnumFacing enumFacing, BlockPos pos, boolean strict) {
        MovingObjectPosition movingObjectPosition = mc.thePlayer.rayTraceCustom(4.5d, rotation.x, rotation.y);
        if (movingObjectPosition == null) {
            return false;
        }
        Vec3 hitVec = movingObjectPosition.hitVec;
        return hitVec != null && movingObjectPosition.getBlockPos().equals(pos) && (!strict || movingObjectPosition.sideHit == enumFacing);
    }

    public static boolean overBlock(EnumFacing enumFacing, BlockPos pos, boolean strict) {
        MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
        if (movingObjectPosition == null) {
            return false;
        }
        Vec3 hitVec = movingObjectPosition.hitVec;
        return hitVec != null && movingObjectPosition.getBlockPos().equals(pos) && (!strict || movingObjectPosition.sideHit == enumFacing);
    }

    public static Boolean overBlock(Vector2f rotation, BlockPos pos) {
        return Boolean.valueOf(overBlock(rotation, EnumFacing.UP, pos, false));
    }

    public static Boolean overBlock(Vector2f rotation, BlockPos pos, EnumFacing enumFacing) {
        return Boolean.valueOf(overBlock(rotation, enumFacing, pos, true));
    }
}
