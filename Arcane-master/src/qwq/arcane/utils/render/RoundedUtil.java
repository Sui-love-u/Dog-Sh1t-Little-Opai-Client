package qwq.arcane.utils.render;

import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.optifine.CustomColormap;
import org.lwjgl.opengl.GL11;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.color.ColorUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/RoundedUtil.class */
public class RoundedUtil implements Instance {
    public static ShaderUtils roundedShader = new ShaderUtils("roundedRect");
    public static ShaderUtils roundedOutlineShader = new ShaderUtils("roundRectOutline");
    private static final ShaderUtils roundedTexturedShader = new ShaderUtils("roundRectTexture");
    private static final ShaderUtils roundedGradientShader = new ShaderUtils("roundedRectGradient");

    public static void scissor(double x, double y, double width, double height) {
        int scaleFactor = 1;
        while (scaleFactor < 2 && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            scaleFactor++;
        }
        GL11.glScissor((int) (x * scaleFactor), (int) (Mine.getMinecraft().displayHeight - ((y + height) * scaleFactor)), (int) (width * scaleFactor), (int) (height * scaleFactor));
    }

    public static Color reAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static void drawCircle(float x, float y, float start, float end, float radius, float width, boolean filled, int color) {
        if (start > end) {
            end = start;
            start = end;
        }
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(3);
        float f = end;
        while (true) {
            float i = f;
            if (i < start) {
                break;
            }
            setColor(color);
            float cos = MathHelper.cos((float) ((i * 3.141592653589793d) / 180.0d)) * radius;
            float sin = MathHelper.sin((float) ((i * 3.141592653589793d) / 180.0d)) * radius;
            GL11.glVertex2f(x + cos, y + sin);
            f = i - 1.0f;
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        if (filled) {
            GL11.glBegin(6);
            float f2 = end;
            while (true) {
                float i2 = f2;
                if (i2 < start) {
                    break;
                }
                setColor(color);
                float cos2 = MathHelper.cos((float) ((i2 * 3.141592653589793d) / 180.0d)) * radius;
                float sin2 = MathHelper.sin((float) ((i2 * 3.141592653589793d) / 180.0d)) * radius;
                GL11.glVertex2f(x + cos2, y + sin2);
                f2 = i2 - 1.0f;
            }
            GL11.glEnd();
        }
        GL11.glEnable(3553);
        GlStateManager.disableBlend();
        resetColor();
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void setColor(int color) {
        GL11.glColor4ub((byte) ((color >> 16) & 255), (byte) ((color >> 8) & 255), (byte) (color & 255), (byte) ((color >> 24) & 255));
    }

    public static void resetColor() {
        color(1.0d, 1.0d, 1.0d, 1.0d);
    }

    public static void drawRect(float left, float top, float width, float height, int color) {
        float right = left + width;
        float bottom = top + height;
        if (left < right) {
            left = right;
            right = left;
        }
        if (top < bottom) {
            top = bottom;
            bottom = top;
        }
        float f3 = ((color >> 24) & 255) / 255.0f;
        float f = ((color >> 16) & 255) / 255.0f;
        float f1 = ((color >> 8) & 255) / 255.0f;
        float f2 = (color & 255) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0d).endVertex();
        worldrenderer.pos(right, bottom, 0.0d).endVertex();
        worldrenderer.pos(right, top, 0.0d).endVertex();
        worldrenderer.pos(left, top, 0.0d).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRound(float x, float y, float width, float height, float radius, Color color) {
        drawRound(x, y, width, height, radius, false, color);
    }

    public static void drawGradientHorizontal(float x, float y, float width, float height, float radius, Color left, Color right) {
        drawGradientRound(x, y, width, height, radius, left, left, right, right);
    }

    public static void drawGradientVertical(float x, float y, float width, float height, float radius, Color top, Color bottom) {
        drawGradientRound(x, y, width, height, radius, bottom, top, bottom, top);
    }

    public static void drawGradientCornerLR(float x, float y, float width, float height, float radius, Color topLeft, Color bottomRight) {
        Color mixedColor = ColorUtil.interpolateColorC(topLeft, bottomRight, 0.5f);
        drawGradientRound(x, y, width, height, radius, mixedColor, topLeft, bottomRight, mixedColor);
    }

    public static void drawGradientCornerRL(float x, float y, float width, float height, float radius, Color bottomLeft, Color topRight) {
        Color mixedColor = ColorUtil.interpolateColorC(topRight, bottomLeft, 0.5f);
        drawGradientRound(x, y, width, height, radius, bottomLeft, mixedColor, mixedColor, topRight);
    }

    public static void drawGradientRound(float x, float y, float width, float height, float radius, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight) {
        RenderUtil.setAlphaLimit(0.0f);
        RenderUtil.resetColor();
        GLUtil.startBlend();
        roundedGradientShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedGradientShader);
        roundedGradientShader.setUniformf("color1", topLeft.getRed() / 255.0f, topLeft.getGreen() / 255.0f, topLeft.getBlue() / 255.0f, topLeft.getAlpha() / 255.0f);
        roundedGradientShader.setUniformf("color2", bottomLeft.getRed() / 255.0f, bottomLeft.getGreen() / 255.0f, bottomLeft.getBlue() / 255.0f, bottomLeft.getAlpha() / 255.0f);
        roundedGradientShader.setUniformf("color3", topRight.getRed() / 255.0f, topRight.getGreen() / 255.0f, topRight.getBlue() / 255.0f, topRight.getAlpha() / 255.0f);
        roundedGradientShader.setUniformf("color4", bottomRight.getRed() / 255.0f, bottomRight.getGreen() / 255.0f, bottomRight.getBlue() / 255.0f, bottomRight.getAlpha() / 255.0f);
        ShaderUtils.drawQuads(x - 1.0f, y - 1.0f, width + 2.0f, height + 2.0f);
        roundedGradientShader.unload();
        GLUtil.endBlend();
    }

    public static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color) {
        RenderUtil.resetColor();
        GLUtil.startBlend();
        GL11.glBlendFunc(770, 771);
        RenderUtil.setAlphaLimit(0.0f);
        roundedShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        ShaderUtils shaderUtils = roundedShader;
        int[] iArr = new int[1];
        iArr[0] = blur ? 1 : 0;
        shaderUtils.setUniformi("blur", iArr);
        roundedShader.setUniformf(CustomColormap.KEY_COLOR, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        ShaderUtils.drawQuads(x - 1.0f, y - 1.0f, width + 2.0f, height + 2.0f);
        roundedShader.unload();
        GLUtil.endBlend();
    }

    public static void drawRoundOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
        RenderUtil.resetColor();
        GLUtil.startBlend();
        GL11.glBlendFunc(770, 771);
        RenderUtil.setAlphaLimit(0.0f);
        roundedOutlineShader.init();
        ScaledResolution sr = new ScaledResolution(Mine.getMinecraft());
        setupRoundedRectUniforms(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", outlineThickness * sr.getScaleFactor());
        roundedOutlineShader.setUniformf(CustomColormap.KEY_COLOR, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        roundedOutlineShader.setUniformf("outlineColor", outlineColor.getRed() / 255.0f, outlineColor.getGreen() / 255.0f, outlineColor.getBlue() / 255.0f, outlineColor.getAlpha() / 255.0f);
        ShaderUtils.drawQuads(x - (2.0f + outlineThickness), y - (2.0f + outlineThickness), width + 4.0f + (outlineThickness * 2.0f), height + 4.0f + (outlineThickness * 2.0f));
        roundedOutlineShader.unload();
        GLUtil.endBlend();
    }

    public static void drawRoundTextured(float x, float y, float width, float height, float radius, float alpha) {
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0.0f);
        GLUtil.startBlend();
        roundedTexturedShader.init();
        roundedTexturedShader.setUniformi("textureIn", 0);
        setupRoundedRectUniforms(x, y, width, height, radius, roundedTexturedShader);
        roundedTexturedShader.setUniformf("alpha", alpha);
        ShaderUtils.drawQuads(x - 1.0f, y - 1.0f, width + 2.0f, height + 2.0f);
        roundedTexturedShader.unload();
        GLUtil.endBlend();
    }

    private static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderUtils roundedTexturedShader2) {
        ScaledResolution sr = new ScaledResolution(Mine.getMinecraft());
        roundedTexturedShader2.setUniformf("location", x * sr.getScaleFactor(), (Mine.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedTexturedShader2.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedTexturedShader2.setUniformf("radius", radius * sr.getScaleFactor());
    }
}
