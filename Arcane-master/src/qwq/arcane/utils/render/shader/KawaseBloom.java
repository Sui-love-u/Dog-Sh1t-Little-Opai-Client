package qwq.arcane.utils.render.shader;

import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.render.RenderUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/shader/KawaseBloom.class */
public class KawaseBloom {
    public static qwq.arcane.utils.render.ShaderUtils bloomShader = new qwq.arcane.utils.render.ShaderUtils("shadow");
    public static Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);
    public static float prevRadius;

    public static void renderBloom(int sourceTexture, int radius, int offset) {
        bloomFramebuffer = RenderUtil.createFrameBuffer(bloomFramebuffer, false);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        bloomFramebuffer.framebufferClear();
        bloomFramebuffer.bindFramebuffer(true);
        bloomShader.init();
        setupUniforms(radius, offset, 0);
        GL11.glBindTexture(3553, sourceTexture);
        qwq.arcane.utils.render.ShaderUtils.drawQuads();
        bloomShader.unload();
        bloomFramebuffer.unbindFramebuffer();
        Instance.mc.getFramebuffer().bindFramebuffer(true);
        bloomShader.init();
        setupUniforms(radius, 0, offset);
        GL13.glActiveTexture(34000);
        GL11.glBindTexture(3553, sourceTexture);
        GL13.glActiveTexture(33984);
        GL11.glBindTexture(3553, bloomFramebuffer.framebufferTexture);
        qwq.arcane.utils.render.ShaderUtils.drawQuads();
        bloomShader.unload();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();
        GlStateManager.bindTexture(0);
    }

    public static void setupUniforms(int radius, int directionX, int directionY) {
        if (radius != prevRadius) {
            FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
            for (int i = 0; i <= radius; i++) {
                weightBuffer.put(MathUtils.calculateGaussianValue(i, radius));
            }
            weightBuffer.rewind();
            bloomShader.setUniformi("inTexture", 0);
            bloomShader.setUniformi("textureToCheck", 16);
            bloomShader.setUniformf("radius", radius);
            GL20.glUniform1(bloomShader.getUniform("weights"), weightBuffer);
            prevRadius = radius;
        }
        bloomShader.setUniformf("texelSize", 1.0f / Instance.mc.displayWidth, 1.0f / Instance.mc.displayHeight);
        bloomShader.setUniformf("direction", directionX, directionY);
    }
}
