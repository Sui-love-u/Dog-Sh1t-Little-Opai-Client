package qwq.arcane.module.impl.visuals;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.TickEvent;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.event.impl.events.render.Shader2DEvent;
import qwq.arcane.gui.clickgui.dropdown.DropDownClickGui;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.combat.KillAura;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.player.PingerUtils;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.utils.render.shader.impl.Bloom;
import qwq.arcane.utils.render.shader.impl.Blur;
import qwq.arcane.utils.render.shader.impl.Shadow;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/InterFace.class */
public class InterFace extends Module {
    public static ModeValue colorMode = new ModeValue("Color Mode", "Fade", new String[]{"Fade", "Rainbow", "Astolfo", "Dynamic", "Tenacity", "Static", "Double"});
    public static final NumberValue colorspeed = new NumberValue("ColorSpeed", () -> {
        return colorMode.is("Tenacity");
    }, 4.0d, 1.0d, 10.0d, 1.0d);
    public static ColorValue mainColor = new ColorValue("MainColor", new Color(41, 128, 185));
    public static ColorValue secondColor = new ColorValue("SecondColor", new Color(109, 213, 250));
    public static BoolValue waterMark = new BoolValue("WaterMark", true);
    public static ModeValue waterMarkmode = new ModeValue("WaterMarkMode", () -> {
        return waterMark.get().booleanValue();
    }, "Exhi", new String[]{"Exhi", "Arcane", "Exhibition", "Sigma"});
    public static BoolValue info = new BoolValue("Info", true);
    public static final ModeValue infomode = new ModeValue("InfoMode", () -> {
        return info.get().booleanValue();
    }, "Exhi", new String[]{"Exhi", "Arcane"});
    public static NumberValue radius = new NumberValue("radius", 6.0d, 0.0d, 8.0d, 1.0d);
    public static BoolValue renderBossHealth = new BoolValue("BossHealth", false);
    public static final BoolValue blur = new BoolValue("Blur", false);
    public static final NumberValue blurRadius;
    public static final NumberValue blurCompression;
    public static final BoolValue shadow;
    public static final NumberValue shadowRadius;
    public static final NumberValue shadowOffset;
    public static final BoolValue bloom;
    public static final NumberValue glowRadius;
    public static final NumberValue glowOffset;
    private final DecimalFormat bpsFormat;
    private final DecimalFormat xyzFormat;
    public static Framebuffer stencilFramebuffer;
    public final Map<EntityPlayer, DecelerateAnimation> animationEntityPlayerMap;

    public InterFace() {
        super("InterFace", Category.Visuals);
        this.bpsFormat = new DecimalFormat("0.00");
        this.xyzFormat = new DecimalFormat("0");
        this.animationEntityPlayerMap = new HashMap();
        setState(true);
    }

    static {
        BoolValue boolValue = blur;
        Objects.requireNonNull(boolValue);
        blurRadius = new NumberValue("Blur Radius", boolValue::get, 8.0d, 1.0d, 50.0d, 1.0d);
        BoolValue boolValue2 = blur;
        Objects.requireNonNull(boolValue2);
        blurCompression = new NumberValue("Blur Compression", boolValue2::get, 2.0d, 1.0d, 50.0d, 1.0d);
        shadow = new BoolValue("Shadow", false);
        BoolValue boolValue3 = shadow;
        Objects.requireNonNull(boolValue3);
        shadowRadius = new NumberValue("Shadow Radius", boolValue3::get, 10.0d, 1.0d, 20.0d, 1.0d);
        BoolValue boolValue4 = shadow;
        Objects.requireNonNull(boolValue4);
        shadowOffset = new NumberValue("Shadow Offset", boolValue4::get, 1.0d, 1.0d, 15.0d, 1.0d);
        bloom = new BoolValue("Bloom", false);
        BoolValue boolValue5 = bloom;
        Objects.requireNonNull(boolValue5);
        glowRadius = new NumberValue("Bloom Radius", boolValue5::get, 3.0d, 1.0d, 10.0d, 1.0d);
        BoolValue boolValue6 = bloom;
        Objects.requireNonNull(boolValue6);
        glowOffset = new NumberValue("Bloom Offset", boolValue6::get, 1.0d, 1.0d, 10.0d, 1.0d);
        stencilFramebuffer = new Framebuffer(1, 1, false);
    }

