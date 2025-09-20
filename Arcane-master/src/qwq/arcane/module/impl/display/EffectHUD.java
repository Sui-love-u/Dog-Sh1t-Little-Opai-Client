package qwq.arcane.module.impl.display;

import java.awt.Color;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import qwq.arcane.event.impl.events.render.Shader2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.ModuleWidget;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.ContinualAnimation;
import qwq.arcane.utils.animations.impl.EaseBackIn;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/display/EffectHUD.class */
public class EffectHUD extends ModuleWidget {
    public ModeValue modeValue;
    private final Map<Integer, Integer> potionMaxDurations;
    private final ContinualAnimation widthanimation;
    private final ContinualAnimation heightanimation;
    private final EaseBackIn animation;
    List<PotionEffect> effects;
    private int maxString;

    public EffectHUD() {
        super("EffectHUD", Category.Display);
        this.modeValue = new ModeValue("Mode", "Normal", new String[]{"Normal", "Custom", "Solitude"});
        this.potionMaxDurations = new HashMap();
        this.widthanimation = new ContinualAnimation();
        this.heightanimation = new ContinualAnimation();
        this.animation = new EaseBackIn(200, 1.0d, 1.3f);
        this.effects = new java.util.ArrayList();
        this.maxString = 0;
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void onShader(Shader2DEvent event) {
        int x;
        int y;
        int i2;
        x = (int) this.renderX;
        y = (int) this.renderY;
        this.effects = (List) mc.thePlayer.getActivePotionEffects().stream().sorted(Comparator.comparingInt(it -> {
            return FontManager.Regular.get(16.0f).getStringWidth(get(it));
        })).collect(Collectors.toList());
        i2 = 16;
        java.util.ArrayList<Integer> needRemove = new java.util.ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : this.potionMaxDurations.entrySet()) {
            if (mc.thePlayer.getActivePotionEffect(Potion.potionTypes[entry.getKey().intValue()]) == null) {
                needRemove.add(entry.getKey());
            }
        }
        Iterator<Integer> it2 = needRemove.iterator();
        while (it2.hasNext()) {
            int id = it2.next().intValue();
            this.potionMaxDurations.remove(Integer.valueOf(id));
        }
        for (PotionEffect effect : this.effects) {
            if (!this.potionMaxDurations.containsKey(Integer.valueOf(effect.getPotionID())) || this.potionMaxDurations.get(Integer.valueOf(effect.getPotionID())).intValue() < effect.getDuration()) {
                this.potionMaxDurations.put(Integer.valueOf(effect.getPotionID()), Integer.valueOf(effect.getDuration()));
            }
        }
        float width = !this.effects.isEmpty() ? Math.max(50 + FontManager.Regular.get(16.0f).getStringWidth(get(this.effects.get(this.effects.size() - 1))), 60 + FontManager.Regular.get(16.0f).getStringWidth(get(this.effects.get(this.effects.size() - 1)))) : 0.0f;
        float height = this.effects.size() * 25;
        this.widthanimation.animate(width, 20);
        this.heightanimation.animate(height, 20);
        if ((mc.currentScreen instanceof GuiChat) && this.effects.isEmpty()) {
            this.animation.setDirection(Direction.FORWARDS);
        } else if (!(mc.currentScreen instanceof GuiChat)) {
            this.animation.setDirection(Direction.BACKWARDS);
        }
        switch (this.modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x, y, 120.0f, 15 + (20 * this.effects.size()), new Color(255, 255, 255, 100).getRGB());
                break;
            case "Custom":
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableLighting();
                for (PotionEffect potioneffect : this.effects) {
                    RoundedUtil.drawRound(x, (y + i2) - 18, 100.0f, 28.0f, 6.0f, ColorUtil.applyOpacity3(Potion.potionTypes[potioneffect.getPotionID()].getLiquidColor(), 1.0f));
                    RoundedUtil.drawRound(x, (y + i2) - 18, (potioneffect.getDuration() / (1.0f * this.potionMaxDurations.get(Integer.valueOf(potioneffect.getPotionID())).intValue())) * 100.0f, 28.0f, 6.0f, ColorUtil.applyOpacity3(Potion.potionTypes[potioneffect.getPotionID()].getLiquidColor(), 1.0f));
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    get(potioneffect);
                    i2 += 32;
                }
                setWidth(100.0f);
                setHeight(22 + i2);
                break;
            case "Normal":
                float f = x;
                float f2 = y;
                float size = this.effects.isEmpty() ? 37.0f : 28 + (this.effects.size() * 25);
                InterFace interFace = INTERFACE;
                RoundedUtil.drawRound(f, f2, 120.0f, size, InterFace.radius.get().floatValue(), new Color(0, 0, 0, 255));
                if (this.effects.isEmpty()) {

                }

                for (PotionEffect effect : this.effects) {
                    RoundedUtil.drawRound(x + 6, y + 16 + 9 + 19 + (this.effects.indexOf(effect) * 25), 106.0f, 3.0f, 1.5f, new Color(1, 1, 1, 255));
                }
                break;
        }
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void render() {
        int x;
        int y;
        int i2;
        x = (int) this.renderX;
        y = (int) this.renderY;
        this.effects = (List) mc.thePlayer.getActivePotionEffects().stream().sorted(Comparator.comparingInt(it -> {
            return FontManager.Regular.get(16.0f).getStringWidth(get(it));
        })).collect(Collectors.toList());
        i2 = 16;
        java.util.ArrayList<Integer> needRemove = new java.util.ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : this.potionMaxDurations.entrySet()) {
            if (mc.thePlayer.getActivePotionEffect(Potion.potionTypes[entry.getKey().intValue()]) == null) {
                needRemove.add(entry.getKey());
            }
        }
        Iterator<Integer> it2 = needRemove.iterator();
        while (it2.hasNext()) {
            int id = it2.next().intValue();
            this.potionMaxDurations.remove(Integer.valueOf(id));
        }
        for (PotionEffect effect : this.effects) {
            if (!this.potionMaxDurations.containsKey(Integer.valueOf(effect.getPotionID())) || this.potionMaxDurations.get(Integer.valueOf(effect.getPotionID())).intValue() < effect.getDuration()) {
                this.potionMaxDurations.put(Integer.valueOf(effect.getPotionID()), Integer.valueOf(effect.getDuration()));
            }
        }
        float width = !this.effects.isEmpty() ? Math.max(50 + FontManager.Regular.get(16.0f).getStringWidth(get(this.effects.get(this.effects.size() - 1))), 60 + FontManager.Regular.get(16.0f).getStringWidth(get(this.effects.get(this.effects.size() - 1)))) : 0.0f;
        float height = this.effects.size() * 25;
        this.widthanimation.animate(width, 20);
        this.heightanimation.animate(height, 20);
        if ((mc.currentScreen instanceof GuiChat) && this.effects.isEmpty()) {
            this.animation.setDirection(Direction.FORWARDS);
        } else if (!(mc.currentScreen instanceof GuiChat)) {
            this.animation.setDirection(Direction.BACKWARDS);
        }
        switch (this.modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x, y, 120.0f, 15 + (20 * this.effects.size()), new Color(255, 255, 255, 100).getRGB());
                RenderUtil.drawRect(x, y, 120.0f, 15.0f, new Color(255, 255, 255, 100).getRGB());
                for (PotionEffect potioneffect : this.effects) {
                    RenderUtil.drawRect(x, (y + i2) - 1, (potioneffect.getDuration() / (1.0f * this.potionMaxDurations.get(Integer.valueOf(potioneffect.getPotionID())).intValue())) * 120.0f, 20.0f, new Color(255, 255, 255, 50));
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    String s1 = get(potioneffect);
                    if (potion.hasStatusIcon()) {
                        GL11.glPushMatrix();
                        boolean is2949 = GL11.glIsEnabled(2929);
                        boolean is3042 = GL11.glIsEnabled(3042);
                        if (is2949) {
                            GL11.glDisable(2929);
                        }
                        if (!is3042) {
                            GL11.glEnable(3042);
                        }
                        GL11.glDepthMask(false);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        int statusIconIndex = potion.getStatusIconIndex();
                        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        Gui.drawTexturedModalRect3(x + 5, y + i2, (statusIconIndex % 8) * 18, 198 + ((statusIconIndex / 8) * 18), 18, 18);
                        GL11.glDepthMask(true);
                        if (!is3042) {
                            GL11.glDisable(3042);
                        }
                        if (is2949) {
                            GL11.glEnable(2929);
                        }
                        GL11.glPopMatrix();
                    }
                    FontManager.Bold.get(18.0f).drawString(s1, x + 21 + 8, ((y + i2) - 14) + 18, -1);
                    i2 += 20;
                }
                Bold.get(18.0f).drawCenteredString("Effects", x + 60, y + 6, -1);
                setWidth(100.0f);
                setHeight(22 + i2);
                break;
            case "Custom":
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableLighting();
                for (PotionEffect potioneffect2 : this.effects) {
                    RoundedUtil.drawRound(x, (y + i2) - 18, 100.0f, 28.0f, 6.0f, ColorUtil.applyOpacity3(Potion.potionTypes[potioneffect2.getPotionID()].getLiquidColor(), 0.4f));
                    RoundedUtil.drawRound(x, (y + i2) - 18, (potioneffect2.getDuration() / (1.0f * this.potionMaxDurations.get(Integer.valueOf(potioneffect2.getPotionID())).intValue())) * 100.0f, 28.0f, 6.0f, ColorUtil.applyOpacity3(Potion.potionTypes[potioneffect2.getPotionID()].getLiquidColor(), 0.4f));
                    Potion potion2 = Potion.potionTypes[potioneffect2.getPotionID()];
                    String s12 = get(potioneffect2);
                    if (potion2.hasStatusIcon()) {
                        GL11.glPushMatrix();
                        boolean is29492 = GL11.glIsEnabled(2929);
                        boolean is30422 = GL11.glIsEnabled(3042);
                        if (is29492) {
                            GL11.glDisable(2929);
                        }
                        if (!is30422) {
                            GL11.glEnable(3042);
                        }
                        GL11.glDepthMask(false);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        int statusIconIndex2 = potion2.getStatusIconIndex();
                        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        Gui.drawTexturedModalRect3(x + 5, (y + i2) - 14, (statusIconIndex2 % 8) * 18, 198 + ((statusIconIndex2 / 8) * 18), 18, 18);
                        GL11.glDepthMask(true);
                        if (!is30422) {
                            GL11.glDisable(3042);
                        }
                        if (is29492) {
                            GL11.glEnable(2929);
                        }
                        GL11.glPopMatrix();
                    }
                    Bold.get(18.0f).drawString(s12, x + 21 + 8, ((y + i2) - 14) + 3, -1);
                    // Bold.get(18.0f).drawString(potioneffect2.getDuration(), x + 21 + 8, ((y + i2) - 14) + 12, -1);
                    i2 += 32;
                }
                setWidth(100.0f);
                setHeight(22 + i2);
                break;
            case "Normal":
                float f = x;
                float f2 = y;
                float size = this.effects.isEmpty() ? 37.0f : 28 + (this.effects.size() * 25);
                InterFace interFace = INTERFACE;
                RoundedUtil.drawRound(f, f2, 120.0f, size, InterFace.radius.get().floatValue(), new Color(0, 0, 0, 100));
                RenderUtil.startGlScissor(x - 2, y - 1, 190, 20);
                InterFace interFace2 = INTERFACE;
                float fIntValue = InterFace.radius.get().intValue();
                RoundedUtil.drawRound(x, y, 120.0f, 30.0f, fIntValue, ColorUtil.applyOpacity(new Color(InterFace.color(1).getRGB()), 0.3f));
                RenderUtil.stopGlScissor();
                Bold.get(18.0f).drawString("Effects", x + 5, y + 6, -1);
                if (this.effects.isEmpty()) {
                    Bold.get(16.0f).drawCenteredString("IsEmpty", x + 60, y + 25, -1);
                }
                for (PotionEffect potioneffect3 : this.effects) {
                    Potion potion3 = Potion.potionTypes[potioneffect3.getPotionID()];
                    if (potion3.hasStatusIcon()) {
                        GL11.glPushMatrix();
                        boolean is29493 = GL11.glIsEnabled(2929);
                        boolean is30423 = GL11.glIsEnabled(3042);
                        if (is29493) {
                            GL11.glDisable(2929);
                        }
                        if (!is30423) {
                            GL11.glEnable(3042);
                        }
                        GL11.glDepthMask(false);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        int statusIconIndex3 = potion3.getStatusIconIndex();
                        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        Gui.drawTexturedModalRect3(x + 6, y + 16 + 8 + (this.effects.indexOf(potioneffect3) * 25), (statusIconIndex3 % 8) * 18, 198 + ((statusIconIndex3 / 8) * 18), 18, 18);
                        GL11.glDepthMask(true);
                        if (!is30423) {
                            GL11.glDisable(3042);
                        }
                        if (is29493) {
                            GL11.glEnable(2929);
                        }
                        GL11.glPopMatrix();
                    }
                    Bold.get(16.0f).drawString(get(potioneffect3), x + 30, y + 30 + (this.effects.indexOf(potioneffect3) * 25), -1);
                    RoundedUtil.drawRound(x + 6, y + 16 + 9 + 19 + (this.effects.indexOf(potioneffect3) * 25), 106.0f, 3.0f, 1.5f, new Color(1, 1, 1, 100));
                    RoundedUtil.drawRound(x + 6, y + 16 + 9 + 19 + (this.effects.indexOf(potioneffect3) * 25), (potioneffect3.getDuration() / (1.0f * this.potionMaxDurations.get(Integer.valueOf(potioneffect3.getPotionID())).intValue())) * 106.0f, 3.0f, 1.5f, new Color(Potion.potionTypes[potioneffect3.getPotionID()].getLiquidColor()).brighter());
                }
                setWidth(100.0f);
                setHeight(22 + 16);
                break;
        }
    }

    private String get(PotionEffect potioneffect) {
        Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
        String s1 = I18n.format(potion.getName(), new Object[0]);
        return s1 + " " + intToRomanByGreedy(potioneffect.getAmplifier() + 1);
    }

    private String intToRomanByGreedy(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length && num >= 0; i++) {
            while (values[i] <= num) {
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }
        }
        return stringBuilder.toString();
    }

    @Override // qwq.arcane.module.ModuleWidget
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
