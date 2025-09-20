package com.amaya.gui.widget.impl;

import com.amaya.Amaya;
import com.amaya.events.impl.render.Shader2DEvent;
import com.amaya.gui.widget.Widget;
import com.amaya.utils.animations.Animation;
import com.amaya.module.Module;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.fontrender.FontRenderer;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.util.Comparator;

import static com.amaya.module.Category.*;

/**
 * @Author: Guyuemang
 * 2025/5/10
 */
public class ArrayList extends Widget {
    public ArrayList() {
        super("ArrayList");
    }
    private com.amaya.module.impl.display.ArrayList value = Amaya.Instance.moduleManager.getModule(com.amaya.module.impl.display.ArrayList.class);

    @Override
    public void onShader(Shader2DEvent event) {
        String suffixstring1 = " ";
        String suffixstring2 = switch (value.suffixmode.getValue()) {
            case "<>" -> {
                suffixstring1 = "<";
                yield ">";
            }
            case "()" -> {
                suffixstring1 = "(";
                yield ")";
            }
            case "[]" -> {
                suffixstring1 = "[";
                yield "]";
            }
            case "None" -> {
                suffixstring1 = " ";
                yield " ";
            }
            default -> "";
        };
        FontRenderer string = Font();

        java.util.ArrayList<Module> enabledMods = getModuleArrayList(suffixstring1, string, suffixstring2);

        int count = 0;
        for (Module module : enabledMods) {
            if (value.importantModules.get()){
                if (module.getCategory() == Display) continue;
                if (module.getCategory() == Render) continue;
            }
            Animation moduleAnimation = module.getAnimations();
            moduleAnimation.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (!module.getState() && moduleAnimation.finished(Direction.BACKWARDS)) continue;

            int renderx = (int) renderX;
            int rendery = (int) renderY;
            boolean flip = renderX + width / 2 <= sr.getScaledWidth() / 2f;
            int x = flip ? (renderx + 5) : (int) (renderx + (this.width - string.getStringWidth(value.suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName())));
            String displayText =  module.getName() + (value.suffix.get() && module.hasMode() ? "§f" + suffixstring1 + module.getSuffix() + suffixstring2 : "");
            if (value.moduletags.get()) {
                displayText =  module.getName() + (value.suffix.get() && module.hasMode() ? "" + suffixstring1 + module.getSuffix() + suffixstring2 : "");
            }
            int w = string.getStringWidth(value.suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName()) + 4;

            switch (value.animation.get()) {
                case "Move In":
                    if (flip) {
                        x -= (int) Math.abs((moduleAnimation.getOutput() - 1.0) * (2.0 + string.getStringWidth(displayText)));
                    } else {
                        x += (int) Math.abs((moduleAnimation.getOutput() - 1.0) * (2.0 + string.getStringWidth(displayText)));
                    }
                    break;
                case "Scale In":
                    if (flip) {
                        RenderUtil.scaleStart(x, rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput().floatValue());
                    } else {
                        RenderUtil.scaleStart(x + string.getStringWidth(value.suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName()), rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput().floatValue());
                    }
                    break;
            }
            if (value.background.getValue()) {
                switch (value.backgroundmod.get()) {
                    case "Rect" :
                        RenderUtil.drawRect(x - 2, rendery + count - 4, w, value.hight.get().intValue(), new Color(10, 10, 10, 255).getRGB());
                        break;
                    case "Round":
                        RoundedUtil.drawRound(x - 2, rendery + count - 4, w, value.hight.get().intValue(),value.radius.get().intValue(), new Color(10, 10, 10,255));
                        break;
                }
            }
            int index = (int) (count * value.colorIndex.getValue());
            int textcolor = ColorUtil.swapAlpha(value.color(enabledMods.size()), (int) 255);
            if (value.color.is("Tenacity")) {
                textcolor = ColorUtil.interpolateColorsBackAndForth(value.colorspeed.getValue().intValue(), index, value.FirstColor.get(), value.SecondColor.get(), false).getRGB();
            }
            switch (value.misc.getValue()) {
                case "Top":
                    if (count == 0) {
                        RenderUtil.drawRect(x - 2, rendery - 4, w, 2, textcolor);
                    }
                    break;
                case "Side":
                    if (flip) {
                        RenderUtil.drawRect(x - 4, rendery - 4 + count, 2, value.hight.get().intValue(),textcolor);
                    }else {
                        RenderUtil.drawRect(x + string.getStringWidth(displayText) + 2, rendery - 4 + count, 2, value.hight.get().intValue(), textcolor);
                    }
                    break;
                default:
                    break;
            }



            if (value.animation.get().equals("Scale In")) {
                RenderUtil.scaleEnd();
            }

            count += (int) (moduleAnimation.getOutput() * (value.hight.get() * value.count.get()));

            this.width = 100;
            this.height = count;
        }
    }

