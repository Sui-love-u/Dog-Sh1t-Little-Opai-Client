package qwq.arcane.gui.clickgui;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/Component.class */
public class Component implements IComponent {
    private float x;
    private float y;
    private float width;
    private float height;

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public boolean isHovered(float mouseX, float mouseY, float height) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + height;
    }

    public boolean isVisible() {
        return true;
    }
}
