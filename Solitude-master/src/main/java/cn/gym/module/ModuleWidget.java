package cn.gym.module;

import cn.gym.Solitude;
import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 14:41
 */
@Getter
@Setter
public abstract class ModuleWidget extends Module {
    protected static Minecraft mc = Minecraft.getMinecraft();
    
    @Expose
    @SerializedName("x")
    public float x;
    
    @Expose
    @SerializedName("y")
    public float y;
    
    protected float renderX, renderY;
    public float width;
    public float height;
    public boolean dragging;
    private int dragX, dragY;
    protected ScaledResolution sr;
    public static Interface INTERFACE = Solitude.Instance.getModuleManager().getModule(Interface.class);

    public ModuleWidget(String name, Category category) {
        super(name, category);
        this.x = 0f;
        this.y = 0f;
        this.width = 100f;
        this.height = 100f;
    }

    public abstract void onShader(Shader2DEvent event);

    public abstract void render();

    public abstract boolean shouldRender();

    public void updatePos() {
        sr = new ScaledResolution(mc);

        renderX = x * sr.getScaledWidth();
        renderY = y * sr.getScaledHeight();

        }
    public final void onChatGUI(int mouseX, int mouseY, boolean drag) {
        boolean hovering = RenderUtil.isHovering(renderX, renderY, width, height, mouseX, mouseY);

        if (dragging) {
            RoundedUtil.drawRoundOutline(renderX, renderY, width, height, 5f, 0.5f, new Color(0, 0, 0, 0), Color.WHITE);
        }

        if (hovering && Mouse.isButtonDown(0) && !dragging && drag) {
            dragging = true;
            dragX = mouseX;
            dragY = mouseY;
        }

        if (!Mouse.isButtonDown(0)) dragging = false;

        if (dragging) {
            float deltaX = (float) (mouseX - dragX) / sr.getScaledWidth();
            float deltaY = (float) (mouseY - dragY) / sr.getScaledHeight();

            x += deltaX;
            y += deltaY;

            dragX = mouseX;
            dragY = mouseY;
        }
    }
}