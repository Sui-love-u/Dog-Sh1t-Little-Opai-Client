package qwq.arcane.utils.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.optifine.shaders.config.ShaderOption;
import org.lwjgl.opengl.GL11;
import qwq.arcane.module.Mine;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.math.MathUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/RenderUtil.class */
public class RenderUtil {
    public static final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    private static final Frustum FRUSTUM = new Frustum();
    private static final Map<Integer, Boolean> glCapMap = new HashMap();
    public static double ticks = 0.0d;
    public static long lastFrame = 0;

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static void drawImage(ResourceLocation image, float x, float y, int width, int height) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Instance.mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawImage(ResourceLocation image, double x, double y, double z, double width, double height, int color1, int color2, int color3, int color4) {
        Instance.mc.getTextureManager().bindTexture(image);
        drawImage(x, y, z, width, height, color1, color2, color3, color4);
    }

    public static void drawImage(double x, double y, double z, double width, double height, int color1, int color2, int color3, int color4) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        boolean blend = GL11.glIsEnabled(3042);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        GL11.glShadeModel(7425);
        GL11.glAlphaFunc(516, 0.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos((float) x, (float) (y + height), (float) z).tex(0.0d, 0.9900000095367432d).color(color1).endVertex();
        worldRenderer.pos((float) (x + width), (float) (y + height), (float) z).tex(1.0d, 0.9900000095367432d).color(color2).endVertex();
        worldRenderer.pos((float) (x + width), (float) y, (float) z).tex(1.0d, 0.0d).color(color3).endVertex();
        worldRenderer.pos((float) x, (float) y, (float) z).tex(0.0d, 0.0d).color(color4).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glShadeModel(7424);
        GlStateManager.blendFunc(770, 0);
        if (!blend) {
            GlStateManager.disableBlend();
        }
    }

