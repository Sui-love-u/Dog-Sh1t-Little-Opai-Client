package cn.gym.module.impl.display;

import cn.gym.Solitude;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.render.Render2DEvent;
import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.gui.notification.NotificationManager;
import cn.gym.module.Category;
import cn.gym.module.ModuleWidget;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.value.impl.ModeValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 13:38
 */
public class Notification extends ModuleWidget {
    public ModeValue modeValue = new ModeValue("Mode", "Normal",new String[]{"Normal","Custom","Solitude"});

    public Notification() {
        super("Notification",Category.Display);
    }

    @Override
    public void onShader(Shader2DEvent event) {
        switch (modeValue.getValue()) {
            case "Custom":
                Solitude.Instance.getNotification().customshader(sr.getScaledHeight() - 6);
                break;
            case "Normal":
                Solitude.Instance.getNotification().shader(sr.getScaledHeight() - 6);
                break;
        }
    }

    @EventTarget
    public void onRender(Render2DEvent event) {
        switch (modeValue.getValue()) {
            case "Custom":
                Solitude.Instance.getNotification().custom(sr.getScaledHeight() - 6);
                break;
            case "Normal":
                Solitude.Instance.getNotification().render(sr.getScaledHeight() - 6);
                break;
        }
    }

    @Override
    public void render() {
    }

    @Override
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
