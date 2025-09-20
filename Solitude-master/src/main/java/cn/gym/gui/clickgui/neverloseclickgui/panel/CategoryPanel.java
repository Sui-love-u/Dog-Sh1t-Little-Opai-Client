package cn.gym.gui.clickgui.neverloseclickgui.panel;

import cn.gym.gui.clickgui.Component;
import cn.gym.gui.clickgui.IComponent;
import cn.gym.gui.clickgui.neverloseclickgui.component.ModuleComponent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.utils.Instance;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.animations.impl.SmoothStepAnimation;
import cn.gym.utils.math.MathUtils;
import cn.gym.utils.render.RenderUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * @Author：Guyuemang
 * @Date：2025/6/9 02:45
 */
@Getter
public class CategoryPanel implements IComponent, Instance {
    private int posX, posY;
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private final Category category;
    @Setter
    private boolean selected;
    private final ObjectArrayList<ModuleComponent> moduleComponents = new ObjectArrayList<>();
    private final Animation animation = new DecelerateAnimation(250,1);

    public CategoryPanel(Category category) {
        this.category = category;
        for (Module module : INSTANCE.getModuleManager().getAllModules()){
            if (module.getCategory().equals(this.category)){
                moduleComponents.add(new ModuleComponent(module));
            }
        }
    }
  @Override
    public void drawScreen(int mouseX, int mouseY) {
        //update coordinate
        posX = INSTANCE.getNeverLoseClickGui().getX();
        posY = INSTANCE.getNeverLoseClickGui().getY();
        //select anim
        animation.setDirection(selected ? Direction.FORWARDS : Direction.BACKWARDS);
        //render module components
        if (isSelected()) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(getPosX() + 120, getPosY() + 39, 416, 376);

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
                    left += 35 + componentOffset;
                } else {
                    right += 35 + componentOffset;
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
        IComponent.super.drawScreen(mouseX, mouseY);
    }

    private float getComponentOffset(int i, float left, float right) {
        ModuleComponent component = moduleComponents.get(i);
        component.setLeft((i + 1) % 2 != 0);
        component.setX(component.isLeft() ? posX + 128 : posX + 334);
        component.setHeight(20);
        component.setY(posY + 21 + component.getHeight() + ((i + 1) % 2 == 0 ? left : right));
        float componentOffset = 0;
        for (Component component2 : component.getComponents()) {
            if (component2.isVisible())
                componentOffset += component2.getHeight();
        }
        component.setHeight(component.getHeight() + componentOffset);
        return componentOffset;
    }

    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        if (RenderUtil.isHovering(getPosX() + 120, getPosY() + 39, 416, 376, mx, my) && moduleComponents.stream().noneMatch(moduleComponent -> moduleComponent.getComponents().stream().anyMatch(component -> component.isHovered(mx,my)))) {
            rawScroll += (float) Mouse.getDWheel();
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }
    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
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
