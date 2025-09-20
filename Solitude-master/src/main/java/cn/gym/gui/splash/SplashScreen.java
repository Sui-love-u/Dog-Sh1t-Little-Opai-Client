package cn.gym.gui.splash;

import cn.gym.module.impl.render.Interface;
import cn.gym.utils.Instance;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 19:00
 */
public class SplashScreen implements Instance {
    public static Animation progressAnim;
    public static boolean menu;
    public static Animation animation = new DecelerateAnimation(250, 1);
    private static Framebuffer framebuffer;
    private static Animation progress2Anim;

    private static int count;

    public static void continueCount() {
        continueCount(true);
    }

    public static void continueCount(boolean continueCount) {
        drawScreen();
        if(continueCount){
            count++;
        }
    }

    public static void drawScreen() {
        ScaledResolution sr = new ScaledResolution(mc);
        // Create the scale factor
        int scaleFactor = sr.getScaleFactor();
        // Bind the width and height to the framebuffer
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        progressAnim = new DecelerateAnimation(7000, 1);
        progress2Anim = new DecelerateAnimation(5000, 1);
        while (!progressAnim.isDone()) {
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);
            // Create the projected image to be rendered
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();


            GlStateManager.color(0, 0, 0, 0);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            drawScreen(sr.getScaledWidth(), sr.getScaledHeight());

            // Unbind the width and height as it's no longer needed
            framebuffer.unbindFramebuffer();

            // Render the previously used frame buffer
            framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);

            // Update the texture to enable alpha drawing
            RenderUtil.setAlphaLimit(1);

            // Update the users screen
            mc.updateDisplay();
        }
    }

    private static void drawScreen(float width, float height) {
        animation.setDirection(progressAnim.getOutput().floatValue() >= 0.5 ? Direction.FORWARDS : Direction.BACKWARDS);
        float progress = progress2Anim.getOutput().floatValue();
        RoundedUtil.drawRound(0, 0, width, height, 0, new Color(244, 248, 251));
        RoundedUtil.drawRound(width / 2 - 150, height / 2 - 90, 300, 150, 8, new Color(255, 255, 255));
        RenderUtil.drawLoadingCircle2(width / 2,height / 2 - 50,6, 360,new Color(221, 228, 255).getRGB());
        RenderUtil.drawLoadingCircle2(width / 2,height / 2 - 50,6,160,Interface.FirstColor.get().getRGB());
        RoundedUtil.drawRound(width / 2 - 75, height / 2 - 15, 150, 14, 6, new Color(221, 228, 255));
        RoundedUtil.drawRound(width / 2 - 75, height / 2 - 15, 150 * progress, 14, 6, Interface.FirstColor.get());
        FontManager.Bold.get(25).drawCenteredString("Welcome Solitude Client",width / 2, height / 2 + 15, Interface.FirstColor.get().darker().getRGB());
        FontManager.Bold.get(20).drawCenteredString("Various calls are in progress please wait",width / 2, height / 2 + 35,Interface.FirstColor.get().darker().getRGB());
    }
}
