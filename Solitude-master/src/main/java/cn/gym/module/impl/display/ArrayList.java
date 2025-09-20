package cn.gym.module.impl.display;

import cn.gym.Solitude;
import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.ModuleWidget;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.fontrender.FontRenderer;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ColorValue;
import cn.gym.value.impl.ModeValue;
import cn.gym.value.impl.NumberValue;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.util.Comparator;

import static cn.gym.module.Category.Display;
import static cn.gym.module.Category.Render;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 13:38
 */
public class ArrayList extends ModuleWidget {
    public ArrayList() {
        super("ArrayList",Category.Display);
    }
    public ColorValue FirstColor = new ColorValue("FirstColor", new Color(155, 255, 190));
    public ColorValue SecondColor = new ColorValue("SecondColor", new Color(195, 155, 255));
    public static BooleanValue importantModules = new BooleanValue("Important", false);
    public static final ModeValue font = new ModeValue("FontMode","Mc",new String[]{"Mc","Regular","Bold","Semibold","Light"});
    public BooleanValue suffix = new BooleanValue("suffix",false);
    public ModeValue suffixmode = new ModeValue("suffix Mode", () -> suffix.get(), "<>",new String[]{"<>","()","[]", ":", "-","None"});
    public ModeValue textShadow = new ModeValue("Text Shadow","None", new String[]{"Black", "Colored", "None"});
    public final ModeValue color = new ModeValue("Color Setting","Rainbow", new String[]{"Custom", "Rainbow", "Dynamic", "Fade","Astolfo","Tenacity"});
    public final NumberValue colorspeed = new NumberValue("ColorSpeed", () -> color.is("Dynamic") || color.is("Fade") || color.is("Tenacity"), 1, 1, 10, 1);
    public final NumberValue colorIndex = new NumberValue("Color Seperation", 20, 5, 100, 1);
    public final BooleanValue moduletags = new BooleanValue("Color Tags", false);
    public ModeValue misc = new ModeValue("Rectangle","None",new String[]{"None", "Top", "Side"});
    public ModeValue animation = new ModeValue("Animation", "Move In",new String[]{"Move In","Scale In"});
    public BooleanValue icontags = new BooleanValue("IconTags",false);
    public BooleanValue background = new BooleanValue("BackGround",false);
    public NumberValue radius = new NumberValue("radius",()-> background.get(),3,0,8,1);
    public ModeValue backgroundmod = new ModeValue("BackGroundMod",()-> background.get(),"Rect",new String[]{"Rect","Round"});
    public final NumberValue backgroundAlpha = new NumberValue("Background Alpha", ()-> background.get(), 0.5, 0, 1, .01);
    public NumberValue hight = new NumberValue("ArrayHight",12.0,1.0,20.0,0.1);
    public NumberValue count = new NumberValue("ArrayCount",1,1.0,5,0.1);
    public NumberValue size = new NumberValue("ArrayFontsize",16,1.0,40.0,1);

