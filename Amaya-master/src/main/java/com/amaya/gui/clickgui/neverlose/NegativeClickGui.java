package com.amaya.gui.clickgui.neverlose;

import com.amaya.Amaya;
import com.amaya.gui.clickgui.neverlose.panel.CategoryPanel;
import com.amaya.gui.clickgui.neverlose.panel.search.SearchPanel;
import com.amaya.module.Category;
import com.amaya.module.impl.render.ClickGui;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.animations.impl.EaseOutSine;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.InputField;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Guyuemang
 */
@Getter
public class NegativeClickGui extends GuiScreen {
    private final List<CategoryPanel> panels = new ArrayList<>();
    private int posX = 40,posY = 40,dragX,dragY,width = 520,height = 420;
    private boolean dragging = false;
    public InputField searchTextField = new InputField(FontManager.REGULAR.get(20));
    private final Animation hover = new DecelerateAnimation(250,1);
    public static Color lineColor;
    public static Color bgcolor , bgcolor2;
    public static Color bgcolor3 , bgcolor4;
    public static Color bgcolor5 , bgcolor6;
    public static Color bgcolor7;
    public static Color textcolor;
    public static Color categorycolor;
    public static Color circlecolor;
    @Getter
    private final Animation openingAnimation = new EaseOutSine(400, 1);
    private boolean closing;

    public NegativeClickGui(String name){
        openingAnimation.setDirection(Direction.BACKWARDS);
        Arrays.stream(Category.values()).filter(moduleCategory -> !(moduleCategory == Category.Search))
                .forEach(ca -> panels
                        .add(new CategoryPanel(ca)));
        panels.add(new SearchPanel(Category.Search));
    }

    @Override
    public void initGui() {
        closing = false;
        openingAnimation.setDirection(Direction.FORWARDS);
        super.initGui();
    }

    private Animation hoverAnimation = new DecelerateAnimation(255, 1);;

    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        if (closing) {
            mc.displayGuiScreen(null);
        }

        lineColor = new Color(100,100,100);
        bgcolor = new Color(15,15,15);
        bgcolor2 = new Color(10,10,10);
        bgcolor3 = new Color(33,33,33);
        bgcolor4 = new Color(44,44,44);
        bgcolor5 = new Color(22,22,22);
        bgcolor6 = new Color(55,55,55);
        bgcolor7 = new Color(70,70,70);
        circlecolor = new Color(200,200,200);
        textcolor = Color();
        categorycolor = Color().darker();
        if (!ClickGui.Darkmode.getValue()){
            lineColor = new Color(155,155,155);
            bgcolor = new Color(240,240,240);
            bgcolor2 = new Color(245,245,245);
            bgcolor3 = new Color(222,222,222);
            bgcolor4 = new Color(211,211,211);
            bgcolor5 = new Color(233,233,233);
            bgcolor6 = new Color(200,200,200);
            bgcolor7 = new Color(175,175,175);
            circlecolor = new Color(55,55,55);
            textcolor = Color();
            categorycolor = Color().brighter();
        }
        if (getSelected() == null) {
            if (!panels.isEmpty()) {
                panels.get(0).setSelected(true);
            }
        }
        if (dragging) {
            posX = mouseX + dragX;
            posY = mouseY + dragY;
        }

        RoundedUtil.drawRound(posX,posY,width,height,6f,bgcolor);
        RenderUtil.startGlScissor(posX + 131,posY,width - 130,height);
        RoundedUtil.drawRound(posX + 130,posY,width - 130,height,6f,bgcolor2);
        RenderUtil.stopGlScissor();
        RoundedUtil.drawRound(posX + 132,posY,.8f,height,0,lineColor.darker());
        RoundedUtil.drawRound(posX + 133,posY + 49,width - 133,.8f,0,lineColor.darker());
        RoundedUtil.drawRound(posX,posY + height - 36,131,.8f,0,lineColor.darker());
        FontManager.BOLD.get(36).drawCenteredString("Amaya",posX + 131 / 2,posY + 12,textcolor.getRGB());

        FontManager.SEMIBOLD.get(18).drawString("Category",posX + 10,posY + 60, textcolor.darker().getRGB());

        RenderUtil.renderPlayer2D(mc.thePlayer, posX + 5, posY + 389, 27, 27, -1);
        FontManager.SEMIBOLD.get(16).drawString(mc.thePlayer.getNameClear(),posX + 37,posY + 396,textcolor.getRGB());
        FontManager.SEMIBOLD.get(16).drawString(EnumChatFormatting.GRAY + "Till: ",posX + 37,posY + 406,textcolor.getRGB());
        FontManager.SEMIBOLD.get(16).drawString("Lifetime",posX + 37 + FontManager.SEMIBOLD.get(16).getStringWidth(EnumChatFormatting.GRAY + "Till: "),posY + 406,textcolor.getRGB());


