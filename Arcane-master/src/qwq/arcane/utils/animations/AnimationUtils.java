package qwq.arcane.utils.animations;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/AnimationUtils.class */
public class AnimationUtils {
    private static int delta;
    public static long deltaTime = 500;

    public static int getDelta() {
        return delta;
    }

    public static void setDelta(int delta2) {
        delta = delta2;
    }

    public static float clamp(float number, float min, float max) {
        return number < min ? min : Math.min(number, max);
    }

    public static float calculateCompensation(float target, float current, long delta2, int speed) {
        float current2;
        float diff = current - target;
        if (delta2 < 1) {
            delta2 = 1;
        }
        double v = ((double) ((((long) speed) * delta2) / 16)) < 0.25d ? 0.5d : (speed * delta2) / 16;
        if (diff > speed) {
            current2 = current - ((float) v);
            if (current2 < target) {
                current2 = target;
            }
        } else if (diff < (-speed)) {
            current2 = current + ((float) v);
            if (current2 > target) {
                current2 = target;
            }
        } else {
            current2 = target;
        }
        return current2;
    }

    public static float animateIDK(double target, double current, double speed) {
        boolean larger = target > current;
        if (speed < 0.0d) {
            speed = 0.0d;
        } else if (speed > 1.0d) {
            speed = 1.0d;
        }
        double dif = Math.abs(current - target);
        double factor = dif * speed;
        return (float) (larger ? current + factor : current - factor);
    }

    public static double animate(double target, double current, double speed) {
        double current2;
        if (current == target) {
            return current;
        }
        boolean larger = target > current;
        if (speed < 0.0d) {
            speed = 0.0d;
        } else if (speed > 1.0d) {
            speed = 1.0d;
        }
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1d) {
            factor = 0.1d;
        }
        if (larger) {
            current2 = current + factor;
            if (current2 >= target) {
                current2 = target;
            }
        } else {
            current2 = current - factor;
            if (current2 <= target) {
                current2 = target;
            }
        }
        return current2;
    }

    public static float animateSmooth(float current, float target, float speed) {
        return purse(target, current, getDelta(), Math.abs(target - current) * speed);
    }

    public static float purse(float target, float current, long delta2, float speed) {
        float current2;
        if (delta2 < 1) {
            delta2 = 1;
        }
        float difference = current - target;
        float smoothing = Math.max(speed * (delta2 / 16.0f), 0.15f);
        if (difference > speed) {
            current2 = Math.max(current - smoothing, target);
        } else if (difference < (-speed)) {
            current2 = Math.min(current + smoothing, target);
        } else {
            current2 = target;
        }
        return current2;
    }

    public static float animate(float target, float current, float speed) {
        float current2;
        if (current == target) {
            return current;
        }
        boolean larger = target > current;
        if (speed < 0.0f) {
            speed = 0.0f;
        } else if (speed > 1.0f) {
            speed = 1.0f;
        }
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1d) {
            factor = 0.1d;
        }
        if (larger) {
            current2 = (float) (current + factor);
            if (current2 >= target) {
                current2 = target;
            }
        } else {
            current2 = (float) (current - factor);
            if (current2 <= target) {
                current2 = target;
            }
        }
        return current2;
    }
}