    @Override
    public void render() {
        String suffixstring1 = " ";
        String suffixstring2 = switch (value.suffixmode.getValue()) {
            case "<>" -> {
                suffixstring1 = "<";
                yield ">";
            }
            case "()" -> {
                suffixstring1 = "(";
                yield ")";
            }
            case "[]" -> {
                suffixstring1 = "[";
                yield "]";
            }
            case "None" -> {
                suffixstring1 = " ";
                yield " ";
            }
            default -> "";
        };
        FontRenderer string = Font();

        java.util.ArrayList<Module> enabledMods = getModuleArrayList(suffixstring1, string, suffixstring2);

        int count = 0;
        for (Module module : enabledMods) {
            if (value.importantModules.get()){
                if (module.getCategory() == Display) continue;
                if (module.getCategory() == Render) continue;
            }
            Animation moduleAnimation = module.getAnimations();
            moduleAnimation.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (!module.getState() && moduleAnimation.finished(Direction.BACKWARDS)) continue;

            int renderx = (int) renderX;
            int rendery = (int) renderY;
            boolean flip = renderX + width / 2 <= sr.getScaledWidth() / 2f;
            int x = flip ? (renderx + 5) : (int) (renderx + (this.width - string.getStringWidth(value.suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName())));
            String displayText =  module.getName() + (value.suffix.get() && module.hasMode() ? "§f" + suffixstring1 + module.getSuffix() + suffixstring2 : "");
            if (value.moduletags.get()) {
                displayText =  module.getName() + (value.suffix.get() && module.hasMode() ? "" + suffixstring1 + module.getSuffix() + suffixstring2 : "");
            }
            int w = string.getStringWidth(value.suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName()) + 4;

            switch (value.animation.get()) {
                case "Move In":
                    if (flip) {
                        x -= (int) Math.abs((moduleAnimation.getOutput() - 1.0) * (2.0 + string.getStringWidth(displayText)));
                    } else {
                        x += (int) Math.abs((moduleAnimation.getOutput() - 1.0) * (2.0 + string.getStringWidth(displayText)));
                    }
                    break;
                case "Scale In":
                    if (flip) {
                        RenderUtil.scaleStart(x, rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput().floatValue());
                    } else {
                        RenderUtil.scaleStart(x + string.getStringWidth(value.suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName()), rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput().floatValue());
                    }
                    break;
            }
            if (value.background.getValue()) {
                switch (value.backgroundmod.get()) {
                    case "Rect" :
                        RenderUtil.drawRect(x - 2, rendery + count - 4, w, value.hight.get().intValue(), new Color(10, 10, 10, value.backgroundAlpha.getValue().intValue()).getRGB());
                    break;
                    case "Round":
                        RoundedUtil.drawRound(x - 2, rendery + count - 4, w, value.hight.get().intValue(),value.radius.get().intValue(), new Color(10, 10, 10, value.backgroundAlpha.getValue().intValue()));
                        break;
                }
            }
            int index = (int) (count * value.colorIndex.getValue());
            int textcolor = ColorUtil.swapAlpha(value.color(enabledMods.size()), (int) 255);
            if (value.color.is("Tenacity")) {
                textcolor = ColorUtil.interpolateColorsBackAndForth(value.colorspeed.getValue().intValue(), index, value.FirstColor.get(), value.SecondColor.get(), false).getRGB();
            }
            switch (value.misc.getValue()) {
                case "Top":
                    if (count == 0) {
                        RenderUtil.drawRect(x - 2, rendery - 4, w, 2, textcolor);
                    }
                    break;
                case "Side":
                    if (flip) {
                        RenderUtil.drawRect(x - 4, rendery - 4 + count, 2, value.hight.get().intValue(),textcolor);
                    }else {
                        RenderUtil.drawRect(x + string.getStringWidth(displayText) + 2, rendery - 4 + count, 2, value.hight.get().intValue(), textcolor);
                    }
                    break;
                default:
                    break;
            }

            switch (value.textShadow.getValue()) {
                case "None":
                    string.drawString(displayText, x, rendery + count,textcolor);
                    break;
                case "Colored":
                    RenderUtil.resetColor();
                    string.drawString(StringUtils.stripColorCodes(displayText), x + 1, rendery + count + 1, ColorUtil.darker(textcolor, .5f));
                    RenderUtil.resetColor();
                    string.drawString(displayText, x, rendery + count, textcolor);
                    break;
                case "Black":
                    float f = 1f;
                    string.drawString(StringUtils.stripColorCodes(displayText), x + f, rendery + count + f,
                            ColorUtil.applyOpacity(Color.BLACK,1f).getRGB());
                    RenderUtil.resetColor();
                    RenderUtil.resetColor();
                    string.drawString(displayText, x, rendery + count, textcolor);
                    break;
            }

            if (value.animation.get().equals("Scale In")) {
                RenderUtil.scaleEnd();
            }

            count += (int) (moduleAnimation.getOutput() * (value.hight.get() * value.count.get()));

            this.width = 100;
            this.height = count;
        }
    }
    private java.util.ArrayList<Module> getModuleArrayList(String suffixstring1, FontRenderer string, String suffixstring2) {
        Comparator<Module> sort = (m1, m2) -> {
            double ab = string.getStringWidth(value.suffix.get() && m1.hasMode() ? m1.getName() + suffixstring1 + m1.getSuffix() + suffixstring2 : m1.getName());
            double bb = string.getStringWidth(value.suffix.get() && m2.hasMode() ? m2.getName() + suffixstring1 + m2.getSuffix() + suffixstring2 : m2.getName());
            return Double.compare(bb, ab);
        };
        java.util.ArrayList<Module> enabledMods = new java.util.ArrayList<>(Amaya.Instance.moduleManager.getAllModules());
        enabledMods.sort(sort);
        return enabledMods;
    }
    public FontRenderer Font(){
        FontRenderer fontRenderer = FontManager.REGULAR.get(value.size.get().intValue());
        switch (value.textFont.getValue()){
            case "Bold":
                fontRenderer = FontManager.SEMIBOLD.get(value.size.get().intValue());
                break;
            case "Regular":
                fontRenderer = FontManager.REGULAR.get(value.size.get().intValue());
                break;
            case "Minecraft":
                fontRenderer = FontManager.MINECRAFT.get(value.size.get().intValue());
                break;
        }
        return fontRenderer;
    }
    @Override
    public boolean shouldRender() {
        return value.getState();
    }
}