        hover.setDirection(RenderUtil.isHovering(posX + width - 20,posY + 18,10,10,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!panels.get(6).isSelected()){
            if (!panels.get(5).isSelected()) {
                FontManager.neverlose.get(20).drawString("j", posX + width - 20, posY + 21, ColorUtil.interpolateColor2(textcolor, textcolor.darker().darker(), (float) hover.getOutput().floatValue()));
            } else {
                FontManager.neverlose.get(24).drawString("j", posX + width - 21, posY + 20, ColorUtil.interpolateColor2(textcolor, textcolor.darker().darker(), (float) hover.getOutput().floatValue()));
            }
        }
        for (CategoryPanel panel : panels) {
            if (panel.getCategory() != Category.Search) {
                panel.drawScreen(mouseX, mouseY);
                if (panel.isSelected()) {
                    RoundedUtil.drawRound(posX + 10, posY + 75 + panel.getCategory().ordinal() * 34, 115, 19, 5, ColorUtil.applyOpacity(categorycolor, (float) panel.getAnimation().getOutput().floatValue()));
                }
                FontManager.ICON.get(40).drawString(panel.getCategory().icon, posX + 20, posY + 78 + panel.getCategory().ordinal() * 34, textcolor.getRGB());
                FontManager.SEMIBOLD.get(20).drawString(panel.getCategory().name(), posX + 40, posY + 81 + panel.getCategory().ordinal() * 34, textcolor.getRGB());
            }else if (!panels.get(6).isSelected()) {
                panel.drawScreen(mouseX,mouseY);
            }
        }
    }

    public CategoryPanel getSelected() {
        return panels.stream().filter(CategoryPanel::isSelected).findAny().orElse(null);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            closing = true;
            return;
        }
        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0){
            for (CategoryPanel panel : panels) {
                if (handleSearchPanel(panel, mouseX, mouseY)) {
                    continue;
                }
                if (handleCategoryPanel(panel, mouseX, mouseY)) {
                    break;
                }
            }
            if (RenderUtil.isHovering(posX,posY,136,42, mouseX, mouseY)) {
                dragging = true;
                dragX = posX - mouseX;
                dragY = posY - mouseY;
            }
        }
        CategoryPanel selected = getSelected();
        if (selected != null) {
            if (!selected.getCategory().name().equals("Search") && !selected.getCategory().name().equals("Configs") && !RenderUtil.isHovering(getPosX() + 140, getPosY() + 49, 380, 368,mouseX,mouseY)) return;
            selected.mouseClicked(mouseX, mouseY, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean handleSearchPanel(CategoryPanel panel, int mouseX, int mouseY) {
        if (panel.getCategory() == Category.Search && !panels.get(6).isSelected() && RenderUtil.isHovering(posX + width - 20, posY + 18, 10, 10, mouseX, mouseY)) {
            if (!panel.isSelected()) {
                for (CategoryPanel p : panels) {
                    p.setSelected(false);
                }
                panel.setSelected(true);
            } else {
                for (CategoryPanel p : panels) {
                    p.setSelected(false);
                }
                panels.get(0).setSelected(true);
            }
            return true;
        }
        return false;
    }

    private boolean handleCategoryPanel(CategoryPanel panel, int mouseX, int mouseY) {
        if (panel.getCategory() != Category.Search && RenderUtil.isHovering(posX + 10,posY + 73 + panel.getCategory().ordinal() * 34,115,19, mouseX, mouseY)) {
            for (CategoryPanel p : panels) {
                p.setSelected(false);
            }
            panel.setSelected(true);
            return true;
        }
        return false;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0){
            dragging = false;
        }
        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.mouseReleased(mouseX, mouseY, state);
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private final ClickGui value = Amaya.Instance.moduleManager.getModule(ClickGui.class);
    public Color Color(){
        int color = Color.WHITE.getRGB();
        int index = (int) (Amaya.Instance.moduleManager.getAllModules().size() * value.colorIndex.getValue());
        switch (value.color2.getValue()) {
            case "Custom":
                color = value.color.get().getRGB();
                break;
            case "Rainbow":
                color = ColorUtil.getRainbow().getRGB();
                break;
            case "Dynamic":
                color = ColorUtil.swapAlpha(ColorUtil.colorSwitch(value.color.get(), new Color(ColorUtil.darker(value.color.get().getRGB(), 0.25F)), 2000.0F, value.colorcounter.get().intValue(), 75L, value.fadeSpeed.get()).getRGB(), 255);
                break;
            case "Fade":
                color = ColorUtil.swapAlpha((ColorUtil.colorSwitch(value.color.get(), value.SecondColor.get(), 2000.0F, value.colorcounter.get().intValue(), 75L, value.fadeSpeed.get()).getRGB()), 255);
                break;
            case "Astolfo":
                color = ColorUtil.swapAlpha(ColorUtil.astolfoRainbow(value.colorcounter.get().intValue(), value.color.getSaturation(), value.color.getBrightness()), 255);
                break;
            case "Tenacity":
                color = ColorUtil.interpolateColorsBackAndForth(value.fadeSpeed.getValue().intValue(), index, value.color.get(), value.SecondColor.get(), false).getRGB();
                break;
        }
        return new Color(color);
    }
}