    @Override
    public void onShader(Shader2DEvent event) {
        String suffixstring1 = "";
        String suffixstring2 = switch (suffixmode.getValue()) {
            case "-" -> {
                suffixstring1 = "-";
                yield "";
            }
            case ":" -> {
                suffixstring1 = ":";
                yield "";
            }
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
                yield "";
            }
            default -> "";
        };
        FontRenderer string = getCurrentFont().get(18);
        java.util.ArrayList<Module> enabledMods = getModuleArrayList(suffixstring1, string, suffixstring2);
        int count = 0;
        for (Module module : enabledMods) {
            if (importantModules.get()){
                if (module.getCategory() == Display) continue;
                if (module.getCategory() == Render) continue;
            }
            Animation moduleAnimation = module.getAnimations();
            moduleAnimation.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (!module.getState() && moduleAnimation.finished(Direction.BACKWARDS)) continue;

            int renderx = (int) renderX;
            int rendery = (int) renderY;
            boolean flip = renderX + width / 2 <= sr.getScaledWidth() / 2f;
            int x = flip ? (renderx + 5) : (int) (renderx + (this.width - string.getStringWidth(suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName())));

            String displayText = flip ? (suffix.get() && module.hasMode() ? "§f" + suffixstring2 + module.getSuffix() + suffixstring1 : "") + "§r" + module.getName() : module.getName() + (suffix.get() && module.hasMode() ? "§f" + suffixstring1 + module.getSuffix() + suffixstring2 : "");
            if (moduletags.get()) {
                displayText =  flip ? (suffix.get() && module.hasMode() ? "" + suffixstring2 + module.getSuffix() + suffixstring1 : "") + module.getName() : module.getName() + (suffix.get() && module.hasMode() ? "" + suffixstring1 + module.getSuffix() + suffixstring2 : "");
            }

            int w = string.getStringWidth(suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName()) + 4;

            switch (animation.get()) {
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
                        RenderUtil.scaleStart(x + string.getStringWidth(suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName()), rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput().floatValue());
                    }
                    break;
            }
            int index = (int) (count * colorIndex.getValue());
            int textcolor = ColorUtil.swapAlpha(color(enabledMods.size()), (int) 255);
            if (color.is("Tenacity")) {
                textcolor = ColorUtil.interpolateColorsBackAndForth(colorspeed.getValue().intValue(), index, FirstColor.get(), SecondColor.get(), false).getRGB();
            }
            if (icontags.get()){
                if (background.get()) {
                    switch (backgroundmod.get()) {
                        case "Rect":
                            RenderUtil.drawRect(flip ? x - 20: x + w + 5, rendery + count - 4, 12, hight.get().intValue(), ColorUtil.applyOpacity(new Color(1,1,1),1));
                            break;
                        case "Round":
                            RoundedUtil.drawRound(flip ? x - 20: x + w + 5, rendery + count - 4, 12, hight.get().intValue(), radius.get().intValue(), ColorUtil.applyOpacity3(new Color(1,1,1).getRGB(), 1));
                            break;
                    }
                }
                switch (textShadow.getValue()) {
                    case "None":
                        Icon.get(35).drawString(module.getCategory().icon,flip ? x - 20: x + w + 5, rendery + count - 3,textcolor);
                        break;
                    case "Colored":
                        RenderUtil.resetColor();
                        Icon.get(35).drawString(StringUtils.stripColorCodes(module.getCategory().icon), flip ? x - 19 : x + w + 6, rendery + count - 2, ColorUtil.darker(textcolor, .5f));
                        RenderUtil.resetColor();
                        Icon.get(35).drawString(module.getCategory().icon,flip ? x - 20 : x + w + 5, rendery + count - 3, textcolor);
                        break;
                    case "Black":
                        float f = 1f;
                        Icon.get(35).drawString(StringUtils.stripColorCodes(module.getCategory().icon), flip ? x - 19 : x + w + 6, rendery + count - 3 + f,
                                ColorUtil.applyOpacity(Color.BLACK,1f).getRGB());
                        RenderUtil.resetColor();
                        RenderUtil.resetColor();
                        Icon.get(35).drawString(module.getCategory().icon,flip ? x - 20 : x + w + 5, rendery + count - 3, textcolor);
                        break;
                }
            }
            if (background.getValue()) {
                switch (backgroundmod.get()) {
                    case "Rect" :
                        RenderUtil.drawRect(x - 2, rendery + count - 4, w, hight.get().intValue(),ColorUtil.applyOpacity(new Color(1,1,1),1));
                        break;
                    case "Round":
                        RoundedUtil.drawRound(x - 2, rendery + count - 4, w, hight.get().intValue(),radius.get().intValue(),ColorUtil.applyOpacity3(new Color(1,1,1).getRGB(),1));
                        break;
                }
            }
            switch (misc.getValue()) {
                case "Top":
                    if (count == 0) {
                        RenderUtil.drawRect(x - 2, rendery - 4, w, 2, textcolor);
                    }
                    break;
                case "Side":
                    if (flip) {
                        RenderUtil.drawRect(x - 4, rendery - 4 + count, 2, hight.get().intValue(),textcolor);
                    }else {
                        RenderUtil.drawRect(x + string.getStringWidth(displayText) + 2, rendery - 4 + count, 2, hight.get().intValue(), textcolor);
                    }
                    break;
                default:
                    break;
            }

