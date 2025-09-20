/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.gui.party;

import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.fontrender.FontRenderer;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.netease.gui.DragComponent;
import net.netease.gui.Scroll;
import net.netease.util.CustomMenuButton;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GermPartyWindow {
    private final String text;
    private final GermPartyGui.SubType type;
    private final List<CustomMenuButton> buttons;
    private float x;
    private float y;
    private float width;
    private float height;
    private final DragComponent dragComponent = new DragComponent();
    private final Scroll scroll = new Scroll();
    private GuiScreen prevGui;

    public GermPartyWindow(String text, GermPartyGui.SubType type, List<CustomMenuButton> buttons, GuiScreen prevGui) {
        this.text = text;
        this.type = type;
        this.buttons = buttons;
        this.prevGui = prevGui;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer font16 = FontManager.REGULAR.get(16);
        FontRenderer bold18 = FontManager.REGULAR.get(18);
        this.dragComponent.setX(this.x);
        this.dragComponent.setY(this.y);
        this.dragComponent.setWidth(this.width);
        this.dragComponent.setHeight(this.height);
        this.dragComponent.setLimitHeight(this.height);
        this.dragComponent.handleDrag(mouseX, mouseY, 0, false);
        this.x = this.dragComponent.getX();
        this.y = this.dragComponent.getY();
        RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 7.0f, new Color(0, 0, 0, 120));
        bold18.drawString(this.text, this.x + 6.0f, this.y + 8.0f, -1);
        bold18.drawString("\u25cf", this.x + this.width - 14.0f, this.y + 8.0f, new Color(233, 30, 99).getRGB());
        RenderUtil.startGlScissor((int)this.x, (int)this.y + 22, (int)this.width, (int)this.height - 22);
        float offsetY = 0.0f;
        for (CustomMenuButton button : this.buttons) {
            button.setFont(font16);
            button.setWidth(font16.getStringWidth(button.getText()) + 15);
            button.setHeight(font16.getHeight() + 10);
            button.setX(this.x + this.width / 2.0f - button.getWidth() / 2.0f);
            button.setY(this.y + 22.0f + offsetY + this.scroll.getAnimationTarget());
            button.drawScreen(mouseX, mouseY, partialTicks);
            offsetY += button.getHeight() + 5.0f;
        }
        this.scroll.setMaxTarget(offsetY - this.height);
        if (RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY)) {
            this.scroll.use();
        }
        this.scroll.animate();
        this.scroll.use();
        RenderUtil.stopGlScissor();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.dragComponent.handleDrag(mouseX, mouseY, mouseButton, true);
        if (RenderUtil.isHovering(this.x + this.width - 14.0f, this.y + 8.0f, 12.0f, 12.0f, mouseX, mouseY)) {
            Minecraft.getMinecraft().displayGuiScreen(this.prevGui);
        }
        if (!RenderUtil.isHovering(this.x, this.y + 22.0f, this.width, this.height, mouseX, mouseY)) {
            return;
        }
        for (CustomMenuButton button : this.buttons) {
            button.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public String getText() {
        return this.text;
    }

    public GermPartyGui.SubType getType() {
        return this.type;
    }

    public List<CustomMenuButton> getButtons() {
        return this.buttons;
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

    public DragComponent getDragComponent() {
        return this.dragComponent;
    }

    public Scroll getScroll() {
        return this.scroll;
    }

    public GuiScreen getPrevGui() {
        return this.prevGui;
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

    public void setPrevGui(GuiScreen prevGui) {
        this.prevGui = prevGui;
    }
}

