package qwq.arcane.utils.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/OGLUtils.class */
public final class OGLUtils {
    private static final FloatBuffer windowPosition = GLAllocation.createDirectFloatBuffer(4);
    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
    private static final float[] BUFFER = new float[3];

    private OGLUtils() {
    }

    public static void enableBlending() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }

    public static void disableTexture2D() {
        GL11.glDisable(3553);
    }

    public static void enableTexture2D() {
        GL11.glEnable(3553);
    }

    public static void enableDepth() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
    }

    public static void disableDepth() {
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
    }

    public static void preDraw(int color, int mode) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        color(color);
        GL11.glBegin(mode);
    }

    public static void postDraw() {
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void color(int color) {
        GL11.glColor4ub((byte) ((color >> 16) & 255), (byte) ((color >> 8) & 255), (byte) (color & 255), (byte) ((color >> 24) & 255));
    }

    public static void startScissorBox(ScaledResolution sr, int x, int y, int width, int height) {
        int sf = sr.getScaleFactor();
        GL11.glScissor(x * sf, (sr.getScaledHeight() - (y + height)) * sf, width * sf, height * sf);
    }

    public static void startScissorBox(LockedResolution lr, int x, int y, int width, int height) {
        GL11.glScissor(x * 2, (lr.getHeight() - (y + height)) * 2, width * 2, height * 2);
    }

    public static float[] project2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat(2982, modelMatrix);
        GL11.glGetFloat(2983, projectionMatrix);
        GL11.glGetInteger(2978, viewport);
        if (GLU.gluProject(x, y, z, modelMatrix, projectionMatrix, viewport, windowPosition)) {
            BUFFER[0] = windowPosition.get(0) / scaleFactor;
            BUFFER[1] = (Display.getHeight() - windowPosition.get(1)) / scaleFactor;
            BUFFER[2] = windowPosition.get(2);
            return BUFFER;
        }
        return null;
    }
}
