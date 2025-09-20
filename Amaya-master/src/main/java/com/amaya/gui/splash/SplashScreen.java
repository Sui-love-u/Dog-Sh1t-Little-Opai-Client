package com.amaya.gui.splash;

import com.amaya.Amaya;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.client.InstanceAccess;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.amaya.module.Module.mc;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class SplashScreen {
    public static Animation progressAnim;
    public static boolean menu;
    public static Animation animation = new DecelerateAnimation(250, 1);
    private static Framebuffer framebuffer;

    public static void drawScreen() {
        ScaledResolution sr = new ScaledResolution(mc);
        // Create the scale factor
        int scaleFactor = sr.getScaleFactor();
        // Bind the width and height to the framebuffer
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        progressAnim = new DecelerateAnimation(7000, 1);
        if (InstanceAccess.mc.gameSettings.guiScale != 2) {
            Amaya.prevGuiScale = InstanceAccess.mc.gameSettings.guiScale;
            Amaya.updateGuiScale = true;
            InstanceAccess.mc.gameSettings.guiScale = 2;
            InstanceAccess.mc.resize(InstanceAccess.mc.displayWidth - 1, InstanceAccess.mc.displayHeight);
            InstanceAccess.mc.resize(InstanceAccess.mc.displayWidth + 1, InstanceAccess.mc.displayHeight);
        }
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
        animation.setDirection(progressAnim.getOutput().floatValue() != 0.8 ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtil.drawRound(0, 0, width, height, 0,new Color(241, 244, 243));
        RenderUtil.scaleStart(width / 2 - 2,height / 2 - 2,animation.getOutput().floatValue());
        RenderUtil.drawLoadingCircle2(width / 2 - 2,height / 2 - 2,4,260,new Color(89, 165, 245).getRGB());
        FontManager.REGULAR.get(20).drawCenteredString("Loading...",width / 2, height / 2 + 20, new Color(92, 92, 92).getRGB());
        RenderUtil.scaleEnd();
    }
}
