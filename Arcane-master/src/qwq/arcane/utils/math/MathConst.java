package qwq.arcane.utils.math;

import net.minecraft.util.MathHelper;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/math/MathConst.class */
public class MathConst {
    public static final float PI = 3.1415927f;
    public static final float TO_RADIANS = 0.017453292f;
    public static final float TO_DEGREES = 57.295776f;
    public static final float[] COSINE = new float[361];
    public static final float[] SINE = new float[361];

    public static void calculate() {
        for (int i = 0; i <= 360; i++) {
            COSINE[i] = MathHelper.cos(i * 0.017453292f);
            SINE[i] = MathHelper.sin(i * 0.017453292f);
        }
    }

    public static int toIntDegree(float angle) {
        return ((int) ((angle % 360.0f) + 360.0f)) % 360;
    }
}
