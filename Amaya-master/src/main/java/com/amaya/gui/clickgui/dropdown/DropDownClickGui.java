package com.amaya.gui.clickgui.dropdown;

import com.amaya.gui.clickgui.dropdown.CategoryPanel;
import com.amaya.module.Category;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.RoundedUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/5/17
 */
public class DropDownClickGui extends GuiScreen {
    private final List<CategoryPanel> panels = new ArrayList<>();
    public DropDownClickGui(String name){
        Arrays.stream(Category.values()).filter(moduleCategory -> !(moduleCategory == Category.Search))
                .forEach(ca -> panels.add(new CategoryPanel(ca)));
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RoundedUtil.drawRound(0,0,width,height,0,new Color(1,1,1,80));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
