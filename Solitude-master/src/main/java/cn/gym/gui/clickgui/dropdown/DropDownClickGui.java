package cn.gym.gui.clickgui.dropdown;

import cn.gym.module.Category;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.EaseOutSine;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/5/17
 */
@Getter
public class DropDownClickGui extends GuiScreen {
    public static Animation openingAnimation = new EaseOutSine(400, 1);
    private final List<CategoryPanel> panels = new ArrayList<>();
    private boolean closing;
    public int scroll;
    public DropDownClickGui(){
        openingAnimation.setDirection(Direction.BACKWARDS);
        for (Category category : Category.values()) {
            panels.add(new CategoryPanel(category));
            float width = 0;
            for (CategoryPanel panel : panels) {
                panel.setX(50 + width);
                panel.setY(20);
                width += panel.getWidth() + 10;
            }
        }
    }

    @Override
    public void initGui() {
        openingAnimation.setDirection(Direction.FORWARDS);
        closing = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Mouse.hasWheel()) {
            final float wheel = Mouse.getDWheel();

            if (wheel != 0)
                scroll += wheel > 0 ? 15 : -15;
        }

        mouseY -= scroll;

        GlStateManager.translate(0, scroll, 0);
        if (closing) {
            openingAnimation.setDirection(Direction.BACKWARDS);
            if (openingAnimation.finished(Direction.BACKWARDS)) {
                mc.displayGuiScreen(null);
            }
        }

        int finalMouseY = mouseY;
        panels.forEach(panel -> panel.drawScreen(mouseX, finalMouseY));
        GlStateManager.translate(0, -scroll, 0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        mouseY -= scroll;
        GlStateManager.translate(0, scroll, 0);
        int finalMouseY = mouseY;
        panels.forEach(panel -> panel.mouseClicked(mouseX, finalMouseY, mouseButton));
        GlStateManager.translate(0, -scroll, 0);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {

        mouseY -= scroll;
        GlStateManager.translate(0, scroll, 0);
        int finalMouseY = mouseY;
        panels.forEach(panel -> panel.mouseReleased(mouseX, finalMouseY, state));
        GlStateManager.translate(0, -scroll, 0);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        panels.forEach(panel -> panel.keyTyped(typedChar, keyCode));
        if (keyCode == Keyboard.KEY_ESCAPE) {
            closing = true;
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
