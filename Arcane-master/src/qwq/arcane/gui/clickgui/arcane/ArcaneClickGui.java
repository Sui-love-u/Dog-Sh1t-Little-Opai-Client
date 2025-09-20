package qwq.arcane.gui.clickgui.arcane;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import qwq.arcane.Client;
import qwq.arcane.gui.clickgui.arcane.panel.CategoryPanel;
import qwq.arcane.module.Category;
import qwq.arcane.module.impl.visuals.ESP;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.fontrender.FontRenderer;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/arcane/ArcaneClickGui.class */
public class ArcaneClickGui extends GuiScreen {
    public int x;
    public int y;
    private int dragX;
    private int dragY;
    public Color backgroundColor;
    public Color backgroundColor2;
    public Color backgroundColor3;
    public Color smallbackgroundColor;
    public Color smallbackgroundColor2;
    public Color linecolor;
    public Color versionColor;
    public Color fontcolor;
    private final List<CategoryPanel> categoryPanels = new ArrayList();
    public int w = 360;
    public final int h = 380;
    private boolean dragging = false;
    private final Animation animations = new DecelerateAnimation(250, 1.0d);
    private final Animation animations2 = new DecelerateAnimation(250, 1.0d);
    private Animation hoverAnimation = new DecelerateAnimation(1000, 1.0d);
    public final ESPComponent espPreviewComponent = new ESPComponent();
    boolean sb = false;

