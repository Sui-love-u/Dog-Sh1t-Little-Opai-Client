package qwq.arcane.gui.clickgui.arcane.component.settings;

import java.awt.Color;
import net.minecraft.util.MathHelper;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.ColorValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/arcane/component/settings/ColorPickerComponent.class */
public class ColorPickerComponent extends Component {
    private final ColorValue setting;
    private final Animation open = new DecelerateAnimation(250, 1.0d);
    private boolean opened;
    private boolean pickingHue;
    private boolean picking;
    private boolean pickingAlpha;

    public ColorPickerComponent(ColorValue setting) {
        this.setting = setting;
        setHeight(22.0f);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        this.open.setDirection(this.opened ? Direction.FORWARDS : Direction.BACKWARDS);
        setHeight((float) (24.0d + (66.0d * this.open.getOutput().doubleValue())));
        RoundedUtil.drawRound(getX() + 10.0f, (getY() + getHeight()) - 4.0f, 145.0f, 1.0f, 0.0f, INSTANCE.getArcaneClickGui().linecolor);
        FontManager.Bold.get(18.0f).drawString(this.setting.getName(), getX() + 10.0f, getY() + 4.0f, ColorUtil.applyOpacity(INSTANCE.getArcaneClickGui().fontcolor.getRGB(), 0.4f));
        RenderUtil.drawCircle(getX() + 149.0f, getY() + 7.0f, 0.0f, 360.0f, 5.0f, 2.0f, true, this.setting.isRainbow() ? ColorUtil.getRainbow().getRGB() : this.setting.get().getRGB());
        RenderUtil.resetColor2();
        if (this.open.getOutput().doubleValue() > 0.0d) {
            float gradientHeight = (float) (60.0d * this.open.getOutput().doubleValue());
            float gradientX = getX() + 34.0f;
            float gradientY = getY() + 18.0f;
            float[] hsb = {this.setting.getHue(), this.setting.getSaturation(), this.setting.getBrightness()};
            float f = 0.0f;
            while (true) {
                float i = f;
                if (i > 60.0d * this.open.getOutput().doubleValue()) {
                    break;
                }
                RenderUtil.drawRect(getX() + 21.0f, getY() + 18.0f + i, 8.0f, 1.0f, Color.getHSBColor((float) ((i / 60.0f) * this.open.getOutput().doubleValue()), 1.0f, 1.0f).getRGB());
                f = i + 1.0f;
            }
            RenderUtil.drawRect(getX() + 20.0f, (float) (getY() + 18.0f + ((this.setting.isRainbow() ? getRainbowHSB(0)[0] : this.setting.getHue()) * 60.0f * this.open.getOutput().doubleValue())), 10.0f, 1.0f, Color.WHITE.getRGB());
            float f2 = 0.0f;
            while (true) {
                float i2 = f2;
                if (i2 > 60.0d * this.open.getOutput().doubleValue()) {
                    break;
                }
                RenderUtil.drawRect(getX() + 11.0f, getY() + 18.0f + i2, 8.0f, 1.0f, ColorUtil.applyOpacity(new Color(this.setting.isRainbow() ? ColorUtil.getRainbow().getRGB() : Color.HSBtoRGB(this.setting.getHue(), this.setting.getSaturation(), this.setting.getBrightness())), this.setting.getAlpha() - (i2 / 60.0f)).getRGB());
                f2 = i2 + 1.0f;
            }
            RenderUtil.drawRect(getX() + 10.0f, (float) (getY() + 18.0f + ((1.0f - this.setting.getAlpha()) * 60.0f * this.open.getOutput().doubleValue())), 10.0f, 1.0f, Color.WHITE.getRGB());
            float pickerY = gradientY + 2.0f + (gradientHeight * (1.0f - hsb[2]));
            float pickerX = gradientX + ((60.0f * hsb[1]) - 1.0f);
            float pickerY2 = Math.max(Math.min((gradientY + gradientHeight) - 2.0f, pickerY), gradientY);
            float pickerX2 = Math.max(Math.min((gradientX + 60.0f) - 2.0f, pickerX), gradientX + 2.0f);
            if (this.pickingHue) {
                this.setting.setHue(MathHelper.clamp_float((mouseY - (getY() + 18.0f)) / 60.0f, 0.0f, 1.0f));
            }
            if (this.pickingAlpha) {
                this.setting.setAlpha(MathHelper.clamp_float(1.0f - ((mouseY - (getY() + 18.0f)) / 60.0f), 0.0f, 1.0f));
            }
            if (this.picking) {
                this.setting.setBrightness(MathHelper.clamp_float(1.0f - ((mouseY - gradientY) / 60.0f), 0.0f, 1.0f));
                this.setting.setSaturation(MathHelper.clamp_float((mouseX - gradientX) / 60.0f, 0.0f, 1.0f));
            }
            Color firstColor = this.setting.isRainbow() ? ColorUtil.getRainbow() : ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 1.0f, 1.0f), 1.0f);
            RoundedUtil.drawRound(gradientX, gradientY, 60.0f, gradientHeight, 2.0f, ColorUtil.applyOpacity(firstColor, 1.0f));
            Color secondColor = Color.getHSBColor(hsb[0], 0.0f, 1.0f);
            RoundedUtil.drawGradientHorizontal(gradientX, gradientY, 60.0f, gradientHeight, 2.5f, ColorUtil.applyOpacity(secondColor, 1.0f), ColorUtil.applyOpacity(secondColor, 0.0f));
            Color thirdColor = Color.getHSBColor(hsb[0], 1.0f, 0.0f);
            RoundedUtil.drawGradientVertical(gradientX, gradientY, 60.0f, gradientHeight, 2.0f, ColorUtil.applyOpacity(thirdColor, 0.0f), ColorUtil.applyOpacity(thirdColor, 1.0f));
            RenderUtil.drawCircle((int) pickerX2, (int) pickerY2, 0.0f, 360.0f, 2.0f, 0.1f, false, -1);
        }
        super.drawScreen(mouseX, mouseY);
    }

    public float[] getRainbowHSB(int counter) {
        double rainbowState = Math.ceil(System.currentTimeMillis() - (counter * 20)) / 8.0d;
        float hue = (float) ((rainbowState % 360.0d) / 360.0d);
        float saturation = InterFace.mainColor.getSaturation();
        float brightness = InterFace.mainColor.getBrightness();
        return new float[]{hue, saturation, brightness};
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering(getX() + 144.0f, getY() + 2.0f, 10.0f, 10.0f, mouseX, mouseY) && mouseButton == 1) {
            this.opened = !this.opened;
        }
        if (this.opened && mouseButton == 0) {
            if (RenderUtil.isHovering(getX() + 34.0f, getY() + 18.0f, 60.0f, 60.0f, mouseX, mouseY)) {
                this.picking = true;
            }
            if (RenderUtil.isHovering(getX() + 21.0f, getY() + 18.0f, 8.0f, 60.0f, mouseX, mouseY)) {
                this.pickingHue = true;
            }
            if (RenderUtil.isHovering(getX() + 11.0f, getY() + 18.0f, 8.0f, 60.0f, mouseX, mouseY)) {
                this.pickingAlpha = true;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.pickingHue = false;
            this.picking = false;
            this.pickingAlpha = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isVisible() {
        return this.setting.isAvailable();
    }
}
