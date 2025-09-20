package qwq.arcane.utils.render.shader;

import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.render.RoundedUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/shader/ShaderElement.class */
public class ShaderElement {
    private static final ArrayList<Runnable> tasks = new ArrayList<>();
    private static final ArrayList<Runnable> bloomTasks = new ArrayList<>();

    public static ArrayList<Runnable> getTasks() {
        return tasks;
    }

    public static void addBlurTask(Runnable context) {
        tasks.add(context);
    }

    public static ArrayList<Runnable> getBloomTasks() {
        return bloomTasks;
    }

    public static void addBloomTask(Runnable context) {
        bloomTasks.add(context);
    }

    public static void bindTexture(int texture) {
        GL11.glBindTexture(3553, texture);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != Mine.getMinecraft().displayWidth || framebuffer.framebufferHeight != Mine.getMinecraft().displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(Mine.getMinecraft().displayWidth, Mine.getMinecraft().displayHeight, true);
        }
        return framebuffer;
    }

    public static void blurArea(double x, double y, double v, double v1) {
        addBlurTask(() -> {
            RoundedUtil.drawRound((int) x, (int) y, (int) v, (int) v1, 0.0f, new Color(255, 255, 255));
        });
    }
}
