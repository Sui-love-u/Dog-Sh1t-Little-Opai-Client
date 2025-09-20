package com.amaya.gui.widget.impl;

import com.amaya.Amaya;
import com.amaya.events.impl.render.Shader2DEvent;
import com.amaya.gui.widget.Widget;
import com.amaya.utils.render.RoundedUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.minecraft.client.gui.Gui.drawTexturedModalRect2;

/**
 * @Author: Guyuemang
 * 2025/5/10
 */
public class PotionHUD extends Widget {
    public PotionHUD() {
        super("PotionHUD");
    }
    private com.amaya.module.impl.display.PotionHUD value = Amaya.Instance.moduleManager.getModule(com.amaya.module.impl.display.PotionHUD.class);

    @Override
    public void onShader(Shader2DEvent event) {

    }

    @Override
    public void render() {
        int x = (int) renderX;
        int y = (int) renderY;
        for (final PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
            final Potion potion = Potion.potionTypes[effect.getPotionID()];
            final String number = intToRomanByGreedy(effect.getAmplifier());
            final String name = I18n.format(potion.getName()) + " " + number;
            final float stringWidth = mc.fontRendererObj.getStringWidth(name)
                    + mc.fontRendererObj.getStringWidth("§f" + Potion.getDurationString(effect));
            RoundedUtil.drawRound(x - 22,y - 9f,stringWidth + 18,22,value.radius.get().intValue(),new Color(1,1,1,100));
            mc.fontRendererObj.drawString(name, x + 2f, y - 7f, Color.white.getRGB(), true);
            mc.fontRendererObj.drawStringWithShadow("§f" + Potion.getDurationString(effect), x, y + 4f, -1);
            if (potion.hasStatusIcon()) {
                GL11.glPushMatrix();
                final boolean is2949 = GL11.glIsEnabled(2929);
                final boolean is3042 = GL11.glIsEnabled(3042);
                if (is2949)
                    GL11.glDisable(2929);
                if (!is3042)
                    GL11.glEnable(3042);
                GL11.glDepthMask(false);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                final int statusIconIndex = potion.getStatusIconIndex();
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                drawTexturedModalRect2(x - 20f, y - 7, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                GL11.glDepthMask(true);
                if (!is3042)
                    GL11.glDisable(3042);
                if (is2949)
                    GL11.glEnable(2929);
                GL11.glPopMatrix();
            }
            y += mc.fontRendererObj.FONT_HEIGHT + 15;
        }
        this.width = 140;
        this.height = 45;
    }

    private String intToRomanByGreedy(int num) {
        int[] values = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
        String[] symbols = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (i < values.length && num >= 0) {
            while (values[i] <= num) {
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }
            i++;
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean shouldRender() {
        return value.getState();
    }
}
