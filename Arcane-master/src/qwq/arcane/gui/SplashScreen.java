package qwq.arcane.gui;

import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/SplashScreen.class */
public class SplashScreen implements Instance {
    public static Animation progressAnim;
    public static boolean menu;
    public static Animation animation = new DecelerateAnimation(250, 1.0d);
    private static Framebuffer framebuffer;
    private static Animation progress2Anim;
    private static Animation progress3Anim;
    private static Animation progress4Anim;
    private static int count;

    public static void continueCount() {
        continueCount(true);
    }

    public static void continueCount(boolean continueCount) {
        drawScreen();
        if (continueCount) {
            count++;
        }
    }

    public static void drawScreen() {
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        progressAnim = new DecelerateAnimation(7000, 1.0d);
        progress2Anim = new DecelerateAnimation(5000, 1.0d);
        progress3Anim = new DecelerateAnimation(400, 1.0d).setDirection(Direction.BACKWARDS);
        progress4Anim = new DecelerateAnimation(5000, 1.0d).setDirection(Direction.BACKWARDS);
        while (!progressAnim.isDone()) {
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0d, sr.getScaledWidth(), sr.getScaledHeight(), 0.0d, 1000.0d, 3000.0d);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0f, 0.0f, -2000.0f);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.color(0.0f, 0.0f, 0.0f, 0.0f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            drawScreen(sr.getScaledWidth(), sr.getScaledHeight());
            framebuffer.unbindFramebuffer();
            framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);
            RenderUtil.setAlphaLimit(1.0f);
            mc.updateDisplay();
        }
    }

    private static void drawScreen(float width, float height) {
        animation.setDirection(((double) progressAnim.getOutput().floatValue()) >= 0.5d ? Direction.FORWARDS : Direction.BACKWARDS);
        float progress = progress2Anim.getOutput().floatValue();
        RoundedUtil.drawGradientHorizontal(0.0f, 0.0f, width, height, 0.0f, ColorUtil.applyOpacity(InterFace.mainColor.get(), 0.2f + (0.2f * animation.getOutput().floatValue())), ColorUtil.applyOpacity(InterFace.secondColor.get(), 0.2f + (0.2f * animation.getOutput().floatValue())));
        float aWidth = FontManager.Bold.get(80.0f).getStringWidth("A");
        if (progress2Anim.getOutput().floatValue() >= 0.99f) {
            progress3Anim.setDirection(Direction.FORWARDS);
            progress4Anim.setDirection(Direction.FORWARDS);
            FontManager.Bold.get(80.0f).drawString("A", (((5.0f + (width / 2.0f)) - (aWidth / 2.0f)) - ((FontManager.Bold.get(80.0f).getStringWidth("rcane") / 2) * progress3Anim.getOutput().doubleValue())) - 5.0d, (7.0f + (height / 2.0f)) - 50.0f, ColorUtil.applyOpacity(InterFace.color(1), 1.0f).getRGB());
            FontManager.Bold.get(80.0f).drawString("rcane", ((width / 2.0f) - (FontManager.Bold.get(80.0f).getStringWidth("rcane") / 2)) + (aWidth / 2.0f), (((7.0f + (height / 2.0f)) - 50.0f) - (120.0d * progress3Anim.getOutput().doubleValue())) + 120.0d, ColorUtil.applyOpacity(-1, progress4Anim.getOutput().floatValue()));
        } else {
            FontManager.Bold.get(80.0f).drawStringDynamic("A", (5.0f + (width / 2.0f)) - (aWidth / 2.0f), ((height / 2.0f) - 50.0f) + 7.0f, -1, 7);
        }
        RoundedUtil.drawRound((width / 2.0f) - 85.0f, (height / 2.0f) + 15.0f, 170.0f, 5.0f, 3.0f, new Color(221, 228, 255));
        RoundedUtil.drawGradientHorizontal((width / 2.0f) - 85.0f, (height / 2.0f) + 15.0f, 170.0f * progress, 5.0f, 3.0f, InterFace.color(1), InterFace.color(7));
    }
}
