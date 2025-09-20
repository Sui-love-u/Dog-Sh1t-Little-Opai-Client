/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.util;

import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.fontrender.FontRenderer;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class CustomMenuButton
extends GuiScreen {
    public final String text;
    private Animation displayAnimation;
    private Animation hoverAnimation = new DecelerateAnimation(500, 1.0);
    public float x;
    public float y;
    public float width;
    public float height;
    public Runnable clickAction;
    public FontRenderer font = FontManager.REGULAR.get(20);

    public CustomMenuButton(String text, Runnable clickAction) {
        this.text = text;
        this.displayAnimation = new DecelerateAnimation(1000, 255.0);
        this.font = FontManager.REGULAR.get(20);
        this.clickAction = clickAction;
    }

    public CustomMenuButton(String text) {
        this.text = text;
        this.displayAnimation = new DecelerateAnimation(1000, 255.0);
        this.font = FontManager.REGULAR.get(20);
    }

    @Override
    public void initGui() {
        this.hoverAnimation = new DecelerateAnimation(500, 1.0);
        this.displayAnimation.setDirection(Direction.FORWARDS);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float ticks) {
        boolean hovered = RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(32, 32, 32, (int)(this.displayAnimation.getOutput() * Math.max(0.7, this.hoverAnimation.getOutput())));
        RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 4.0f, rectColor);
        this.font.drawCenteredString(this.text, this.x + this.width / 2.0f, this.y + this.font.getMiddleOfBox(this.height) + 2.0f, new Color(255, 255, 255, (int)this.displayAnimation.getOutput().intValue()).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        if (hovered) {
            this.clickAction.run();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    @Override
    public void onGuiClosed() {
        this.displayAnimation.setDirection(Direction.BACKWARDS);
    }

    public void setDisplayAnimation(Animation displayAnimation) {
        this.displayAnimation = displayAnimation;
    }

    public void setHoverAnimation(Animation hoverAnimation) {
        this.hoverAnimation = hoverAnimation;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setClickAction(Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public void setFont(FontRenderer font) {
        this.font = font;
    }

    public String getText() {
        return this.text;
    }

    public Animation getDisplayAnimation() {
        return this.displayAnimation;
    }

    public Animation getHoverAnimation() {
        return this.hoverAnimation;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Runnable getClickAction() {
        return this.clickAction;
    }

    public FontRenderer getFont() {
        return this.font;
    }
}

