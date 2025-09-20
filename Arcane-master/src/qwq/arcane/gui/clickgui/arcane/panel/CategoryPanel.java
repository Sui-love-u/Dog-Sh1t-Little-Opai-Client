package qwq.arcane.gui.clickgui.arcane.panel;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Objects;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.gui.clickgui.IComponent;
import qwq.arcane.gui.clickgui.arcane.component.ModuleComponent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.animations.impl.SmoothStepAnimation;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.render.RenderUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/arcane/panel/CategoryPanel.class */
public class CategoryPanel implements IComponent, Instance {
    private int posX;
    private int posY;
    private float rawScroll;
    private float scroll;
    public final Category category;
    private boolean selected;
    private float maxScroll = Float.MAX_VALUE;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0.0d, Direction.BACKWARDS);
    private final ObjectArrayList<ModuleComponent> moduleComponents = new ObjectArrayList<>();
    private final Animation animation = new DecelerateAnimation(250, 1.0d);

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setMaxScroll(float maxScroll) {
        this.maxScroll = maxScroll;
    }

    public void setRawScroll(float rawScroll) {
        this.rawScroll = rawScroll;
    }

    public void setScroll(float scroll) {
        this.scroll = scroll;
    }

    public void setScrollAnimation(Animation scrollAnimation) {
        this.scrollAnimation = scrollAnimation;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public float getMaxScroll() {
        return this.maxScroll;
    }

    public float getRawScroll() {
        return this.rawScroll;
    }

    public Animation getScrollAnimation() {
        return this.scrollAnimation;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public ObjectArrayList<ModuleComponent> getModuleComponents() {
        return this.moduleComponents;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public CategoryPanel(Category category) {
        this.category = category;
        for (Module module : INSTANCE.getModuleManager().getAllModules()) {
            if (module.getCategory().equals(this.category)) {
                this.moduleComponents.add(new ModuleComponent(module));
            }
        }
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        this.posX = INSTANCE.getArcaneClickGui().getX();
        this.posY = INSTANCE.getArcaneClickGui().getY();
        this.animation.setDirection(this.selected ? Direction.FORWARDS : Direction.BACKWARDS);
        if (isSelected()) {
            GL11.glEnable(3089);
            double posX = getPosX();
            double posY = getPosY() + 75;
            double d = INSTANCE.getArcaneClickGui().w;
            Objects.requireNonNull(INSTANCE.getArcaneClickGui());
            RenderUtil.scissor(posX, posY, d, 380 - 120);
            float left = 0.0f;
            float right = 0.0f;
            for (int i = 0; i < this.moduleComponents.size(); i++) {
                ModuleComponent module = (ModuleComponent) this.moduleComponents.get(i);
                float componentOffset = getComponentOffset(i, left, right);
                module.drawScreen(mouseX, mouseY);
                double scroll = getScroll();
                module.setScroll((int) MathUtils.roundToHalf(scroll));
                onScroll(30, mouseX, mouseY);
                this.maxScroll = Math.max(0, this.moduleComponents.isEmpty() ? 0 : ((ModuleComponent) this.moduleComponents.get(this.moduleComponents.size() - 1)).getMaxScroll());
                if ((i + 1) % 2 == 0) {
                    left += 30.0f + componentOffset;
                } else {
                    right += 30.0f + componentOffset;
                }
            }
            GL11.glDisable(3089);
        }
        IComponent.super.drawScreen(mouseX, mouseY);
    }

    private float getComponentOffset(int i, float left, float right) {
        ModuleComponent component = (ModuleComponent) this.moduleComponents.get(i);
        component.setLeft((i + 1) % 2 != 0);
        component.setX(component.isLeft() ? this.posX + 10 : this.posX + 185);
        component.setHeight(24.0f);
        component.setY(this.posY + 54 + component.getHeight() + ((i + 1) % 2 == 0 ? left : right));
        float componentOffset = 0.0f;
        ObjectListIterator it = component.getComponents().iterator();
        while (it.hasNext()) {
            Component component2 = (Component) it.next();
            if (component2.isVisible()) {
                componentOffset += component2.getHeight();
            }
        }
        component.setHeight(component.getHeight() + componentOffset);
        return componentOffset;
    }

    public void onScroll(int ms, int mx, int my) {
        this.scroll = (float) (this.rawScroll - this.scrollAnimation.getOutput().doubleValue());
        float posX = getPosX();
        float posY = getPosY() + 75;
        float f = INSTANCE.getArcaneClickGui().w;
        Objects.requireNonNull(INSTANCE.getArcaneClickGui());
        if (RenderUtil.isHovering(posX, posY, f, 380 - 120, mx, my) && this.moduleComponents.stream().noneMatch(moduleComponent -> {
            return moduleComponent.getComponents().stream().anyMatch(component -> {
                return component.isHovered(mx, my);
            });
        })) {
            this.rawScroll += Mouse.getDWheel();
        }
        this.rawScroll = Math.max(Math.min(0.0f, this.rawScroll), -this.maxScroll);
        this.scrollAnimation = new SmoothStepAnimation(ms, this.rawScroll - this.scroll, Direction.BACKWARDS);
    }

    public float getScroll() {
        this.scroll = (float) (this.rawScroll - this.scrollAnimation.getOutput().doubleValue());
        return this.scroll;
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isSelected()) {
            this.moduleComponents.forEach(moduleComponent -> {
                moduleComponent.mouseClicked(mouseX, mouseY, mouseButton);
            });
        }
        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (isSelected()) {
            this.moduleComponents.forEach(moduleComponent -> {
                moduleComponent.mouseReleased(mouseX, mouseY, state);
            });
        }
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void keyTyped(char typedChar, int keyCode) {
        if (isSelected()) {
            this.moduleComponents.forEach(moduleComponent -> {
                moduleComponent.keyTyped(typedChar, keyCode);
            });
        }
        IComponent.super.keyTyped(typedChar, keyCode);
    }
}
