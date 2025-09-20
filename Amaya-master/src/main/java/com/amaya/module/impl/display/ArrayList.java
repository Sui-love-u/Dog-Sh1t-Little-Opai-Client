package com.amaya.module.impl.display;

import com.amaya.Amaya;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ColorSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.render.ColorUtil;

import java.awt.*;

/**
 * @Author: Guyuemang
 * 2025/5/10
 */
@ModuleInfo(name = "ArrayList",category = Category.Display)
public class ArrayList extends Module {
    public static BooleanSetting importantModules = new BooleanSetting("Important", false);
    public BooleanSetting suffix = new BooleanSetting("suffix",true);
    public ModeSetting textFont = new ModeSetting("Text Font","Regular",new String[]{"Bold", "Regular","Minecraft"});
    public ModeSetting textShadow = new ModeSetting("Text Shadow","None", new String[]{"Black", "Colored", "None"});
    public final ModeSetting color = new ModeSetting("Color Setting","Rainbow", new String[]{"Custom", "Rainbow", "Dynamic", "Fade","Astolfo","Tenacity"});
    public final NumberSetting colorspeed = new NumberSetting("ColorSpeed", () -> color.is("Dynamic") || color.is("Fade") || color.is("Tenacity"), 1, 1, 10, 1);
    public final NumberSetting colorIndex = new NumberSetting("Color Seperation", 20, 5, 100, 1);
    public final BooleanSetting moduletags = new BooleanSetting("Color Tags", true);
    public ModeSetting suffixmode = new ModeSetting("suffix Mode", () -> suffix.get(), "<>",new String[]{"<>","()","[]","None"});
    public ModeSetting misc = new ModeSetting("Rectangle","Top",new String[]{"None", "Top", "Side"});
    public ModeSetting animation = new ModeSetting("Animation", "Move In",new String[]{"Move In","Scale In"});
    public BooleanSetting background = new BooleanSetting("BackGround",true);
    public NumberSetting radius = new NumberSetting("radius",()-> background.get(),3,0,8,1);
    public ModeSetting backgroundmod = new ModeSetting("BackGroundMod",()-> background.get(),"Rect",new String[]{"Rect","Round"});
    public final NumberSetting backgroundAlpha = new NumberSetting("Background Alpha", ()-> background.get(), 100, 0, 255, .01);
    public ColorSetting FirstColor = new ColorSetting("FirstColor", new Color(255, 155, 221));
    public ColorSetting SecondColor = new ColorSetting("SecondColor", new Color(155, 205, 255));
   public NumberSetting hight = new NumberSetting("ArrayHight",12.0,1.0,20.0,0.1);
    public NumberSetting count = new NumberSetting("ArrayCount",1,1.0,5,0.1);
    public NumberSetting size = new NumberSetting("ArrayFontsize",16,1.0,40.0,1);
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
            case "Tenacity" -> ColorUtil.interpolateColorsBackAndForth(colorspeed.getValue().intValue(), Amaya.Instance.moduleManager.getAllModules().size() * count.get().intValue(), FirstColor.get(), SecondColor.get(), false).getRGB();
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

}
