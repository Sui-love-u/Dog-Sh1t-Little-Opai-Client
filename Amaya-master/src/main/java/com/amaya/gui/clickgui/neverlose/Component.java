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
package com.amaya.gui.clickgui.neverlose;

import com.amaya.module.impl.render.ClickGui;
import com.amaya.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class Component implements IComponent {

    private float x, y, width, height;
    private Color color = INSTANCE.moduleManager.getModule(ClickGui.class).color.get();
    private int colorRGB = color.getRGB();

    public void drawBackground(Color color) {
        RoundedUtil.drawRound(x, y, width, height,0, color);
    }
    public void drawRoundBackground(Color color) {
        RoundedUtil.drawRound(x, y, width, height,0, color);
    }
    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean isHovered(float mouseX, float mouseY, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean isVisible() {
        return true;
    }
}
