package com.amaya.gui.widget.impl;

import com.amaya.Amaya;
import com.amaya.events.impl.render.Shader2DEvent;
import com.amaya.gui.widget.Widget;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;

import java.awt.*;

/**
 * @Author: Guyuemang
 * 2025/5/10
 */
public class Inventory extends Widget {
    public Inventory() {
        super("Inventory");
    }
    private com.amaya.module.impl.display.Inventory value = Amaya.Instance.moduleManager.getModule(com.amaya.module.impl.display.Inventory.class);

    @Override
    public void onShader(Shader2DEvent event) {
        int x = (int) renderX;
        int y = (int) renderY;
        RoundedUtil.drawRound(x,y, 165, 75, value.radius.get().intValue(), new Color(0, 0, 0, 255));
    }

    @Override
    public void render() {
        int x = (int) renderX;
        int y = (int) renderY;
        boolean hasStacks = false;
        RoundedUtil.drawRound(x,y, 165, 75, value.radius.get().intValue(), new Color(0, 0, 0, 89));
        RenderUtil.startGlScissor(x - 2,y - 1, 169, 20);
        RoundedUtil.drawRound(x,y, 165, 30, value.radius.get().intValue(), new Color(50, 50, 50, 255));
        RenderUtil.stopGlScissor();
        FontManager.BOLD.get(20).drawString("Inventory",x + 5,y + 5,-1);

        for (int i1 = 9; i1 < mc.thePlayer.inventoryContainer.inventorySlots.size() - 9; ++i1) {
            Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i1);
            if (slot.getHasStack()) hasStacks = true;
            RenderHelper.enableGUIStandardItemLighting();
            int i = slot.xDisplayPosition;
            int j = slot.yDisplayPosition;
            mc.getRenderItem().renderItemAndEffectIntoGUI(slot.getStack(), (int) (x + i - 6), (int) (y + j - 65));
            mc.getRenderItem().renderItemOverlayIntoGUI(FontManager.BOLD.get(18), slot.getStack(), (int) x + i - 6, (int) y + j -65, null);
            RenderHelper.disableStandardItemLighting();
        }

        if (!hasStacks) {
            FontManager.BOLD.get(18).drawString("Is Empty... ", 60 + x, y + 35, new Color(255, 255, 255, 255).getRGB());
        }

        this.width = 165;
        this.height = 70;
    }

    @Override
    public boolean shouldRender() {
        return value.getState();
    }
}
