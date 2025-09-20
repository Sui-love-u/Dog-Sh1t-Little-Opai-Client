package qwq.arcane.utils.color;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.minecraft.entity.EntityLivingBase;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.math.MathUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/color/ColorUtil.class */
public class ColorUtil {
    public static Color reAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static int getColorFromPercentage(float percentage) {
        return Color.HSBtoRGB(Math.min(1.0f, Math.max(0.0f, percentage)) / 3.0f, 0.9f, 0.9f);
    }

    public static Color tripleColor(int rgbValue) {
        return tripleColor(rgbValue, 1.0f);
    }

    public static int getHealthColor(EntityLivingBase player) {
        float f = player.getHealth();
        float f1 = player.getMaxHealth();
        float f2 = Math.max(0.0f, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0f, 0.75f, 1.0f) | (-16777216);
    }

    public static Color tripleColor(int rgbValue, float alpha) {
        return new Color(rgbValue, rgbValue, rgbValue, (int) (255.0f * Math.min(1.0f, Math.max(0.0f, alpha))));
    }

    public static Color mixColors(Color color1, Color color2, double percent) {
        double inverse_percent = 1.0d - percent;
        int redPart = (int) ((color1.getRed() * percent) + (color2.getRed() * inverse_percent));
        int greenPart = (int) ((color1.getGreen() * percent) + (color2.getGreen() * inverse_percent));
        int bluePart = (int) ((color1.getBlue() * percent) + (color2.getBlue() * inverse_percent));
        return new Color(redPart, greenPart, bluePart);
    }

