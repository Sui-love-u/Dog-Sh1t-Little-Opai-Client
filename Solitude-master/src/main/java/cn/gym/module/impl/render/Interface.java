package cn.gym.module.impl.render;

import cn.gym.Solitude;
import cn.gym.events.EventManager;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.TickEvent;
import cn.gym.events.impl.render.Render2DEvent;
import cn.gym.events.impl.render.Shader2DEvent;
import cn.gym.gui.clickgui.dropdown.DropDownClickGui;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.combat.KillAura;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.utils.render.shader.impl.Bloom;
import cn.gym.utils.render.shader.impl.Blur;
import cn.gym.utils.render.shader.impl.Shadow;
import cn.gym.value.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 17:14
 */
public class Interface extends Module {
    public Interface() {
        super("InterFace",Category.Render);
    }
    public static TextValue name = new TextValue("ClientName","Solitude");
    public static ColorValue FirstColor = new ColorValue("FirstColor", new Color(89, 139, 184));
    public static ColorValue SecondColor = new ColorValue("SecondColor", new Color(221, 228, 255));
    public static final ModeValue color = new ModeValue("Color Setting", "Custom", new String[]{"Custom", "Rainbow", "Dynamic", "Fade","Astolfo","Tenacity"});
    public final NumberValue fadeSpeed = new NumberValue("Fade Speed",() -> color.is("Dynamic") || color.is("Fade"), 1, 1, 10, 1);
    public static BooleanValue watermark = new BooleanValue("WaterMark",false);
    public static final ModeValue watermarkmode = new ModeValue("WatermarkMode",()->watermark.get(),"Logo",new String[]{"Exhi","Logo"});
    public static BooleanValue info = new BooleanValue("Info",false);
    public static final ModeValue infomode = new ModeValue("InfoMode",()->info.get(),"Exhi",new String[]{"Exhi"});
    public static BooleanValue hotbar = new BooleanValue("Hotbar",false);
    public static NumberValue radius = new NumberValue("radius",6,0,8,1);
    public static BooleanValue renderBossHealth = new BooleanValue("BossHealth",false);
    private final DecimalFormat bpsFormat = new DecimalFormat("0.00");
    private final DecimalFormat xyzFormat = new DecimalFormat("0");
    public static final BooleanValue blur = new BooleanValue("Blur", false);
    public static final NumberValue blurRadius = new NumberValue("Blur Radius", blur::get, 8.0, 1.0, 50.0, 1.0);
    public static final NumberValue blurCompression = new NumberValue("Blur Compression", blur::get,2.0, 1.0, 50.0, 1.0);
    public static final BooleanValue shadow = new BooleanValue("Shadow", false);
    public static final NumberValue shadowRadius = new NumberValue("Shadow Radius", shadow::get,10.0, 1.0, 20.0, 1.0);
    public static final NumberValue shadowOffset = new NumberValue("Shadow Offset", shadow::get,1.0, 1.0, 15.0, 1.0);
    public static final BooleanValue bloom = new BooleanValue("Bloom", false);
    public static final NumberValue glowRadius = new NumberValue("Bloom Radius", bloom::get, 3.0, 1.0, 10.0, 1.0);
    public static final NumberValue glowOffset = new NumberValue("Bloom Offset", bloom::get ,1.0, 1.0, 10.0, 1.0);
    public static Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    public final Map<EntityPlayer, DecelerateAnimation> animationEntityPlayerMap = new HashMap<>();

    @EventTarget
    public void onRender2D(Render2DEvent event){
        setSuffix(color.get());
        if (watermark.get()){
            switch (watermarkmode.get()) {
                case "Logo":
                    FontManager.ICON.get(100).drawString("A", 5,5,color());
                    break;
                case "Exhi":
                    boolean shouldChange = RenderUtil.COLOR_PATTERN.matcher(name.get()).find();
                    String text = shouldChange ? "§r" + name.getText() : name.getText().charAt(0) + "§r§f" + name.getText().substring(1) +
                            "§7[§f" + Minecraft.getDebugFPS() + " FPS§7]§r ";
                    mc.fontRendererObj.drawStringWithShadow(text, 2.0f, 2.0f, color());
                    break;
            }
        }
        if (info.get()){
            switch (infomode.get()) {
                case "Exhi":
                    float textY = (event.getScaledResolution().getScaledHeight() - 9) + (mc.currentScreen instanceof GuiChat ? -14.0f : -3.0f);
                    mc.fontRendererObj.drawStringWithShadow("XYZ: " + EnumChatFormatting.WHITE +
                                    xyzFormat.format(mc.thePlayer.posX) + " " +
                                    xyzFormat.format(mc.thePlayer.posY) + " " +
                                    xyzFormat.format(mc.thePlayer.posZ) + " " + EnumChatFormatting.RESET + "BPS: " + EnumChatFormatting.WHITE + this.bpsFormat.format(getBPS())
                            , 2, textY, color());
                    break;
            }
        }
    }

    @EventTarget
    public void onShader(Shader2DEvent event) {
    }

    public static double getBPS() {
        return getBPS(mc.thePlayer);
    }

    public static double getBPS(EntityPlayer player) {
        if (player == null || player.ticksExisted < 1) {
            return 0.0;
        }
        return getDistance(player.lastTickPosX, player.lastTickPosZ) * (20.0f * mc.timer.timerSpeed);
    }