            switch (textShadow.getValue()) {
                case "None":
                    string.drawString(displayText, x, rendery + count - 2,textcolor);
                    break;
                case "Colored":
                    RenderUtil.resetColor();
                    string.drawString(StringUtils.stripColorCodes(displayText), x + 1, rendery + count + 1 - 2, ColorUtil.darker(textcolor, .5f));
                    RenderUtil.resetColor();
                    string.drawString(displayText, x, rendery + count - 2, textcolor);
                    break;
                case "Black":
                    float f = 1f;
                    string.drawString(StringUtils.stripColorCodes(displayText), x + f, rendery + count + f - 2,
                            ColorUtil.applyOpacity(Color.BLACK,1f).getRGB());
                    RenderUtil.resetColor();
                    RenderUtil.resetColor();
                    string.drawString(displayText, x, rendery + count - 2, textcolor);
                    break;
            }

            if (animation.get().equals("Scale In")) {
                RenderUtil.scaleEnd();
            }

            count += (int) (moduleAnimation.getOutput() * (hight.get() * this.count.get()));

            this.width = 100;
            this.height = count;
        }
    }

    @Override
    public void render() {
        String suffixstring1 = "";
        String suffixstring2 = switch (suffixmode.getValue()) {
            case "-" -> {
                suffixstring1 = "-";
                yield "";
            }
            case ":" -> {
                suffixstring1 = ":";
                yield "";
            }
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
                yield "";
            }
            default -> "";
        };
        FontRenderer string = getCurrentFont().get(18);
        java.util.ArrayList<Module> enabledMods = getModuleArrayList(suffixstring1, string, suffixstring2);
        int count = 0;
        for (Module module : enabledMods) {
            if (importantModules.get()){
                if (module.getCategory() == Display) continue;
                if (module.getCategory() == Render) continue;
            }
            Animation moduleAnimation = module.getAnimations();
            moduleAnimation.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (!module.getState() && moduleAnimation.finished(Direction.BACKWARDS)) continue;

            int renderx = (int) renderX;
            int rendery = (int) renderY;
            boolean flip = renderX + width / 2 <= sr.getScaledWidth() / 2f;
            int x = flip ? (renderx + 5) : (int) (renderx + (this.width - string.getStringWidth(suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName())));
            String displayText = flip ? (suffix.get() && module.hasMode() ? "§f" + suffixstring2 + module.getSuffix() + suffixstring1 : "") + "§r" + module.getName() : module.getName() + (suffix.get() && module.hasMode() ? "§f" + suffixstring1 + module.getSuffix() + suffixstring2 : "");
            if (moduletags.get()) {
                displayText =  flip ? (suffix.get() && module.hasMode() ? "" + suffixstring2 + module.getSuffix() + suffixstring1 : "") + module.getName() : module.getName() + (suffix.get() && module.hasMode() ? "" + suffixstring1 + module.getSuffix() + suffixstring2 : "");
            }
            int w = string.getStringWidth(suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName()) + 4;

            switch (animation.get()) {
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
                        RenderUtil.scaleStart(x + string.getStringWidth(suffix.get() && module.hasMode() ? module.getName() + suffixstring1 + module.getSuffix() + suffixstring2 : module.getName()), rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput().floatValue());
                    }
                    break;
            }
            int index = (int) (count * colorIndex.getValue());
            int textcolor = ColorUtil.swapAlpha(color(enabledMods.size()), (int) 255);
            if (color.is("Tenacity")) {
                textcolor = ColorUtil.interpolateColorsBackAndForth(colorspeed.getValue().intValue(), index, FirstColor.get(), SecondColor.get(), false).getRGB();
            }
            if (icontags.get()){
                if (background.get()) {
                    switch (backgroundmod.get()) {
                        case "Rect":
                            RenderUtil.drawRect(flip ? x - 20: x + w + 5, rendery + count - 4, 12, hight.get().intValue(), ColorUtil.applyOpacity(new Color(1,1,1), backgroundAlpha.get().floatValue()));
                            break;
                        case "Round":
                            RoundedUtil.drawRound(flip ? x - 20: x + w + 5, rendery + count - 4, 12, hight.get().intValue(), radius.get().intValue(), ColorUtil.applyOpacity3(new Color(1,1,1).getRGB(), backgroundAlpha.get().floatValue()));
                            break;
                    }
                }
                switch (textShadow.getValue()) {
                    case "None":
                        Icon.get(35).drawString(module.getCategory().icon,flip ? x - 20: x + w + 5, rendery + count - 3,textcolor);
                        break;
                    case "Colored":
                        RenderUtil.resetColor();
                        Icon.get(35).drawString(StringUtils.stripColorCodes(module.getCategory().icon), flip ? x - 19 : x + w + 6, rendery + count - 2, ColorUtil.darker(textcolor, .5f));
                        RenderUtil.resetColor();
                        Icon.get(35).drawString(module.getCategory().icon,flip ? x - 20 : x + w + 5, rendery + count - 3, textcolor);
                        break;
                    case "Black":
                        float f = 1f;
                        Icon.get(35).drawString(StringUtils.stripColorCodes(module.getCategory().icon), flip ? x - 19 : x + w + 6, rendery + count - 3 + f,
                                ColorUtil.applyOpacity(Color.BLACK,1f).getRGB());
                        RenderUtil.resetColor();
                        RenderUtil.resetColor();
                        Icon.get(35).drawString(module.getCategory().icon,flip ? x - 20 : x + w + 5, rendery + count - 3, textcolor);
                        break;
                }
            }
            if (background.getValue()) {
                switch (backgroundmod.get()) {
                    case "Rect" :
                        RenderUtil.drawRect(x - 2, rendery + count - 4, w, hight.get().intValue(),ColorUtil.applyOpacity(new Color(1,1,1),backgroundAlpha.get().floatValue()));
                        break;
                    case "Round":
                        RoundedUtil.drawRound(x - 2, rendery + count - 4, w, hight.get().intValue(),radius.get().intValue(),ColorUtil.applyOpacity3(new Color(1,1,1).getRGB(),backgroundAlpha.get().floatValue()));
                        break;
                }
            }
            switch (misc.getValue()) {
                case "Top":
                    if (count == 0) {
                        RenderUtil.drawRect(x - 2, rendery - 4, w, 2, textcolor);
                    }
                    break;
                case "Side":
                    if (flip) {
                        RenderUtil.drawRect(x - 4, rendery - 4 + count, 2, hight.get().intValue(),textcolor);
                    }else {
                        RenderUtil.drawRect(x + string.getStringWidth(displayText) + 2, rendery - 4 + count, 2, hight.get().intValue(), textcolor);
                    }
                    break;
                default:
                    break;
            }

            switch (textShadow.getValue()) {
                case "None":
                    string.drawString(displayText, x, rendery + count - 2,textcolor);
                    break;
                case "Colored":
                    RenderUtil.resetColor();
                    string.drawString(StringUtils.stripColorCodes(displayText), x + 1, rendery + count + 1 - 2, ColorUtil.darker(textcolor, .5f));
                    RenderUtil.resetColor();
                    string.drawString(displayText, x, rendery + count - 2, textcolor);
                    break;
                case "Black":
                    float f = 1f;
                    string.drawString(StringUtils.stripColorCodes(displayText), x + f, rendery + count + f - 2,
                            ColorUtil.applyOpacity(Color.BLACK,1f).getRGB());
                    RenderUtil.resetColor();
                    RenderUtil.resetColor();
                    string.drawString(displayText, x, rendery + count - 2, textcolor);
                    break;
            }

            if (animation.get().equals("Scale In")) {
                RenderUtil.scaleEnd();
            }

            count += (int) (moduleAnimation.getOutput() * (hight.get() * this.count.get()));

            this.width = 100;
            this.height = count;
        }
    }
    private java.util.ArrayList<Module> getModuleArrayList(String suffixstring1, FontRenderer string, String suffixstring2) {
        Comparator<Module> sort = (m1, m2) -> {
            double ab = string.getStringWidth(suffix.get() && m1.hasMode() ? m1.getName() + suffixstring1 + m1.getSuffix() + suffixstring2 : m1.getName());
            double bb = string.getStringWidth(suffix.get() && m2.hasMode() ? m2.getName() + suffixstring1 + m2.getSuffix() + suffixstring2 : m2.getName());
            return Double.compare(bb, ab);
        };
        java.util.ArrayList<Module> enabledMods = new java.util.ArrayList<>(INSTANCE.getModuleManager().getAllModules());
        enabledMods.sort(sort);
        return enabledMods;
    }
    public FontManager getCurrentFont() {
        return switch (font.get()) {
            case "Mc" -> Mc;
            case "Regular" -> Regular;
            case "Bold" -> Bold;
            case "Semibold" -> Semibold;
            case "Light" -> Light;
            default -> Mc;
        };
    }
    public int color(int counter) {
        return color(counter, FirstColor.get().getAlpha());
    }
    public int color(int counter, int alpha) {
        int colors = FirstColor.get().getRGB();
        colors = switch (color.get()) {
            case "Rainbow" -> ColorUtil.swapAlpha(getRainbow(counter), alpha);
            case "Dynamic" ->
                    ColorUtil.swapAlpha(ColorUtil.colorSwitch(FirstColor.get(), new Color(ColorUtil.darker(FirstColor.get().getRGB(), 0.25F)), 2000.0F, counter, 75L, colorspeed.get()).getRGB(), alpha);
            case "Fade" ->
                    ColorUtil.swapAlpha((ColorUtil.colorSwitch(FirstColor.get(), SecondColor.get(), 2000.0F, counter, 75L, colorspeed.get()).getRGB()), alpha);
            case "Astolfo" ->
                    ColorUtil.swapAlpha(astolfoRainbow(counter, FirstColor.getSaturation(), FirstColor.getBrightness()), alpha);
            case "Custom" -> ColorUtil.swapAlpha(FirstColor.get().getRGB(), alpha);
            case "Tenacity" -> ColorUtil.interpolateColorsBackAndForth(colorspeed.getValue().intValue(), Solitude.Instance.getModuleManager().getAllModules().size() * count.get().intValue(), FirstColor.get(), SecondColor.get(), false).getRGB();
            default -> colors;
        };
        return new Color(colors,true).getRGB();
    }
    public int getRainbow(int counter) {
        return Color.HSBtoRGB(getRainbowHSB(counter)[0], getRainbowHSB(counter)[1], getRainbowHSB(counter)[2]);
    }
    public float[] getRainbowHSB(int counter) {
        final int width = 20;

        double rainbowState = Math.ceil(System.currentTimeMillis() - (long) counter * width) / 8;
        rainbowState %= 360;

        float hue = (float) (rainbowState / 360);
        float saturation = FirstColor.getSaturation();
        float brightness = FirstColor.getBrightness();

        return new float[]{hue, saturation, brightness};
    }
    public static int astolfoRainbow(final int offset, final float saturation, final float brightness) {
        double currentColor = Math.ceil((double)(System.currentTimeMillis() + offset * 20L)) / 6.0;
        return Color.getHSBColor(((float)((currentColor %= 360.0) / 360.0) < 0.5) ? (-(float)(currentColor / 360.0)) : ((float)(currentColor / 360.0)), saturation, brightness).getRGB();
    }
    @Override
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
