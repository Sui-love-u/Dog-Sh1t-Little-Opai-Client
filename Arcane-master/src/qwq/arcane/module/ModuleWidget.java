package qwq.arcane.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import qwq.arcane.Client;
import qwq.arcane.event.impl.events.render.Shader2DEvent;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/ModuleWidget.class */
public abstract class ModuleWidget extends Module {

    @SerializedName("x")
    @Expose
    public float x;

    @SerializedName("y")
    @Expose
    public float y;
    protected float renderX;
    protected float renderY;
    public float width;
    public float height;
    public boolean dragging;
    private int dragX;
    private int dragY;
    protected ScaledResolution sr;
    protected static Mine mc = Mine.getMinecraft();
    public static InterFace INTERFACE = (InterFace) Client.Instance.getModuleManager().getModule(InterFace.class);
    public static InterFace setting = (InterFace) Client.Instance.getModuleManager().getModule(InterFace.class);

    public abstract void onShader(Shader2DEvent shader2DEvent);

    public abstract void render();

    public abstract boolean shouldRender();

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setRenderX(float renderX) {
        this.renderX = renderX;
    }

    public void setRenderY(float renderY) {
        this.renderY = renderY;
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

    public void setDragX(int dragX) {
        this.dragX = dragX;
    }

    public void setDragY(int dragY) {
        this.dragY = dragY;
    }

    public void setSr(ScaledResolution sr) {
        this.sr = sr;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getRenderX() {
        return this.renderX;
    }

    public float getRenderY() {
        return this.renderY;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public int getDragX() {
        return this.dragX;
    }

    public int getDragY() {
        return this.dragY;
    }

    public ScaledResolution getSr() {
        return this.sr;
    }

    public ModuleWidget(String name, Category category) {
        super(name, category);
        this.x = 0.0f;
        this.y = 0.0f;
        this.width = 0.0f;
        this.height = 0.0f;
    }

    public void updatePos() {
        this.sr = new ScaledResolution(mc);
        this.renderX = this.x * this.sr.getScaledWidth();
        this.renderY = this.y * this.sr.getScaledHeight();
        if (this.renderX < 0.0f) {
            this.x = 0.0f;
        }
        if (this.renderX > this.sr.getScaledWidth() - this.width) {
            this.x = (this.sr.getScaledWidth() - this.width) / this.sr.getScaledWidth();
        }
        if (this.renderY < 0.0f) {
            this.y = 0.0f;
        }
        if (this.renderY > this.sr.getScaledHeight() - this.height) {
            this.y = (this.sr.getScaledHeight() - this.height) / this.sr.getScaledHeight();
        }
    }

    public final void onChatGUI(int mouseX, int mouseY, boolean drag) {
        boolean hovering = RenderUtil.isHovering(this.renderX, this.renderY, this.width, this.height, mouseX, mouseY);
        Bold.get(16.0f).drawString(this.name, this.renderX, this.renderY - 10.0f, Color.WHITE.getRGB());
        if (this.dragging) {
            RoundedUtil.drawRoundOutline(this.renderX, this.renderY, this.width, this.height, 5.0f, 0.5f, new Color(0, 0, 0, 0), Color.WHITE);
        }
        if (hovering && Mouse.isButtonDown(0) && !this.dragging && drag) {
            this.dragging = true;
            this.dragX = mouseX;
            this.dragY = mouseY;
        }
        if (!Mouse.isButtonDown(0)) {
            this.dragging = false;
        }
        if (this.dragging) {
            float deltaX = (mouseX - this.dragX) / this.sr.getScaledWidth();
            float deltaY = (mouseY - this.dragY) / this.sr.getScaledHeight();
            this.x += deltaX;
            this.y += deltaY;
            this.dragX = mouseX;
            this.dragY = mouseY;
        }
    }
}
