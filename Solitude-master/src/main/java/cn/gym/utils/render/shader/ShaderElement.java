package cn.gym.utils.render.shader;

import cn.gym.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class ShaderElement {
    private static final ArrayList<Runnable> tasks = new ArrayList<>();

    public static ArrayList<Runnable> getTasks() {
        return tasks;
    }

    public static void addBlurTask(Runnable context) {
        tasks.add(context);
    }

    private static final ArrayList<Runnable> bloomTasks = new ArrayList<>();

    public static ArrayList<Runnable> getBloomTasks() {
        return bloomTasks;
    }

    public static void addBloomTask(Runnable context) {
        bloomTasks.add(context);
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }


    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != Minecraft.getMinecraft().displayWidth || framebuffer.framebufferHeight != Minecraft.getMinecraft().displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, true);
        }
        return framebuffer;
    }

    public static void blurArea(double x, double y, double v, double v1) {
        addBlurTask(() -> RoundedUtil.drawRound((int) x,(int) y,(int) v,(int) v1,0,new Color(255,255,255)));
    }
}