    @EventTarget
    public void onRender(Render2DEvent event) {
        String text;
        String text3;
        setsuffix(colorMode.get());
        if (waterMark.get().booleanValue()) {
            boolean shouldChange = RenderUtil.COLOR_PATTERN.matcher(Client.name).find();
            text = shouldChange ? "§r" + Client.name : Client.name.charAt(0) + "§r§f" + Client.name.substring(1) + "§7[§f" + Mine.getDebugFPS() + " FPS§7]§r ";
            text3 = shouldChange ? "§r" + Client.name : Client.name.charAt(0) + "§r§f" + Client.name.substring(1);
            switch (waterMarkmode.get()) {
                case "Exhi":
                    mc.fontRendererObj.drawStringWithShadow(text, 2.0f, 2.0f, color(1).getRGB());
                    break;
                case "Arcane":
                    Bold.get(60.0f).drawString(text3, 5.0f, 5.0f, color(1).getRGB());
                    break;
                case "Sigma":
                    Semibold.get(60.0f).drawString(text3, 5.0f, 5.0f, new Color(255, 255, 255, 190).getRGB());
                    Semibold.get(20.0f).drawString(Client.version, 0.0f, 37.0f, new Color(255, 255, 255, 190).getRGB());
                    break;
                case "Exhibition":
                    String text2 = "§fArc§rance§f - " + mc.thePlayer.getName() + " -  - " + PingerUtils.getPing() + "ms ";
                    RenderUtil.resetColor();
                    int lineColor = new Color(59, 57, 57).darker().getRGB();
                    Gui.drawRect2(4.5f, 4.5f, Semibold.get(16.0f).getStringWidth(text2) + 7, 18.5d, new Color(59, 57, 57).getRGB());
                    Gui.drawRect2(4.5f + 2.5d, 4.5f + 2.5d, Semibold.get(16.0f).getStringWidth(text2) + 2, 13.0d, new Color(23, 23, 23).getRGB());
                    Gui.drawRect2(4.5f + 1.0f, 4.5f + 1.0f, Semibold.get(16.0f).getStringWidth(text2) + 5, 0.5d, lineColor);
                    Gui.drawRect2(4.5f + 1.0f, 4.5f + 17.0f, Semibold.get(16.0f).getStringWidth(text2) + 5, 0.5d, lineColor);
                    Gui.drawRect2(4.5f + 1.0f, 4.5f + 1.5d, 0.5d, 16.0d, lineColor);
                    Gui.drawRect2(4.5f + 1.5d + Semibold.get(16.0f).getStringWidth(text2) + 4.0d, 4.5f + 1.5d, 0.5d, 16.0d, lineColor);
                    RoundedUtil.drawGradientHorizontal(4.5f + 2.5f, 4.5f + 14.5f, Semibold.get(16.0f).getStringWidth(text2) + 2, 0.1f, 0.0f, mainColor.get(), secondColor.get());
                    Gui.drawRect2(4.5f + 2.5d, 4.5f + 16.0f, Semibold.get(16.0f).getStringWidth(text2) + 2, 0.5d, lineColor);
                    Semibold.get(16.0f).drawString(text2, 4.5f + 4.5f, 4.5f + 5.5f, secondColor.get().getRGB());
                    RenderUtil.resetColor();
                    break;
            }
        }
        if (info.get().booleanValue()) {
            switch (infomode.get()) {
                case "Exhi":
                    mc.fontRendererObj.drawStringWithShadow("XYZ: " + EnumChatFormatting.WHITE + this.xyzFormat.format(mc.thePlayer.posX) + " " + this.xyzFormat.format(mc.thePlayer.posY) + " " + this.xyzFormat.format(mc.thePlayer.posZ) + " " + EnumChatFormatting.RESET + "BPS: " + EnumChatFormatting.WHITE + this.bpsFormat.format(getBPS()), 2.0f, (int) ((event.getScaledResolution().getScaledHeight() - 9) + (mc.currentScreen instanceof GuiChat ? -14.0f : -3.0f)), color());
                    break;
                case "Arcane":
                    Bold.get(18.0f).drawString("XYZ: " + EnumChatFormatting.WHITE + this.xyzFormat.format(mc.thePlayer.posX) + " " + this.xyzFormat.format(mc.thePlayer.posY) + " " + this.xyzFormat.format(mc.thePlayer.posZ) + " " + EnumChatFormatting.RESET, 5.0f, event.getScaledResolution().getScaledHeight() - 20, color());
                    Bold.get(18.0f).drawString("BPS: " + EnumChatFormatting.WHITE + this.bpsFormat.format(getBPS()), 5.0f, event.getScaledResolution().getScaledHeight() - 30, color());
                    break;
            }
        }
    }

