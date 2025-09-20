package qwq.arcane.module.impl.display;

import java.awt.Color;
import net.minecraft.item.ItemStack;
import qwq.arcane.event.impl.events.render.Shader2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.ModuleWidget;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/display/Inventory.class */
public class Inventory extends ModuleWidget {
    public ModeValue modeValue;

    public Inventory() {
        super("Inventory", Category.Display);
        this.modeValue = new ModeValue("Mode", "Normal", new String[]{"Normal", "Custom", "Solitude"});
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void onShader(Shader2DEvent event) {
        float x;
        float y;
        x = this.renderX;
        y = this.renderY;
        switch (this.modeValue.getValue()) {
            case "Solitude":
                InterFace interFace = INTERFACE;
                RoundedUtil.drawRound(x, y, 14.0f + 120.0f, 65.0f, InterFace.radius.get().intValue(), new Color(255, 255, 255, 255));
                break;
            case "Custom":
                RoundedUtil.drawRound(x, y, 130.0f, 66.0f, 6.0f, new Color(255, 255, 255, 255));
                break;
            case "Normal":
                InterFace interFace2 = INTERFACE;
                RoundedUtil.drawRound(x, y, 14.0f + 120.0f, 65.0f, InterFace.radius.get().intValue(), new Color(0, 0, 0, 255));
                break;
        }
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void render() {
        float x;
        float y;
        x = this.renderX;
        y = this.renderY;
        switch (this.modeValue.getValue()) {
            case "Solitude":
                RenderUtil.drawRect(x, y, 14.0f + 120.0f, 65.0f, new Color(255, 255, 255, 89));
                RenderUtil.drawRect(x, y, 14.0f + 120.0f, 15.0f, new Color(255, 255, 255, 89));
                Bold.get(18.0f).drawCenteredString("Inventory", x + (14.0f / 2.0f) + 60.0f, y + 5.0f, new Color(255, 255, 255).getRGB());
                for (int i = 9; i < 36; i++) {
                    ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
                    RenderUtil.drawRect(x + 1.7f, y + 17.5f, 13.0f, 13.0f, new Color(255, 255, 255, 30));
                    RenderUtil.renderItemStack(slot, x + 1.7f, y + 17.5f, 0.8f);
                    x = x + 14.0f + 0.7f;
                    if (i == 17) {
                        y += 17.0f - 1.0f;
                        x = (x - (14.0f * 9.0f)) - (0.7f * 8.5f);
                    }
                    if (i == 26) {
                        y += 17.0f - 1.0f;
                        x = (x - (14.0f * 9.0f)) - (0.7f * 9.0f);
                    }
                }
                break;
            case "Custom":
                RoundedUtil.drawRound(x, y, 130.0f, 66.0f, 6.0f, new Color(255, 255, 255, 80));
                RenderUtil.startGlScissor((int) (x - 2.0f), (int) (y + 52.0f), 134, 20);
                RoundedUtil.drawRound(x, y + 44.0f, 130.0f, 22.0f, 6.0f, new Color(255, 255, 255, 100));
                RenderUtil.stopGlScissor();
                Bold.get(18.0f).drawCenteredString("Inventory", x + 26.0f, y + 55.0f, new Color(255, 255, 255).getRGB());
                for (int i2 = 9; i2 < 36; i2++) {
                    ItemStack slot2 = mc.thePlayer.inventory.getStackInSlot(i2);
                    RenderUtil.renderItemStack(slot2, x + 0.7f, y + 4.0f, 0.8f);
                    x = x + 14.0f + 0.7f;
                    if (i2 == 17) {
                        y += 17.0f - 1.0f;
                        x = (x - (14.0f * 9.0f)) - (0.7f * 8.5f);
                    }
                    if (i2 == 26) {
                        y += 17.0f - 1.0f;
                        x = (x - (14.0f * 9.0f)) - (0.7f * 9.0f);
                    }
                }
                this.width = (14.0f * 9.1f) + (0.7f * 9.0f);
                this.height = (14.0f * 3.0f) + 19.0f;
                break;
            case "Normal":
                InterFace interFace = INTERFACE;
                RoundedUtil.drawRound(x, y, 14.0f + 120.0f, 65.0f, InterFace.radius.get().intValue(), new Color(0, 0, 0, 89));
                RenderUtil.startGlScissor((int) (x - 2.0f), (int) (y - 1.0f), 159, 18);
                InterFace interFace2 = INTERFACE;
                float fIntValue = InterFace.radius.get().intValue();
                InterFace interFace3 = setting;
                RoundedUtil.drawRound(x, y, 14.0f + 120.0f, 29.0f, fIntValue, ColorUtil.applyOpacity(new Color(InterFace.colors(1)), 0.3f));
                RenderUtil.stopGlScissor();
                FontManager.Bold.get(18.0f).drawString("Inventory", x + 5.0f, y + 5.0f, -1);
                for (int i3 = 9; i3 < 36; i3++) {
                    ItemStack slot3 = mc.thePlayer.inventory.getStackInSlot(i3);
                    RenderUtil.renderItemStack(slot3, x + 0.7f, y + 17.5f, 0.8f);
                    x = x + 14.0f + 0.7f;
                    if (i3 == 17) {
                        y += 17.0f - 1.0f;
                        x = (x - (14.0f * 9.0f)) - (0.7f * 8.5f);
                    }
                    if (i3 == 26) {
                        y += 17.0f - 1.0f;
                        x = (x - (14.0f * 9.0f)) - (0.7f * 9.0f);
                    }
                }
                this.width = (14.0f * 9.1f) + (0.7f * 9.0f);
                this.height = (14.0f * 3.0f) + 19.0f;
                break;
        }
    }

    @Override // qwq.arcane.module.ModuleWidget
    public boolean shouldRender() {
        return getState() && setting.getState();
    }
}