    public static double getDistance(final double x, final double z) {
        final double xSpeed = mc.thePlayer.posX - x;
        final double zSpeed = mc.thePlayer.posZ - z;
        return MathHelper.sqrt_double(xSpeed * xSpeed + zSpeed * zSpeed);
    }

    public Color getMainColor() {
        return FirstColor.get();
    }

    public Color getSecondColor() {
        return SecondColor.get();
    }

    public int color(int counter) {
        return color(counter, getMainColor().getAlpha());
    }

    public int color(int counter, int alpha) {
        int colors = getMainColor().getRGB();
        colors = switch (color.get()) {
            case "Rainbow" -> ColorUtil.swapAlpha(getRainbow(counter), alpha);
            case "Dynamic" ->
                    ColorUtil.swapAlpha(ColorUtil.colorSwitch(getMainColor(), new Color(ColorUtil.darker(getMainColor().getRGB(), 0.25F)), 2000.0F, counter, 75L, fadeSpeed.get()).getRGB(), alpha);
            case "Fade" ->
                    ColorUtil.swapAlpha((ColorUtil.colorSwitch(getMainColor(), getSecondColor(), 2000.0F, counter, 75L, fadeSpeed.get()).getRGB()), alpha);
            case "Astolfo" ->
                    ColorUtil.swapAlpha(astolfoRainbow(counter, FirstColor.getSaturation(), FirstColor.getBrightness()), alpha);
            case "Custom" -> ColorUtil.swapAlpha(FirstColor.get().getRGB(), alpha);
            default -> colors;
        };
        return new Color(colors,true).getRGB();
    }

    public int color() {
        return color(0);
    }

    public int getRainbow(int counter) {
        return Color.HSBtoRGB(getRainbowHSB(counter)[0], getRainbowHSB(counter)[1], getRainbowHSB(counter)[2]);
    }

    public float[] getRainbowHSB(int counter) {
        final int width = 20;

        double rainbowState = Math.ceil(System.currentTimeMillis() - (long) counter * width) / 8;
        rainbowState %= 360;

        float hue = (float) (rainbowState / 360);
        float saturation = FirstColor.getSaturation();
        float brightness = FirstColor.getBrightness();

        return new float[]{hue, saturation, brightness};
    }

    public static int astolfoRainbow(final int offset, final float saturation, final float brightness) {
        double currentColor = Math.ceil((double)(System.currentTimeMillis() + offset * 20L)) / 6.0;
        return Color.getHSBColor(((float)((currentColor %= 360.0) / 360.0) < 0.5) ? (-(float)(currentColor / 360.0)) : ((float)(currentColor / 360.0)), saturation, brightness).getRGB();
    }

    @EventTarget
    public void onTick(TickEvent event) {
        FirstColor.setRainbow(color.is("Rainbow"));
        KillAura aura = getModule(KillAura.class);
        if (aura.isEnabled()) {
            animationEntityPlayerMap.entrySet().removeIf(entry -> entry.getKey().isDead || (!aura.targets.contains(entry.getKey()) && entry.getKey() != mc.thePlayer));
        }
        if (!aura.isEnabled() && !(mc.currentScreen instanceof GuiChat)) {
            Iterator<Map.Entry<EntityPlayer, DecelerateAnimation>> iterator = animationEntityPlayerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<EntityPlayer, DecelerateAnimation> entry = iterator.next();
                DecelerateAnimation animation = entry.getValue();

                animation.setDirection(Direction.BACKWARDS);
                if (animation.finished(Direction.BACKWARDS)) {
                    iterator.remove();
                }
            }
        }
        if (!aura.targets.isEmpty() && !(mc.currentScreen instanceof GuiChat)) {
            for (EntityLivingBase entity : aura.targets) {
                if (entity instanceof EntityPlayer && entity != mc.thePlayer) {
                    animationEntityPlayerMap.putIfAbsent((EntityPlayer) entity, new DecelerateAnimation(175, 1));
                    animationEntityPlayerMap.get(entity).setDirection(Direction.FORWARDS);
                }
            }
        }
        if (aura.isEnabled() && aura.target == null && !(mc.currentScreen instanceof GuiChat)) {
            Iterator<Map.Entry<EntityPlayer, DecelerateAnimation>> iterator = animationEntityPlayerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<EntityPlayer, DecelerateAnimation> entry = iterator.next();
                DecelerateAnimation animation = entry.getValue();

                animation.setDirection(Direction.BACKWARDS);
                if (animation.finished(Direction.BACKWARDS)) {
                    iterator.remove();
                }
            }
        }
        if (mc.currentScreen instanceof GuiChat) {
            animationEntityPlayerMap.putIfAbsent(mc.thePlayer, new DecelerateAnimation(175, 1));
            animationEntityPlayerMap.get(mc.thePlayer).setDirection(Direction.FORWARDS);
        }
        if (mc.currentScreen instanceof DropDownClickGui) {
            animationEntityPlayerMap.putIfAbsent(mc.thePlayer, new DecelerateAnimation(175, 1));
            animationEntityPlayerMap.get(mc.thePlayer).setDirection(Direction.FORWARDS);
        }
    }

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