    public void renderShaders() {
        if (getState()) {
            if (blur.get().booleanValue()) {
                Blur.startBlur();
                Client.Instance.getEventManager().call(new Shader2DEvent(Shader2DEvent.ShaderType.BLUR));
                Blur.endBlur(blurRadius.getValue().floatValue(), blurCompression.getValue().floatValue());
            }
            if (bloom.get().booleanValue()) {
                stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
                stencilFramebuffer.framebufferClear();
                stencilFramebuffer.bindFramebuffer(false);
                Client.Instance.getEventManager().call(new Shader2DEvent(Shader2DEvent.ShaderType.GLOW));
                stencilFramebuffer.unbindFramebuffer();
                Bloom.renderBlur(stencilFramebuffer.framebufferTexture, (int) glowRadius.get().floatValue(), (int) glowOffset.get().floatValue());
            }
            if (shadow.get().booleanValue()) {
                stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer, true);
                stencilFramebuffer.framebufferClear();
                stencilFramebuffer.bindFramebuffer(true);
                Client.Instance.getEventManager().call(new Shader2DEvent(Shader2DEvent.ShaderType.SHADOW));
                stencilFramebuffer.unbindFramebuffer();
                Shadow.renderBloom(stencilFramebuffer.framebufferTexture, (int) shadowRadius.get().floatValue(), (int) shadowOffset.get().floatValue());
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        mainColor.setRainbow(colorMode.is("Rainbow"));
        KillAura aura = (KillAura) getModule(KillAura.class);
        if (aura.isEnabled()) {
            this.animationEntityPlayerMap.entrySet().removeIf(entry -> {
                return ((EntityPlayer) entry.getKey()).isDead || !(aura.targets.contains(entry.getKey()) || entry.getKey() == mc.thePlayer);
            });
        }
        if (!aura.isEnabled() && !(mc.currentScreen instanceof GuiChat)) {
            Iterator<Map.Entry<EntityPlayer, DecelerateAnimation>> iterator = this.animationEntityPlayerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<EntityPlayer, DecelerateAnimation> entry2 = iterator.next();
                DecelerateAnimation animation = entry2.getValue();
                animation.setDirection(Direction.BACKWARDS);
                if (animation.finished(Direction.BACKWARDS)) {
                    iterator.remove();
                }
            }
        }
        if (!aura.targets.isEmpty() && !(mc.currentScreen instanceof GuiChat)) {
            for (EntityLivingBase entity : aura.targets) {
                if ((entity instanceof EntityPlayer) && entity != mc.thePlayer) {
                    this.animationEntityPlayerMap.putIfAbsent((EntityPlayer) entity, new DecelerateAnimation(175, 1.0d));
                    this.animationEntityPlayerMap.get(entity).setDirection(Direction.FORWARDS);
                }
            }
        }
        if (aura.isEnabled() && KillAura.target == null && !(mc.currentScreen instanceof GuiChat)) {
            Iterator<Map.Entry<EntityPlayer, DecelerateAnimation>> iterator2 = this.animationEntityPlayerMap.entrySet().iterator();
            while (iterator2.hasNext()) {
                Map.Entry<EntityPlayer, DecelerateAnimation> entry3 = iterator2.next();
                DecelerateAnimation animation2 = entry3.getValue();
                animation2.setDirection(Direction.BACKWARDS);
                if (animation2.finished(Direction.BACKWARDS)) {
                    iterator2.remove();
                }
            }
        }
        if (mc.currentScreen instanceof GuiChat) {
            this.animationEntityPlayerMap.putIfAbsent(mc.thePlayer, new DecelerateAnimation(175, 1.0d));
            this.animationEntityPlayerMap.get(mc.thePlayer).setDirection(Direction.FORWARDS);
        }
        if (mc.currentScreen instanceof DropDownClickGui) {
            this.animationEntityPlayerMap.putIfAbsent(mc.thePlayer, new DecelerateAnimation(175, 1.0d));
            this.animationEntityPlayerMap.get(mc.thePlayer).setDirection(Direction.FORWARDS);
        }
    }

