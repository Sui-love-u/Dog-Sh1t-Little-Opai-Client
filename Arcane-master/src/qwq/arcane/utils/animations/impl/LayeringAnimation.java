package qwq.arcane.utils.animations.impl;

import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.animations.AnimationUtils;
import qwq.arcane.utils.render.RenderUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/LayeringAnimation.class */
public class LayeringAnimation {
    private static GuiScreen targetScreen;
    private static int progress;
    private static boolean played = false;

    public static void play(GuiScreen target) {
        targetScreen = target;
        progress = 0;
        played = true;
    }

    public static void drawAnimation() {
        if (played) {
            progress = (int) AnimationUtils.animateSmooth(progress, targetScreen == null ? 0.0f : 25500.0f, 0.2f);
            if (progress > 25400) {
                Mine.getMinecraft().displayGuiScreen(targetScreen);
                targetScreen = null;
            }
            ScaledResolution scaledResolution = new ScaledResolution(Mine.getMinecraft());
            RenderUtil.drawRect(0.0f, 0.0f, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(0, 0, 0, progress / 100).getRGB());
        }
    }
}
