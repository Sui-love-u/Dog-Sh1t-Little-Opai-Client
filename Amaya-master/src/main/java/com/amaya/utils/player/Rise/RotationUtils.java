package com.amaya.utils.player.Rise;

import com.amaya.events.EventManager;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.utils.math.MathUtils;
import com.amaya.utils.math.Rotation;
import com.amaya.utils.math.Vector2f;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;

import java.util.*;

public final class RotationUtils {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static net.minecraft.util.Vec3 getNearestPointBB(final net.minecraft.util.Vec3 eye, final AxisAlignedBB box) {
		final double[] origin = { eye.xCoord, eye.yCoord, eye.zCoord };
		final double[] destMins = { box.minX, box.minY, box.minZ };
		final double[] destMaxs = { box.maxX, box.maxY, box.maxZ };
		for (int i = 0; i < 3; ++i) {
			if (origin[i] > destMaxs[i]) {
				origin[i] = destMaxs[i];
			}
			else if (origin[i] < destMins[i]) {
				origin[i] = destMins[i];
			}
		}
		return new net.minecraft.util.Vec3(origin[0], origin[1], origin[2]);
	}
	public static net.minecraft.util.Vec3 getHitOrigin(final Entity entity) {
		return new net.minecraft.util.Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
	}

	public static AxisAlignedBB getHittableBoundingBox(final Entity entity,
													   final double boundingBoxScale) {
		return entity.getEntityBoundingBox().expand(boundingBoxScale, boundingBoxScale, boundingBoxScale);
	}
	public static Vector2f getRotations(double posX, double posY, double posZ) {
		EntityPlayerSP player = mc.thePlayer;
		double x = posX - player.posX;
		double y = posY - (player.posY + (double) player.getEyeHeight());
		double z = posZ - player.posZ;
		double dist = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
		float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / 3.141592653589793));
		return new Vector2f(yaw, pitch);
	}
	public static float[] getRotations(final Vector2f lastRotations,
									   final float smoothing,
									   final net.minecraft.util.Vec3 start,
									   final net.minecraft.util.Vec3 dst) {
		// Get rotations from start - dst
		final float[] rotations = getRotations(start, dst);
		// Apply smoothing to them
		applySmoothing(lastRotations, smoothing, rotations);
		return rotations;
	}

	public static void applySmoothing(final Vector2f lastRotations,
									  final float smoothing,
									  final float[] dstRotation) {
		if (smoothing > 0.0F) {
			final float yawChange = MathHelper.wrapAngleTo180_float(dstRotation[0] - lastRotations.getX());
			final float pitchChange = MathHelper.wrapAngleTo180_float(dstRotation[1] - lastRotations.getY());

			final float smoothingFactor = Math.max(1.0F, smoothing / 10.0F);

			dstRotation[0] = lastRotations.getX() + yawChange / smoothingFactor;
			dstRotation[1] = Math.max(Math.min(90.0F, lastRotations.getY() + pitchChange / smoothingFactor), -90.0F);
		}
	}
	private static final double RAD_TO_DEG = 180.0 / Math.PI;
	public static float[] getRotations(final net.minecraft.util.Vec3 start,
									   final net.minecraft.util.Vec3 dst) {
		final double xDif = dst.xCoord - start.xCoord;
		final double yDif = dst.yCoord - start.yCoord;
		final double zDif = dst.zCoord - start.zCoord;

		final double distXZ = Math.sqrt(xDif * xDif + zDif * zDif);

		return new float[]{
				(float) (Math.atan2(zDif, xDif) * RAD_TO_DEG) - 90.0F,
				(float) (-(Math.atan2(yDif, distXZ) * RAD_TO_DEG))
		};
	}

	public static Vector2f calculateSimple(final Entity entity, double range, double wallRange) {
		AxisAlignedBB aabb = entity.getEntityBoundingBox().contract(-0.05, -0.05, -0.05).contract(0.05, 0.05, 0.05);
		range += 0.05;
		wallRange += 0.05;
		net.minecraft.util.Vec3 eyePos = mc.thePlayer.getPositionEyes(1F);
		net.minecraft.util.Vec3 nearest = new net.minecraft.util.Vec3(
				MathUtils.clamp(eyePos.xCoord, aabb.minX, aabb.maxX),
				MathUtils.clamp(eyePos.yCoord, aabb.minY, aabb.maxY),
				MathUtils.clamp(eyePos.zCoord, aabb.minZ, aabb.maxZ)
		);
		Vector2f rotation = toRotation(nearest, false);
		if (nearest.subtract(eyePos).lengthSquared() <= wallRange * wallRange) {
			return rotation;
		}

		MovingObjectPosition result = rayCast(rotation, range, 0F, false);
		final double maxRange = Math.max(wallRange, range);
		if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && result.entityHit == entity && result.hitVec.subtract(eyePos).lengthSquared() <= maxRange * maxRange) {
			return rotation;
		}

		return null;
	}
	public static net.minecraft.util.Vec3 getVec(Entity entity) {
		return new net.minecraft.util.Vec3(entity.posX, entity.posY, entity.posZ);
	}
	private static List<Double> xzPercents = Arrays.asList(0.5, 0.4, 0.3, 0.2, 0.1, 0.0, -0.1, -0.2, -0.3, -0.4, -0.5);

	public static Vector2f calculate(final Entity entity, final boolean adaptive, final double range, final double wallRange, boolean predict, boolean randomCenter) {
		if (mc.thePlayer == null) return null;

		final double rangeSq = range * range;
		final double wallRangeSq = wallRange * wallRange;

		Vector2f simpleRotation = calculateSimple(entity, range, wallRange);
		if (simpleRotation != null) return simpleRotation;

		Vector2f normalRotations = toRotation(getVec(entity), predict);

		if (!randomCenter) {
			MovingObjectPosition normalResult = rayCast(normalRotations, range, 0F, false);
			if (normalResult != null && normalResult.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
				return normalRotations;
			}
		}

		double yStart = 1, yEnd = 0, yStep = -0.5;
		if (randomCenter && MathUtils.secureRandom.nextBoolean()) {
			yStart = 0;
			yEnd = 1;
			yStep = 0.5;
		}
		for (double yPercent = yStart; Math.abs(yEnd - yPercent) > 1e-3; yPercent += yStep) {
			double xzStart = 0.5, xzEnd = -0.5, xzStep = -0.1;
			if (randomCenter) {
				Collections.shuffle(xzPercents);
			}
			for (double xzPercent : xzPercents) {
				for (int side = 0; side <= 3; side++) {
					double xPercent = 0F, zPercent = 0F;
					switch (side) {
						case 0: {
							xPercent = xzPercent;
							zPercent = 0.5F;
							break;
						}
						case 1: {
							xPercent = xzPercent;
							zPercent = -0.5F;
							break;

						}
						case 2: {
							xPercent = 0.5F;
							zPercent = xzPercent;
							break;

						}
						case 3: {
							xPercent = -0.5F;
							zPercent = xzPercent;
							break;
						}
					}
					net.minecraft.util.Vec3 Vec3 = getVec(entity).add(
							new net.minecraft.util.Vec3((entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent,
									(entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent,
									(entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent));
					double distanceSq = Vec3.squareDistanceTo(mc.thePlayer.getPositionEyes(1F));

					Rotation rotation = toRotationRot(Vec3, predict);
					rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
					rotation.distanceSq = distanceSq;

					if (distanceSq <= wallRangeSq) {
						MovingObjectPosition result = rayCast(rotation.toVec2f(), wallRange, 0F, true);
						if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
							return rotation.toVec2f();
						}
					}

					if (distanceSq <= rangeSq) {
						MovingObjectPosition result = rayCast(rotation.toVec2f(), range, 0F, false);
						if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
							return rotation.toVec2f();
						}
					}
				}
			}
		}

		return null;
	}
	public static Rotation toRotationRot(final net.minecraft.util.Vec3 vec, final boolean predict) {
		final net.minecraft.util.Vec3 eyesPos = new net.minecraft.util.Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY +
				mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		if (predict) eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

		final double diffX = vec.xCoord - eyesPos.xCoord;
		final double diffY = vec.yCoord - eyesPos.yCoord;
		final double diffZ = vec.zCoord - eyesPos.zCoord;

		return new Rotation(MathHelper.wrapAngleTo180_float(
				(float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
		), MathHelper.wrapAngleTo180_float(
				(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
		));
	}

	public static float[] getAngles(Entity entity) {
		if (entity == null)
			return null;
		final EntityPlayerSP thePlayer = mc.thePlayer;

		final double diffX = entity.posX - thePlayer.posX,
				diffY = entity.posY + entity.getEyeHeight() * 0.9 - (thePlayer.posY + thePlayer.getEyeHeight()),
				diffZ = entity.posZ - thePlayer.posZ, dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ); // @on

		final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F,
				pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		return new float[]{thePlayer.rotationYaw + MathHelper.wrapDegrees(yaw - thePlayer.rotationYaw), thePlayer.rotationPitch + MathHelper.wrapDegrees(pitch - thePlayer.rotationPitch)};
	}
	public static MovingObjectPosition rayCast(final Vector2f rotation, final double range, final float expand, final boolean throughWall) {
		return rayCast(rotation, range, expand, mc.thePlayer, throughWall);
	}
	public static MovingObjectPosition rayCast(final Vector2f rotation, final double range, final float expand, Entity entity, boolean throughWall) {
		final float partialTicks = mc.timer.renderPartialTicks;
		MovingObjectPosition objectMouseOver;

		if (entity != null && mc.theWorld != null) {
			objectMouseOver = entity.rayTraceCustom(range, rotation.x, rotation.y);
			double d1 = range;
			final net.minecraft.util.Vec3 vec3 = entity.getPositionEyes(2F);

			if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && !throughWall) {
				d1 = objectMouseOver.hitVec.distanceTo(vec3);
				// RayCastUtil.rayCast(new Rotation(mc.player.rotationYaw, mc.player.rotationPitch), 3, 0F, false)
			}

			final net.minecraft.util.Vec3 vec31 = mc.thePlayer.getVectorForRotation(rotation.y, rotation.x);
			final net.minecraft.util.Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
			Entity pointedEntity = null;
			net.minecraft.util.Vec3 vec33 = null;
			final float f = 1.0F;
			final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
			double d2 = d1;

			for (final Entity entity1 : list) {
				final float f1 = entity1.getCollisionBorderSize() + expand;
				AxisAlignedBB original = entity1.getEntityBoundingBox();
				// predict
				original = original.offset(entity.posX - entity.prevPosX, entity.posY - entity.prevPosY, entity.posZ - entity.prevPosZ);
				final AxisAlignedBB axisalignedbb = f1 >= 0 ? original.expand(f1, f1, f1).expand(-f1, -f1, -f1) : original.contract(f1, f1, f1).contract(-f1, -f1, -f1);
				final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

				if (axisalignedbb.isVecInside(vec3)) {
					if (d2 >= 0.0D) {
						pointedEntity = entity1;
						vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
						d2 = 0.0D;
					}
				} else if (movingobjectposition != null) {
					final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

					if (d3 < d2 || d2 == 0.0D) {
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
	public static Vector2f getNewRotation(Entity target) {
		double yDist = target.posY - mc.thePlayer.posY;
		net.minecraft.util.Vec3 pos = yDist >= 1.7 ? new net.minecraft.util.Vec3(target.posX, target.posY, target.posZ) :
				(yDist <= -1.7 ? new net.minecraft.util.Vec3(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ) :
						new net.minecraft.util.Vec3(target.posX, target.posY + (double)(target.getEyeHeight() / 2.0f), target.posZ));

		net.minecraft.util.Vec3 vec = new net.minecraft.util.Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
		double xDist = pos.xCoord - vec.xCoord;
		double yDist2 = pos.yCoord - vec.yCoord;
		double zDist = pos.zCoord - vec.zCoord;
		float yaw = (float)Math.toDegrees(Math.atan2(zDist, xDist)) - 90.0f;
		float pitch = (float)(-Math.toDegrees(Math.atan2(yDist2, Math.sqrt(xDist * xDist + zDist * zDist))));

		return new Vector2f(yaw, Math.min(Math.max(pitch, -90.0f), 90.0f));
	}
	public static float[] getHVHRotation(Entity entity, double maxRange) {
		if (entity == null) {
			return null;
		} else {
			double diffX = entity.posX - mc.thePlayer.posX;
			double diffZ = entity.posZ - mc.thePlayer.posZ;
			net.minecraft.util.Vec3 BestPos = getNearestPointBB(mc.thePlayer.getPositionEyes(1f), entity.getEntityBoundingBox());
			Location myEyePos = new Location(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY +
					mc.thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);

			double diffY;

			diffY = BestPos.yCoord - myEyePos.getY();
			double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
			float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
			float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0D / 3.141592653589793D));
			return new float[]{yaw, pitch};
		}
	}

	public static void setVisualRotations(float yaw, float pitch) {

		mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = yaw;


		mc.thePlayer.renderPitchHead = pitch;
	}
	public static Rotation getPlayerRotation() {
		return new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
	}
	public static float[] getBlockPosRotation(BlockPos pos) {
		return RotationUtils.getRotationFromPosition(pos.getX(), pos.getZ(), pos.getY());
	}
	public static float[] getRotationFromPosition(double x, double z, double y) {
		double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
		double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
		double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;
		double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
		float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
		return new float[]{yaw, pitch};
	}
	public static Vector2f smoothReal(Vector2f targetRotation) {
		float yaw = targetRotation.x;
		float pitch = targetRotation.y;
		float randomYaw = (float) (Math.random() * 2.0 - 1.0) / 10.0f;
		float randomPitch = (float) (Math.random() * 2.0 - 1.0) / 10.0f;
		Vector2f rotations = new Vector2f(yaw += randomYaw, pitch += randomPitch);
		yaw = MathHelper.wrapDegrees(rotations.x);
		pitch = MathHelper.clamp_float(rotations.y, -90.0f, 90.0f);
		return new Vector2f(yaw, pitch);
	}
	public static Vector2f getThrowRotation(Entity entity, double maxRange) {
		if (entity == null) {
			return null;
		}
		Minecraft mc = Minecraft.getMinecraft();
		double deltaX = entity.posX - mc.thePlayer.posX - mc.thePlayer.motionX;
		double deltaY = entity.posY + (double)entity.getEyeHeight() - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
		double deltaZ = entity.posZ - mc.thePlayer.posZ - mc.thePlayer.motionZ;
		double horizontalDistance = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
		float yaw = (float)(Math.atan2(deltaZ, deltaX) * 180.0 / Math.PI) - 90.0f;
		float pitch = (float)(-(Math.atan2(deltaY, horizontalDistance) * 180.0 / Math.PI));
		float finalYaw = mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
		float finalPitch = mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch);
		return new Vector2f(finalYaw, finalPitch);
	}
	public static float[] getRotationBlock(BlockPos pos) {
		return getRotationsByVec(mc.thePlayer.getPositionVector().addVector(0.0D, (double)mc.thePlayer.getEyeHeight(), 0.0D), new net.minecraft.util.Vec3((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D));
	}
	public static float[] getRotationBlock2(final BlockPos pos) {
		return getRotationsByVec(mc.thePlayer.getPositionVector().addVector(0.0, mc.thePlayer.getEyeHeight(), 0.0), new net.minecraft.util.Vec3(pos.getX() + 0.51, pos.getY() + 0.51, pos.getZ() + 0.51));
	}
	private static float[] getRotationsByVec(net.minecraft.util.Vec3 origin, net.minecraft.util.Vec3 position) {
		net.minecraft.util.Vec3 difference = position.subtract(origin);
		double distance = difference.flat().lengthVector();
		float yaw = (float)Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0F;
		float pitch = (float)(-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
		return new float[]{yaw, pitch};
	}
	private static final Random random = new Random();
	public static float[] getBlockRotations(double x, double y, double z) {
		double var4 = x - mc.thePlayer.posX + 0.5;
		double var6 = z - mc.thePlayer.posZ + 0.5;
		double var8 = y - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight() - 1.0);
		double var14 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
		float var12 = (float)(Math.atan2(var6, var4) * 180.0 / Math.PI) - 90.0f;
		return new float[]{var12, (float)(-Math.atan2(var8, var14) * 180.0 / Math.PI)};
	}
	private static int keepLength;

	public static Rotation targetRotation;
	public static Rotation serverRotation = new Rotation(0F, 0F);

	public static boolean keepCurrentRotation = false;

	private static double x = random.nextDouble();
	private static double y = random.nextDouble();
	private static double z = random.nextDouble();

	public RotationUtils(){
		EventManager.register(this);
	}

	/**
	 * Allows you to check if your crosshair is over your target entity
	 *
	 * @param targetEntity your target entity
	 * @param blockReachDistance your reach
	 * @return if crosshair is over target
	 */


	/**
	 * Face block
	 *
	 * @param blockPos target block
	 */
	public static VecRotation faceBlock(final BlockPos blockPos) {
		if (blockPos == null)
			return null;

		VecRotation vecRotation = null;

		for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
			for (double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
				for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
					final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
					final Vec3 posVec = new Vec3(blockPos).addVector(xSearch, ySearch, zSearch);
					final double dist = eyesPos.distanceTo(posVec);

					final double diffX = posVec.getX() - eyesPos.getX();
					final double diffY = posVec.getY() - eyesPos.getY();
					final double diffZ = posVec.getZ() - eyesPos.getZ();

					final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

					final Rotation rotation = new Rotation(
							MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
							MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
					);

					final Vec3 rotationVector = getVectorForRotation(rotation);
					final Vec3 vector = eyesPos.addVector(rotationVector.getX() * dist, rotationVector.getY() * dist,
							rotationVector.getZ() * dist);
					final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(new net.minecraft.util.Vec3(eyesPos.getX(),eyesPos.getY(),eyesPos.getZ()), new net.minecraft.util.Vec3(vector.getX(),vector.getY(),vector.getZ()), false,
							false, true);

					if (obj != null && obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
						final VecRotation currentVec = new VecRotation(posVec, rotation);

						if (vecRotation == null || getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))
							vecRotation = currentVec;
					}
				}
			}
		}

		return vecRotation;
	}

	/**
	 * Face target with bow
	 *
	 * @param target      your enemy
	 * @param silent      client side rotations
	 * @param predict     predict new enemy position
	 * @param predictSize predict size of predict
	 */
	public static void faceBow(final Entity target, final boolean silent, final boolean predict, final float predictSize) {
		final EntityPlayerSP player = mc.thePlayer;

		final double posX = target.posX + (predict ? (target.posX - target.prevPosX) * predictSize : 0) - (player.posX + (predict ? (player.posX - player.prevPosX) : 0));
		final double posY = target.getEntityBoundingBox().minY + (predict ? (target.getEntityBoundingBox().minY - target.prevPosY) * predictSize : 0) + target.getEyeHeight() - 0.15 - (player.getEntityBoundingBox().minY + (predict ? (player.posY - player.posX) : 0)) - player.getEyeHeight();
		final double posZ = target.posZ + (predict ? (target.posZ - target.prevPosZ) * predictSize : 0) - (player.posZ + (predict ? (player.posZ - player.prevPosZ) : 0));
		final double posSqrt = Math.sqrt(posX * posX + posZ * posZ);

		float velocity = player.getItemInUseDuration() / 20F;
		velocity = (velocity * velocity + velocity * 2) / 3;

		if (velocity > 1) velocity = 1;

		final Rotation rotation = new Rotation(
				(float) (Math.atan2(posZ, posX) * 180 / Math.PI) - 90,
				(float) -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity - 0.006F * (0.006F * (posSqrt * posSqrt) + 2 * posY * (velocity * velocity)))) / (0.006F * posSqrt)))
		);

		if (silent)
			setTargetRotation(rotation);
		else
			limitAngleChange(new Rotation(player.rotationYaw, player.rotationPitch), rotation, 10 +
					new Random().nextInt(6)).toPlayer(mc.thePlayer);
	}

	/**
	 * Translate vec to rotation
	 *
	 * @param vec     target vec
	 * @param predict predict new location of your body
	 * @return rotation
	 */
	public static Vector2f toRotation(final net.minecraft.util.Vec3 vec, final boolean predict) {
		final net.minecraft.util.Vec3 eyesPos = new net.minecraft.util.Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY +
				mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		if (predict) eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

		final double diffX = vec.xCoord - eyesPos.xCoord;
		final double diffY = vec.yCoord - eyesPos.yCoord;
		final double diffZ = vec.zCoord - eyesPos.zCoord;

		return new Vector2f(MathHelper.wrapAngleTo180_float(
				(float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
		), MathHelper.wrapAngleTo180_float(
				(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
		));
	}
	public static Rotation toRotation(final Vec3 vec, final boolean predict) {
		final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY +
				mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		if (predict)
			eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

		final double diffX = vec.getX() - eyesPos.getX();
		final double diffY = vec.getY() - eyesPos.getY();
		final double diffZ = vec.getZ() - eyesPos.getZ();

		return new Rotation(MathHelper.wrapAngleTo180_float(
				(float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
		), MathHelper.wrapAngleTo180_float(
				(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
		));
	}

	/**
	 * Get the center of a box
	 *
	 * @param bb your box
	 * @return center of box
	 */
	public static Vec3 getCenter(final AxisAlignedBB bb) {
		return new Vec3(bb.minX + (bb.maxX) - bb.minX * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}
	/**
	 * Search good center
	 *
	 * @param bb           enemy box
	 * @param outborder    outborder option
	 * @param random       random option
	 * @param predict      predict option
	 * @param throughWalls throughWalls option
	 * @return center
	 */
	public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean outborder, final boolean random,
										   final boolean predict, final boolean throughWalls, final float distance) {
		if (outborder) {
			final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
			return new VecRotation(vec3, toRotation(vec3, predict));
		}

		final Vec3 randomVec = new Vec3(bb.minX + (bb.maxX - bb.minX) * x * 0.8, bb.minY + (bb.maxY - bb.minY) * y * 0.8, bb.minZ + (bb.maxZ - bb.minZ) * z * 0.8);
		final Rotation randomRotation = toRotation(randomVec, predict);

		final net.minecraft.util.Vec3 eyes = mc.thePlayer.getPositionEyes(1F);

		VecRotation vecRotation = null;

		for(double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
			for (double ySearch = 0.15D; ySearch < 1D; ySearch += 0.1D) {
				for (double zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
					final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch,
							bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
					final Rotation rotation = toRotation(vec3, predict);
					final double vecDist = eyes.distanceTo(vec3.mc());

					if (vecDist > distance)
						continue;

					if (throughWalls || isVisible(vec3)) {
						final VecRotation currentVec = new VecRotation(vec3, rotation);

						if (vecRotation == null || (random ? getRotationDifference(currentVec.getRotation(), randomRotation) < getRotationDifference(vecRotation.getRotation(), randomRotation) : getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
							vecRotation = currentVec;
					}
				}
			}
		}

		return vecRotation;
	}

	public static VecRotation calculateCenter(final String calMode, final String randMode, final double randomRange, final AxisAlignedBB bb, final boolean predict, final boolean throughWalls) {

        /*if(outborder) {
            final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
            return new VecRotation(vec3, toRotation(vec3, predict));
        }*/

		//final Rotation randomRotation = toRotation(randomVec, predict);

		VecRotation vecRotation = null;

		double xMin = 0.0D;
		double yMin = 0.0D;
		double zMin = 0.0D;
		double xMax = 0.0D;
		double yMax = 0.0D;
		double zMax = 0.0D;
		double xDist = 0.0D;
		double yDist = 0.0D;
		double zDist = 0.0D;

		xMin = 0.15D; xMax = 0.85D; xDist = 0.1D;
		yMin = 0.15D; yMax = 1.00D; yDist = 0.1D;
		zMin = 0.15D; zMax = 0.85D; zDist = 0.1D;

		Vec3 curVec3 = null;

		switch(calMode) {
			case "LiquidBounce":
				xMin = 0.15D; xMax = 0.85D; xDist = 0.1D;
				yMin = 0.15D; yMax = 1.00D; yDist = 0.1D;
				zMin = 0.15D; zMax = 0.85D; zDist = 0.1D;
				break;
			case "Full":
				xMin = 0.00D; xMax = 1.00D; xDist = 0.1D;
				yMin = 0.00D; yMax = 1.00D; yDist = 0.1D;
				zMin = 0.00D; zMax = 1.00D; zDist = 0.1D;
				break;
			case "HalfUp":
				xMin = 0.10D; xMax = 0.90D; xDist = 0.1D;
				yMin = 0.50D; yMax = 0.90D; yDist = 0.1D;
				zMin = 0.10D; zMax = 0.90D; zDist = 0.1D;
				break;
			case "HalfDown":
				xMin = 0.10D; xMax = 0.90D; xDist = 0.1D;
				yMin = 0.10D; yMax = 0.50D; yDist = 0.1D;
				zMin = 0.10D; zMax = 0.90D; zDist = 0.1D;
				break;
			case "CenterSimple":
				xMin = 0.45D; xMax = 0.55D; xDist = 0.0125D;
				yMin = 0.65D; yMax = 0.75D; yDist = 0.0125D;
				zMin = 0.45D; zMax = 0.55D; zDist = 0.0125D;
				break;
			case "CenterLine":
				xMin = 0.45D; xMax = 0.55D; xDist = 0.0125D;
				yMin = 0.10D; yMax = 0.90D; yDist = 0.1D;
				zMin = 0.45D; zMax = 0.55D; zDist = 0.0125D;
				break;
		}

		for(double xSearch = xMin; xSearch < xMax; xSearch += xDist) {
			for (double ySearch = yMin; ySearch < yMax; ySearch += yDist) {
				for (double zSearch = zMin; zSearch < zMax; zSearch += zDist) {
					final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch, bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
					final Rotation rotation = toRotation(vec3, predict);

					if(throughWalls || isVisible(vec3)) {
						final VecRotation currentVec = new VecRotation(vec3, rotation);

						if (vecRotation == null || (getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))) {
							vecRotation = currentVec;
							curVec3 = vec3;
						}
					}
				}
			}
		}

		if(vecRotation == null || Objects.equals(randMode, "Off"))
			return vecRotation;

		double rand1 = random.nextDouble();
		double rand2 = random.nextDouble();
		double rand3 = random.nextDouble();

		final double xRange = bb.maxX - bb.minX;
		final double yRange = bb.maxY - bb.minY;
		final double zRange = bb.maxZ - bb.minZ;
		double minRange = 999999.0D;

		if(xRange<=minRange) minRange = xRange;
		if(yRange<=minRange) minRange = yRange;
		if(zRange<=minRange) minRange = zRange;

		rand1 = rand1 * minRange * randomRange;
		rand2 = rand2 * minRange * randomRange;
		rand3 = rand3 * minRange * randomRange;

		final double xPrecent = minRange * randomRange / xRange;
		final double yPrecent = minRange * randomRange / yRange;
		final double zPrecent = minRange * randomRange / zRange;

		Vec3 randomVec3 = new Vec3(
				curVec3.getX() - xPrecent * (curVec3.getX() - bb.minX) + rand1,
				curVec3.getY() - yPrecent * (curVec3.getY() - bb.minY) + rand2,
				curVec3.getZ() - zPrecent * (curVec3.getZ() - bb.minZ) + rand3
		);
		switch(randMode) {
			case "Horizonal":
				randomVec3 = new Vec3(
						curVec3.getX() - xPrecent * (curVec3.getX() - bb.minX) + rand1,
						curVec3.getY(),
						curVec3.getZ() - zPrecent * (curVec3.getZ() - bb.minZ) + rand3
				);
				break;
			case "Vertical":
				randomVec3 = new Vec3(
						curVec3.getX(),
						curVec3.getY() - yPrecent * (curVec3.getY() - bb.minY) + rand2,
						curVec3.getZ()
				);
				break;
		}

		final Rotation randomRotation = toRotation(randomVec3, predict);
		vecRotation =  new VecRotation(randomVec3, randomRotation);

		return vecRotation;
	}


	// LnkuidBance
	public static VecRotation searchCenterLnk(final AxisAlignedBB bb,final boolean throughWalls, final float distance) {
		double ySearch;
		boolean entityonl = false;
		boolean entityonr = false;
		VecRotation vecRotation = null;
		if ((bb.maxX - bb.minX) < (bb.maxZ - bb.minZ)) {
			entityonr = true;
		}if ((bb.maxX - bb.minX) > (bb.maxZ - bb.minZ)) {
			entityonl = true;
		}if((bb.maxX - bb.minX) == (bb.maxZ - bb.minZ)) {
			entityonr = false;
			entityonl = false;
		}
		double  x = bb.minX + (bb.maxX - bb.minX) * (entityonl ? 0.25: 0.5);
		double  z = bb.minZ + (bb.maxZ - bb.minZ) * (entityonr ? 0.25: 0.5);
		for(double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.15D) {
			for (ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.15D) {
				for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.15D) {
					double pitch = bb.minY + (bb.maxY - bb.minY) * ySearch;
					Vec3 vec3;
					vec3 = new Vec3(x, pitch, z);
					final Rotation rotation = toRotation(vec3, false);
					final net.minecraft.util.Vec3 eyes = mc.thePlayer.getPositionEyes(1F);

					final double vecDist = eyes.distanceTo(vec3.mc());
					if (vecDist > distance)
						continue;

					if (vecDist <= (double)distance && (throughWalls || isVisible(vec3))) {
						final VecRotation currentVec = new VecRotation(vec3, rotation);
						if (vecRotation == null || (getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))) {
							vecRotation = currentVec;
						}
					}
				}
			}
		}
		return vecRotation;
	}



	/**
	 * Calculate difference between the client rotation and your entity
	 *
	 * @param entity your entity
	 * @return difference between rotation
	 */
	public static double getRotationDifference(final Entity entity) {
		final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);

		return getRotationDifference(rotation, new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
	}

	/**
	 * Calculate difference between the server rotation and your rotation
	 *
	 * @param rotation your rotation
	 * @return difference between rotation
	 */
	public static double getRotationDifference(final Rotation rotation) {
		return serverRotation == null ? 0D : getRotationDifference(rotation, serverRotation);
	}

	/**
	 * Calculate difference between two rotations
	 *
	 * @param a rotation
	 * @param b rotation
	 * @return difference between rotation
	 */
	public static double getRotationDifference(final Rotation a, final Rotation b) {
		return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
	}

	/**
	 * Limit your rotation using a turn speed
	 *
	 * @param currentRotation your current rotation
	 * @param targetRotation your goal rotation
	 * @param turnSpeed your turn speed
	 * @return limited rotation
	 */
	public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
		final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
		final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

		return new Rotation(
				currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
				currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)
				));
	}

	/**
	 * Calculate difference between two angle points
	 *
	 * @param a angle point
	 * @param b angle point
	 * @return difference between angle points
	 */
	public static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	/**
	 * Calculate rotation to vector
	 *
	 * @param rotation your rotation
	 * @return target vector
	 */


	public static Vec3 getVectorForRotation(final Rotation rotation) {
		float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
		float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
		float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
		float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
		return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
	}public static Vec3 getVectorForRotations(final Rotation rotation) {
		float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
		float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
		float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
		float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
		return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
	}

	public static net.minecraft.util.Vec3 getVectorForRotation(Vector2f rotation) {
		float yawCos = MathHelper.cos(-rotation.getX() * 0.017453292F - 3.1415927F);
		float yawSin = MathHelper.sin(-rotation.getX() * 0.017453292F - 3.1415927F);
		float pitchCos = -MathHelper.cos(-rotation.getY() * 0.017453292F);
		float pitchSin = MathHelper.sin(-rotation.getY() * 0.017453292F);
		return new net.minecraft.util.Vec3((double)(yawSin * pitchCos), (double)pitchSin, (double)(yawCos * pitchCos));
	}

	public static Vec3 getVectorForRotation(float p_getVectorForRotation_1_, float p_getVectorForRotation_2_) {
		float f = MathHelper.cos(-p_getVectorForRotation_2_ * ((float)Math.PI / 180) - (float)Math.PI);
		float f1 = MathHelper.sin(-p_getVectorForRotation_2_ * ((float)Math.PI / 180) - (float)Math.PI);
		float f2 = -MathHelper.cos(-p_getVectorForRotation_1_ * ((float)Math.PI / 180));
		float f3 = MathHelper.sin(-p_getVectorForRotation_1_ * ((float)Math.PI / 180));
		return new Vec3(f1 * f2, f3, f * f2);
	}
	public static VecRotation lockView(final AxisAlignedBB bb, final boolean outborder, final boolean random,
									   final boolean predict, final boolean throughWalls, final float distance) {
		if (outborder) {
			final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
			return new VecRotation(vec3, toRotation(vec3, predict));
		}

		final Vec3 randomVec = new Vec3(bb.minX + (bb.maxX - bb.minX) * x * 0.8, bb.minY + (bb.maxY - bb.minY) * y * 0.8, bb.minZ + (bb.maxZ - bb.minZ) * z * 0.8);
		final Rotation randomRotation = toRotation(randomVec, predict);

		final Vec3  e = mc.thePlayer.getPositionEyes2(1F);

		double xMin = 0.0D;
		double yMin = 0.0D;
		double zMin = 0.0D;
		double xMax = 0.0D;
		double yMax = 0.0D;
		double zMax = 0.0D;
		double xDist = 0.0D;
		double yDist = 0.0D;
		double zDist = 0.0D;
		VecRotation vecRotation = null;
		xMin = 0.45D; xMax = 0.55D; xDist = 0.0125D;
		yMin = 0.65D; yMax = 0.75D; yDist = 0.0125D;
		zMin = 0.45D; zMax = 0.55D; zDist = 0.0125D;
		for(double xSearch = xMin; xSearch < xMax; xSearch += xDist) {
			for (double ySearch = yMin; ySearch < yMax; ySearch += yDist) {
				for (double zSearch = zMin; zSearch < zMax; zSearch += zDist) {
					final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch, bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);

					final Rotation rotation = toRotation(vec3, predict);
					final double vecDist = e.distanceTo(vec3);

					if (vecDist > distance)
						continue;

					if (throughWalls || isVisible(vec3)) {
						final VecRotation currentVec = new VecRotation(vec3, rotation);

						if (vecRotation == null || (random ? getRotationDifference(currentVec.getRotation(), randomRotation) < getRotationDifference(vecRotation.getRotation(), randomRotation) : getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
							vecRotation = currentVec;
					}
				}
			}
		}

		return vecRotation;
	}
	/**
	 * Allows you to check if your enemy is behind a wall
	 */
	public static boolean isVisible(final Vec3 vec3) {
		final net.minecraft.util.Vec3 eyesPos = new net.minecraft.util.Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		return mc.theWorld.rayTraceBlocks(eyesPos, new net.minecraft.util.Vec3(vec3.getX(),vec3.getY(),vec3.getZ())) == null;
	}

	/**
	 * Handle minecraft tick
	 *
	 * @param event Tick event
	 */
	@EventTarget
	public void onTick(final TickEvent event) {
		if(targetRotation != null) {
			keepLength--;

			if (keepLength <= 0)
				reset();
		}

		if(random.nextGaussian() > 0.8D) x = Math.random();
		if(random.nextGaussian() > 0.8D) y = Math.random();
		if(random.nextGaussian() > 0.8D) z = Math.random();
	}

	/**
	 * Set your target rotation
	 *
	 * @param rotation your target rotation
	 */
	public static void setTargetRotation(final Rotation rotation, final int keepLength) {
		if (Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch())
				|| rotation.getPitch() > 90 || rotation.getPitch() < -90)
			return;
		rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
		targetRotation = rotation;
		RotationUtils.keepLength = keepLength;
	}

	/**
	 * Set your target rotation
	 *
	 * @param rotation your target rotation
	 */
	public static void setTargetRotation(final Rotation rotation) {
		setTargetRotation(rotation, 0);
	}

	/**
	 * Handle packet
	 *
	 * @param event Packet Event
	 */
	@EventTarget
	public void onPacket(final PacketSendEvent event) {
		final Packet<?> packet = event.getPacket();
		if (packet instanceof C03PacketPlayer) {
			final C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;
			if (targetRotation != null && !keepCurrentRotation && (targetRotation.getYaw() != serverRotation.getYaw() || targetRotation.getPitch() != serverRotation.getPitch())) {
				packetPlayer.yaw = targetRotation.getYaw();
				packetPlayer.pitch = targetRotation.getPitch();
				packetPlayer.rotating = true;
			}

			if (packetPlayer.rotating) {
				serverRotation = new Rotation(packetPlayer.getYaw(), packetPlayer.getPitch());
			}
		}
	}

	/**
	 * NCP Rotation
	 * @param a1 target
	 * @return rotation
	 */
	public static float[] rotateNCP(Entity a1) {
		//SkidSense Rotation
		if (a1 == null) {
			return null;
		} else {
			double v1 = a1.posX - Minecraft.getMinecraft().thePlayer.posX;
			double v3 = a1.posY + (double) a1.getEyeHeight() * 0.9D - (Minecraft.getMinecraft().thePlayer.posY + (double) Minecraft.getMinecraft().thePlayer.getEyeHeight());
			double v5 = a1.posZ - Minecraft.getMinecraft().thePlayer.posZ;
			double v7 = MathHelper.ceiling_float_int((float) (v1 * v1 + v5 * v5));
			float v9 = (float) (Math.atan2(v5, v1) * 180.0D / 3.141592653589793D) - 90.0F;
			float v10 = (float) (-(Math.atan2(v3, v7) * 180.0D / 3.141592653589793D));
			return new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(v9 - Minecraft.getMinecraft().thePlayer.rotationYaw), Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(v10 - Minecraft.getMinecraft().thePlayer.rotationPitch)};
		}
	}

	/**
	 * Reset your target rotation
	 */
	public static void reset() {
		keepLength = 0;
		targetRotation = null;
	}
}
