package cn.gym.module.impl.display;

import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.module.Category;
import cn.gym.module.ModuleWidget;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.ContinualAnimation;
import cn.gym.utils.animations.impl.EaseBackIn;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.utils.render.ShaderElement;
import cn.gym.value.impl.ModeValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 13:38
 */
public class EffectHUD extends ModuleWidget {
    public ModeValue modeValue = new ModeValue("Mode", "Normal",new String[]{"Normal","Custom","Solitude"});

    private final Map<Integer, Integer> potionMaxDurations = new HashMap<>();
    private final ContinualAnimation widthanimation = new ContinualAnimation();
    private final ContinualAnimation heightanimation = new ContinualAnimation();
    private final EaseBackIn animation = new EaseBackIn(200, 1F, 1.3F);
    List<PotionEffect> effects = new ArrayList<>();

    public EffectHUD() {
        super("EffectHUD",Category.Display);
    }

    @Override
    public void onShader(Shader2DEvent event) {
        int x = (int) renderX;
        int y = (int) renderY;
        effects = mc.thePlayer.getActivePotionEffects().stream()
                .sorted(Comparator.comparingInt((PotionEffect it) -> FontManager.Regular.get(16).getStringWidth(
                        get(it)
                )))
                .collect(Collectors.toList());
        int offsetX = 21;
        int offsetY = 14;

        int i2 = 16;
        final ArrayList<Integer> needRemove = new ArrayList<Integer>();
        for (final Map.Entry<Integer, Integer> entry : this.potionMaxDurations.entrySet()) {
            if (mc.thePlayer.getActivePotionEffect(Potion.potionTypes[entry.getKey()]) == null) {
                needRemove.add(entry.getKey());
            }
        }
        for (final int id : needRemove) {
            this.potionMaxDurations.remove(id);
        }
        for (final PotionEffect effect : effects) {
            if (!this.potionMaxDurations.containsKey(effect.getPotionID()) || this.potionMaxDurations.get(effect.getPotionID()) < effect.getDuration()) {
                this.potionMaxDurations.put(effect.getPotionID(), effect.getDuration());
            }
        }
        float width = !effects.isEmpty() ? Math.max(50 + FontManager.Regular.get(16).getStringWidth(get(effects.get(effects.size() - 1))), 60 + FontManager.Regular.get(16).getStringWidth(get(effects.get(effects.size() - 1)))) : 0;
        float height = effects.size() * 25;
        widthanimation.animate(width, 20);
        heightanimation.animate(height, 20);
        if (mc.currentScreen instanceof GuiChat && effects.isEmpty()) {
            animation.setDirection(Direction.FORWARDS);
        } else if (!(mc.currentScreen instanceof GuiChat)) {
            animation.setDirection(Direction.BACKWARDS);
        }
        switch (modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x, y, 120, 15 + 20 * effects.size(), new Color(255, 255, 255, 100).getRGB());
                break;
            case "Custom":
                int l = 36;
                for (PotionEffect potioneffect : effects) {
                    RenderUtil.drawRect(x + 15, y + i2 - 18, 120, 32, new Color(1, 1, 1, 255));
                    i2 += l;
                }
                setWidth(100);
                setHeight(22 + i2);
                break;
            case "Normal":
                RoundedUtil.drawRound(x, y, 120, effects.isEmpty() ? 37 : 28 + effects.size() * 25, Interface.radius.get().floatValue(), new Color(0, 0, 0, 255));
                if (effects.isEmpty()){
                }
                for (PotionEffect potioneffect : effects) {
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];

                    RoundedUtil.drawRound(x + 6, y + i2 + 9 + 19 + effects.indexOf(potioneffect) * 25, 106, 3, 1.5f, new Color(1, 1, 1, 255));
                }
                break;
        }
    }
    private int maxString = 0;

    @Override
    public void render() {
        int x = (int) renderX;
        int y = (int) renderY;
        effects = mc.thePlayer.getActivePotionEffects().stream()
                .sorted(Comparator.comparingInt((PotionEffect it) -> FontManager.Regular.get(16).getStringWidth(
                        get(it)
                )))
                .collect(Collectors.toList());
        int offsetX = 21;
        int offsetY = 14;

        int i2 = 16;
        final ArrayList<Integer> needRemove = new ArrayList<Integer>();
        for (final Map.Entry<Integer, Integer> entry : this.potionMaxDurations.entrySet()) {
            if (mc.thePlayer.getActivePotionEffect(Potion.potionTypes[entry.getKey()]) == null) {
                needRemove.add(entry.getKey());
            }
        }
        for (final int id : needRemove) {
            this.potionMaxDurations.remove(id);
        }
        for (final PotionEffect effect : effects) {
            if (!this.potionMaxDurations.containsKey(effect.getPotionID()) || this.potionMaxDurations.get(effect.getPotionID()) < effect.getDuration()) {
                this.potionMaxDurations.put(effect.getPotionID(), effect.getDuration());
            }
        }
        float width = !effects.isEmpty() ? Math.max(50 + FontManager.Regular.get(16).getStringWidth(get(effects.get(effects.size() - 1))), 60 + FontManager.Regular.get(16).getStringWidth(get(effects.get(effects.size() - 1)))) : 0;
        float height = effects.size() * 25;
        widthanimation.animate(width, 20);
        heightanimation.animate(height, 20);
        if (mc.currentScreen instanceof GuiChat && effects.isEmpty()) {
            animation.setDirection(Direction.FORWARDS);
        } else if (!(mc.currentScreen instanceof GuiChat)) {
            animation.setDirection(Direction.BACKWARDS);
        }
        switch (modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x, y, 120, 15 + 20 * effects.size(), new Color(255, 255, 255, 100).getRGB());
                RenderUtil.drawRect(x, y, 120, 15, new Color(255, 255, 255, 100).getRGB());
                int l1 = 20;
                for (PotionEffect potioneffect : effects) {
                    RenderUtil.drawRect(x, y + i2 - 1, (float) ((potioneffect.getDuration() / (1.0f * this.potionMaxDurations.get(potioneffect.getPotionID()))) * 120), 20, new Color(255, 255, 255, 50));
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    String s1 = get(potioneffect);
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
                        Gui.drawTexturedModalRect3(x + 5, y + i2, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                        GL11.glDepthMask(true);
                        if (!is3042)
                            GL11.glDisable(3042);
                        if (is2949)
                            GL11.glEnable(2929);
                        GL11.glPopMatrix();
                    }
                    FontManager.Semibold.get(18).drawString(s1, x + offsetX + 8, (y + i2) - offsetY + 18, -1);

                    i2 += l1;
                }
                Semibold.get(18).drawCenteredString("Effects", x + 60, y + 6, -1);
                break;
            case "Custom":
                int l = 36;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableLighting();
                for (PotionEffect potioneffect : effects) {
                    RenderUtil.drawRect(x + 15, y + i2 - 18, 120, 32, new Color(1, 1, 1, 60));
                    RenderUtil.drawRect(x + 15, (y + i2) - 18, (float) ((potioneffect.getDuration() / (1.0f * this.potionMaxDurations.get(potioneffect.getPotionID()))) * 120), 32, new Color(1, 1, 1, 50));
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    String s1 = get(potioneffect);
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
                        Gui.drawTexturedModalRect3(x + 20, y + i2 - 10, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                        GL11.glDepthMask(true);
                        if (!is3042)
                            GL11.glDisable(3042);
                        if (is2949)
                            GL11.glEnable(2929);
                        GL11.glPopMatrix();
                    }
                    FontManager.Mc.get(18).drawString(s1, x + offsetX + 22, (y + i2) - offsetY + 10, -1);

                    i2 += l;
                }
                setWidth(100);
                setHeight(22 + i2);
                break;
            case "Normal":
                RoundedUtil.drawRound(x, y, 120, effects.isEmpty() ? 37 : 28 + effects.size() * 25, Interface.radius.get().floatValue(), new Color(0, 0, 0, 100));
                RenderUtil.startGlScissor(x - 2, y - 1, 190, 20);
                RoundedUtil.drawRound(x, y, 120, 30, Interface.radius.get().intValue(), ColorUtil.applyOpacity(new Color(INTERFACE.color(1)), (float) 0.3f));
                RenderUtil.stopGlScissor();
                Semibold.get(18).drawString("Effects", x + 5, y + 6, -1);
                if (effects.isEmpty()){
                    Semibold.get(16).drawCenteredString("IsEmpty", x + 60, y + 25, -1);
                }
                for (PotionEffect potioneffect : effects) {
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
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
                        Gui.drawTexturedModalRect3(x + 6, y + i2 + 8 + effects.indexOf(potioneffect) * 25, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                        GL11.glDepthMask(true);
                        if (!is3042)
                            GL11.glDisable(3042);
                        if (is2949)
                            GL11.glEnable(2929);
                        GL11.glPopMatrix();
                    }
                    Semibold.get(16).drawString(get(potioneffect), x + 30, y + 30 + effects.indexOf(potioneffect) * 25, -1);
                    RoundedUtil.drawRound(x + 6, y + i2 + 9 + 19 + effects.indexOf(potioneffect) * 25, 106, 3, 1.5f, new Color(1, 1, 1, 100));
                    RoundedUtil.drawRound(x + 6, y + i2 + 9 + 19 + effects.indexOf(potioneffect) * 25, (float) ((potioneffect.getDuration() / (1.0f * this.potionMaxDurations.get(potioneffect.getPotionID()))) * 106), 3, 1.5f, new Color(Potion.potionTypes[potioneffect.getPotionID()].getLiquidColor()).brighter());
                }
                break;
        }
    }
    private String get(PotionEffect potioneffect) {
        Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
        String s1 = I18n.format(potion.getName(), new Object[0]);
        s1 = s1 + " " + intToRomanByGreedy(potioneffect.getAmplifier() + 1);
        return s1;
    }

    private String intToRomanByGreedy(int num) {
        final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        final String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length && num >= 0; i++)
            while (values[i] <= num) {
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }

        return stringBuilder.toString();
    }
    @Override
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
