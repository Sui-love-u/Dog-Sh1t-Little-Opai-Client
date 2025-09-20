/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package com.amaya.gui.widget;

import com.amaya.Amaya;
import com.amaya.events.impl.render.Shader2DEvent;
import com.amaya.module.impl.render.HUD;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;

@Getter
@Setter
public abstract class Widget {
    public Minecraft mc = Minecraft.getMinecraft();
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("x")
    public float x;
    @Expose
    @SerializedName("y")
    public float y;
    protected float renderX, renderY;
    public float width;
    public float height;
    public boolean dragging;
    private int dragX, dragY;
    protected ScaledResolution sr;
    protected HUD setting = Amaya.Instance.moduleManager.getModule(HUD.class);

    public Widget(String name) {
        this.name = name;
        this.x = 0f;
        this.y = 0f;
        this.width = 100f;
        this.height = 100f;
    }

    public abstract void onShader(Shader2DEvent event);
    public abstract void render();

    public void updatePos() {
        sr = new ScaledResolution(mc);

        renderX = x * sr.getScaledWidth();
        renderY = y * sr.getScaledHeight();

        if (renderX < 0f) x = 0f;
        if (renderX > sr.getScaledWidth() - width) x = (sr.getScaledWidth() - width) / sr.getScaledWidth();
        if (renderY < 0f) y = 0f;
        if (renderY > sr.getScaledHeight() - height) y = (sr.getScaledHeight() - height) / sr.getScaledHeight();
    }

    public final void onChatGUI(int mouseX, int mouseY, boolean drag) {
        boolean hovering = RenderUtil.isHovering(renderX, renderY, width, height, mouseX, mouseY);

        if (dragging) {
            FontManager.SEMIBOLD.get(16).drawString(name,renderX,renderY - 10,-1);
            RoundedUtil.drawRoundOutline(renderX, renderY, width, height, 5f, 0.5f, new Color(0, 0, 0, 0), Color.WHITE);
        }

        if (hovering && Mouse.isButtonDown(0) && !dragging && drag) {
            dragging = true;
            dragX = mouseX;
            dragY = mouseY;
        }

        if (!Mouse.isButtonDown(0)) dragging = false;

        if (dragging) {
            float deltaX = (float) (mouseX - dragX) / sr.getScaledWidth();
            float deltaY = (float) (mouseY - dragY) / sr.getScaledHeight();

            x += deltaX;
            y += deltaY;

            dragX = mouseX;
            dragY = mouseY;
        }

    }

    public abstract boolean shouldRender();
}
