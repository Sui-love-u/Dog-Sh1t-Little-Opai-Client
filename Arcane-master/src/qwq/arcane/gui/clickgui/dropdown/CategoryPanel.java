package qwq.arcane.gui.clickgui.dropdown;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.awt.Color;
import qwq.arcane.Client;
import qwq.arcane.gui.clickgui.IComponent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/dropdown/CategoryPanel.class */
public class CategoryPanel implements IComponent, Instance {
    private float x;
    private float y;
    private float dragX;
    private float dragY;
    private float height;
    private final Category category;
    private boolean dragging;
    private boolean opened;
    public static int i;
    private float width = 100.0f;
    private final ObjectArrayList<ModuleComponent> moduleComponents = new ObjectArrayList<>();

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setDragX(float dragX) {
        this.dragX = dragX;
    }

    public void setDragY(float dragY) {
        this.dragY = dragY;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getDragX() {
        return this.dragX;
    }

    public float getDragY() {
        return this.dragY;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public boolean isOpened() {
        return this.opened;
    }

    public ObjectArrayList<ModuleComponent> getModuleComponents() {
        return this.moduleComponents;
    }

    public CategoryPanel(Category category) {
        this.category = category;
        i = 0;
        while (i < Client.Instance.getModuleManager().getModsByCategory(category).size()) {
            Module module = Client.Instance.getModuleManager().getModsByCategory(category).get(i);
            this.moduleComponents.add(new ModuleComponent(module));
            i++;
        }
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        update(mouseX, mouseY);
        RoundedUtil.drawRound(this.x, this.y - 2.0f, this.width, 19.0f + (this.height - 19.0f), 6.0f, new Color(1, 1, 1, 120));
        FontManager.Bold.get(20.0f).drawCenteredString(this.category.name(), this.x + (this.width / 2.0f), this.y + 4.5d, -1);
        float componentOffsetY = 18.0f;
        ObjectListIterator it = this.moduleComponents.iterator();
        while (it.hasNext()) {
            ModuleComponent component = (ModuleComponent) it.next();
            component.setX(this.x);
            component.setY(this.y + componentOffsetY);
            component.setWidth(this.width);
            component.drawScreen(mouseX, mouseY);
            componentOffsetY += component.getHeight();
        }
        this.height = componentOffsetY + 8.0f;
        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(this.x, this.y - 2.0f, this.width, 19.0f, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    this.dragging = true;
                    this.dragX = this.x - mouseX;
                    this.dragY = this.y - mouseY;
                    break;
                case 1:
                    this.opened = !this.opened;
                    break;
            }
        }
        this.moduleComponents.forEach(component -> {
            component.mouseClicked(mouseX, mouseY, mouseButton);
        });
        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void keyTyped(char typedChar, int keyCode) {
        this.moduleComponents.forEach(component -> {
            component.keyTyped(typedChar, keyCode);
        });
        IComponent.super.keyTyped(typedChar, keyCode);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
        this.moduleComponents.forEach(component -> {
            component.mouseReleased(mouseX, mouseY, state);
        });
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    public void update(int mouseX, int mouseY) {
        if (this.dragging) {
            this.x = mouseX + this.dragX;
            this.y = mouseY + this.dragY;
        }
    }
}
