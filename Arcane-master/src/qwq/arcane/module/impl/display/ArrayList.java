package qwq.arcane.module.impl.display;

import java.awt.Color;
import java.util.Comparator;
import java.util.Iterator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;
import qwq.arcane.Client;
import qwq.arcane.event.impl.events.render.Shader2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.ModuleWidget;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.fontrender.FontRenderer;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/display/ArrayList.class */
public class ArrayList extends ModuleWidget {
    public ModeValue style;
    public static BoolValue importantModules = new BoolValue("Important", false);
    public ModeValue fontmode;
    public ModeValue textShadow;
    public BoolValue suffixColor;
    public final ModeValue tags;
    public ModeValue animation;
    public final ModeValue color;
    public final NumberValue colorspeed;
    public final NumberValue colorIndex;
    public ColorValue FirstColor;
    public ColorValue SecondColor;
    public BoolValue background;
    public BoolValue white;
    public ModeValue misc;
    public NumberValue radius;
    public ModeValue backgroundmod;
    public final NumberValue backgroundAlpha;
    public NumberValue hight2;
    public NumberValue hight;
    public NumberValue sb;
    public NumberValue count;
    FontRenderer fontManager;

    public ArrayList() {
        super("ArrayList", Category.Display);
        this.style = new ModeValue("Style", "Simple", new String[]{"HotKey", "Simple", "Icon"});
        this.fontmode = new ModeValue("FontMode", "Custom", new String[]{"Custom", "Bold", "Semibold", "Regular", "Light"});
        this.textShadow = new ModeValue("Text Shadow", "None", new String[]{"Black", "Colored", "None"});
        this.suffixColor = new BoolValue("SuffixColor", false);
        this.tags = new ModeValue("Suffix", "Bracket", new String[]{"None", "Simple", "Bracket", "Dash"});
        this.animation = new ModeValue("Animation", "Move In", new String[]{"Move In", "Scale In"});
        this.color = new ModeValue("Color Setting", "Fade", new String[]{"Custom", "Rainbow", "Dynamic", "Double", "Astolfo", "Tenacity"});
        this.colorspeed = new NumberValue("ColorSpeed", () -> {
            return this.color.is("Dynamic") || this.color.is("Fade") || this.color.is("Tenacity");
        }, 4.0d, 1.0d, 10.0d, 1.0d);
        this.colorIndex = new NumberValue("Color Seperation", 1.0d, 1.0d, 50.0d, 1.0d);
        this.FirstColor = new ColorValue("MainColor", new Color(41, 128, 185));
        this.SecondColor = new ColorValue("SecondColor", new Color(109, 213, 250));
        this.background = new BoolValue("BackGround", false);
        this.white = new BoolValue("White", false);
        this.misc = new ModeValue("Rectangle", "None", new String[]{"None", "Top", "Side"});
        this.radius = new NumberValue("radius", () -> {
            return this.background.get().booleanValue();
        }, 3.0d, 0.0d, 8.0d, 1.0d);
        this.backgroundmod = new ModeValue("BackGroundMod", () -> {
            return this.background.get().booleanValue();
        }, "Rect", new String[]{"Rect", "Round"});
        this.backgroundAlpha = new NumberValue("Background Alpha", () -> {
            return this.background.get().booleanValue();
        }, 0.5d, 0.0d, 1.0d, 0.01d);
        this.hight2 = new NumberValue("RectangleHight", 1.0d, 1.0d, 20.0d, 0.1d);
        this.hight = new NumberValue("ArrayHight", 14.0d, 1.0d, 20.0d, 0.1d);
        this.sb = new NumberValue("FontCount", 0.0d, -20.0d, 20.0d, 0.1d);
        this.count = new NumberValue("ArrayCount", 1.0d, -5.0d, 5.0d, 0.1d);
        this.fontManager = FontManager.Regular.get(18.0f);
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void onShader(Shader2DEvent event) {
        rendermodule(true);
    }

    @Override // qwq.arcane.module.ModuleWidget
    public void render() {
        switch (this.fontmode.get()) {
            case "Bold":
                this.fontManager = FontManager.Bold.get(18.0f);
                break;
            case "Semibold":
                this.fontManager = FontManager.Semibold.get(18.0f);
                break;
            case "Regular":
                this.fontManager = FontManager.Regular.get(18.0f);
                break;
            case "Light":
                this.fontManager = FontManager.Light.get(18.0f);
                break;
        }
        rendermodule(false);
    }

    public void rendermodule(boolean shader) {
        int i;
        int rendery;
        boolean flip;
        String displayText;
        int stringWidth;
        int x;
        int y;
        int textcolor;
        int w;
        java.util.ArrayList<Module> enabledMods = getModuleArrayList(this.fontManager);
        int count = 0;
        int counts = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        int renderx2 = ((int) this.renderX) - 2;
        int rendery2 = ((int) this.renderY) + 4;
        boolean flip2 = this.renderX + (this.width / 2.0f) <= ((float) sr.getScaledWidth()) / 2.0f;
        if (flip2) {
            i = renderx2 + 4;
        } else {
            i = ((int) (renderx2 + (this.width - (this.fontmode.get().equals("Custom") ? 80 : 80)))) + 21;
        }
        int x2 = i;
        int y2 = (rendery2 + 0) - 4;
        if (this.style.is("HotKey")) {
            RoundedUtil.drawRound(x2, y2 - 18, 80.0f, 13.0f, this.radius.get().intValue(), ColorUtil.applyOpacity3(this.white.get().booleanValue() ? new Color(255, 255, 255).getRGB() : new Color(1, 1, 1).getRGB(), shader ? 1.0f : this.backgroundAlpha.get().floatValue()));
            Bold.get(18.0f).drawString("Hot Key", x2 + 17, y2 - 14.5d, INTERFACE.color());
            Icon.get(20.0f).drawString("F", x2 + 4, y2 - 12.5d, INTERFACE.color());
        }
        Iterator<Module> it = enabledMods.iterator();
        while (it.hasNext()) {
            Module module = it.next();
            if (!importantModules.get().booleanValue() || (module.getCategory() != Category.Visuals && module.getCategory() != Category.Display)) {
                Animation moduleAnimation = module.getAnimations();
                moduleAnimation.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
                if (module.getState() || !moduleAnimation.finished(Direction.BACKWARDS)) {
                    int renderx = ((int) this.renderX) - 2;
                    rendery = ((int) this.renderY) + 4;
                    flip = this.renderX + (this.width / 2.0f) <= ((float) sr.getScaledWidth()) / 2.0f;
                    displayText = module.getName() + module.getSuffix();
                    if (flip) {
                        stringWidth = renderx + 4;
                    } else {
                        stringWidth = (int) (renderx + (this.width - (this.fontmode.get().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) : this.fontManager.getStringWidth(displayText))));
                    }
                    x = stringWidth;
                    y = rendery + count + this.sb.get().intValue();
                    switch (this.animation.get()) {
                        case "Move In":
                            if (flip) {
                                x -= (int) Math.abs((moduleAnimation.getOutput().doubleValue() - 1.0d) * (12.0d + this.fontManager.getStringWidth(displayText)));
                                break;
                            } else {
                                x += (int) Math.abs((moduleAnimation.getOutput().doubleValue() - 1.0d) * (12.0d + this.fontManager.getStringWidth(displayText)));
                                break;
                            }
                        case "Scale In":
                            if (flip) {
                                RenderUtil.scaleStart(x, rendery + count + mc.fontRendererObj.FONT_HEIGHT, moduleAnimation.getOutput().floatValue());
                                break;
                            } else {
                                RenderUtil.scaleStart(x + this.fontManager.getStringWidth(displayText), rendery + count + mc.fontRendererObj.FONT_HEIGHT, moduleAnimation.getOutput().floatValue());
                                break;
                            }
                    }
                    int index = (int) (counts * this.colorIndex.getValue().doubleValue());
                    textcolor = ColorUtil.swapAlpha(color(index), 255.0f);
                    if (this.color.is("Tenacity")) {
                        textcolor = ColorUtil.interpolateColorsBackAndForth(this.colorspeed.getValue().intValue(), index, this.FirstColor.get(), this.SecondColor.get(), false).getRGB();
                    }
                    w = this.fontmode.get().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) + 4 : this.fontManager.getStringWidth(displayText) + 6;
                    switch (this.misc.getValue()) {
                        case "Top":
                            if (count == 0) {
                                RenderUtil.drawRect(x - 2, rendery - 4, w, 2.0f, textcolor);
                                break;
                            }
                            break;
                        case "Side":
                            if (!flip) {
                                RenderUtil.drawRect(x + (this.fontmode.get().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) + 2 : this.fontManager.getStringWidth(displayText) + 2), y - 4.5f, 2.0f, this.hight2.get().intValue(), textcolor);
                                break;
                            } else {
                                RenderUtil.drawRect(x - 4, y - 4, 2.0f, this.hight.get().intValue(), textcolor);
                                break;
                            }
                    }
                    if (this.style.is("HotKey") || (this.style.is("Icon") && this.background.getValue().booleanValue())) {
                        RoundedUtil.drawRound(x + 6 + (this.fontmode.get().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) : this.fontManager.getStringWidth(displayText) + 4), (rendery - 4) + count, 15.0f, this.hight.get().intValue(), this.radius.get().intValue(), ColorUtil.applyOpacity3(this.white.get().booleanValue() ? new Color(255, 255, 255).getRGB() : new Color(1, 1, 1).getRGB(), shader ? 255.0f : this.backgroundAlpha.get().floatValue()));
                    }
                    if (this.style.is("HotKey")) {
                        Icon.get(30.0f).drawString("T", x + 7 + (this.fontmode.get().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) : this.fontManager.getStringWidth(displayText) + 4), (rendery - 1) + count, textcolor);
                    }
                    if (!shader && this.style.is("Icon")) {
                        Icon.get(22.0f).drawString(module.getCategory().icon, ((x + 13) - (Icon.get(22.0f).getStringWidth(module.getCategory().icon) / 2)) + (this.fontmode.get().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) : this.fontManager.getStringWidth(displayText) + 4), rendery + 1 + count, textcolor);
                    }
                    if (this.background.getValue().booleanValue()) {
                        switch (this.backgroundmod.get()) {
                            case "Rect":
                                RenderUtil.drawRect(x - 2, (rendery + count) - 4, w, this.hight.get().intValue(), ColorUtil.applyOpacity(this.white.get().booleanValue() ? new Color(255, 255, 255) : new Color(1, 1, 1), shader ? 255.0f : this.backgroundAlpha.get().floatValue()));
                                break;
                            case "Round":
                                RoundedUtil.drawRound(x - 2, (rendery + count) - 4, w, this.hight.get().intValue(), this.radius.get().intValue(), ColorUtil.applyOpacity3(this.white.get().booleanValue() ? new Color(255, 255, 255).getRGB() : new Color(1, 1, 1).getRGB(), shader ? 255.0f : this.backgroundAlpha.get().floatValue()));
                                break;
                        }
                    }
                    if (!shader) {
                        switch (this.textShadow.getValue()) {
                            case "None":
                                if (this.fontmode.get().equals("Custom")) {
                                    mc.fontRendererObj.drawString(displayText, x + 1, y, textcolor);
                                    break;
                                } else {
                                    this.fontManager.drawString(displayText, x + 1, y, textcolor);
                                    break;
                                }
                            case "Colored":
                                RenderUtil.resetColor();
                                if (this.fontmode.get().equals("Custom")) {
                                    mc.fontRendererObj.drawString(StringUtils.stripColorCodes(displayText), x + 2, (y + 1) - 2, ColorUtil.darker(textcolor, 0.5f));
                                } else {
                                    this.fontManager.drawString(StringUtils.stripColorCodes(displayText), x + 2, (y + 1) - 2, ColorUtil.darker(textcolor, 0.5f));
                                }
                                RenderUtil.resetColor();
                                if (this.fontmode.get().equals("Custom")) {
                                    mc.fontRendererObj.drawString(displayText, x + 1, y - 2, textcolor);
                                    break;
                                } else {
                                    this.fontManager.drawString(displayText, x + 1, y - 2, textcolor);
                                    break;
                                }
                            case "Black":
                                RenderUtil.resetColor();
                                if (this.fontmode.get().equals("Custom")) {
                                    mc.fontRendererObj.drawStringWithShadow(displayText, x + 1, y - 2, textcolor);
                                    break;
                                } else {
                                    this.fontManager.drawStringWithShadow(displayText, x + 1, y - 2, textcolor);
                                    break;
                                }
                        }
                    }
                    if (this.animation.get().equals("Scale In")) {
                        RenderUtil.scaleEnd();
                    }
                    count += (int) (moduleAnimation.getOutput().doubleValue() * this.hight.get().doubleValue() * this.count.get().doubleValue());
                    counts++;
                    this.height = count;
                }
            }
        }
        this.width = 52.0f;
    }

    public int color(int counter) {
        return color(counter, this.FirstColor.get().getAlpha());
    }

    public int color(int counter, int alpha) {
        int colors;
        int rgb;
        colors = this.FirstColor.get().getRGB();
        switch (this.color.get()) {
            case "Rainbow":
                rgb = RenderUtil.getRainbow(System.currentTimeMillis(), 2000, counter);
                break;
            case "Dynamic":
                rgb = ColorUtil.swapAlpha(ColorUtil.colorSwitch(this.FirstColor.get(), new Color(ColorUtil.darker(this.FirstColor.get().getRGB(), 0.25f)), 2000.0f, counter, counter * 10, this.colorspeed.get().doubleValue()).getRGB(), alpha);
                break;
            case "Double":
                rgb = new Color(RenderUtil.colorSwitch(this.FirstColor.get(), this.SecondColor.get(), 2000.0f, (-counter) / 40, 75L, 2.0d)).getRGB();
                break;
            case "Astolfo":
                rgb = ColorUtil.swapAlpha(astolfoRainbow(counter, this.FirstColor.getSaturation(), this.FirstColor.getBrightness()), alpha);
                break;
            case "Custom":
                rgb = ColorUtil.swapAlpha(this.FirstColor.get().getRGB(), alpha);
                break;
            case "Tenacity":
                rgb = ColorUtil.interpolateColorsBackAndForth(this.colorspeed.getValue().intValue(), Client.Instance.getModuleManager().getAllModules().size() * this.count.get().intValue(), this.FirstColor.get(), this.SecondColor.get(), false).getRGB();
                break;
            default:
                rgb = colors;
                break;
        }
        int colors2 = rgb;
        return new Color(colors2, true).getRGB();
    }

    public static int astolfoRainbow(int offset, float saturation, float brightness) {
        double currentColor = (Math.ceil(System.currentTimeMillis() + (offset * 20)) / 6.0d) % 360.0d;
        return Color.getHSBColor(((double) ((float) (currentColor / 360.0d))) < 0.5d ? -((float) (currentColor / 360.0d)) : (float) (currentColor / 360.0d), saturation, brightness).getRGB();
    }

    private java.util.ArrayList<Module> getModuleArrayList(FontRenderer string) {
        Comparator<Module> sort = (m1, m2) -> {
            double ab = this.fontmode.get().equals("Custom") ? mc.fontRendererObj.getStringWidth(m1.getName() + m1.getSuffix()) : string.getStringWidth(m1.getName() + m1.getSuffix());
            double bb = this.fontmode.get().equals("Custom") ? mc.fontRendererObj.getStringWidth(m2.getName() + m2.getSuffix()) : string.getStringWidth(m2.getName() + m2.getSuffix());
            return Double.compare(bb, ab);
        };
        java.util.ArrayList<Module> enabledMods = new java.util.ArrayList<>(INSTANCE.getModuleManager().getAllModules());
        enabledMods.sort(sort);
        return enabledMods;
    }

    @Override // qwq.arcane.module.ModuleWidget
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
