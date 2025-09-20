package qwq.arcane.utils.render.shader.impl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.render.GLUtil;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.shader.ShaderUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/shader/impl/Bloom.class */
public class Bloom implements Instance {
    private static int currentIterations;
    public static ShaderUtils kawaseDown = new ShaderUtils("kawaseDownBloom");
    public static ShaderUtils kawaseUp = new ShaderUtils("kawaseUpBloom");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private static final List<Framebuffer> framebufferList = new ArrayList();

    private static void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer2 : framebufferList) {
            framebuffer2.deleteFramebuffer();
        }
        framebufferList.clear();
        List<Framebuffer> list = framebufferList;
        Framebuffer framebufferCreateFrameBuffer = RenderUtil.createFrameBuffer(null, false);
        framebuffer = framebufferCreateFrameBuffer;
        list.add(framebufferCreateFrameBuffer);
        for (int i = 1; i <= iterations; i++) {
            Framebuffer currentBuffer = new Framebuffer((int) (mc.displayWidth / Math.pow(2.0d, i)), (int) (mc.displayHeight / Math.pow(2.0d, i)), false);
            currentBuffer.setFramebufferFilter(9729);
            GlStateManager.bindTexture(currentBuffer.framebufferTexture);
            GL11.glTexParameteri(3553, 10242, 33648);
            GL11.glTexParameteri(3553, 10243, 33648);
            GlStateManager.bindTexture(0);
            framebufferList.add(currentBuffer);
        }
    }

    public static void renderBlur(int framebufferTexture, int iterations, int offset) {
        if (currentIterations != iterations || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            initFramebuffers(iterations);
            currentIterations = iterations;
        }
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(1, 1);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        float currentOffset = offset;
        renderFBO(framebufferList.get(1), framebufferTexture, kawaseDown, currentOffset);
        for (int i = 1; i < iterations; i++) {
            float currentOffset2 = offset / ((float) Math.pow(1.5d, i));
            renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, currentOffset2);
        }
        for (int i2 = iterations; i2 > 1; i2--) {
            float currentOffset3 = offset / ((float) Math.pow(1.5d, i2 - 1));
            renderFBO(framebufferList.get(i2 - 1), framebufferList.get(i2).framebufferTexture, kawaseUp, currentOffset3);
        }
        Framebuffer lastBuffer = framebufferList.get(0);
        lastBuffer.framebufferClear();
        lastBuffer.bindFramebuffer(false);
        kawaseUp.init();
        kawaseUp.setUniformf("offset", offset, offset);
        kawaseUp.setUniformi("inTexture", 0);
        kawaseUp.setUniformi("check", 1);
        kawaseUp.setUniformi("textureToCheck", 16);
        kawaseUp.setUniformf("halfpixel", 1.0f / lastBuffer.framebufferWidth, 1.0f / lastBuffer.framebufferHeight);
        kawaseUp.setUniformf("iResolution", lastBuffer.framebufferWidth, lastBuffer.framebufferHeight);
        GlStateManager.setActiveTexture(34000);
        RenderUtil.bindTexture(framebufferTexture);
        GlStateManager.setActiveTexture(33984);
        RenderUtil.bindTexture(framebufferList.get(1).framebufferTexture);
        ShaderUtils.drawQuads();
        kawaseUp.unload();
        GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
        mc.getFramebuffer().bindFramebuffer(false);
        RenderUtil.bindTexture(framebufferList.get(0).framebufferTexture);
        RenderUtil.setAlphaLimit(0.0f);
        GLUtil.startBlend();
        ShaderUtils.drawQuads();
        GlStateManager.bindTexture(0);
        RenderUtil.setAlphaLimit(0.0f);
        GLUtil.startBlend();
    }

    private static void renderFBO(Framebuffer framebuffer2, int framebufferTexture, ShaderUtils shader, float offset) {
        framebuffer2.framebufferClear();
        framebuffer2.bindFramebuffer(false);
        shader.init();
        RenderUtil.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        shader.setUniformi("check", 0);
        shader.setUniformf("halfpixel", 1.0f / framebuffer2.framebufferWidth, 1.0f / framebuffer2.framebufferHeight);
        shader.setUniformf("iResolution", framebuffer2.framebufferWidth, framebuffer2.framebufferHeight);
        ShaderUtils.drawQuads();
        shader.unload();
    }
}
