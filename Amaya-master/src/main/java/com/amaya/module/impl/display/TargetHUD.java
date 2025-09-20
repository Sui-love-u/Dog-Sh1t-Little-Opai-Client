package com.amaya.module.impl.display;

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
@ModuleInfo(name = "TargetHUD",category = Category.Display)
public class TargetHUD extends Module {
    public final ModeSetting targetHudMode = new ModeSetting("TargetHUD Mode", "Astolfo", new String[]{"Astolfo", "Type 1", "Type 2","Felix","Exhi","Adjust","Moon","Augustus","New","Novo 1","Novo 2","Novo 3","Novo 4","Novo 5","Akrien","Innominate"});
    public final BooleanSetting targetHudParticle = new BooleanSetting("TargetHUD Particle",true);
    public final ModeSetting color = new ModeSetting("Color Setting", "Rainbow", new String[]{"Custom", "Rainbow", "Dynamic", "Fade","Astolfo"});
    public static ColorSetting FirstColor = new ColorSetting("FirstColor", new Color(128, 128, 255));
    public static ColorSetting SecondColor = new ColorSetting("FirstColor", new Color(255, 128, 200));
    public final ModeSetting bgColor = new ModeSetting("Background Color", "Dark", new String[]{"Dark", "Synced","Custom"});
    private final ColorSetting bgCustomColor = new ColorSetting("Background Custom Color",()-> bgColor.is("Custom"), new Color(32, 32, 64));
    private final NumberSetting bgAlpha = new NumberSetting("Background Alpha",100,1,255,1);
    public final NumberSetting fadeSpeed = new NumberSetting("Fade Speed", () -> color.is("Dynamic") || color.is("Fade"), 1, 1, 10, 1);

    public int color() {
        return color(0);
    }
    public Color getMainColor() {
        return FirstColor.get();
    }
    public Color getSecondColor() {
        return SecondColor.get();
    }
    public int color(int counter) {
        return color(counter, getMainColor().getAlpha());
    }
    public int color(int counter, int alpha) {
        int colors = getMainColor().getRGB();
        colors = switch (color.get()) {
            case "Rainbow" -> ColorUtil.swapAlpha(getRainbow(counter), alpha);
            case "Dynamic" ->
                    ColorUtil.swapAlpha(ColorUtil.colorSwitch(getMainColor(), new Color(ColorUtil.darker(getMainColor().getRGB(), 0.25F)), 2000.0F, counter, 75L, fadeSpeed.get()).getRGB(), alpha);
            case "Fade" ->
                    ColorUtil.swapAlpha((ColorUtil.colorSwitch(getMainColor(), getSecondColor(), 2000.0F, counter, 75L, fadeSpeed.get()).getRGB()), alpha);
            case "Astolfo" ->
                    ColorUtil.swapAlpha(astolfoRainbow(counter, FirstColor.getSaturation(), FirstColor.getBrightness()), alpha);
            case "Custom" -> ColorUtil.swapAlpha(FirstColor.get().getRGB(), alpha);
            default -> colors;
        };
        return new Color(colors,true).getRGB();
    }
    public int getRainbow(int counter) {
        return Color.HSBtoRGB(getRainbowHSB(counter)[0], getRainbowHSB(counter)[1], getRainbowHSB(counter)[2]);
    }
    public static int astolfoRainbow(final int offset, final float saturation, final float brightness) {
        double currentColor = Math.ceil((double)(System.currentTimeMillis() + offset * 20L)) / 6.0;
        return Color.getHSBColor(((float)((currentColor %= 360.0) / 360.0) < 0.5) ? (-(float)(currentColor / 360.0)) : ((float)(currentColor / 360.0)), saturation, brightness).getRGB();
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

    public int bgColor(int counter, int alpha) {
        int colors = getMainColor().getRGB();
        colors = switch (bgColor.get()) {
            case "Dark" -> (new Color(21, 21, 21, alpha)).getRGB();
            case "Synced" ->
                    new Color(ColorUtil.applyOpacity(color(counter, alpha), alpha / 255f), true).darker().darker().getRGB();
            case "None" -> new Color(0, 0, 0, 0).getRGB();
            case "Custom" -> ColorUtil.swapAlpha(bgCustomColor.get().getRGB(), alpha);
            default -> colors;
        };
        return colors;
    }
    public int bgColor(int counter) {
        return bgColor(counter,bgAlpha.get().intValue());
    }

    public int bgColor() {
        return bgColor(0);
    }
}
