package com.amaya.module.impl.render;

import com.amaya.events.EventManager;
import com.amaya.events.impl.render.Shader2DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.shader.impl.Bloom;
import com.amaya.utils.render.shader.impl.Blur;
import com.amaya.utils.render.shader.impl.Shadow;
import net.minecraft.client.shader.Framebuffer;

/**
 * @Author: Guyuemang
 */
@ModuleInfo(name = "PostProcessing",category = Category.Render)
public class PostProcessing extends Module {
    public static final BooleanSetting blur = new BooleanSetting("Blur", true);
    public static final NumberSetting blurRadius = new NumberSetting("Blur Radius", blur::get, 8.0, 1.0, 50.0, 1.0);
    public static final NumberSetting blurCompression = new NumberSetting("Blur Compression", blur::get,2.0, 1.0, 50.0, 1.0);
    public static final BooleanSetting shadow = new BooleanSetting("Shadow", true);
    public static final NumberSetting shadowRadius = new NumberSetting("Shadow Radius", shadow::get,10.0, 1.0, 20.0, 1.0);
    public static final NumberSetting shadowOffset = new NumberSetting("Shadow Offset", shadow::get,1.0, 1.0, 15.0, 1.0);
    public static final BooleanSetting bloom = new BooleanSetting("Bloom", false);
    public static final NumberSetting glowRadius = new NumberSetting("Bloom Radius", bloom::get, 3.0, 1.0, 10.0, 1.0);
    public static final NumberSetting glowOffset = new NumberSetting("Bloom Offset", bloom::get ,1.0, 1.0, 10.0, 1.0);
    public static Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    public void renderShaders() {
        if (!this.getState()) return;

        if (this.blur.get()) {
            Blur.startBlur();
            EventManager.call(new Shader2DEvent(Shader2DEvent.ShaderType.BLUR));
            Blur.endBlur(blurRadius.getValue().floatValue(), blurCompression.getValue().floatValue());
        }

        if (bloom.get()) {
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            EventManager.call(new Shader2DEvent(Shader2DEvent.ShaderType.GLOW));
            stencilFramebuffer.unbindFramebuffer();

            Bloom.renderBlur(stencilFramebuffer.framebufferTexture, (int) glowRadius.get().floatValue(), (int) glowOffset.get().floatValue());
        }

        if (shadow.get()) {
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer, true);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(true);
            EventManager.call(new Shader2DEvent(Shader2DEvent.ShaderType.SHADOW));
            stencilFramebuffer.unbindFramebuffer();

            Shadow.renderBloom(stencilFramebuffer.framebufferTexture, (int) shadowRadius.get().floatValue(), (int) shadowOffset.get().floatValue());
        }
    }

}