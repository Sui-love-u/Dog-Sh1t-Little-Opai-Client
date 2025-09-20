package com.amaya.gui.altlogin.util;

import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.GlowUtils;
import com.amaya.utils.render.RoundedUtil;

import java.awt.*;

public class Button {
    public String displayName;
    public float x;
    public float y;
    public float width;
    public float height;
    private final Animation hoverAnimation = new DecelerateAnimation(500, 1.0);;
    private boolean isHovered;
    public Button(String displayName, float x, float y, float width, float height) {
        this.displayName = displayName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawButton(int mouseX, int mouseY) {
        isHovered = isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(isHovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(35, 37, 43, 102);
        rectColor = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, 0.4f), hoverAnimation.getOutput().floatValue());
        RoundedUtil.drawRoundOutline(x, y + 1, width, height - 2, 4.5F, 0.5F, rectColor, new Color(255,255,255));
        GlowUtils.drawGlow(x, y + 1, width, height - 2, 50, new Color(0, 0, 0, 200));
        FontManager.SEMIBOLD.get(20).drawCenteredString(displayName, x + width / 2, y + height / 2 - 3, new Color(255, 255, 255, 200).getRGB());
    }

    public void clicked(int mouse, Runnable runnable) {
        if (isHovered && mouse == 0) {
            runnable.run();
        }
    }

    public void update(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
