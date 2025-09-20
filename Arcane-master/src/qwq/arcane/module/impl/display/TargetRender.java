package qwq.arcane.module.impl.display;

import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import qwq.arcane.event.impl.events.render.Shader2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.ModuleWidget;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/display/TargetRender.class */
public class TargetRender extends ModuleWidget {
    public final ModeValue targetHudMode;
    public final BoolValue targetHudParticle;
    public final ModeValue bgColor;
    private final ColorValue bgCustomColor;
    private final NumberValue bgAlpha;

    public TargetRender() {
        super("TargetHud", Category.Display);
        this.targetHudMode = new ModeValue("TargetRender Mode", "Astolfo", new String[]{"Astolfo", "Type 1", "Type 2", "Felix", "Exhi", "Adjust", "Moon", "Augustus", "New", "Novo 1", "Novo 2", "Novo 3", "Novo 5", "Akrien", "Innominate"});
        this.targetHudParticle = new BoolValue("TargetParticle", true);
        this.bgColor = new ModeValue("Background Color", "Dark", new String[]{"Dark", "Synced", "Custom"});
        this.bgCustomColor = new ColorValue("Background Custom Color", () -> {
            return this.bgColor.is("Custom");
        }, new Color(32, 32, 64));
        this.bgAlpha = new NumberValue("Background Alpha", 100.0d, 1.0d, 255.0d, 1.0d);
        this.x = 0.5f;
        this.y = 0.8f;
    }

    public int bgColor(int counter, int alpha) {
        int colors;
        int iSwapAlpha;
        colors = InterFace.mainColor.get().getRGB();
        switch (this.bgColor.get()) {
            case "Dark":
                iSwapAlpha = new Color(21, 21, 21, alpha).getRGB();
                break;
            case "Synced":
                InterFace interFace = setting;
                iSwapAlpha = new Color(ColorUtil.applyOpacity(InterFace.colors(counter), alpha / 255.0f), true).darker().darker().getRGB();
                break;
            case "None":
                iSwapAlpha = new Color(0, 0, 0, 0).getRGB();
                break;
            case "Custom":
                iSwapAlpha = ColorUtil.swapAlpha(this.bgCustomColor.get().getRGB(), alpha);
                break;
            default:
                iSwapAlpha = colors;
                break;
        }
        int colors2 = iSwapAlpha;
        return colors2;
    }

    public int bgColor(int counter) {
        return bgColor(counter, this.bgAlpha.get().intValue());
    }

    public int bgColor() {
        return bgColor(0);
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void onShader(Shader2DEvent event) {
        int count = 0;
        float lastTargetWidth = 0.0f;
        for (EntityPlayer target : setting.animationEntityPlayerMap.keySet()) {
            this.height = getTHUDHeight();
            float currentTargetWidth = getTHUDWidth(target);
            this.width = currentTargetWidth;
            if (count <= 9) {
                TargetHUD targetHUD = new TargetHUD((float) (this.renderX + ((count % 3) * (lastTargetWidth + 4.0f) * setting.animationEntityPlayerMap.get(target).getOutput().doubleValue())), (float) (this.renderY + ((count / 3) * (this.height + 4.0f) * setting.animationEntityPlayerMap.get(target).getOutput().doubleValue())), target, setting.animationEntityPlayerMap.get(target), true, this.targetHudMode);
                targetHUD.render();
                lastTargetWidth = currentTargetWidth;
                count++;
            }
        }
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void render() {
        int count = 0;
        float lastTargetWidth = 0.0f;
        for (EntityPlayer target : setting.animationEntityPlayerMap.keySet()) {
            this.height = getTHUDHeight();
            float currentTargetWidth = getTHUDWidth(target);
            this.width = currentTargetWidth;
            if (count <= 9) {
                TargetHUD targetRender = new TargetHUD((float) (this.renderX + ((count % 3) * (lastTargetWidth + 4.0f) * setting.animationEntityPlayerMap.get(target).getOutput().doubleValue())), (float) (this.renderY + ((count / 3) * (this.height + 4.0f) * setting.animationEntityPlayerMap.get(target).getOutput().doubleValue())), target, setting.animationEntityPlayerMap.get(target), false, this.targetHudMode);
                targetRender.render();
                lastTargetWidth = currentTargetWidth;
                count++;
            }
        }
    }

    @Override // qwq.arcane.module.ModuleWidget
    public boolean shouldRender() {
        return getState() && setting.getState();
    }

    public float getTHUDWidth(Entity entity) {
        switch (this.targetHudMode.get()) {
            case "Type 1":
                return Math.max(120, Bold.get(18.0f).getStringWidth(entity.getName()) + 50);
            case "Astolfo":
                return Math.max(130, mc.fontRendererObj.getStringWidth(entity.getName()) + 60);
            case "Type 2":
                return Math.max(100, mc.fontRendererObj.getStringWidth(entity.getDisplayName().getFormattedText())) + 11;
            case "Exhi":
                return Math.max(124.0f, Bold.get(17.0f).getStringWidth(entity.getName()) + 54.0f);
            case "Adjust":
                return 130.0f;
            case "Moon":
                return 35 + Semibold.get(18.0f).getStringWidth(entity.getName()) + 33;
            case "Augustus":
                return 35 + Semibold.get(18.0f).getStringWidth(entity.getName()) + 33;
            case "New":
                return Math.max(160, Semibold.get(17.0f).getStringWidth(entity.getName()) + 30);
            case "Novo 1":
            case "Novo 2":
                return 35 + mc.fontRendererObj.getStringWidth(entity.getName()) + 33;
            case "Novo 3":
                return 35 + mc.fontRendererObj.getStringWidth(entity.getName()) + 34;
            case "Novo 4":
                return 135.0f;
            case "Novo 5":
                return Math.max(118, Semibold.get(17.0f).getStringWidth(entity.getName()) + 38) + 27.0f;
            case "Akrien":
                return 114.0f + ((35 + Semibold.get(21.0f).getStringWidth(entity.getName())) / 25.0f);
            case "Felix":
                return 140.0f;
            case "Innominate":
                return Math.max(78, Semibold.get(17.0f).getStringWidth(entity.getName()) + 39) + 22;
            default:
                return 0.0f;
        }
    }

    public float getTHUDHeight() {
        switch (this.targetHudMode.get()) {
            case "Type 1":
                return 44.0f;
            case "Astolfo":
                return 56.0f;
            case "Type 2":
                return 38.0f;
            case "Exhi":
                return 38.0f;
            case "Adjust":
                return 35.0f;
            case "Moon":
                return 40.5f;
            case "Augustus":
                return 40.5f;
            case "New":
                return 40.5f;
            case "Novo 1":
                return 37.5f;
            case "Novo 2":
            case "Novo 3":
                return 36.0f;
            case "Novo 4":
                return 45.0f;
            case "Novo 5":
                return 47.0f;
            case "Akrien":
                return 39.5f;
            case "Felix":
                return 37.6f;
            case "Innominate":
                return 35.0f;
            default:
                return 0.0f;
        }
    }
}
