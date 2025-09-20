package cn.gym.module.impl.display;

import cn.gym.events.impl.render.Render2DEvent;
import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.gui.notification.IslandRender;
import cn.gym.module.Category;
import cn.gym.module.ModuleWidget;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 13:38
 */
public class IsLand extends ModuleWidget {
    public IsLand() {
        super("IsLand",Category.Display);
    }

    @Override
    public void onShader(Shader2DEvent event) {
        IslandRender.INSTANCE.rendershader(new ScaledResolution(mc));
    }

    @Override
    public void render() {
        IslandRender.INSTANCE.render(new ScaledResolution(mc));
    }

    @Override
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
