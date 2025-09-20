package cn.gym.gui.clickgui.neverloseclickgui;

import cn.gym.gui.clickgui.neverloseclickgui.panel.CategoryPanel;
import cn.gym.module.Category;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @Author：Guyuemang
 * @Date：2025/6/9 02:16
 */
@Getter
public class NeverLoseClickGui extends GuiScreen {
    private final List<CategoryPanel> categoryPanels = new ArrayList<>();
    public int x;
    public int y;
    public int w = 540;
    public final int h = 420;
    private int dragX;
    private int dragY;
    private boolean dragging = false;

    public NeverLoseClickGui(){
        Arrays.stream(Category.values())
                .forEach(moduleCategory -> categoryPanels
                        .add(new CategoryPanel(moduleCategory)));
        x = 40;
        y = 40;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            x = mouseX + dragX;
            y = mouseY + dragY;
        }
        RoundedUtil.drawRound(x,y,w,h,4,new Color(0x0C1313));
        RenderUtil.startGlScissor(x + 120,y,w,h);
        RoundedUtil.drawRound(x + 120,y,w - 120,h,4,new Color(0x0E1213));
        RenderUtil.stopGlScissor();
        RoundedUtil.drawRound(x + 120,y,1,h,1,new Color(0x010605));
        RoundedUtil.drawRound(x,y + h - 40,120,1,1,new Color(0x010605));
        FontManager.Bold2.get(35).drawCenteredString("Solitude".toUpperCase(Locale.ROOT),x + 60,y + 20,-1);
        FontManager.Bold.get(14).drawString("Violent",x + 14,y + 62,Color.GRAY.getRGB());
        FontManager.Bold.get(14).drawString("Other",x + 14,y + 162,Color.GRAY.getRGB());
        FontManager.Bold.get(14).drawString("Render",x + 14,y + 202,Color.GRAY.getRGB());
        FontManager.Bold.get(14).drawString("Module",x + 14,y + 249,Color.GRAY.getRGB());
        
        for (CategoryPanel categoryPanel : categoryPanels){
            categoryPanel.drawScreen(mouseX,mouseY);
            if (categoryPanel.isSelected()) {
                RoundedUtil.drawRound(x + 10, (categoryPanel.getCategory().ordinal() >= 5 ? y + 108 + categoryPanel.getCategory().ordinal() * 30 : categoryPanel.getCategory().ordinal() >= 4 ? y + 93 + categoryPanel.getCategory().ordinal() * 30 : categoryPanel.getCategory().ordinal() >= 3 ? y + 82 + categoryPanel.getCategory().ordinal() * 30
                        : y + 73 + categoryPanel.getCategory().ordinal() * 30),100, 19, 5, new Color(0x2B2F31));
            }
            FontManager.Bold2.get(18).drawString(categoryPanel.getCategory().name(), x + 36, (categoryPanel.getCategory().ordinal() >= 5 ? y + 115 + categoryPanel.getCategory().ordinal() * 30 : categoryPanel.getCategory().ordinal() >= 4 ? y + 100 + categoryPanel.getCategory().ordinal() * 30 : categoryPanel.getCategory().ordinal() >= 3 ? y + 89 + categoryPanel.getCategory().ordinal() * 30
                    : y + 80 + categoryPanel.getCategory().ordinal() * 30), -1);
            FontManager.ICON.get(45).drawString(categoryPanel.getCategory().icon, x + 14, (categoryPanel.getCategory().ordinal() >= 5 ? y + 110 + categoryPanel.getCategory().ordinal() * 30 : categoryPanel.getCategory().ordinal() >= 4 ? y + 95 + categoryPanel.getCategory().ordinal() * 30 : categoryPanel.getCategory().ordinal() >= 3 ? y + 84 + categoryPanel.getCategory().ordinal() * 30
                    : y + 75 + categoryPanel.getCategory().ordinal() * 30), new Color(0x598BB8).getRGB());
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (RenderUtil.isHovering(x,y,120,52, mouseX, mouseY)) {
            dragging = true;
            dragX = x - mouseX;
            dragY = y - mouseY;
        }
        if (mouseButton == 0){
            for (CategoryPanel panel : categoryPanels) {
                if (handleCategoryPanel(panel, mouseX, mouseY)) {
                    break;
                }
            }
            if (RenderUtil.isHovering(x,y,136,42, mouseX, mouseY)) {
                dragging = true;
                dragX = x - mouseX;
                dragY = y - mouseY;
            }
        }
        CategoryPanel selected = getSelected();
        if (selected != null) {
            if (!RenderUtil.isHovering(x + 120, y + 39, w - 120, h,mouseX,mouseY)) return;
            selected.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    private boolean handleCategoryPanel(CategoryPanel panel, int mouseX, int mouseY) {
        if (RenderUtil.isHovering(x + 36, (panel.getCategory().ordinal() >= 5 ? y + 115 + panel.getCategory().ordinal() * 30 : panel.getCategory().ordinal() >= 4 ? y + 100 + panel.getCategory().ordinal() * 30 : panel.getCategory().ordinal() >= 3 ? y + 89 + panel.getCategory().ordinal() * 30
                : y + 80 + panel.getCategory().ordinal() * 30), 120, 19, mouseX, mouseY)) {
            for (CategoryPanel p : categoryPanels) {
                p.setSelected(false);
            }
            panel.setSelected(true);
            return true;
        }
        return false;
    }
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0){
            dragging = false;
        }
        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.mouseReleased(mouseX, mouseY, state);
        }
    }
    public CategoryPanel getSelected() {
        return categoryPanels.stream().filter(CategoryPanel::isSelected).findAny().orElse(null);
    }
    @Override
    public boolean doesGuiPauseGame() {
        //先给这个脑残世界停止关你妈逼了
        return false;
    }
}
