package qwq.arcane.gui.clickgui.dropdown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import qwq.arcane.module.Category;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.EaseOutSine;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/dropdown/DropDownClickGui.class */
public class DropDownClickGui extends GuiScreen {
    public static Animation openingAnimation = new EaseOutSine(400, 1.0d);
    private final List<CategoryPanel> panels = new ArrayList();
    private boolean closing;
    public int scroll;

    public List<CategoryPanel> getPanels() {
        return this.panels;
    }

    public boolean isClosing() {
        return this.closing;
    }

    public int getScroll() {
        return this.scroll;
    }

    public DropDownClickGui() {
        openingAnimation.setDirection(Direction.BACKWARDS);
        for (Category category : Category.values()) {
            this.panels.add(new CategoryPanel(category));
            float width = 0.0f;
            for (CategoryPanel panel : this.panels) {
                panel.setX(50.0f + width);
                panel.setY(20.0f);
                width += panel.getWidth() + 10.0f;
            }
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void initGui() {
        openingAnimation.setDirection(Direction.FORWARDS);
        this.closing = false;
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Mouse.hasWheel()) {
            float wheel = Mouse.getDWheel();
            if (wheel != 0.0f) {
                this.scroll += wheel > 0.0f ? 15 : -15;
            }
        }
        int mouseY2 = mouseY - this.scroll;
        GlStateManager.translate(0.0f, this.scroll, 0.0f);
        if (this.closing) {
            mc.displayGuiScreen(null);
        }
        this.panels.forEach(panel -> {
            panel.drawScreen(mouseX, mouseY2);
        });
        GlStateManager.translate(0.0f, -this.scroll, 0.0f);
        super.drawScreen(mouseX, mouseY2, partialTicks);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int mouseY2 = mouseY - this.scroll;
        GlStateManager.translate(0.0f, this.scroll, 0.0f);
        this.panels.forEach(panel -> {
            panel.mouseClicked(mouseX, mouseY2, mouseButton);
        });
        GlStateManager.translate(0.0f, -this.scroll, 0.0f);
        super.mouseClicked(mouseX, mouseY2, mouseButton);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        int mouseY2 = mouseY - this.scroll;
        GlStateManager.translate(0.0f, this.scroll, 0.0f);
        this.panels.forEach(panel -> {
            panel.mouseReleased(mouseX, mouseY2, state);
        });
        GlStateManager.translate(0.0f, -this.scroll, 0.0f);
        super.mouseReleased(mouseX, mouseY2, state);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.panels.forEach(panel -> {
            panel.keyTyped(typedChar, keyCode);
        });
        if (keyCode == 1) {
            this.closing = true;
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public boolean doesGuiPauseGame() {
        return false;
    }
}
