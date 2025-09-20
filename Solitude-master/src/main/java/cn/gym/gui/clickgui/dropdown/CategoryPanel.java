package cn.gym.gui.clickgui.dropdown;

import cn.gym.Solitude;
import cn.gym.gui.clickgui.IComponent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.Instance;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.EaseInOutQuad;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * @Author: Guyuemang
 * 2025/5/17
 */
@Getter
@Setter
public class CategoryPanel implements IComponent, Instance {
    private float x, y, dragX, dragY;
    private float width = 100, height;
    private final Category category;
    private boolean dragging, opened;
    private final ObjectArrayList<ModuleComponent> moduleComponents = new ObjectArrayList<>();
    private final EaseInOutQuad openAnimation = new EaseInOutQuad(250, 1);
    public static int i;
    public CategoryPanel(Category category) {
        this.category = category;
        this.openAnimation.setDirection(Direction.BACKWARDS);
        for (i = 0; i < (Solitude.Instance.getModuleManager().getModsByCategory(category).size()); ++i){
            Module module = Solitude.Instance.getModuleManager().getModsByCategory(category).get(i);
            moduleComponents.add(new ModuleComponent(module));
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        update(mouseX, mouseY);
        RenderUtil.scaleStart((float) new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() / 2, (float) new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() / 2, (float) DropDownClickGui.openingAnimation.getOutput().doubleValue());

        RoundedUtil.drawRound(x, y - 2, width, (float) (19 + ((height - 19) * openAnimation.getOutput())), 6, new Color(20, 20, 20, 255));

        FontManager.Bold.get(20).drawCenteredString(category.name(), x + width / 2, y + 4.5, -1);

        float componentOffsetY = 18;

        for (ModuleComponent component : moduleComponents) {
            component.setX(x);
            component.setY(y + componentOffsetY);
            component.setWidth(width);
            if (openAnimation.getOutput() > 0.7f) {
                component.drawScreen(mouseX, mouseY);
            }
            componentOffsetY += (float) (component.getHeight() * openAnimation.getOutput());
        }
        height = componentOffsetY + 8;

        RenderUtil.scaleEnd();
        IComponent.super.drawScreen(mouseX, mouseY);
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(x, y - 2, width, 19, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0 -> {
                    dragging = true;
                    dragX = x - mouseX;
                    dragY = y - mouseY;
                }
                case 1 -> opened = !opened;
            }
        }
        if (opened && !RenderUtil.isHovering(x, y - 2, width, 19, mouseX, mouseY)) {
            moduleComponents.forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
        }
        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        moduleComponents.forEach(component -> component.keyTyped(typedChar, keyCode));
        IComponent.super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) dragging = false;
        moduleComponents.forEach(component -> component.mouseReleased(mouseX, mouseY, state));
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    public void update(int mouseX, int mouseY) {
        this.openAnimation.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);

        if (dragging) {
            x = (mouseX + dragX);
            y = (mouseY + dragY);
        }
    }
}
