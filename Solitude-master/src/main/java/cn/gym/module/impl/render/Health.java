package cn.gym.module.impl.render;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.player.UpdateEvent;
import cn.gym.events.impl.render.Render2DEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
public class Health extends Module {
    private final DecimalFormat decimalFormat;
    private final Random random;
    private int width;

    public Health() {
        super("Health", Category.Render);
        this.decimalFormat = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ENGLISH));
        this.random = new Random();
    }

    @EventTarget
    public void onRenderGuiEvent(final UpdateEvent event) {
        if (mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest || mc.currentScreen instanceof GuiContainerCreative) {
            this.renderHealth();
        }
    }

    @EventTarget
    public void onRender2DEvent(final Render2DEvent event) {
        if (!(mc.currentScreen instanceof GuiInventory) && !(mc.currentScreen instanceof GuiChest)) {
            this.renderHealth();
        }
    }

    private void renderHealth() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final GuiScreen screen = mc.currentScreen;
        final float absorptionHealth = mc.thePlayer.getAbsorptionAmount();
        final String string = this.decimalFormat.format(mc.thePlayer.getHealth() / 2.0f) + "\u00a7c\u2764 " + ((absorptionHealth <= 0.0f) ? "" : ("\u00a7e" + this.decimalFormat.format(absorptionHealth / 2.0f) + "\u00a76\u2764"));
        int offsetY = 0;
        if ((mc.thePlayer.getHealth() >= 0.0f && mc.thePlayer.getHealth() < 10.0f) || (mc.thePlayer.getHealth() >= 10.0f && mc.thePlayer.getHealth() < 100.0f)) {
            this.width = 3;
        }
        if (screen instanceof GuiInventory) {
            offsetY = 70;
        }
        else if (screen instanceof GuiContainerCreative) {
            offsetY = 80;
        }
        else if (screen instanceof GuiChest) {
            offsetY = ((GuiChest)screen).ySize / 2 - 15;
        }
        final int x = new ScaledResolution(mc).getScaledWidth() / 2 - this.width;
        final int y = new ScaledResolution(mc).getScaledHeight() / 2 + 25 + offsetY;
        final Color color = blendColors(new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { new Color(255, 37, 0), Color.YELLOW, Color.GREEN }, mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth());
        mc.fontRendererObj.drawString(string, (absorptionHealth > 0.0f) ? (x - 15.5f) : (x - 3.5f), (float)y, color.getRGB(), true);
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(Gui.icons);
        this.random.setSeed(mc.ingameGUI.getUpdateCounter() * 312871L);
        final float width = scaledResolution.getScaledWidth() / 2.0f - mc.thePlayer.getMaxHealth() / 2.5f * 10.0f / 2.0f;
        final float maxHealth = mc.thePlayer.getMaxHealth();
        final int lastPlayerHealth = mc.ingameGUI.lastPlayerHealth;
        final int healthInt = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
        int l2 = -1;
        final boolean flag = mc.ingameGUI.healthUpdateCounter > mc.ingameGUI.getUpdateCounter() && (mc.ingameGUI.healthUpdateCounter - mc.ingameGUI.getUpdateCounter()) / 3L % 2L == 1L;
        if (mc.thePlayer.isPotionActive(Potion.regeneration)) {
            l2 = mc.ingameGUI.getUpdateCounter() % MathHelper.ceiling_float_int(maxHealth + 5.0f);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int i6 = MathHelper.ceiling_float_int(maxHealth / 2.0f) - 1; i6 >= 0; --i6) {
            int xOffset = 16;
            if (mc.thePlayer.isPotionActive(Potion.poison)) {
                xOffset += 36;
            }
            else if (mc.thePlayer.isPotionActive(Potion.wither)) {
                xOffset += 72;
            }
            int k3 = 0;
            if (flag) {
                k3 = 1;
            }
            final float renX = width + i6 % 10 * 8;
            float renY = scaledResolution.getScaledHeight() / 2.0f + 15.0f + offsetY;
            if (healthInt <= 4) {
                renY += this.random.nextInt(2);
            }
            if (i6 == l2) {
                renY -= 2.0f;
            }
            int yOffset = 0;
            if (mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                yOffset = 5;
            }
            Gui.drawTexturedModalRect2(renX, renY, 16 + k3 * 9, 9 * yOffset, 9, 9);
            if (flag) {
                if (i6 * 2 + 1 < lastPlayerHealth) {
                    Gui.drawTexturedModalRect2(renX, renY, xOffset + 54, 9 * yOffset, 9, 9);
                }
                if (i6 * 2 + 1 == lastPlayerHealth) {
                    Gui.drawTexturedModalRect2(renX, renY, xOffset + 63, 9 * yOffset, 9, 9);
                }
            }
            if (i6 * 2 + 1 < healthInt) {
                Gui.drawTexturedModalRect2(renX, renY, xOffset + 36, 9 * yOffset, 9, 9);
            }
            if (i6 * 2 + 1 == healthInt) {
                Gui.drawTexturedModalRect2(renX, renY, xOffset + 45, 9 * yOffset, 9, 9);
            }
        }
        GL11.glPopMatrix();
    }
    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        } else if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        } else if (fractions.length == colors.length) {
            int[] indicies = getFractionIndicies(fractions, progress);
            float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
            Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            Color color = blend(colorRange[0], colorRange[1], (double)(1.0F - weight));
            return color;
        } else {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
    }
    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int[] range = new int[2];

        int startPoint;
        for(startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }

        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }

        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }
    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float)ratio;
        float ir = 1.0F - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0F) {
            red = 0.0F;
        } else if (red > 255.0F) {
            red = 255.0F;
        }

        if (green < 0.0F) {
            green = 0.0F;
        } else if (green > 255.0F) {
            green = 255.0F;
        }

        if (blue < 0.0F) {
            blue = 0.0F;
        } else if (blue > 255.0F) {
            blue = 255.0F;
        }

        Color color3 = null;

        try {
            color3 = new Color(red, green, blue);
        } catch (IllegalArgumentException var14) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format((double)red) + "; " + nf.format((double)green) + "; " + nf.format((double)blue));
            var14.printStackTrace();
        }

        return color3;
    }
}