    public static String getPing() {
        int temp;
        int latency = 0;
        if (!mc.isSingleplayer()) {
            NetworkPlayerInfo info2 = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            if (info2 != null) {
                latency = info2.getResponseTime();
            }
            if (isOnHypixel() && latency == 1 && (temp = Client.INSTANCE.getPingerUtils().getServerPing().intValue()) != -1) {
                latency = temp;
            }
            return latency == 0 ? "?" : String.valueOf(latency);
        }
        return "SinglePlayer";
    }

    public static boolean isOnHypixel() {
        if (mc.isSingleplayer() || mc.getCurrentServerData() == null || mc.getCurrentServerData().serverIP == null) {
            return false;
        }
        String ip = mc.getCurrentServerData().serverIP.toLowerCase();
        if (ip.contains("hypixel")) {
            if (mc.thePlayer == null) {
                return true;
            }
            String brand = mc.thePlayer.getClientBrand();
            return brand != null && brand.startsWith("Hypixel BungeeCord");
        }
        return false;
    }

    public int color() {
        return color(1).getRGB();
    }

    public static double getBPS() {
        return getBPS(mc.thePlayer);
    }

    public static double getBPS(EntityPlayer player) {
        if (player == null || player.ticksExisted < 1) {
            return 0.0d;
        }
        return getDistance(player.lastTickPosX, player.lastTickPosZ) * 20.0f * mc.timer.timerSpeed;
    }

    public static double getDistance(double x, double z) {
        double xSpeed = mc.thePlayer.posX - x;
        double zSpeed = mc.thePlayer.posZ - z;
        return MathHelper.sqrt_double((xSpeed * xSpeed) + (zSpeed * zSpeed));
    }

    public static int colors(int tick) {
        return color(tick).getRGB();
    }

    public static Color color(int tick) {
        Color textColor;
        textColor = new Color(-1);
        switch (colorMode.get()) {
            case "Fade":
                textColor = ColorUtil.fade(5, tick * 20, new Color(mainColor.get().getRGB()), 1.0f);
                break;
            case "Static":
                textColor = mainColor.get();
                break;
            case "Astolfo":
                textColor = new Color(ColorUtil.swapAlpha(astolfoRainbow(tick, mainColor.getSaturation(), mainColor.getBrightness()), 255.0f));
                break;
            case "Rainbow":
                textColor = new Color(RenderUtil.getRainbow(System.currentTimeMillis(), 2000, tick));
                break;
            case "Tenacity":
                textColor = ColorUtil.interpolateColorsBackAndForth(colorspeed.getValue().intValue(), Client.Instance.getModuleManager().getAllModules().size() * tick, mainColor.get(), secondColor.get(), false);
                break;
            case "Dynamic":
                textColor = new Color(ColorUtil.swapAlpha(ColorUtil.colorSwitch(mainColor.get(), new Color(ColorUtil.darker(mainColor.get().getRGB(), 0.25f)), 2000.0f, 0, 10L, colorspeed.get().doubleValue()).getRGB(), 255.0f));
                break;
            case "Double":
                textColor = new Color(RenderUtil.colorSwitch(mainColor.get(), secondColor.get(), 2000.0f, (-(tick * 200)) / 40, 75L, 2.0d));
                break;
        }
        return textColor;
    }

    public static int astolfoRainbow(int offset, float saturation, float brightness) {
        double currentColor = (Math.ceil(System.currentTimeMillis() + (offset * 20)) / 6.0d) % 360.0d;
        return Color.getHSBColor(((double) ((float) (currentColor / 360.0d))) < 0.5d ? -((float) (currentColor / 360.0d)) : (float) (currentColor / 360.0d), saturation, brightness).getRGB();
    }
}
