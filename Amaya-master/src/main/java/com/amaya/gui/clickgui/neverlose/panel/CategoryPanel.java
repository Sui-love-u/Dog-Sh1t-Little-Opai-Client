package com.amaya.gui.clickgui.neverlose.panel;

import com.amaya.Amaya;
import com.amaya.gui.clickgui.neverlose.Component;
import com.amaya.gui.clickgui.neverlose.IComponent;
import com.amaya.gui.clickgui.neverlose.component.ModuleComponent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.animations.impl.SmoothStepAnimation;
import com.amaya.utils.client.InstanceAccess;
import com.amaya.utils.math.MathUtils;
import com.amaya.utils.render.RenderUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * @Author: Guyuemang
 */
@Getter
public class CategoryPanel implements IComponent, InstanceAccess {
    private int posX, posY;
    private final Category category;
    private final ObjectArrayList<ModuleComponent> moduleComponents = new ObjectArrayList<>();
    @Setter
    private boolean selected;
    private final Animation animation = new DecelerateAnimation(250,1);
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    public static int i;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

    public CategoryPanel(Category category) {
        this.category = category;
        for (i = 0; i < (Amaya.Instance.moduleManager.getModsByCategory(category).size()); ++i){
            Module module = Amaya.Instance.moduleManager.getModsByCategory(category).get(i);
            moduleComponents.add(new ModuleComponent(module));
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        posX = INSTANCE.getNegative().getPosX();
        posY = INSTANCE.getNegative().getPosY();
        animation.setDirection(selected ? Direction.FORWARDS : Direction.BACKWARDS);
        
        if (isSelected()) {
            //GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(getPosX() + 140, getPosY() + 51, 380, 367);

            float left = 0, right = 0;
            for (int i = 0; i < moduleComponents.size(); i++) {
                ModuleComponent module = moduleComponents.get(i);
                float componentOffset = getComponentOffset(i,left,right);

                module.drawScreen(mouseX, mouseY);

                double scroll = getScroll();
                module.setScroll((int) MathUtils.roundToHalf(scroll));
                onScroll(30, mouseX, mouseY);

                maxScroll = Math.max(0, moduleComponents.isEmpty() ? 0 : moduleComponents.get(moduleComponents.size() - 1).getMaxScroll());
                if ((i + 1) % 2 == 0) {
                    left += 40 + componentOffset;
                } else {
                    right += 40 + componentOffset;
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            //GL11.glPopAttrib();

        }
        IComponent.super.drawScreen(mouseX, mouseY);
    }
    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        if (RenderUtil.isHovering(getPosX() + 140, getPosY() + 49, 380, 368, mx, my) && moduleComponents.stream().noneMatch(moduleComponent -> moduleComponent.getComponents().stream().anyMatch(component -> component.isHovered(mx,my)))) {
            rawScroll += (float) Mouse.getDWheel() * 1;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }
    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }

    private float getComponentOffset(int i, float left, float right) {
        ModuleComponent component = moduleComponents.get(i);
        component.setLeft((i + 1) % 2 != 0);
        component.setX(component.isLeft() ? posX + 140 : posX + 330);
        component.setHeight(20);
        component.setY(posY + 32 + component.getHeight() + ((i + 1) % 2 == 0 ? left : right));
        float componentOffset = 0;
        for (Component component2 : component.getComponents()) {
            if (component2.isVisible())
                componentOffset += component2.getHeight();
        }
        component.setHeight(component.getHeight() + componentOffset);
        return componentOffset;
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isSelected()) {
            moduleComponents.forEach(moduleComponent -> moduleComponent.mouseClicked(mouseX,mouseY,mouseButton));
        }
        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (isSelected()) {
            moduleComponents.forEach(moduleComponent -> moduleComponent.mouseReleased(mouseX,mouseY,state));
        }
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (isSelected()) {
            moduleComponents.forEach(moduleComponent -> moduleComponent.keyTyped(typedChar,keyCode));
        }
        IComponent.super.keyTyped(typedChar, keyCode);
    }
}
