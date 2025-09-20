package com.amaya.utils.render.shader;

import com.amaya.utils.math.MathUtils;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.ShaderUtil;
import com.amaya.utils.render.StencilUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static com.amaya.utils.client.InstanceAccess.mc;
import static net.minecraft.client.renderer.OpenGlHelper.glUniform1;

/**
 * @author cedo
 * @since 05/13/2022
 */
public class GaussianBlur{

    private static final ShaderUtil gaussianBlur = new ShaderUtil("amaya/shader/gaussian.frag");

    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    private static void setupUniforms(float dir1, float dir2, float radius) {
        gaussianBlur.setUniformi("textureIn", 0);
        gaussianBlur.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        gaussianBlur.setUniformf("direction", dir1, dir2);
        gaussianBlur.setUniformf("radius", radius);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtils.calculateGaussianValue(i, radius / 2));
        }

        weightBuffer.rewind();
        glUniform1(gaussianBlur.getUniform("weights"), weightBuffer);
    }

    public static void startBlur() {
        StencilUtils.write(false);
    }

    public static void endBlur(float radius, float compression) {
        StencilUtils.erase(true);

        framebuffer = ShaderElement.createFrameBuffer(framebuffer);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        gaussianBlur.init();
        setupUniforms(compression, 0, radius);

        RenderUtil.bindTexture(mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        gaussianBlur.unload();

        mc.getFramebuffer().bindFramebuffer(false);
        gaussianBlur.init();
        setupUniforms(0, compression, radius);

        RenderUtil.bindTexture(framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        gaussianBlur.unload();

        StencilUtils.dispose();
        RenderUtil.resetColor();
        GlStateManager.bindTexture(0);

    }

}
