package qwq.arcane.module.impl.display;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import qwq.arcane.Client;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.render.ParticleRenderer;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.ModeValue;

/* compiled from: TargetRender.java */
/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/display/TargetHUD.class */
class TargetHUD implements Instance {
    private float x;
    private float y;
    private float width;
    private float height;
    private EntityPlayer target;
    private Animation animation;
    private boolean shader;
    private ModeValue style;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0");
    private TargetRender value = (TargetRender) Client.Instance.getModuleManager().getModule(TargetRender.class);
    private InterFace setting = (InterFace) Client.Instance.getModuleManager().getModule(InterFace.class);

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setTarget(EntityPlayer target) {
        this.target = target;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void setShader(boolean shader) {
        this.shader = shader;
    }

    public void setStyle(ModeValue style) {
        this.style = style;
    }

    public void setValue(TargetRender value) {
        this.value = value;
    }

    public void setSetting(InterFace setting) {
        this.setting = setting;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public EntityPlayer getTarget() {
        return this.target;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public boolean isShader() {
        return this.shader;
    }

    public ModeValue getStyle() {
        return this.style;
    }

    public DecimalFormat getDecimalFormat() {
        return this.decimalFormat;
    }

    public TargetRender getValue() {
        return this.value;
    }

    public InterFace getSetting() {
        return this.setting;
    }

    public TargetHUD(float x, float y, EntityPlayer target, Animation animation, boolean shader, ModeValue style) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.animation = animation;
        this.shader = shader;
        this.style = style;
    }

    public void render() {
        setWidth(((TargetRender) INSTANCE.getModuleManager().getModule(TargetRender.class)).getTHUDWidth(this.target));
        setHeight(((TargetRender) INSTANCE.getModuleManager().getModule(TargetRender.class)).getTHUDHeight());
        GlStateManager.pushMatrix();
        if (!this.style.is("Exhi")) {
            GlStateManager.translate(this.x + (this.width / 2.0f), this.y + (this.height / 2.0f), 0.0f);
            GlStateManager.scale(this.animation.getOutput().doubleValue(), this.animation.getOutput().doubleValue(), this.animation.getOutput().doubleValue());
            GlStateManager.translate(-(this.x + (this.width / 2.0f)), -(this.y + (this.height / 2.0f)), 0.0f);
        }
        switch (this.style.get()) {
            case "Astolfo":
                if (!this.shader) {
                    RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 0.0f, ColorUtil.applyOpacity(new Color(0, 0, 0), (float) (0.4d * this.animation.getOutput().doubleValue())));
                    GlStateManager.pushMatrix();
                    GuiInventory.drawEntityOnScreen((int) (this.x + 22.0f), (int) (this.y + 51.0f), 24, mc.thePlayer.rotationYaw, -mc.thePlayer.rotationPitch, this.target);
                    mc.fontRendererObj.drawStringWithShadow(this.target.getName(), this.x + 50.0f, this.y + 6.0f, -1);
                    GlStateManager.scale(1.5d, 1.5d, 1.5d);
                    FontRenderer fontRenderer = mc.fontRendererObj;
                    String str = String.format("%.1f", Float.valueOf(this.target.getHealth())) + " ❤";
                    float f = (this.x + 50.0f) / 1.5f;
                    float f2 = (this.y + 22.0f) / 1.5f;
                    InterFace interFace = this.setting;
                    fontRenderer.drawStringWithShadow(str, f, f2, InterFace.colors(1));
                    GlStateManager.popMatrix();
                    float healthWidth = this.width - 54.0f;
                    this.target.healthAnimation.animate(healthWidth * MathHelper.clamp_float(this.target.getHealth() / this.target.getMaxHealth(), 0.0f, 1.0f), 30);
                    float f3 = this.x + 48.0f;
                    float f4 = this.y + 42.0f;
                    float f5 = this.width - 54.0f;
                    InterFace interFace2 = this.setting;
                    RoundedUtil.drawRound(f3, f4, f5, 7.0f, 0.0f, ColorUtil.applyOpacity(new Color(InterFace.colors(1)).darker().darker().darker(), (float) (1.0d * this.animation.getOutput().doubleValue())));
                    float f6 = this.x + 48.0f;
                    float f7 = this.y + 42.0f;
                    float output = this.target.healthAnimation.getOutput();
                    InterFace interFace3 = this.setting;
                    RoundedUtil.drawRound(f6, f7, output, 7.0f, 0.0f, ColorUtil.applyOpacity(new Color(InterFace.colors(1)), (float) (1.0d * this.animation.getOutput().doubleValue())));
                    break;
                } else {
                    RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 0.0f, ColorUtil.applyOpacity(new Color(0, 0, 0), (float) (1.0d * this.animation.getOutput().doubleValue())));
                    break;
                }
            case "Type 1":
                this.target.healthAnimation.animate((this.width - 52.0f) * MathHelper.clamp_float(this.target.getHealth() / this.target.getMaxHealth(), 0.0f, 1.0f), 30);
                float hurtTime = (this.target.hurtTime == 0 ? 0.0f : this.target.hurtTime - mc.timer.renderPartialTicks) * 0.5f;
                if (!this.shader) {
                    RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 6.0f, ColorUtil.applyOpacity(Color.BLACK, (float) (0.4000000059604645d * this.animation.getOutput().doubleValue())));
                    RenderUtil.renderPlayer2D(this.target, this.x + 4.0f + (hurtTime / 2.0f), this.y + 4.0f + (hurtTime / 2.0f), 34.0f - hurtTime, 8.0f, ColorUtil.interpolateColor2(Color.WHITE, Color.RED, hurtTime / 7.0f));
                    Bold.get(18.0f).drawString(this.target.getName(), this.x + 43.0f, this.y + 10.0f, ColorUtil.applyOpacity(Color.WHITE, this.animation.getOutput().intValue()).getRGB());
                    Bold.get(14.0f).drawString("HP: " + String.format("%.1f", Float.valueOf((this.target.healthAnimation.getOutput() / (this.width - 52.0f)) * this.target.getMaxHealth())), this.x + 43.0f, this.y + 20.0f, ColorUtil.applyOpacity(Color.WHITE, this.animation.getOutput().intValue()).getRGB());
                    RoundedUtil.drawRound(this.x + 44.0f, this.y + 30.0f, this.width - 52.0f, 6.0f, 3.0f, ColorUtil.applyOpacity(Color.BLACK, (float) (0.4699999988079071d * this.animation.getOutput().doubleValue())));
                    float f8 = this.x + 44.0f;
                    float f9 = this.y + 30.0f;
                    float output2 = this.target.healthAnimation.getOutput();
                    InterFace interFace4 = this.setting;
                    Color colorApplyOpacity = ColorUtil.applyOpacity(new Color(InterFace.colors(0)), this.animation.getOutput().intValue());
                    InterFace interFace5 = this.setting;
                    RoundedUtil.drawGradientHorizontal(f8, f9, output2, 6.0f, 3.0f, colorApplyOpacity, ColorUtil.applyOpacity(new Color(InterFace.colors(10)), this.animation.getOutput().intValue()));
                } else {
                    RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 6.0f, ColorUtil.applyOpacity(Color.BLACK, (float) (1.0d * this.animation.getOutput().doubleValue())));
                }
                if (this.value.targetHudParticle.get().booleanValue()) {
                    ParticleRenderer.renderParticle(this.target, this.x + 4.0f, this.y + 4.0f);
                    break;
                }
                break;
            case "Type 2":
                if (!this.shader) {
                    this.target.healthAnimation.animate((this.width - 46.5f) * MathHelper.clamp_float(this.target.getHealth() / this.target.getMaxHealth(), 0.0f, 1.0f), 30);
                    RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 4.0f, new Color(this.value.bgColor(), true));
                    RenderUtil.renderPlayer2D(this.target, this.x + 5.0f, this.y + 6.8f, 26.5f, 2.0f, -1);
                    RoundedUtil.drawRound(this.x + 10.0f + 26.5f, this.y + 6.8f, 0.5f, 26.5f, 2.0f, new Color(30, 30, 30));
                    Semibold.get(14.0f).drawString(this.target.getDisplayName().getFormattedText(), this.x + 15.0f + 26.5f, this.y + 6.8f + (Semibold.get(14.0f).getHeight() / 2.0f), -1);
                    Regular.get(12.0f).drawString(((int) MathUtils.roundToHalf(this.target.getHealth())) + "HP", this.x + 15.0f + 26.5f, this.y + 15.300000429153442d + (Semibold.get(14.0f).getHeight() / 2.0f), -1);
                    float f10 = this.x + 15.0f + 26.5f;
                    float f11 = this.y + 26.5f;
                    float output3 = this.target.healthAnimation.getOutput();
                    InterFace interFace6 = this.setting;
                    Color color = new Color(InterFace.colors(0));
                    InterFace interFace7 = this.setting;
                    RoundedUtil.drawGradientHorizontal(f10, f11, output3, 3.8f, 2.0f, color, new Color(InterFace.colors(90)));
                    RenderUtil.drawRect(this.x + 15.0f + 26.5f + Regular.get(12.0f).getStringWidth(((int) MathUtils.roundToHalf(this.target.getHealth())) + "HP") + 2.0f, this.y + 15.3f + 1.5f, 0.5f, Regular.get(12.0f).getHeight(), new Color(128, 128, 128).getRGB());
                    List<ItemStack> items = new java.util.ArrayList<>();
                    if (this.target.getHeldItem() != null) {
                        items.add(this.target.getHeldItem());
                    }
                    for (int index = 3; index >= 0; index--) {
                        ItemStack stack = this.target.inventory.armorInventory[index];
                        if (stack != null) {
                            items.add(stack);
                        }
                    }
                    float i = 0.0f;
                    Iterator<ItemStack> it = items.iterator();
                    while (it.hasNext()) {
                        RenderUtil.renderItemStack(it.next(), i + this.x + 15.0f + 26.5f + Regular.get(12.0f).getStringWidth(((int) MathUtils.roundToHalf(this.target.getHealth())) + "HP") + 2.0f + 1.0f, this.y + 15.3f + 1.5f, 0.5f);
                        i += 7.5f;
                    }
                } else {
                    float f12 = this.x;
                    float f13 = this.y;
                    float f14 = this.width;
                    float f15 = this.height;
                    InterFace interFace8 = this.setting;
                    Color color2 = new Color(InterFace.colors(0));
                    InterFace interFace9 = this.setting;
                    RoundedUtil.drawGradientHorizontal(f12, f13, f14, f15, 4.0f, color2, new Color(InterFace.colors(90)));
                }
                if (this.value.targetHudParticle.get().booleanValue()) {
                    ParticleRenderer.renderParticle(this.target, this.x + 5.0f, this.y + 6.8f);
                    break;
                }
                break;
            case "Exhi":
                float health = this.target.getHealth();
                float totalHealth = health + this.target.getAbsorptionAmount();
                float progress = health / this.target.getMaxHealth();
                if (health < 19.5d || health < 10.0f || health < 15.0f || health < 5.0f || health < 2.0f) {
                    progress = (float) (progress - 0.175d);
                }
                float healthLocation = 50.0f * progress;
                Color color3 = new Color(ColorUtil.getHealthColor(this.target));
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.x, this.y, 0.0d);
                RenderUtil.drawExhiRect(0.0f, -2.0f, this.width, this.height, 1.0f);
                RenderUtil.drawRect(42.5f, 9.5f, 61.5f, 3.5f, ColorUtil.darker(color3.getRGB(), 0.2f));
                RenderUtil.drawRect(42.5f, 9.5f, 11.0f + healthLocation, 3.5f, color3.getRGB());
                RenderUtil.drawBorderedRect(42.0f, 9.0f, 61.5f, 4.5f, 0.5f, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0).getRGB());
                for (int i2 = 1; i2 < 10; i2++) {
                    float separator = 5.882353f * i2;
                    RenderUtil.drawRect(43.5f + separator, 9.0f, 0.5f, 4.5f, new Color(0, 0, 0).getRGB());
                }
                Bold.get(17.0f).drawString(this.target.getName(), 42.0f, 1.0f, -1);
                Regular.get(10.0f, false).drawString("HP: " + ((int) totalHealth) + " | Dist: " + ((int) mc.thePlayer.getDistanceToEntity(this.target)), 42.5f, 15.5f, -1);
                List<ItemStack> items2 = new java.util.ArrayList<>();
                if (this.target.getHeldItem() != null) {
                    items2.add(this.target.getHeldItem());
                }
                for (int index2 = 3; index2 >= 0; index2--) {
                    ItemStack stack2 = this.target.inventory.armorInventory[index2];
                    if (stack2 != null) {
                        items2.add(stack2);
                    }
                }
                float i3 = 0.0f;
                Iterator<ItemStack> it2 = items2.iterator();
                while (it2.hasNext()) {
                    RenderUtil.renderItemStack(it2.next(), i3 + 28.0f + 16.0f, 19.0d, 1.0f, true, 0.5f);
                    i3 += 16.0f;
                }
                GlStateManager.scale(0.31d, 0.31d, 0.31d);
                GlStateManager.translate(73.0f, 102.0f, 40.0f);
                RenderUtil.drawEntityOnScreen(this.target.rotationYaw, this.target.rotationPitch, this.target);
                GlStateManager.popMatrix();
                break;
            case "Felix":
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.x, this.y, 0.0f);
                RenderUtil.drawRect(1.0f, 1.0f, 140.0f, 37.6f, new Color(25, 25, 25, 210));
                String string = String.format("%.1f", Float.valueOf(this.target.getHealth() / 2.0f));
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                mc.fontRendererObj.drawStringWithShadow(string.replace(".0", ""), 29.0f, 7.0f, ColorUtil.getHealthColor(this.target));
                mc.fontRendererObj.drawStringWithShadow("❤", 20.0f, 6.0f, ColorUtil.getHealthColor(this.target));
                GlStateManager.popMatrix();
                float healthPercentage = this.target.getHealth() / this.target.getMaxHealth();
                float healthWidth2 = (this.width - 2.0f) * healthPercentage;
                this.target.healthAnimation.animate(healthWidth2, 50);
                RenderUtil.drawRect(2.0f, 34.0f, 138.0f, 3.5f, ColorUtil.darker(ColorUtil.getHealthColor(this.target), 0.35f));
                RenderUtil.drawRect(2.0f, 34.0f, this.target.healthAnimation.getOutput(), 3.5f, ColorUtil.getHealthColor(this.target));
                String name = this.target.getName();
                mc.fontRendererObj.drawStringWithShadow(name, 35.0f, 3.0f, -855638017);
                RenderUtil.renderPlayer2D(this.target, 2.0f, 2.0f, 31.0f, 0.0f, -1);
                GlStateManager.popMatrix();
                break;
            case "Adjust":
                float healthX = this.x + 2.0f;
                float healthPercentage2 = this.target.getHealth() / this.target.getMaxHealth();
                float healthWidth3 = (this.width - (2.0f * 2.0f)) * healthPercentage2;
                this.target.healthAnimation.animate(healthWidth3, 25);
                String sheesh = this.decimalFormat.format(Math.abs(mc.thePlayer.getHealth() - this.target.getHealth()));
                String healthDiff = mc.thePlayer.getHealth() < this.target.getHealth() ? "-" + sheesh : "+" + sheesh;
                if (!this.shader) {
                    RenderUtil.drawRect(this.x, this.y, this.width, this.height, new Color(0, 0, 0, 150).getRGB());
                    float f16 = (this.y + this.height) - 5.0f;
                    float f17 = this.width - (2.0f * 2.0f);
                    InterFace interFace10 = this.setting;
                    RenderUtil.drawRect(healthX, f16, f17, 4.0f, ColorUtil.darker(InterFace.colors(0), 0.3f));
                    float f18 = (this.y + this.height) - 5.0f;
                    float output4 = this.target.healthAnimation.getOutput();
                    InterFace interFace11 = this.setting;
                    RenderUtil.drawRect(healthX, f18, output4, 4.0f, InterFace.colors(0));
                    RenderUtil.renderPlayer2D(this.target, this.x + 2.0f, this.y + 2.0f, 28.0f - 2.0f, 0.0f, -1);
                    Regular.get(15.0f).drawStringWithShadow(this.target.getName(), this.x + 2.0f + 30.0f, this.y + 3.0f + 2.0f, -1);
                    Regular.get(15.0f).drawStringWithShadow(healthDiff, ((this.x + this.width) - 2.0f) - Regular.get(15.0f).getStringWidth(healthDiff), ((this.y + this.height) - 10.0f) - 2.0f, -1);
                    List<ItemStack> items3 = new java.util.ArrayList<>();
                    if (this.target.getHeldItem() != null) {
                        items3.add(this.target.getHeldItem());
                    }
                    for (int index3 = 3; index3 >= 0; index3--) {
                        ItemStack stack3 = this.target.inventory.armorInventory[index3];
                        if (stack3 != null) {
                            items3.add(stack3);
                        }
                    }
                    float i4 = this.x + 30.0f + 2.0f;
                    Iterator<ItemStack> it3 = items3.iterator();
                    while (it3.hasNext()) {
                        RenderUtil.renderItemStack(it3.next(), i4, this.y + 10.0f + 2.0f, 1.0f, true, 0.5f);
                        i4 += 16.0f;
                    }
                }
                if (this.value.targetHudParticle.get().booleanValue()) {
                    ParticleRenderer.renderParticle(this.target, this.x + 2.0f, this.y + 2.0f);
                    break;
                }
                break;
            case "Moon":
                float healthPercentage3 = this.target.getHealth() / this.target.getMaxHealth();
                float space = (this.width - 48.0f) / 100.0f;
                this.target.healthAnimation.animate(100.0f * space * MathHelper.clamp_float(healthPercentage3, 0.0f, 1.0f), 30);
                if (!this.shader) {
                    RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 8.0f, new Color(this.value.bgColor(), true));
                    RoundedUtil.drawRound(this.x + 42.0f, this.y + 26.5f, 100.0f * space, 8.0f, 4.0f, new Color(0, 0, 0, 150));
                    String text = String.format("%.1f", Float.valueOf(this.target.getHealth()));
                    float f19 = this.x + 42.0f;
                    float f20 = this.y + 26.5f;
                    float output5 = this.target.healthAnimation.getOutput();
                    InterFace interFace12 = this.setting;
                    RoundedUtil.drawRound(f19, f20, output5, 8.5f, 4.0f, new Color(InterFace.colors(0)));
                    RenderUtil.renderPlayer2D(this.target, this.x + 2.5f, this.y + 2.5f, 35.0f, 10.0f, -1);
                    Semibold.get(13.0f).drawStringWithShadow(text + "HP", this.x + 40.0f, this.y + 17.0f, -1);
                    Semibold.get(18.0f).drawStringWithShadow(this.target.getName(), this.x + 40.0f, this.y + 6.0f, -1);
                } else {
                    float f21 = this.x;
                    float f22 = this.y;
                    float f23 = this.width;
                    float f24 = this.height;
                    InterFace interFace13 = this.setting;
                    RoundedUtil.drawRound(f21, f22, f23, f24, 8.0f, new Color(InterFace.colors(1)));
                }
                if (this.value.targetHudParticle.get().booleanValue()) {
                    ParticleRenderer.renderParticle(this.target, this.x + 2.5f, this.y + 2.5f);
                    break;
                }
                break;
            case "Augustus":
                this.target.healthAnimation.animate((this.width - 52.0f) * MathHelper.clamp_float(this.target.getHealth() / this.target.getMaxHealth(), 0.0f, 1.0f), 30);
                float hurtTime2 = (this.target.hurtTime == 0 ? 0.0f : this.target.hurtTime - mc.timer.renderPartialTicks) * 0.5f;
                float healthPercentage4 = this.target.getHealth() / this.target.getMaxHealth();
                float space2 = (this.width - 51.0f) / 100.0f;
                this.target.healthAnimation.animate(100.0f * space2 * MathHelper.clamp_float(healthPercentage4, 0.0f, 1.0f), 30);
                if (!this.shader) {
                    RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 8.0f, new Color(0, 0, 0, 100));
                    RoundedUtil.drawRound(this.x + 45.0f, this.y + 23.0f, 100.0f * space2, 10.0f, 5.0f, new Color(0, 0, 0, 255));
                    RoundedUtil.drawRound(this.x + 45.0f, this.y + 23.0f, this.target.healthAnimation.getOutput(), 10.0f, 4.0f, new Color(255, 0, 0));
                    RenderUtil.renderPlayer2D(this.target, this.x + 2.5f + (hurtTime2 / 2.0f), this.y + 2.5f + (hurtTime2 / 2.0f), 35.0f - hurtTime2, 15.0f, ColorUtil.interpolateColor2(Color.WHITE, Color.RED, hurtTime2 / 7.0f));
                    Semibold.get(18.0f).drawString(this.target.getName(), this.x + 52.5f, this.y + 10.5f, -1);
                } else {
                    float f25 = this.x;
                    float f26 = this.y;
                    float f27 = this.width;
                    float f28 = this.height;
                    InterFace interFace14 = this.setting;
                    RoundedUtil.drawRound(f25, f26, f27, f28, 8.0f, new Color(InterFace.colors(1)));
                }
                if (this.value.targetHudParticle.get().booleanValue()) {
                    ParticleRenderer.renderParticle(this.target, this.x + 2.5f, this.y + 2.5f);
                    break;
                }
                break;
            case "New":
                float healthPercentage5 = this.target.getHealth() / this.target.getMaxHealth();
                float space3 = (this.width - 48.0f) / 100.0f;
                this.target.healthAnimation.animate(100.0f * space3 * MathHelper.clamp_float(healthPercentage5, 0.0f, 1.0f), 30);
                if (!this.shader) {
                    RoundedUtil.drawRound(this.x, this.y, this.width, this.height, 8.0f, new Color(this.value.bgColor(), true));
                    RoundedUtil.drawRound(this.x + 42.0f, this.y + 22.0f, 100.0f * space3, 6.0f, 3.0f, new Color(0, 0, 0, 120));
                    String text2 = String.format("%.1f", Float.valueOf(this.target.getHealth()));
                    float f29 = this.x + 42.0f;
                    float f30 = this.y + 22.0f;
                    float output6 = this.target.healthAnimation.getOutput();
                    InterFace interFace15 = this.setting;
                    RoundedUtil.drawRound(f29, f30, output6, 6.0f, 3.0f, new Color(InterFace.colors(0)));
                    float f31 = this.x;
                    float f32 = this.y;
                    float f33 = this.width;
                    float f34 = this.height;
                    Color color4 = new Color(0, 0, 0, 0);
                    InterFace interFace16 = this.setting;
                    RoundedUtil.drawRoundOutline(f31, f32, f33, f34, 5.0f, 0.1f, color4, new Color(InterFace.colors(0)));
                    RenderUtil.renderPlayer2D(this.target, this.x + 4.0f, this.y + 3.3f, 33.0f, 12.0f, -1);
                    float f35 = this.x + 134.0f;
                    float f36 = this.y + 9.0f;
                    InterFace interFace17 = this.setting;
                    Semibold.get(19.0f).drawString(text2 + "  ", f35, f36, InterFace.colors(1));
                    qwq.arcane.utils.fontrender.FontRenderer fontRenderer2 = Semibold.get(17.0f);
                    String name2 = this.target.getName();
                    float f37 = this.x + 42.0f;
                    float f38 = this.y + 9.0f;
                    InterFace interFace18 = this.setting;
                    fontRenderer2.drawString(name2, f37, f38, InterFace.colors(1));
                } else {
                    float f39 = this.x;
                    float f40 = this.y;
                    float f41 = this.width;
                    float f42 = this.height;
                    InterFace interFace19 = this.setting;
                    RoundedUtil.drawRound(f39, f40, f41, f42, 3.0f, new Color(InterFace.colors(1)));
                    float f43 = this.x;
                    float f44 = this.y;
                    float f45 = this.width;
                    float f46 = this.height;
                    Color color5 = new Color(0, 0, 0, 0);
                    InterFace interFace20 = this.setting;
                    RoundedUtil.drawRoundOutline(f43, f44, f45, f46, 5.0f, 0.1f, color5, new Color(InterFace.colors(0)));
                }
                if (this.value.targetHudParticle.get().booleanValue()) {
                    ParticleRenderer.renderParticle(this.target, this.x + 2.5f, this.y + 2.5f);
                    break;
                }
                break;
            case "Novo 1":
                float healthPercentage6 = this.target.getHealth() / this.target.getMaxHealth();
                float space4 = (this.width - 50.0f) / 100.0f;
                this.target.healthAnimation.animate(100.0f * space4 * MathHelper.clamp_float(healthPercentage6, 0.0f, 1.0f), 30);
                if (!this.shader) {
                    RenderUtil.drawEntityOnScreen(this.x + 18.0f, this.y + 32.0f, 15.0f, this.target);
                    RenderUtil.drawBorderedRect(this.x, this.y, this.width, this.height, 1.0f, new Color(0, 0, 0, 50).getRGB(), new Color(40, 40, 40, 178).getRGB());
                    RenderUtil.drawRect(this.x, this.y, this.width, this.height, new Color(40, 40, 40, 178).getRGB());
                    RenderUtil.drawRect(this.x + 40.0f, this.y + 14.0f, 100.0f * space4, 10.0f, new Color(30, 30, 30, 203).getRGB());
                    RenderUtil.drawRect(this.x + 40.0f, this.y + 15.5f, this.target.healthAnimation.getOutput(), 8.5f, new Color(0, 255, 0, 255).getRGB());
                    String text3 = String.format("%.1f", Float.valueOf(this.target.getHealth() / 2.0f));
                    mc.fontRendererObj.drawStringWithShadow(text3, this.x + 40.0f, this.y + 27.0f, -1);
                    mc.fontRendererObj.drawStringWithShadow(this.target.getName(), this.x + 40.0f, this.y + 4.0f, -1);
                    mc.fontRendererObj.drawStringWithShadow("❤", this.x + 40.0f + 23.0f, this.y + 27.0f, new Color(255, 100, 100).getRGB());
                    break;
                }
                break;
            case "Novo 2":
                float healthPercentage7 = this.target.getHealth() / this.target.getMaxHealth();
                float space5 = (this.width - 50.0f) / 100.0f;
                this.target.healthAnimation.animate(100.0f * space5 * MathHelper.clamp_float(healthPercentage7, 0.0f, 1.0f), 30);
                if (!this.shader) {
                    RenderUtil.drawBorderedRect(this.x, this.y, this.width, this.height, 1.0f, new Color(0, 0, 0, 50).getRGB(), new Color(29, 29, 29, 180).getRGB());
                    RenderUtil.drawRect(this.x, this.y, this.width, this.height, new Color(40, 40, 40, 180).getRGB());
                    RenderUtil.renderPlayer2D(this.target, this.x + 1.5f + 1.0f, (float) (this.y + 0.4d), 35.0f, 0.0f, -1);
                    String text4 = String.format("%.1f", Float.valueOf(this.target.getHealth()));
                    FontRenderer fontRenderer3 = mc.fontRendererObj;
                    float f47 = this.x + 62.0f + 1.0f;
                    float f48 = this.y + 26.6f;
                    InterFace interFace21 = this.setting;
                    fontRenderer3.drawStringWithShadow("❤", f47, f48, InterFace.colors(0));
                    float f49 = this.x + 40.0f + 1.0f;
                    float f50 = this.y + 16.5f;
                    float output7 = this.target.healthAnimation.getOutput();
                    InterFace interFace22 = this.setting;
                    RenderUtil.drawRect(f49, f50, output7, 8.8f, InterFace.colors(0));
                    mc.fontRendererObj.drawStringWithShadow(text4, this.x + 40.0f + 1.0f, this.y + 28.0f, -1);
                    mc.fontRendererObj.drawStringWithShadow(this.target.getName(), this.x + 40.0f, this.y + 4.0f, -1);
                }
                if (this.value.targetHudParticle.get().booleanValue()) {
                    ParticleRenderer.renderParticle(this.target, this.x + 1.5f + 1.0f, (float) (this.y + 0.4d));
                    break;
                }
                break;
            case "Novo 3":
                float healthPercentage8 = this.target.getHealth() / this.target.getMaxHealth();
                float space6 = (this.width - 50.0f) / 100.0f;
                this.target.healthAnimation.animate(100.0f * space6 * MathHelper.clamp_float(healthPercentage8, 0.0f, 1.0f), 30);
                if (!this.shader) {
                    RenderUtil.drawBorderedRect(this.x, this.y, this.width, this.height, 1.0f, new Color(0, 0, 0, 50).getRGB(), new Color(29, 29, 29, 180).getRGB());
                    RenderUtil.drawRect(this.x, this.y, this.width, this.height, new Color(40, 40, 40, 130).getRGB());
                    RenderUtil.drawRect(this.x + 40.0f + 1.0f, this.y + 16.5f, (100.0f * space6) + 1.0f, 10.8f, new Color(0, 0, 0, 50).getRGB());
                    float f51 = this.x + 40.0f + 1.0f;
                    float f52 = this.y + 16.5f;
                    float output8 = this.target.healthAnimation.getOutput();
                    InterFace interFace23 = this.setting;
                    RenderUtil.drawRect(f51, f52, output8, 10.8f, InterFace.colors(1));
                    RenderUtil.renderPlayer2D(this.target, this.x + 1.5f + 1.0f, (float) (this.y + 0.4d), 35.0f, 0.0f, -1);
                    String text5 = String.format("%.1f", Float.valueOf(healthPercentage8 * 100.0f)) + "%";
                    mc.fontRendererObj.drawStringWithShadow(text5, (((this.x + 1.0f) + 40.0f) + (50.0f * space6)) - (mc.fontRendererObj.getStringWidth(text5) / 2.0f), this.y + 18.0f, -1);
                    mc.fontRendererObj.drawStringWithShadow(this.target.getName(), this.x + 1.0f + 40.0f, this.y + 4.0f, -1);
                }
                if (this.value.targetHudParticle.get().booleanValue()) {
                    ParticleRenderer.renderParticle(this.target, this.x + 1.5f + 1.0f, (float) (this.y + 0.4d));
                    break;
                }
                break;
            case "Novo 4":
                float healthPercentage9 = this.target.getHealth() / this.target.getMaxHealth();
                float space7 = this.width;
                this.target.healthAnimation.animate(space7 * MathHelper.clamp_float(healthPercentage9, 0.0f, 1.0f), 30);
                if (!this.shader) {
                    RenderUtil.drawRect(this.x - 1.0f, this.y + 4.0f, this.width, this.height, new Color(0, 0, 0, 150));
                    mc.fontRendererObj.drawStringWithShadow(this.target.getName(), this.x + 30.0f, this.y + 13.0f, -1);
                    RenderUtil.renderItemStack(this.target, this.x + 13.0f, this.y + 25.0f, 1.0f, 0.5f);
                    GuiInventory.drawEntityOnScreen((int) (this.x + 15.0f), (int) (this.y + 40.0f), 15, this.target.rotationYaw, -this.target.rotationPitch, this.target);
                    RenderUtil.drawRect(this.x - 1.0f, this.y + 47.0f, this.target.healthAnimation.getOutput(), 2.0f, ColorUtil.getHealthColor(this.target));
                    break;
                }
                break;
            case "Novo 5":
                float healthPercentage10 = this.target.getHealth() / this.target.getMaxHealth();
                float space8 = ((this.width - Regular.get(17.0f).getStringWidth("20.0")) - 3.0f) - 3.2f;
                this.target.healthAnimation.animate(space8 * MathHelper.clamp_float(healthPercentage10, 0.0f, 1.0f), 30);
                if (!this.shader) {
                    RenderUtil.drawRect(this.x, this.y, this.width, this.height, this.value.bgColor());
                    Regular.get(20.0f).drawStringWithShadow(this.target.getName(), this.x + 40.0f, this.y + 3.2f + 1.0f, -1);
                    String text6 = String.format("%.1f", Float.valueOf(this.target.getHealth()));
                    Regular.get(17.0f).drawString(text6, this.x + this.target.healthAnimation.getOutput() + 3.0f, this.y + 37.0f, -1);
                    RenderUtil.renderPlayer2D(this.target, this.x + 3.2f, this.y + 3.2f, 33.0f, 0.0f, -1);
                    double d = this.x + 3.2f;
                    double d2 = this.y + 39.0f;
                    double output9 = this.target.healthAnimation.getOutput();
                    InterFace interFace24 = this.setting;
                    int iColors = InterFace.colors(0);
                    InterFace interFace25 = this.setting;
                    RenderUtil.drawGradientRect(d, d2, output9, 4.0d, true, iColors, InterFace.colors(90));
                    RenderUtil.renderItemStack(this.target, this.x + 40.0f, this.y + 15.0f, 1.0f, false, true);
                    if (this.value.targetHudParticle.get().booleanValue()) {
                        ParticleRenderer.renderParticle(this.target, this.x + 3.2f, this.y + 3.2f);
                        break;
                    }
                } else {
                    float f53 = this.x;
                    float f54 = this.y;
                    float f55 = this.width;
                    float f56 = this.height;
                    InterFace interFace26 = this.setting;
                    RenderUtil.drawRect(f53, f54, f55, f56, InterFace.colors(0));
                    break;
                }
                break;
            case "Akrien":
                float healthPercentage11 = this.target.getHealth() / this.target.getMaxHealth();
                float space9 = this.width - 2.0f;
                if (!this.shader) {
                    this.target.healthAnimation.animate(space9 * MathHelper.clamp_float(healthPercentage11, 0.0f, 1.0f), 30);
                    RenderUtil.drawRect(this.x, this.y + 2.0f, this.width, this.height, this.value.bgColor());
                    RenderUtil.drawBorderedRect(this.x + 1.0f, this.y + 34.5f, space9, 2.5f, 0.74f, new Color(0, 0, 0, 100).getRGB(), new Color(0, 0, 0, 100).getRGB());
                    RenderUtil.drawBorderedRect(this.x + 1.0f, this.y + 38.5f, space9, 2.5f, 0.74f, new Color(0, 0, 0, 100).getRGB(), new Color(0, 0, 0, 100).getRGB());
                    RenderUtil.drawHorizontalGradientSideways(this.x + 1.0f, this.y + 34.5f, this.target.healthAnimation.getOutput(), 2.5d, new Color(40, 145, 90).getRGB(), new Color(170, 255, 220).getRGB());
                    if (this.target.getTotalArmorValue() > 0) {
                        RenderUtil.drawHorizontalGradientSideways(this.x + 1.0f, this.y + 38.5f, this.target.getTotalArmorValue() * 5.75f, 2.5d, new Color(40, 110, 160).getRGB(), new Color(100, 225, 255).getRGB());
                    }
                    String text7 = String.format("%.1f", Float.valueOf(this.target.getHealth()));
                    String text22 = String.format("%.1f", Float.valueOf(mc.thePlayer.getDistanceToEntity(this.target)));
                    Regular.get(15.0f).drawStringWithShadow("Health: " + text7, this.x + 32.5f, this.y + 16.0f + 2.0f, -1);
                    Regular.get(15.0f).drawStringWithShadow("Distance: " + text22 + "m", this.x + 32.5f, this.y + 24.5f + 2.0f, -1);
                    Bold.get(21.0f).drawStringWithShadow(this.target.getName(), this.x + 32.5f, this.y + 3.0f + 2.0f, -1);
                    RenderUtil.renderPlayer2D(this.target, this.x + 1.0f, this.y + 3.0f, 30.0f, 0.0f, -1);
                    break;
                } else {
                    float f57 = this.x;
                    float f58 = this.y;
                    float f59 = this.width;
                    float f60 = this.height;
                    InterFace interFace27 = this.setting;
                    RenderUtil.drawRect(f57, f58, f59, f60, InterFace.colors(1));
                    break;
                }
            case "Innominate":
                float healthPercentage12 = this.target.getHealth() / this.target.getMaxHealth();
                float space10 = this.width - 4.0f;
                if (!this.shader) {
                    this.target.healthAnimation.animate(space10 * MathHelper.clamp_float(healthPercentage12, 0.0f, 1.0f), 30);
                    RenderUtil.drawBorderedRect(this.x, this.y, this.width, this.height, 1.0f, Color.BLACK.getRGB(), Color.DARK_GRAY.getRGB());
                    String text8 = String.format("%.1f", Float.valueOf(this.target.getHealth()));
                    Regular.get(16.0f).drawStringWithShadow(this.target.getName() + " | " + text8, this.x + 2.0f, this.y + 2.0f + 2.0f, -1);
                    RenderUtil.renderItemStack(this.target, this.x + 2.0f, this.y + 2.0f + Regular.get(16.0f).getHeight(), 1.0f, false, 0.0f, false, false);
                    RenderUtil.drawBorderedRect(this.x + 2.0f, this.y + 2.0f + Regular.get(16.0f).getHeight() + 16.0f, this.width - 4.0f, 5.0f, 1.0f, Color.BLACK.getRGB(), Color.DARK_GRAY.getRGB());
                    float f61 = this.x + 2.0f;
                    float height = this.y + 2.0f + Regular.get(16.0f).getHeight() + 16.0f;
                    float output10 = this.target.healthAnimation.getOutput();
                    InterFace interFace28 = this.setting;
                    RenderUtil.drawBorderedRect(f61, height, output10, 5.0f, 1.0f, InterFace.colors(0), Color.DARK_GRAY.getRGB());
                    break;
                } else {
                    RenderUtil.drawBorderedRect(this.x, this.y, this.width, this.height, 1.0f, Color.BLACK.getRGB(), Color.DARK_GRAY.getRGB());
                    break;
                }
        }
        GlStateManager.popMatrix();
    }
}