    public static void scissorStart(double x, double y, double width, double height) {
        GL11.glEnable(3089);
        ScaledResolution sr = new ScaledResolution(Mine.getMinecraft());
        double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        GL11.glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    public static void scissorEnd() {
        GL11.glDisable(3089);
    }

    public static void drawItemStack(ItemStack stack, float x, float y) {
        GL11.glPushMatrix();
        Mine mc = Mine.getMinecraft();
        if (mc.theWorld != null) {
            RenderHelper.enableGUIStandardItemLighting();
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear(256);
        GlStateManager.enableBlend();
        mc.getRenderItem().zLevel = -150.0f;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, (int) x, (int) y);
        mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.enableBlend();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glPopMatrix();
    }

    public static String sessionTime() {
        int elapsedTime = (((int) System.currentTimeMillis()) - Instance.INSTANCE.getStartTime()) / 1000;
        String days = elapsedTime > 86400 ? (elapsedTime / 86400) + "d " : "";
        int elapsedTime2 = !days.isEmpty() ? elapsedTime % 86400 : elapsedTime;
        String hours = elapsedTime2 > 3600 ? (elapsedTime2 / 3600) + "h " : "";
        int elapsedTime3 = !hours.isEmpty() ? elapsedTime2 % 3600 : elapsedTime2;
        String minutes = elapsedTime3 > 60 ? (elapsedTime3 / 60) + "m " : "";
        int elapsedTime4 = !minutes.isEmpty() ? elapsedTime3 % 60 : elapsedTime3;
        String seconds = elapsedTime4 > 0 ? elapsedTime4 + "s" : "";
        return days + hours + minutes + seconds;
    }

    public static void drawBlockBox(BlockPos blockPos, Color color, boolean outline) {
        Instance.mc.getRenderManager();
        Timer timer = Instance.mc.timer;
        double x = blockPos.getX() - RenderManager.renderPosX;
        double y = blockPos.getY() - RenderManager.renderPosY;
        double z = blockPos.getZ() - RenderManager.renderPosZ;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0d, y + 1.0d, z + 1.0d);
        Block block = Instance.mc.theWorld.getBlockState(blockPos).getBlock();
        if (block != null) {
            EntityPlayer player = Instance.mc.thePlayer;
            double posX = player.lastTickPosX + ((player.posX - player.lastTickPosX) * timer.renderPartialTicks);
            double posY = player.lastTickPosY + ((player.posY - player.lastTickPosY) * timer.renderPartialTicks);
            double posZ = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * timer.renderPartialTicks);
            axisAlignedBB = block.getSelectedBoundingBox(Instance.mc.theWorld, blockPos).expand(0.0020000000949949026d, 0.0020000000949949026d, 0.0020000000949949026d).offset(-posX, -posY, -posZ);
        }
        GL11.glBlendFunc(770, 771);
        enableGlCap(3042);
        disableGlCap(3553, 2929);
        GL11.glDepthMask(false);
        glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() != 255 ? color.getAlpha() : outline ? 26 : 35);
        drawFilledBox(axisAlignedBB);
        if (outline) {
            GL11.glLineWidth(1.0f);
            enableGlCap(2848);
            glColor(color.getRGB());
            drawSelectionBoundingBox(axisAlignedBB);
        }
        GlStateManager.resetColor();
        GL11.glDepthMask(true);
        resetCaps();
    }

    public static void disableGlCap(int... caps) {
        for (int cap : caps) {
            setGlCap(cap, false);
        }
    }

    public static void drawFilledBox(AxisAlignedBB axisAlignedBB) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }

    public static void resetCaps() {
        glCapMap.forEach((v0, v1) -> {
            setGlState(v0, v1);
        });
    }

    public static void setGlState(int cap, boolean state) {
        if (state) {
            GL11.glEnable(cap);
        } else {
            GL11.glDisable(cap);
        }
    }

    public static void enableGlCap(int cap) {
        setGlCap(cap, true);
    }

    public static void setGlCap(int cap, boolean state) {
        glCapMap.put(Integer.valueOf(cap), Boolean.valueOf(GL11.glGetBoolean(cap)));
        setGlState(cap, state);
    }

    public static void glColor(int red, int green, int blue, int alpha) {
        GlStateManager.color(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
    }

    public static void renderItemStack(ItemStack stack, double x, double y, float scale) {
        renderItemStack(stack, x, y, scale, false);
    }

    public static void renderItemStack(ItemStack stack, double x, double y, float scale, boolean enchantedText) {
        renderItemStack(stack, x, y, scale, enchantedText, scale);
    }

    public static void renderItemStack(ItemStack stack, double x, double y, float scale, boolean enchantedText, float textScale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        Instance.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (enchantedText) {
            renderEnchantText(stack, 0.0d, 0.0d, textScale);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void drawEnchantTag(String text, double x, double y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GL11.glTranslated(x, y, x);
        GL11.glScaled(scale, scale, scale);
        Instance.mc.fontRendererObj.drawOutlinedString(text, 0.0f, 0.0f, 1.0f, -1, new Color(0, 0, 0, 140).getRGB());
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private static String getColor(int n) {
        if (n != 1) {
            if (n == 2) {
                return ShaderOption.COLOR_GREEN;
            }
            if (n == 3) {
                return "§3";
            }
            if (n == 4) {
                return "§4";
            }
            if (n >= 5) {
                return "§e";
            }
            return "§f";
        }
        return "§f";
    }

    public static void drawExhiRect(float x, float y, float x2, float y2, float alpha) {
        float x22 = x + x2;
        float y22 = y + y2;
        Gui.drawRect(x - 3.5f, y - 3.5f, x22 + 3.5f, y22 + 3.5f, new Color(0.0f, 0.0f, 0.0f, alpha).getRGB());
        Gui.drawRect(x - 3.0f, y - 3.0f, x22 + 3.0f, y22 + 3.0f, new Color(0.19607843f, 0.19607843f, 0.19607843f, alpha).getRGB());
        Gui.drawRect(x - 2.5f, y - 2.5f, x22 + 2.5f, y22 + 2.5f, new Color(0.101960786f, 0.101960786f, 0.101960786f, alpha).getRGB());
        Gui.drawRect(x - 0.5f, y - 0.5f, x22 + 0.5f, y22 + 0.5f, new Color(0.19607843f, 0.19607843f, 0.19607843f, alpha).getRGB());
        Gui.drawRect(x, y, x22, y22, new Color(0.07058824f, 0.07058824f, 0.07058824f, alpha).getRGB());
    }

    public static void drawBorderedRect(float x, float y, float width, float height, float outlineThickness, int rectColor, int outlineColor) {
        drawRect(x, y, width, height, rectColor);
        drawBorder(x, y, width, height, outlineThickness, outlineColor);
    }

    public static void drawBorder(float x, float y, float width, float height, float outlineThickness, int outlineColor) {
        GL11.glEnable(2848);
        color(outlineColor);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GL11.glLineWidth(outlineThickness);
        float cornerValue = (float) (outlineThickness * 0.19d);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y - cornerValue);
        GL11.glVertex2d(x, y + height + cornerValue);
        GL11.glVertex2d(x + width, y + height + cornerValue);
        GL11.glVertex2d(x + width, y - cornerValue);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glDisable(2848);
    }

    public static void drawEntityOnScreen(float yaw, float pitch, EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 50.0f);
        GlStateManager.scale(-50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float renderYawOffset = entityLivingBase.renderYawOffset;
        float rotationYaw = entityLivingBase.rotationYaw;
        float rotationPitch = entityLivingBase.rotationPitch;
        float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) ((-Math.atan(pitch / 40.0f)) * 20.0d), 1.0f, 0.0f, 0.0f);
        entityLivingBase.renderYawOffset = yaw - 0.4f;
        entityLivingBase.rotationYaw = yaw - 0.2f;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager renderManager = Instance.mc.getRenderManager();
        renderManager.setPlayerViewY(180.0f);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, boolean sideways, int startColor, int endColor) {
        double right2 = left + right;
        double bottom2 = top + bottom;
        GL11.glDisable(3553);
        GLUtil.startBlend();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        color(startColor);
        if (sideways) {
            GL11.glVertex2d(left, top);
            GL11.glVertex2d(left, bottom2);
            color(endColor);
            GL11.glVertex2d(right2, bottom2);
            GL11.glVertex2d(right2, top);
        } else {
            GL11.glVertex2d(left, top);
            color(endColor);
            GL11.glVertex2d(left, bottom2);
            GL11.glVertex2d(right2, bottom2);
            color(startColor);
            GL11.glVertex2d(right2, top);
        }
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glShadeModel(7424);
        GLUtil.endBlend();
        GL11.glEnable(3553);
    }

    public static void drawHorizontalGradientSideways(double x, double y, double width, double height, int leftColor, int rightColor) {
        drawGradientRect(x, y, width, height, true, leftColor, rightColor);
    }

    public static void drawVerticalGradientSideways(double x, double y, double width, double height, int topColor, int bottomColor) {
        drawGradientRect(x, y, width, height, false, topColor, bottomColor);
    }

    public static void drawEntityOnScreen(float posX, float posY, float scale, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.color(255.0f, 255.0f, 255.0f);
        GlStateManager.translate(posX, posY, 50.0f);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager rendermanager = Instance.mc.getRenderManager();
        rendermanager.setPlayerViewY(1.0f);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(ent, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void renderItemStack(EntityPlayer target, float x, float y, float scale, boolean enchantedText, float textScale, boolean bg, boolean info) {
        List<ItemStack> items = new ArrayList<>();
        if (target.getHeldItem() != null) {
            items.add(target.getHeldItem());
        }
        for (int index = 3; index >= 0; index--) {
            ItemStack stack = target.inventory.armorInventory[index];
            if (stack != null) {
                items.add(stack);
            }
        }
        float i = x;
        for (ItemStack stack2 : items) {
            if (bg) {
                drawRect(i, y, 16.0f * scale, 16.0f * scale, new Color(0, 0, 0, 150).getRGB());
            }
            if (info) {
                int damage = stack2.getMaxDamage() - stack2.getItemDamage();
                FontManager.Regular.get(8.0f * scale).drawCenteredStringWithShadow(String.valueOf(damage), i + ((16.0f * scale) / 2.0f), (y + 16.0f + 2.0f) * scale, -1);
            }
            renderItemStack(stack2, i, y, scale, enchantedText, textScale);
            i += 16.0f;
        }
    }

    public static void renderItemStack(EntityPlayer target, float x, float y, float scale, boolean bg, boolean info) {
        renderItemStack(target, x, y, scale, false, 0.0f, bg, info);
    }

    public static void renderItemStack(EntityPlayer target, float x, float y, float scale, float textScale) {
        renderItemStack(target, x, y, scale, true, textScale, false, false);
    }

    public static void renderItemStack(EntityPlayer target, float x, float y, float scale) {
        renderItemStack(target, x, y, scale, scale);
    }

    public static void renderEnchantText(ItemStack stack, double x, double y, float scale) {
        RenderHelper.disableStandardItemLighting();
        double height = y;
        if (stack.getItem() instanceof ItemArmor) {
            int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
            int unBreakingLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            int thornLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
            if (protectionLevel > 0) {
                drawEnchantTag("P" + getColor(protectionLevel) + protectionLevel, x, height, scale);
                height += 8.0f * scale;
            }
            if (unBreakingLevel2 > 0) {
                drawEnchantTag("U" + getColor(unBreakingLevel2) + unBreakingLevel2, x, height, scale);
                height += 8.0f * scale;
            }
            if (thornLevel > 0) {
                drawEnchantTag("T" + getColor(thornLevel) + thornLevel, x, height, scale);
                height += 8.0f * scale;
            }
        }
        if (stack.getItem() instanceof ItemBow) {
            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
            int unBreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (powerLevel > 0) {
                drawEnchantTag("Pow" + getColor(powerLevel) + powerLevel, x, height, scale);
                height += 8.0f * scale;
            }
            if (punchLevel > 0) {
                drawEnchantTag("Pun" + getColor(punchLevel) + punchLevel, x, height, scale);
                height += 8.0f * scale;
            }
            if (flameLevel > 0) {
                drawEnchantTag("F" + getColor(flameLevel) + flameLevel, x, height, scale);
                height += 8.0f * scale;
            }
            if (unBreakingLevel > 0) {
                drawEnchantTag("U" + getColor(unBreakingLevel) + unBreakingLevel, x, height, scale);
                height += 8.0f * scale;
            }
        }
        if (stack.getItem() instanceof ItemSword) {
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int knockBackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
            int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            int unBreakingLevel3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (sharpnessLevel > 0) {
                drawEnchantTag("S" + getColor(sharpnessLevel) + sharpnessLevel, x, height, scale);
                height += 8.0f * scale;
            }
            if (knockBackLevel > 0) {
                drawEnchantTag("K" + getColor(knockBackLevel) + knockBackLevel, x, height, scale);
                height += 8.0f * scale;
            }
            if (fireAspectLevel > 0) {
                drawEnchantTag("F" + getColor(fireAspectLevel) + fireAspectLevel, x, height, scale);
                height += 8.0f * scale;
            }
            if (unBreakingLevel3 > 0) {
                drawEnchantTag("U" + getColor(unBreakingLevel3) + unBreakingLevel3, x, height, scale);
                height += 8.0f * scale;
            }
        }
        if (stack.getRarity() == EnumRarity.EPIC) {
            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GL11.glTranslated(x, y, x);
            GL11.glScaled(scale, scale, scale);
            Instance.mc.fontRendererObj.drawOutlinedString("God", (float) x, (float) height, 1.0f, new Color(255, 255, 0).getRGB(), new Color(100, 100, 0, 140).getRGB());
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    public static void drawGoodCircle(double x, double y, float radius, int color) {
        color(color);
        GLUtil.setup2DRendering(() -> {
            GL11.glEnable(2832);
            GL11.glHint(3153, 4354);
            GL11.glPointSize(radius * 2 * Mine.getMinecraft().gameSettings.guiScale);
            GLUtil.render(0, () -> {
                GL11.glVertex2d(x, y);
            });
        });
    }

    public static void bindTexture(int texture) {
        GlStateManager.bindTexture(texture);
    }

    public static void start3D() {
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        GlStateManager.disableCull();
    }

    public static void stop3D() {
        GlStateManager.enableCull();
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }

    public static void renderBoundingBox(AxisAlignedBB aabb, Color color, int alpha) {
        GlStateManager.pushMatrix();
        GLUtil.setup2DRendering();
        GLUtil.enableCaps(3042, 2832, 2881, 2848);
        GL11.glLineWidth(5.0f);
        float actualAlpha = 0.3f * alpha;
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), actualAlpha);
        color(color.getRGB(), actualAlpha);
        RenderGlobal.drawOutlinedBoundingBox(aabb, color.getRed(), color.getGreen(), color.getBlue(), alpha);
        GLUtil.disableCaps();
        GLUtil.end2DRendering();
        GlStateManager.popMatrix();
    }

    public static void renderBoundingBox(EntityLivingBase entityLivingBase, Color color, float alpha) {
        AxisAlignedBB bb = getInterpolatedBoundingBox(entityLivingBase);
        GlStateManager.pushMatrix();
        GLUtil.setup2DRendering();
        GLUtil.enableCaps(3042, 2832, 2881, 2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(true);
        GL11.glLineWidth(1.0f);
        float actualAlpha = 0.5f * alpha;
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), actualAlpha);
        color(color.getRGB(), actualAlpha);
        RenderGlobal.renderCustomBoundingBox(bb, false, true);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GLUtil.disableCaps();
        GLUtil.end2DRendering();
        GlStateManager.popMatrix();
    }

    public static AxisAlignedBB getInterpolatedBoundingBox(Entity entity) {
        double[] renderingEntityPos = getInterpolatedPos(entity);
        double entityRenderWidth = entity.width / 1.5d;
        return new AxisAlignedBB(renderingEntityPos[0] - entityRenderWidth, renderingEntityPos[1], renderingEntityPos[2] - entityRenderWidth, renderingEntityPos[0] + entityRenderWidth, renderingEntityPos[1] + entity.height + (entity.isSneaking() ? -0.3d : 0.18d), renderingEntityPos[2] + entityRenderWidth).expand(0.15d, 0.15d, 0.15d);
    }

    public static double[] getInterpolatedPos(Entity entity) {
        float ticks2 = Instance.mc.timer.renderPartialTicks;
        double dDoubleValue = MathUtils.interpolate(entity.lastTickPosX, entity.posX, ticks2).doubleValue();
        Instance.mc.getRenderManager();
        double dDoubleValue2 = MathUtils.interpolate(entity.lastTickPosY, entity.posY, ticks2).doubleValue();
        Instance.mc.getRenderManager();
        double dDoubleValue3 = MathUtils.interpolate(entity.lastTickPosZ, entity.posZ, ticks2).doubleValue();
        Instance.mc.getRenderManager();
        return new double[]{dDoubleValue - RenderManager.viewerPosX, dDoubleValue2 - RenderManager.viewerPosY, dDoubleValue3 - RenderManager.viewerPosZ};
    }

    public static void color(int color, float alpha) {
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        GlStateManager.color(r, g, b, alpha);
    }

    public static void drawTracerLine(Entity entity, float width, Color color, float alpha) {
        float ticks2 = Instance.mc.timer.renderPartialTicks;
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Instance.mc.entityRenderer.orientCamera(ticks2);
        double[] pos = getInterpolatedPos(entity);
        GL11.glDisable(2929);
        GLUtil.setup2DRendering();
        double yPos = pos[1] + (entity.height / 2.0f);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(3);
        color(color.getRGB(), alpha);
        GL11.glVertex3d(pos[0], yPos, pos[2]);
        GL11.glVertex3d(0.0d, Instance.mc.thePlayer.getEyeHeight(), 0.0d);
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GLUtil.end2DRendering();
        GL11.glPopMatrix();
    }

    public static void renderBreadCrumbs(Iterable<Vec3> vec3s) {
        GlStateManager.disableDepth();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        int i = 0;
        try {
            for (Vec3 v : vec3s) {
                i++;
                boolean draw = true;
                double d = v.xCoord;
                Instance.mc.getRenderManager();
                double x = d - RenderManager.renderPosX;
                double d2 = v.yCoord;
                Instance.mc.getRenderManager();
                double y = d2 - RenderManager.renderPosY;
                double d3 = v.zCoord;
                Instance.mc.getRenderManager();
                double z = d3 - RenderManager.renderPosZ;
                double distanceFromPlayer = Instance.mc.thePlayer.getDistance(v.xCoord, v.yCoord - 1.0d, v.zCoord);
                int quality = (int) ((distanceFromPlayer * 4.0d) + 10.0d);
                if (quality > 350) {
                    quality = 350;
                }
                if (i % 10 != 0 && distanceFromPlayer > 25.0d) {
                    draw = false;
                }
                if (i % 3 == 0 && distanceFromPlayer > 15.0d) {
                    draw = false;
                }
                if (draw) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y, z);
                    GL11.glScalef(-0.04f, -0.04f, -0.04f);
                    GL11.glRotated(-Instance.mc.getRenderManager().playerViewY, 0.0d, 1.0d, 0.0d);
                    GL11.glRotated(Instance.mc.getRenderManager().playerViewX, 1.0d, 0.0d, 0.0d);
                    Color c = new Color(InterFace.color(0).getRGB());
                    drawFilledCircleNoGL(0.0f, 0.0f, 0.7d, c.hashCode(), quality);
                    if (distanceFromPlayer < 4.0d) {
                        drawFilledCircleNoGL(0.0f, 0.0f, 1.4d, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);
                    }
                    if (distanceFromPlayer < 20.0d) {
                        drawFilledCircleNoGL(0.0f, 0.0f, 2.3d, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);
                    }
                    GL11.glScalef(0.8f, 0.8f, 0.8f);
                    GL11.glPopMatrix();
                }
            }
        } catch (ConcurrentModificationException e) {
        }
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GlStateManager.enableDepth();
        GL11.glColor3d(255.0d, 255.0d, 255.0d);
    }

    public static void drawFilledCircleNoGL(float x, float y, double r, int c, int quality) {
        float f = ((c >> 24) & 255) / 255.0f;
        float f1 = ((c >> 16) & 255) / 255.0f;
        float f2 = ((c >> 8) & 255) / 255.0f;
        float f3 = (c & 255) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(6);
        for (int i = 0; i <= 360 / quality; i++) {
            double x2 = Math.sin(((i * quality) * 3.141592653589793d) / 180.0d) * r;
            double y2 = Math.cos(((i * quality) * 3.141592653589793d) / 180.0d) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }
        GL11.glEnd();
    }

    public static void enableRender3D(boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
    }

    public static void disableRender3D(boolean enableDepth) {
        if (enableDepth) {
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static boolean isInViewFrustum(Entity entity) {
        return isInViewFrustum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    private static boolean isInViewFrustum(AxisAlignedBB bb) {
        Entity current = Instance.mc.getRenderViewEntity();
        FRUSTUM.setPosition(current.posX, current.posY, current.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(bb);
    }

    public static void renderBlock(BlockPos blockPos, int color, boolean outline, boolean shade) {
        renderBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0d, 1.0d, 1.0d, color, outline, shade);
    }

    public static void renderBox(int x, int y, int z, double x2, double y2, double z2, int color, boolean outline, boolean shade) {
        Instance.mc.getRenderManager();
        double xPos = x - RenderManager.viewerPosX;
        Instance.mc.getRenderManager();
        double yPos = y - RenderManager.viewerPosY;
        Instance.mc.getRenderManager();
        double zPos = z - RenderManager.viewerPosZ;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(xPos, yPos, zPos, xPos + x2, yPos + y2, zPos + z2);
        drawAxisAlignedBB(axisAlignedBB, shade, outline, color);
    }

    public static void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB, boolean filled, boolean outline, int color) {
        drawSelectionBoundingBox(axisAlignedBB, outline, filled, color);
    }

    public static void drawOutlineBoundingBox(AxisAlignedBB bb, Color color) {
        RenderGlobal.drawOutlinedBoundingBox(bb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(1, DefaultVertexFormats.POSITION);
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB bb, boolean outline, boolean filled, int color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glLineWidth(2.0f);
        GlStateManager.disableTexture2D();
        GL11.glDisable(2929);
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        if (outline) {
            GL11.glEnable(2848);
            drawOutlineBoundingBox(bb, new Color(color, true));
            GL11.glDisable(2848);
        }
        if (filled) {
            drawFilledBoundingBox(bb, new Color(color, true));
        }
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GL11.glEnable(2929);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawFilledBoundingBox(AxisAlignedBB bb, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
    }

    public static int darker(int color) {
        return darker(color, 0.6f);
    }

    public static double interpolate(double old, double now, float partialTicks) {
        return old + ((now - old) * partialTicks);
    }

    public static float interpolate(float old, float now, float partialTicks) {
        return old + ((now - old) * partialTicks);
    }

    public static int getColorFromPercentage(float percentage) {
        return Color.HSBtoRGB(Math.min(1.0f, Math.max(0.0f, percentage)) / 3.0f, 0.9f, 0.9f);
    }

    public static int fadeBetween(int startColor, int endColor) {
        return fadeBetween(startColor, endColor, (System.currentTimeMillis() % 2000) / 1000.0f);
    }

    public static int fadeBetween(int startColor, int endColor, float progress) {
        if (progress > 1.0f) {
            progress = 1.0f - (progress % 1.0f);
        }
        return fadeTo(startColor, endColor, progress);
    }

    public static int darker(int color, float factor) {
        int r = (int) (((color >> 16) & 255) * factor);
        int g = (int) (((color >> 8) & 255) * factor);
        int b = (int) ((color & 255) * factor);
        int a = (color >> 24) & 255;
        return ((r & 255) << 16) | ((g & 255) << 8) | (b & 255) | ((a & 255) << 24);
    }

    public static boolean isBBInFrustum(AxisAlignedBB aabb) {
        EntityPlayerSP player = Instance.mc.thePlayer;
        FRUSTUM.setPosition(player.posX, player.posY, player.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(aabb);
    }

    public static int fadeTo(int startColor, int endColor, float progress) {
        float invert = 1.0f - progress;
        int r = (int) ((((startColor >> 16) & 255) * invert) + (((endColor >> 16) & 255) * progress));
        int g = (int) ((((startColor >> 8) & 255) * invert) + (((endColor >> 8) & 255) * progress));
        int b = (int) (((startColor & 255) * invert) + ((endColor & 255) * progress));
        int a = (int) ((((startColor >> 24) & 255) * invert) + (((endColor >> 24) & 255) * progress));
        return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

    public static double progressiveAnimation(double now, double desired, double speed) {
        double dif = Math.abs(now - desired);
        int fps = Mine.getDebugFPS();
        if (dif > 0.0d) {
            double animationSpeed = MathUtils.roundToDecimalPlace(Math.min(10.0d, Math.max(0.05d, (144.0d / fps) * (dif / 10.0d) * speed)), 0.05d);
            if (dif != 0.0d && dif < animationSpeed) {
                animationSpeed = dif;
            }
            if (now < desired) {
                return now + animationSpeed;
            }
            if (now > desired) {
                return now - animationSpeed;
            }
        }
        return now;
    }

    public static double linearAnimation(double now, double desired, double speed) {
        double dif = Math.abs(now - desired);
        int fps = Mine.getDebugFPS();
        if (dif > 0.0d) {
            double animationSpeed = MathUtils.roundToDecimalPlace(Math.min(10.0d, Math.max(0.005d, (144.0d / fps) * speed)), 0.005d);
            if (dif != 0.0d && dif < animationSpeed) {
                animationSpeed = dif;
            }
            if (now < desired) {
                return now + animationSpeed;
            }
            if (now > desired) {
                return now - animationSpeed;
            }
        }
        return now;
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(Instance.mc.displayWidth, Instance.mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        float f = ((color >> 24) & 255) / 255.0f;
        float f1 = ((color >> 16) & 255) / 255.0f;
        float f2 = ((color >> 8) & 255) / 255.0f;
        float f3 = (color & 255) / 255.0f;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5d, 0.5d, 0.5d);
        float x2 = x * 2.0f;
        float y2 = y * 2.0f;
        float x1 = (x + width) * 2.0f;
        float y1 = (y + height) * 2.0f;
        GL11.glDisable(3553);
        GL11.glColor4f(f1, f2, f3, f);
        GlStateManager.enableBlend();
        GL11.glEnable(2848);
        GL11.glBegin(9);
        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x2 + radius + (MathHelper.sin((float) (i * 0.017453292519943295d)) * radius * (-1.0f)), y2 + radius + (MathHelper.cos((float) (i * 0.017453292519943295d)) * radius * (-1.0f)));
        }
        for (int i2 = 90; i2 <= 180; i2 += 3) {
            GL11.glVertex2d(x2 + radius + (MathHelper.sin((float) (i2 * 0.017453292519943295d)) * radius * (-1.0f)), (y1 - radius) + (MathHelper.cos((float) (i2 * 0.017453292519943295d)) * radius * (-1.0f)));
        }
        for (int i3 = 0; i3 <= 90; i3 += 3) {
            GL11.glVertex2d((x1 - radius) + (MathHelper.sin((float) (i3 * 0.017453292519943295d)) * radius), (y1 - radius) + (MathHelper.cos((float) (i3 * 0.017453292519943295d)) * radius));
        }
        for (int i4 = 90; i4 <= 180; i4 += 3) {
            GL11.glVertex2d((x1 - radius) + (MathHelper.sin((float) (i4 * 0.017453292519943295d)) * radius), y2 + radius + (MathHelper.cos((float) (i4 * 0.017453292519943295d)) * radius));
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glScaled(2.0d, 2.0d, 2.0d);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void color(int color) {
        float f = ((color >> 24) & 255) / 255.0f;
        float f1 = ((color >> 16) & 255) / 255.0f;
        float f2 = ((color >> 8) & 255) / 255.0f;
        float f3 = (color & 255) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
    }

    public static void renderPlayer2D(EntityLivingBase abstractClientPlayer, float x, float y, float size, float radius, int color) {
        if (abstractClientPlayer instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer) abstractClientPlayer;
            StencilUtils.initStencilToWrite();
            drawRoundedRect(x, y, size, size, radius, -1);
            StencilUtils.readStencilBuffer(1);
            color(color);
            GLUtil.startBlend();
            Instance.mc.getTextureManager().bindTexture(player.getLocationSkin());
            Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, size, size, 64.0f, 64.0f);
            GLUtil.endBlend();
            StencilUtils.uninitStencilBuffer();
        }
    }

    public static void stopScissor() {
        GL11.glDisable(3089);
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return (framebuffer != null && framebuffer.framebufferWidth == Instance.mc.displayWidth && framebuffer.framebufferHeight == Instance.mc.displayHeight) ? false : true;
    }

    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return ((float) mouseX) >= x && ((float) mouseY) >= y && ((float) mouseX) < x + width && ((float) mouseY) < y + height;
    }

    public static String stripColor(String input) {
        return COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static void drawGlow(double x, double y, double z, Color color, float radius, int segments) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glTranslated(x, y, z);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glBegin(6);
        GL11.glVertex3d(0.0d, 0.0d, 0.0d);
        for (int i = 0; i <= segments; i++) {
            double angle = (6.283185307179586d * i) / segments;
            GL11.glVertex3d(Math.cos(angle) * radius, Math.sin(angle) * radius, 0.0d);
        }
        GL11.glEnd();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawTracer(double x, double y, double z, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glLineWidth(1.5f);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glBegin(1);
        GL11.glVertex3d(0.0d, Instance.mc.thePlayer.getEyeHeight(), 0.0d);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static double deltaTime() {
        if (Mine.getDebugFPS() > 0) {
            return 1.0d / Mine.getDebugFPS();
        }
        return 1.0d;
    }

    public static float animate(float end, float start, float multiple) {
        return ((1.0f - MathHelper.clamp_float((float) (deltaTime() * multiple), 0.0f, 1.0f)) * end) + (MathHelper.clamp_float((float) (deltaTime() * multiple), 0.0f, 1.0f) * start);
    }

    public static double animate(double value, double target) {
        return animate(value, target, 1.0d, false);
    }

    public static double animate(double value, double target, double speed, boolean minedelta) {
        double c = value + ((target - value) / (3.0d + (speed * deltaTime())));
        double v = value + ((target - value) / (2.0d + speed));
        return minedelta ? v : c;
    }

    public static void scaleEnd() {
        GlStateManager.popMatrix();
    }

    public static void scaleStart(float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0f);
        GlStateManager.scale(scale, scale, 1.0f);
        GlStateManager.translate(-x, -y, 0.0f);
    }

    public static void drawRect(float left, float top, float width, float height, Color color) {
        drawRect(left, top, width, height, color.getRGB());
    }

    public static void drawRect(float left, float top, float width, float height, int color) {
        float right = left + width;
        float bottom = top + height;
        if (left < right) {
            left = right;
            right = left;
        }
        if (top < bottom) {
            top = bottom;
            bottom = top;
        }
        float f3 = ((color >> 24) & 255) / 255.0f;
        float f = ((color >> 16) & 255) / 255.0f;
        float f1 = ((color >> 8) & 255) / 255.0f;
        float f2 = (color & 255) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0d).endVertex();
        worldrenderer.pos(right, bottom, 0.0d).endVertex();
        worldrenderer.pos(right, top, 0.0d).endVertex();
        worldrenderer.pos(left, top, 0.0d).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


    public static void drawLoadingCircle2(final float x, final float y, final float size,final int width,int color) {
        for (int i = 0; i < 2; ++i) {
            final int rot = (int)(System.nanoTime() / 2000000L * i % 360L);
            drawCircle(x, y, (float)(i * (size + 8)), rot - width, rot,color);
        }
    }

    public static void drawCircle(final float x, final float y, final float radius, final int start, final int end,int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        glColor(color);
        GL11.glEnable(2848);
        GL11.glLineWidth(3.0f);
        GL11.glBegin(3);
        for (float i = (float)end; i >= start; i -= 4.0f) {
            GL11.glVertex2f((float)(x + Math.cos(i * 3.141592653589793 / 180.0) * (radius * 1.001f)), (float)(y + Math.sin(i * 3.141592653589793 / 180.0) * (radius * 1.001f)));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircle(Entity entity, float partialTicks, double rad, int color, float alpha) {
        ticks += 0.004d * (System.currentTimeMillis() - lastFrame);
        lastFrame = System.currentTimeMillis();
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glShadeModel(7425);
        GlStateManager.disableCull();
        double dDoubleValue = MathUtils.interpolate(entity.lastTickPosX, entity.posX, Instance.mc.timer.renderPartialTicks).doubleValue();
        Instance.mc.getRenderManager();
        double x = dDoubleValue - RenderManager.renderPosX;
        double dDoubleValue2 = MathUtils.interpolate(entity.lastTickPosY, entity.posY, Instance.mc.timer.renderPartialTicks).doubleValue();
        Instance.mc.getRenderManager();
        double y = (dDoubleValue2 - RenderManager.renderPosY) + Math.sin(ticks) + 1.0d;
        double dDoubleValue3 = MathUtils.interpolate(entity.lastTickPosZ, entity.posZ, Instance.mc.timer.renderPartialTicks).doubleValue();
        Instance.mc.getRenderManager();
        double z = dDoubleValue3 - RenderManager.renderPosZ;
        GL11.glBegin(5);
        float f = 0.0f;
        while (true) {
            float i = f;
            if (i >= 6.283185307179586d) {
                break;
            }
            double vecX = x + (rad * Math.cos(i));
            double vecZ = z + (rad * Math.sin(i));
            color(color, 0.0f);
            GL11.glVertex3d(vecX, y - (Math.sin(ticks + 1.0d) / 2.700000047683716d), vecZ);
            color(color, 0.52f * alpha);
            GL11.glVertex3d(vecX, y, vecZ);
            f = (float) (i + 0.09817477042468103d);
        }
        GL11.glEnd();
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.5f);
        GL11.glBegin(3);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        color(color, 0.5f * alpha);
        for (int i2 = 0; i2 <= 180; i2++) {
            GL11.glVertex3d(x - (Math.sin((i2 * MathHelper.PI2) / 90.0f) * rad), y, z + (Math.cos((i2 * MathHelper.PI2) / 90.0f) * rad));
        }
        GL11.glEnd();
        GL11.glShadeModel(7424);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawCircle(float x, float y, float start, float end, float radius, float width, boolean filled, int color) {
        if (start > end) {
            end = start;
            start = end;
        }
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(3);
        float f = end;
        while (true) {
            float i = f;
            if (i < start) {
                break;
            }
            setColor(color);
            float cos = MathHelper.cos((float) ((i * 3.141592653589793d) / 180.0d)) * radius;
            float sin = MathHelper.sin((float) ((i * 3.141592653589793d) / 180.0d)) * radius;
            GL11.glVertex2f(x + cos, y + sin);
            f = i - 1.0f;
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        if (filled) {
            GL11.glBegin(6);
            float f2 = end;
            while (true) {
                float i2 = f2;
                if (i2 < start) {
                    break;
                }
                setColor(color);
                float cos2 = MathHelper.cos((float) ((i2 * 3.141592653589793d) / 180.0d)) * radius;
                float sin2 = MathHelper.sin((float) ((i2 * 3.141592653589793d) / 180.0d)) * radius;
                GL11.glVertex2f(x + cos2, y + sin2);
                f2 = i2 - 1.0f;
            }
            GL11.glEnd();
        }
        GL11.glEnable(3553);
        GlStateManager.disableBlend();
        resetColor();
    }

    public static void setColor(int color) {
        GL11.glColor4ub((byte) ((color >> 16) & 255), (byte) ((color >> 8) & 255), (byte) (color & 255), (byte) ((color >> 24) & 255));
    }

    public static void resetColor2() {
        color(1.0d, 1.0d, 1.0d, 1.0d);
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static int colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed) {
        return colorSwitch(firstColor, secondColor, time, index, timePerIndex, speed, 255.0d);
    }

    public static int getRainbow(long currentMillis, int speed, int offset) {
        return getRainbow(currentMillis, speed, offset, 1.0f);
    }

    public static int getRainbow(long currentMillis, int speed, int offset, float alpha) {
        int rainbow = Color.HSBtoRGB(1.0f - (((currentMillis + (offset * 100)) % speed) / speed), 0.9f, 0.9f);
        int r = (rainbow >> 16) & 255;
        int g = (rainbow >> 8) & 255;
        int b = rainbow & 255;
        int a = (int) (alpha * 255.0f);
        return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

    public static void drawCircleCGUI(double x, double y, float radius, int color) {
        if (radius == 0.0f) {
            return;
        }
        float correctRadius = radius * 2.0f;
        setup2DRendering(() -> {
            glColor(color);
            GL11.glEnable(2832);
            GL11.glHint(3153, 4354);
            GL11.glPointSize(correctRadius);
            GLUtil.setupRendering(0, () -> {
                GL11.glVertex2d(x, y);
            });
            GL11.glDisable(2832);
            GlStateManager.resetColor();
        });
    }

    public static void setup2DRendering(Runnable f) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        f.run();
        GL11.glEnable(3553);
        GlStateManager.disableBlend();
    }

    public static void glColor(int hex) {
        float alpha = ((hex >> 24) & 255) / 255.0f;
        float red = ((hex >> 16) & 255) / 255.0f;
        float green = ((hex >> 8) & 255) / 255.0f;
        float blue = (hex & 255) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static int colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed, double alpha) {
        long now = (long) ((speed * System.currentTimeMillis()) + (index * timePerIndex));
        float redDiff = (firstColor.getRed() - secondColor.getRed()) / time;
        float greenDiff = (firstColor.getGreen() - secondColor.getGreen()) / time;
        float blueDiff = (firstColor.getBlue() - secondColor.getBlue()) / time;
        int red = Math.round(secondColor.getRed() + (redDiff * (now % ((long) time))));
        int green = Math.round(secondColor.getGreen() + (greenDiff * (now % ((long) time))));
        int blue = Math.round(secondColor.getBlue() + (blueDiff * (now % ((long) time))));
        float redInverseDiff = (secondColor.getRed() - firstColor.getRed()) / time;
        float greenInverseDiff = (secondColor.getGreen() - firstColor.getGreen()) / time;
        float blueInverseDiff = (secondColor.getBlue() - firstColor.getBlue()) / time;
        int inverseRed = Math.round(firstColor.getRed() + (redInverseDiff * (now % ((long) time))));
        int inverseGreen = Math.round(firstColor.getGreen() + (greenInverseDiff * (now % ((long) time))));
        int inverseBlue = Math.round(firstColor.getBlue() + (blueInverseDiff * (now % ((long) time))));
        if (now % (((long) time) * 2) < ((long) time)) {
            return ColorUtil.getColor(inverseRed, inverseGreen, inverseBlue, (int) alpha);
        }
        return ColorUtil.getColor(red, green, blue, (int) alpha);
    }

    public static void startGlScissor(int x, int y, int width, int height) {
        Mine mc = Mine.getMinecraft();
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            scaleFactor++;
        }
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        GL11.glScissor(x * scaleFactor, mc.displayHeight - ((y + height) * scaleFactor), width * scaleFactor, height * scaleFactor);
    }

    public static void stopGlScissor() {
        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    public static void scissor(double x, double y, double width, double height) {
        int scaleFactor = 1;
        while (scaleFactor < 2 && Instance.mc.displayWidth / (scaleFactor + 1) >= 320 && Instance.mc.displayHeight / (scaleFactor + 1) >= 240) {
            scaleFactor++;
        }
        GL11.glScissor((int) (x * scaleFactor), (int) (Mine.getMinecraft().displayHeight - ((y + height) * scaleFactor)), (int) (width * scaleFactor), (int) (height * scaleFactor));
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, (float) (limit * 0.01d));
    }

    public static void resetColor() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