    public static Color colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed) {
        return colorSwitch(firstColor, secondColor, time, index, timePerIndex, speed, 255.0d);
    }

    public static Color getRainbow() {
        return new Color(Color.HSBtoRGB(((float) ((Mine.getMinecraft().thePlayer.ticksExisted / 50.0d) + Math.sin(0.032d))) % 1.0f, 0.5f, 1.0f));
    }

    public static int astolfoRainbow(int offset, float saturation, float brightness) {
        double currentColor = (Math.ceil(System.currentTimeMillis() + (offset * 20)) / 6.0d) % 360.0d;
        return Color.getHSBColor(((double) ((float) (currentColor / 360.0d))) < 0.5d ? -((float) (currentColor / 360.0d)) : (float) (currentColor / 360.0d), saturation, brightness).getRGB();
    }

    public static Color colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed, double alpha) {
        long now = (long) ((speed * System.currentTimeMillis()) + (index * timePerIndex));
        float redDiff = (firstColor.getRed() - secondColor.getRed()) / time;
        float greenDiff = (firstColor.getGreen() - secondColor.getGreen()) / time;
        float blueDiff = (firstColor.getBlue() - secondColor.getBlue()) / time;
        int red = Math.round(secondColor.getRed() + (redDiff * (now % ((long) time))));
        int green = Math.round(secondColor.getGreen() + (greenDiff * (now % ((long) time))));
        int blue = Math.round(secondColor.getBlue() + (blueDiff * (now % ((long) time))));
        float redInverseDiff = (secondColor.getRed() - firstColor.getRed()) / time;
        float greenInverseDiff = (secondColor.getGreen() - firstColor.getGreen()) / time;
        float blueInverseDiff = (secondColor.getBlue() - firstColor.getBlue()) / time;
        int inverseRed = Math.round(firstColor.getRed() + (redInverseDiff * (now % ((long) time))));
        int inverseGreen = Math.round(firstColor.getGreen() + (greenInverseDiff * (now % ((long) time))));
        int inverseBlue = Math.round(firstColor.getBlue() + (blueInverseDiff * (now % ((long) time))));
        return now % (((long) time) * 2) < ((long) time) ? new Color(inverseRed, inverseGreen, inverseBlue, (int) alpha) : new Color(red, green, blue, (int) alpha);
    }

    public static int interpolateColor2(Color color1, Color color2, float fraction) {
        int red = (int) (color1.getRed() + ((color2.getRed() - color1.getRed()) * fraction));
        int green = (int) (color1.getGreen() + ((color2.getGreen() - color1.getGreen()) * fraction));
        int blue = (int) (color1.getBlue() + ((color2.getBlue() - color1.getBlue()) * fraction));
        int alpha = (int) (color1.getAlpha() + ((color2.getAlpha() - color1.getAlpha()) * fraction));
        try {
            return new Color(red, green, blue, alpha).getRGB();
        } catch (Exception e) {
            return -1;
        }
    }

    public static int swapAlpha(int color, float alpha) {
        int f = (color >> 16) & 255;
        int f1 = (color >> 8) & 255;
        int f2 = color & 255;
        return getColor(f, f1, f2, (int) alpha);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0 | (alpha << 24);
        return color | (red << 16) | (green << 8) | blue;
    }

    public static Color[] getAnalogousColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[]) null);
        float newHueAdded = hsb[0] + 0.083333336f;
        float newHueSubtracted = hsb[0] - 0.083333336f;
        Color[] colors = {new Color(Color.HSBtoRGB(newHueAdded, hsb[1], hsb[2])), new Color(Color.HSBtoRGB(newHueSubtracted, hsb[1], hsb[2]))};
        return colors;
    }

    public static Color getRandomColor() {
        return new Color(Color.HSBtoRGB((float) Math.random(), (float) (0.5d + (Math.random() / 2.0d)), (float) (0.5d + (Math.random() / 2.0d))));
    }

    public static Color hslToRGB(float[] hsl) {
        float red;
        float green;
        float blue;
        if (hsl[1] == 0.0f) {
            blue = 1.0f;
            green = 1.0f;
            red = 1.0f;
        } else {
            float q = ((double) hsl[2]) < 0.5d ? hsl[2] * (1.0f + hsl[1]) : (hsl[2] + hsl[1]) - (hsl[2] * hsl[1]);
            float p = (2.0f * hsl[2]) - q;
            red = hueToRGB(p, q, hsl[0] + 0.33333334f);
            green = hueToRGB(p, q, hsl[0]);
            blue = hueToRGB(p, q, hsl[0] - 0.33333334f);
        }
        return new Color((int) (red * 255.0f), (int) (green * 255.0f), (int) (blue * 255.0f));
    }

    public static float hueToRGB(float p, float q, float t) {
        float newT = t;
        if (newT < 0.0f) {
            newT += 1.0f;
        }
        if (newT > 1.0f) {
            newT -= 1.0f;
        }
        return newT < 0.16666667f ? p + ((q - p) * 6.0f * newT) : newT < 0.5f ? q : newT < 0.6666667f ? p + ((q - p) * (0.6666667f - newT) * 6.0f) : p;
    }

    public static float[] rgbToHSL(Color rgb) {
        float red = rgb.getRed() / 255.0f;
        float green = rgb.getGreen() / 255.0f;
        float blue = rgb.getBlue() / 255.0f;
        float max = Math.max(Math.max(red, green), blue);
        float min = Math.min(Math.min(red, green), blue);
        float c = (max + min) / 2.0f;
        float[] hsl = {c, c, c};
        if (max == min) {
            hsl[1] = 0.0f;
            hsl[0] = 0.0f;
        } else {
            float d = max - min;
            hsl[1] = ((double) hsl[2]) > 0.5d ? d / ((2.0f - max) - min) : d / (max + min);
            if (max == red) {
                hsl[0] = ((green - blue) / d) + (green < blue ? 6 : 0);
            } else if (max == blue) {
                hsl[0] = ((blue - red) / d) + 2.0f;
            } else if (max == green) {
                hsl[0] = ((red - green) / d) + 4.0f;
            }
            hsl[0] = hsl[0] / 6.0f;
        }
        return hsl;
    }

    public static Color imitateTransparency(Color backgroundColor, Color accentColor, float percentage) {
        return new Color(interpolateColor(backgroundColor, accentColor, (255.0f * percentage) / 255.0f));
    }

    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    public static Color applyOpacity3(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity);
    }

    public static Color applyOpacity(Color color, float opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * Math.min(1.0f, Math.max(0.0f, opacity))));
    }

    public static Color applyOpacity2(Color color, float opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * Math.min(1.0f, Math.max(0.0f, opacity))));
    }

    public static Color darker(Color color, float FACTOR) {
        return new Color(Math.max((int) (color.getRed() * FACTOR), 0), Math.max((int) (color.getGreen() * FACTOR), 0), Math.max((int) (color.getBlue() * FACTOR), 0), color.getAlpha());
    }

    public static int darker(int color, float factor) {
        int r = (int) (((color >> 16) & 255) * factor);
        int g = (int) (((color >> 8) & 255) * factor);
        int b = (int) ((color & 255) * factor);
        int a = (color >> 24) & 255;
        return ((r & 255) << 16) | ((g & 255) << 8) | (b & 255) | ((a & 255) << 24);
    }

    public static Color brighter(Color color, float FACTOR) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();
        int i = (int) (1.0d / (1.0d - FACTOR));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) {
            r = i;
        }
        if (g > 0 && g < i) {
            g = i;
        }
        if (b > 0 && b < i) {
            b = i;
        }
        return new Color(Math.min((int) (r / FACTOR), 255), Math.min((int) (g / FACTOR), 255), Math.min((int) (b / FACTOR), 255), alpha);
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) (((System.currentTimeMillis() / speed) + index) % 360);
        float hue = angle / 360.0f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) (opacity * 255.0f))));
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int) (((System.currentTimeMillis() / speed) + index) % 360);
        int angle2 = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? interpolateColorHue(start, end, angle2 / 360.0f) : interpolateColorC(start, end, angle2 / 360.0f);
    }

    public static int interpolateColor(Color color1, Color color2, float amount) {
        return interpolateColorC(color1, color2, Math.min(1.0f, Math.max(0.0f, amount))).getRGB();
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        float amount2 = Math.min(1.0f, Math.max(0.0f, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount2).getRGB();
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        float amount2 = Math.min(1.0f, Math.max(0.0f, amount));
        return new Color(MathUtils.interpolateInt(color1.getRed(), color2.getRed(), amount2), MathUtils.interpolateInt(color1.getGreen(), color2.getGreen(), amount2), MathUtils.interpolateInt(color1.getBlue(), color2.getBlue(), amount2), MathUtils.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount2));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        float amount2 = Math.min(1.0f, Math.max(0.0f, amount));
        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), (float[]) null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), (float[]) null);
        Color resultColor = Color.getHSBColor(MathUtils.interpolateFloat(color1HSB[0], color2HSB[0], amount2), MathUtils.interpolateFloat(color1HSB[1], color2HSB[1], amount2), MathUtils.interpolateFloat(color1HSB[2], color2HSB[2], amount2));
        return applyOpacity(resultColor, MathUtils.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount2) / 255.0f);
    }

    public static Color fade(int speed, int index, Color color, float alpha) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[]) null);
        int angle = (int) (((System.currentTimeMillis() / speed) + index) % 360);
        Color colorHSB = new Color(Color.HSBtoRGB(hsb[0], hsb[1], ((angle > 180 ? 360 - angle : angle) + 180) / 360.0f));
        return new Color(colorHSB.getRed(), colorHSB.getGreen(), colorHSB.getBlue(), Math.max(0, Math.min(255, (int) (alpha * 255.0f))));
    }

    private static float getAnimationEquation(int index, int speed) {
        int angle = (int) (((System.currentTimeMillis() / speed) + index) % 360);
        return ((angle > 180 ? 360 - angle : angle) + 180) / 360.0f;
    }

    public static int[] createColorArray(int color) {
        return new int[]{bitChangeColor(color, 16), bitChangeColor(color, 8), bitChangeColor(color, 0), bitChangeColor(color, 24)};
    }

    public static int getOppositeColor(int color) {
        int R = bitChangeColor(color, 0);
        int G = bitChangeColor(color, 8);
        int B = bitChangeColor(color, 16);
        int A = bitChangeColor(color, 24);
        return (255 - R) + ((255 - G) << 8) + ((255 - B) << 16) + (A << 24);
    }

    public static Color getOppositeColor(Color color) {
        return new Color(getOppositeColor(color.getRGB()));
    }

    private static int bitChangeColor(int color, int bitChange) {
        return (color >> bitChange) & 255;
    }
}