    public List<CategoryPanel> getCategoryPanels() {
        return this.categoryPanels;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getW() {
        return this.w;
    }

    public int getH() {
        Objects.requireNonNull(this);
        return 380;
    }

    public int getDragX() {
        return this.dragX;
    }

    public int getDragY() {
        return this.dragY;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public Color getBackgroundColor2() {
        return this.backgroundColor2;
    }

    public Color getBackgroundColor3() {
        return this.backgroundColor3;
    }

    public Color getSmallbackgroundColor() {
        return this.smallbackgroundColor;
    }

    public Color getSmallbackgroundColor2() {
        return this.smallbackgroundColor2;
    }

    public Color getLinecolor() {
        return this.linecolor;
    }

    public Color getVersionColor() {
        return this.versionColor;
    }

    public Color getFontcolor() {
        return this.fontcolor;
    }

    public Animation getAnimations() {
        return this.animations;
    }

    public Animation getAnimations2() {
        return this.animations2;
    }

    public Animation getHoverAnimation() {
        return this.hoverAnimation;
    }

    public ESPComponent getEspPreviewComponent() {
        return this.espPreviewComponent;
    }

    public boolean isSb() {
        return this.sb;
    }

    public ArcaneClickGui() {
        Arrays.stream(Category.values()).forEach(moduleCategory -> {
            CategoryPanel panel = new CategoryPanel(moduleCategory);
            if (moduleCategory == Category.Combat) {
                panel.setSelected(true);
            }
            this.categoryPanels.add(panel);
        });
        this.animations2.setDirection(Direction.FORWARDS);
        this.backgroundColor = new Color(22, 22, 26, 255);
        this.backgroundColor2 = new Color(17, 17, 19, 255);
        this.backgroundColor3 = new Color(15, 15, 17, 255);
        this.smallbackgroundColor = new Color(22, 22, 26, 255);
        this.smallbackgroundColor2 = new Color(29, 29, 35, 255);
        this.linecolor = new Color(30, 30, 30, 255);
        this.versionColor = new Color(255, 255, 255, 50);
        this.fontcolor = new Color(255, 255, 255, 255);
        this.x = 260;
        this.y = 50;
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float fOrdinal;
        float fOrdinal2;
        int iOrdinal;
        float fOrdinal3;
        if (this.dragging) {
            this.x = mouseX + this.dragX;
            this.y = mouseY + this.dragY;
        }
        RoundedUtil.drawRound(this.x, this.y, this.w, 380.0f, 7.0f, this.backgroundColor);
        RoundedUtil.drawRound(this.x, this.y + 34, this.w, 0.5f, 0.0f, this.linecolor);
        RenderUtil.startGlScissor(this.x, this.y + 35, this.w, 345);
        RoundedUtil.drawGradientVertical(this.x, this.y + 30, this.w, 350.0f, 7.0f, this.backgroundColor2, this.backgroundColor3);
        RenderUtil.stopGlScissor();
        FontManager.Bold.get(30.0f).drawString("ARC", this.x + 10, this.y + 10, this.fontcolor.getRGB());
        FontManager.Bold.get(30.0f).drawStringDynamic("ANE", this.x + 10 + FontManager.Bold.get(30.0f).getStringWidth("ARC"), this.y + 10, 1, 6);
        FontManager.Bold.get(18.0f).drawString("Release", (this.x + this.w) - 46, this.y + 16, this.versionColor.getRGB());
        RoundedUtil.drawRound((this.x + this.w) - 68, this.y + 44, 60.0f, 25.0f, 5.0f, this.smallbackgroundColor);
        RoundedUtil.drawRound(this.x + 10, this.y + 44, 96.0f, 25.0f, 5.0f, this.smallbackgroundColor);
        RoundedUtil.drawRound(this.x + 53, this.y + 44, 1.0f, 25.0f, 5.0f, this.smallbackgroundColor2);
        if (Mouse.isButtonDown(0)) {
            if (RenderUtil.isHovering(this.x + 10, this.y + 44, 48.0f, 25.0f, mouseX, mouseY)) {
                this.animations2.setDirection(Direction.BACKWARDS);
                for (CategoryPanel panel : this.categoryPanels) {
                    panel.setSelected(panel.getCategory() == Category.Visuals);
                    this.sb = true;
                }
            } else if (RenderUtil.isHovering(this.x + 10 + 48, this.y + 44, 48.0f, 25.0f, mouseX, mouseY)) {
                this.animations2.setDirection(Direction.FORWARDS);
                this.sb = false;
            }
        }
        RoundedUtil.drawRound(this.x + 10 + (48.0f * this.animations2.getOutput().floatValue()), this.y + 44, 48.0f, 25.0f, 5.0f, this.smallbackgroundColor2);
        if (((ESP) Client.Instance.getModuleManager().getModule(ESP.class)).isEnabled() && this.sb) {
            this.espPreviewComponent.drawScreen(mouseX, mouseY);
        }
        Animation moduleAnimation = this.animations;
        if (Mouse.isButtonDown(0)) {
            if (RenderUtil.isHovering((this.x + this.w) - 38, this.y + 44, 30.0f, 25.0f, mouseX, mouseY)) {
                this.backgroundColor = new Color(250, 250, 254, 255);
                this.backgroundColor2 = new Color(255, 255, 255, 255);
                this.backgroundColor3 = new Color(217, 217, 216, 255);
                this.smallbackgroundColor = new Color(246, 248, 252, 255);
                this.smallbackgroundColor2 = new Color(234, 236, 243, 255);
                this.linecolor = new Color(210, 210, 210, 255);
                this.versionColor = new Color(0, 0, 0, 50);
                this.fontcolor = new Color(0, 0, 0, 255);
                moduleAnimation.setDirection(Direction.BACKWARDS);
            } else if (RenderUtil.isHovering((this.x + this.w) - 68, this.y + 44, 30.0f, 25.0f, mouseX, mouseY)) {
                this.backgroundColor = new Color(22, 22, 26, 255);
                this.backgroundColor2 = new Color(17, 17, 19, 255);
                this.backgroundColor3 = new Color(15, 15, 17, 255);
                this.smallbackgroundColor = new Color(22, 22, 26, 255);
                this.smallbackgroundColor2 = new Color(29, 29, 35, 255);
                this.linecolor = new Color(50, 50, 50, 255);
                this.versionColor = new Color(255, 255, 255, 50);
                this.fontcolor = new Color(255, 255, 255, 255);
                moduleAnimation.setDirection(Direction.FORWARDS);
            }
        }
        RoundedUtil.drawRound(((this.x + this.w) - (30.0f * moduleAnimation.getOutput().floatValue())) - 38.0f, this.y + 44, 30.0f, 25.0f, 5.0f, this.smallbackgroundColor2);
        RoundedUtil.drawRound(this.x + 10, (this.y + 380) - 35, 158.0f, 25.0f, 5.0f, this.smallbackgroundColor);
        FontManager.Icon.get(18.0f).drawStringDynamic("R", (this.x + this.w) - 56, this.y + 55, 1, 6);
        FontManager.Icon.get(20.0f).drawStringDynamic("S", (this.x + this.w) - 28, this.y + 54.5f, 1, 6);
        RoundedUtil.drawRound((this.x + this.w) - 90, (this.y + 380) - 35, 80.0f, 25.0f, 5.0f, this.smallbackgroundColor);
        FontManager.Bold.get(16.0f).drawStringDynamic("DEV", (this.x + this.w) - 86, (this.y + 380) - 30, 1, 6);
        FontManager.Semibold.get(16.0f).drawString("Time remaining:", (this.x + this.w) - 86, (this.y + 380) - 20, this.fontcolor.getRGB());
        FontManager.Semibold.get(16.0f).drawStringDynamic("30D", ((this.x + this.w) - 86) + FontManager.Semibold.get(16.0f).getStringWidth("Time remaining:"), (this.y + 380) - 20, 1, 6);
        FontManager.Icon.get(25.0f).drawStringDynamic("M", this.x + 14, this.y + 53.5f, 1, 6);
        FontManager.Icon.get(20.0f).drawStringDynamic("G", this.x + 60, this.y + 54.5f, 1, 6);
        FontManager.Bold.get(16.0f).drawString("ESP", this.x + 28, this.y + 54.5f, this.fontcolor.getRGB());
        Color rectColor = this.smallbackgroundColor2;
        Color rectColor2 = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, 0.6f), this.hoverAnimation.getOutput().floatValue());
        boolean hovered = RenderUtil.isHovering(this.x + 10, (this.y + 380) - 35, 158.0f, 25.0f, mouseX, mouseY);
        for (CategoryPanel categoryPanel : this.categoryPanels) {
            this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
            categoryPanel.drawScreen(mouseX, mouseY);
            if (categoryPanel.getCategory() != Category.Display) {
                if (categoryPanel.isSelected()) {
                    if (categoryPanel.getCategory().ordinal() >= 5) {
                        iOrdinal = this.x + 5 + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 4) {
                        iOrdinal = this.x + 7 + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 3) {
                        iOrdinal = this.x + 9 + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 2) {
                        iOrdinal = this.x + 11 + (categoryPanel.getCategory().ordinal() * 28);
                    } else {
                        iOrdinal = categoryPanel.getCategory().ordinal() >= 1 ? this.x + 13 + (categoryPanel.getCategory().ordinal() * 28) : this.x + 15 + (categoryPanel.getCategory().ordinal() * 28);
                    }
                    RoundedUtil.drawRound(iOrdinal, categoryPanel.getCategory().ordinal() >= 6 ? this.y + 44 : (this.y + 380) - 30.5f, 15.0f, 15.0f, 5.0f, rectColor2.brighter());
                    FontRenderer fontRenderer = FontManager.Icon.get(22.0f);
                    String str = categoryPanel.getCategory().icon;
                    if (categoryPanel.getCategory().ordinal() >= 6) {
                        fOrdinal3 = this.x + 62;
                    } else if (categoryPanel.getCategory().ordinal() >= 5) {
                        fOrdinal3 = this.x + 6.5f + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 4) {
                        fOrdinal3 = this.x + 10 + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 3) {
                        fOrdinal3 = this.x + 12.5f + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 2) {
                        fOrdinal3 = this.x + 13 + (categoryPanel.getCategory().ordinal() * 28);
                    } else {
                        fOrdinal3 = categoryPanel.getCategory().ordinal() >= 1 ? this.x + 17 + (categoryPanel.getCategory().ordinal() * 28) : this.x + 18 + (categoryPanel.getCategory().ordinal() * 28);
                    }
                    fontRenderer.drawStringDynamic(str, fOrdinal3, categoryPanel.getCategory().ordinal() >= 6 ? this.y + 54.5f : (this.y + 380) - 25, 1, 6);
                } else {
                    FontRenderer fontRenderer2 = FontManager.Icon.get(22.0f);
                    String str2 = categoryPanel.getCategory().icon;
                    if (categoryPanel.getCategory().ordinal() >= 6) {
                        fOrdinal = this.x + 62;
                    } else if (categoryPanel.getCategory().ordinal() >= 5) {
                        fOrdinal = this.x + 6.5f + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 4) {
                        fOrdinal = this.x + 10 + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 3) {
                        fOrdinal = this.x + 12.5f + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 2) {
                        fOrdinal = this.x + 13 + (categoryPanel.getCategory().ordinal() * 28);
                    } else {
                        fOrdinal = categoryPanel.getCategory().ordinal() >= 1 ? this.x + 17 + (categoryPanel.getCategory().ordinal() * 28) : this.x + 18 + (categoryPanel.getCategory().ordinal() * 28);
                    }
                    fontRenderer2.drawString(str2, fOrdinal, categoryPanel.getCategory().ordinal() >= 6 ? this.y + 54.5f : (this.y + 380) - 25, this.versionColor.getRGB());
                }
                if (hovered && categoryPanel.isSelected()) {
                    FontRenderer fontRenderer3 = FontManager.Bold.get(16.0f);
                    String strName = categoryPanel.getCategory().name();
                    if (categoryPanel.getCategory().ordinal() >= 6) {
                        fOrdinal2 = this.x + 62;
                    } else if (categoryPanel.getCategory().ordinal() >= 5) {
                        fOrdinal2 = this.x + 6.5f + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 4) {
                        fOrdinal2 = this.x + 10 + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 3) {
                        fOrdinal2 = this.x + 12.5f + (categoryPanel.getCategory().ordinal() * 28);
                    } else if (categoryPanel.getCategory().ordinal() >= 2) {
                        fOrdinal2 = this.x + 13 + (categoryPanel.getCategory().ordinal() * 28);
                    } else {
                        fOrdinal2 = categoryPanel.getCategory().ordinal() >= 1 ? this.x + 17 + (categoryPanel.getCategory().ordinal() * 28) : this.x + 18 + (categoryPanel.getCategory().ordinal() * 28);
                    }
                    fontRenderer3.drawCenteredString(strName, fOrdinal2 + 5.0f, categoryPanel.getCategory().ordinal() >= 6 ? this.y + 54.5f : (this.y + 380) - 45, rectColor2.getRGB());
                }
            }
        }
        FontManager.Bold.get(16.0f).drawString("Display", this.x + 73, this.y + 54.5f, this.fontcolor.getRGB());
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (RenderUtil.isHovering(this.x, this.y, 100.0f, 35.0f, mouseX, mouseY)) {
            this.dragging = true;
            this.dragX = this.x - mouseX;
            this.dragY = this.y - mouseY;
        }
        this.espPreviewComponent.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            for (CategoryPanel panel : this.categoryPanels) {
                if (handleCategoryPanel(panel, mouseX, mouseY)) {
                    break;
                }
            }
        }
        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    private boolean handleCategoryPanel(CategoryPanel panel, int mouseX, int mouseY) {
        int iOrdinal;
        if (panel.getCategory().ordinal() >= 6) {
            iOrdinal = this.x + 10 + 48;
        } else if (panel.getCategory().ordinal() >= 5) {
            iOrdinal = this.x + 5 + (panel.getCategory().ordinal() * 28);
        } else if (panel.getCategory().ordinal() >= 4) {
            iOrdinal = this.x + 7 + (panel.getCategory().ordinal() * 28);
        } else if (panel.getCategory().ordinal() >= 3) {
            iOrdinal = this.x + 9 + (panel.getCategory().ordinal() * 28);
        } else if (panel.getCategory().ordinal() >= 2) {
            iOrdinal = this.x + 11 + (panel.getCategory().ordinal() * 28);
        } else {
            iOrdinal = panel.getCategory().ordinal() >= 1 ? this.x + 13 + (panel.getCategory().ordinal() * 28) : this.x + 15 + (panel.getCategory().ordinal() * 28);
        }
        if (RenderUtil.isHovering(iOrdinal, panel.getCategory().ordinal() >= 6 ? this.y + 44 : (this.y + 380) - 30.5f, panel.getCategory().ordinal() >= 6 ? 48.0f : 15.0f, panel.getCategory().ordinal() >= 6 ? 25.0f : 15.0f, mouseX, mouseY)) {
            for (CategoryPanel p : this.categoryPanels) {
                p.setSelected(false);
            }
            panel.setSelected(true);
            return true;
        }
        return false;
    }

    @Override // net.minecraft.client.gui.GuiScreen
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.espPreviewComponent.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            this.dragging = false;
        }
        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.mouseReleased(mouseX, mouseY, state);
        }
    }

    public CategoryPanel getSelected() {
        return this.categoryPanels.stream().filter((v0) -> {
            return v0.isSelected();
        }).findAny().orElse(null);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public boolean doesGuiPauseGame() {
        return false;
    }
}
