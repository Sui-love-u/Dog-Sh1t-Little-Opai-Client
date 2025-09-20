package qwq.arcane.utils.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import qwq.arcane.module.Mine;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/math/MathUtils.class */
public class MathUtils {
    public static final DecimalFormat DF_0 = new DecimalFormat("0");
    public static final DecimalFormat DF_1 = new DecimalFormat("0.0");
    public static final DecimalFormat DF_2 = new DecimalFormat("0.00");
    public static final DecimalFormat DF_1D = new DecimalFormat("0.#");
    public static final DecimalFormat DF_2D = new DecimalFormat("0.##");
    public static final SecureRandom secureRandom = new SecureRandom();

    public static double clamp(double num, double min, double max) {
        if (num < min) {
            return min;
        }
        return Math.min(num, max);
    }

    public static int getRandom(int min, int max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            min = max;
            max = min;
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static int randomizeInt(float min, float max) {
        return (int) randomizeDouble(min, max);
    }

    public static Vec3 closestPointOnFace(AxisAlignedBB aabb, EnumFacing face, Vec3 vec) {
        return closestPointOnFace(aabb, face, vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static Vec3 closestPointOnFace(AxisAlignedBB aabb, EnumFacing face, double x, double y, double z) {
        double closestX;
        double closestY;
        double closestZ;
        switch (face) {
            case DOWN:
            case UP:
                closestX = Math.max(aabb.minX, Math.min(x, aabb.maxX));
                closestY = face == EnumFacing.DOWN ? aabb.minY : aabb.maxY;
                closestZ = Math.max(aabb.minZ, Math.min(z, aabb.maxZ));
                break;
            case NORTH:
            case SOUTH:
                closestX = Math.max(aabb.minX, Math.min(x, aabb.maxX));
                closestY = Math.max(aabb.minY, Math.min(y, aabb.maxY));
                closestZ = face == EnumFacing.NORTH ? aabb.minZ : aabb.maxZ;
                break;
            case WEST:
            case EAST:
                closestX = face == EnumFacing.WEST ? aabb.minX : aabb.maxX;
                closestY = Math.max(aabb.minY, Math.min(y, aabb.maxY));
                closestZ = Math.max(aabb.minZ, Math.min(z, aabb.maxZ));
                break;
            default:
                throw new IllegalArgumentException("Invalid face: " + face);
        }
        return new Vec3(closestX, closestY, closestZ);
    }

    public static double linearInterpolate(double min, double max, double norm) {
        return ((max - min) * norm) + min;
    }

    public static double randomizeDouble(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }

    public static int getRandomInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static float wrapAngleTo180_float(float angle) {
        float angle2 = angle % 360.0f;
        if (angle2 >= 180.0f) {
            angle2 -= 360.0f;
        }
        if (angle2 < -180.0f) {
            angle2 += 360.0f;
        }
        return angle2;
    }

    public static int nextInt(int min, int max) {
        if (min == max || max - min <= 0.0d) {
            return min;
        }
        return (int) (min + ((max - min) * Math.random()));
    }

    public static double[] yawPos(double value) {
        return yawPos(Mine.getMinecraft().thePlayer.rotationYaw * MathHelper.deg2Rad, value);
    }

    public static double incValue(double val, double inc) {
        double one = 1.0d / inc;
        return Math.round(val * one) / one;
    }

    public static double[] yawPos(float yaw, double value) {
        return new double[]{(-MathHelper.sin(yaw)) * value, MathHelper.cos(yaw) * value};
    }

    public static float getRandomInRange(float min, float max) {
        SecureRandom random = new SecureRandom();
        return (random.nextFloat() * (max - min)) + min;
    }

    public static double getRandomInRange(double min, double max) {
        SecureRandom random = new SecureRandom();
        return min == max ? min : (random.nextDouble() * (max - min)) + min;
    }

    public static int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static double lerp(double old, double newVal, double amount) {
        return ((1.0d - amount) * old) + (amount * newVal);
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return Double.valueOf(oldValue + ((newValue - oldValue) * interpolationValue));
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double output = 1.0d / Math.sqrt(6.283185307179586d * (sigma * sigma));
        return (float) (output * Math.exp((-(x * x)) / (2.0d * (sigma * sigma))));
    }

    public static double roundToHalf(double d) {
        return Math.round(d * 2.0d) / 2.0d;
    }

    public static double round(double num, double increment) {
        BigDecimal bd = new BigDecimal(num);
        return bd.setScale((int) increment, RoundingMode.HALF_UP).doubleValue();
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public static String round(String value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        return bd.stripTrailingZeros().setScale(places, RoundingMode.HALF_UP).toString();
    }

    public static float getRandomFloat(float max, float min) {
        SecureRandom random = new SecureRandom();
        return (random.nextFloat() * (max - min)) + min;
    }

    public static int getNumberOfDecimalPlace(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return Math.max(0, bigDecimal.stripTrailingZeros().scale());
    }

    public static double roundToDecimalPlace(double value, double inc) {
        double halfOfInc = inc / 2.0d;
        double floored = StrictMath.floor(value / inc) * inc;
        if (value >= floored + halfOfInc) {
            return new BigDecimal(StrictMath.ceil(value / inc) * inc, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
        }
        return new BigDecimal(floored, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
    }
}
