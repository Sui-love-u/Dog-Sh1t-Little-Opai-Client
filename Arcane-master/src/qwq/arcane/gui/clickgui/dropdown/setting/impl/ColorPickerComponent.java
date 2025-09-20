package qwq.arcane.gui.clickgui.dropdown.setting.impl;

import java.awt.Color;
import qwq.arcane.gui.clickgui.Component;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.EaseOutSine;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.ColorValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/clickgui/dropdown/setting/impl/ColorPickerComponent.class */
public class ColorPickerComponent extends Component {
    private final ColorValue setting;
    private final Animation open = new EaseOutSine(250, 1.0d);
    private boolean opened;
    private boolean pickingHue;
    private boolean pickingOthers;
    private boolean pickingAlpha;

    public ColorPickerComponent(ColorValue setting) {
        this.setting = setting;
        this.open.setDirection(Direction.BACKWARDS);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void drawScreen(int mouseX, int mouseY) {
        this.open.setDirection(this.opened ? Direction.FORWARDS : Direction.BACKWARDS);
        setHeight((float) (FontManager.Bold.get(15.0f).getHeight() + ((FontManager.Bold.get(15.0f).getHeight() + 2 + 45 + 2 + 12) * this.open.getOutput().doubleValue())));
        float[] hsb = {this.setting.getHue(), this.setting.getSaturation(), this.setting.getBrightness()};
        float alpha = this.setting.getAlpha();
        FontManager.Bold.get(15.0f).drawString(this.setting.getName(), getX() + 4.0f, getY(), -1);
        RoundedUtil.drawRound((getX() + getWidth()) - 18.0f, getY(), 15.0f, FontManager.Bold.get(15.0f).getHeight() - 3, 2.0f, this.setting.get());
        if (this.opened) {
            RoundedUtil.drawGradientRound(getX() + 2.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f, getWidth() - 4.0f, (float) (45.0d * this.open.getOutput().doubleValue()), 4.0f, Color.BLACK, Color.WHITE, Color.BLACK, Color.getHSBColor(this.setting.getHue(), 1.0f, 1.0f));
            int max = (int) (getWidth() - 8.0f);
            for (int i = 0; i < max; i++) {
                RoundedUtil.drawRound(getX() + i + 4.0f, (float) (getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f + (45.0d * this.open.getOutput().doubleValue()) + 4.0d), 2.0f, 4.0f, 2.0f, Color.getHSBColor(i / max, 1.0f, 1.0f));
            }
            float alphaSliderY = (float) (getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f + (45.0d * this.open.getOutput().doubleValue()) + 12.0d);
            drawCheckerboard(getX() + 4.0f, alphaSliderY, getWidth() - 8.0f, 4.0f);
            int max2 = (int) (getWidth() - 8.0f);
            for (int i2 = 0; i2 < max2; i2++) {
                float alphaValue = i2 / max2;
                Color alphaColor = new Color(this.setting.get().getRed(), this.setting.get().getGreen(), this.setting.get().getBlue(), (int) (alphaValue * 255.0f));
                RoundedUtil.drawRound(getX() + i2 + 4.0f, alphaSliderY, 2.0f, 4.0f, 1.0f, alphaColor);
            }
            float sliderX = getX() + 4.0f;
            float sliderWidth = getWidth() - 8.0f;
            float alphaHandleX = sliderX + (sliderWidth * alpha);
            RenderUtil.drawCircle((int) Math.max(sliderX + 2.0f, Math.min((sliderX + sliderWidth) - 2.0f, alphaHandleX)), ((int) alphaSliderY) + 2, 0.0f, 360.0f, 2.0f, 0.1f, false, -1);
            float gradientX = getX() + 4.0f;
            float gradientY = getY() + Bold.get(15.0f).getHeight() + 2.0f;
            float gradientWidth = getWidth() - 8.0f;
            float gradientHeight = (float) (45.0d * this.open.getOutput().doubleValue());
            float pickerY = gradientY + (gradientHeight * (1.0f - hsb[2]));
            float pickerX = gradientX + ((gradientWidth * hsb[1]) - 1.0f);
            float pickerY2 = Math.max(Math.min((gradientY + gradientHeight) - 2.0f, pickerY), gradientY - 2.0f);
            float pickerX2 = Math.max(Math.min((gradientX + gradientWidth) - 2.0f, pickerX), gradientX - 2.0f);
            if (this.pickingHue) {
                this.setting.setHue(Math.min(1.0f, Math.max(0.0f, (mouseX - gradientX) / gradientWidth)));
            }
            if (this.pickingOthers) {
                this.setting.setBrightness(Math.min(1.0f, Math.max(0.0f, 1.0f - ((mouseY - gradientY) / gradientHeight))));
                this.setting.setSaturation(Math.min(1.0f, Math.max(0.0f, (mouseX - gradientX) / gradientWidth)));
            }
            if (this.pickingAlpha) {
                float newAlpha = (mouseX - sliderX) / sliderWidth;
                this.setting.setAlpha(Math.max(0.0f, Math.min(1.0f, newAlpha)));
            }
            RenderUtil.drawCircle((int) pickerX2, (int) pickerY2, 0.0f, 360.0f, 2.0f, 0.1f, false, -1);
        }
        super.drawScreen(mouseX, mouseY);
    }

    private void drawCheckerboard(float x, float y, float width, float height) {
        RoundedUtil.drawRound(x, y, width, height, 2.0f, new Color(200, 200, 200));
        boolean white = true;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < width) {
                int i3 = 0;
                while (true) {
                    int j = i3;
                    if (j >= height) {
                        break;
                    }
                    if (!white) {
                        Color color = new Color(150, 150, 150);
                        float drawWidth = Math.min(4, width - i2);
                        float drawHeight = Math.min(4, height - j);
                        if ((i2 > 2 && i2 < width - 2.0f) || (j > 0 && j < height - 0.0f)) {
                            RoundedUtil.drawRound(x + i2, y + j, drawWidth, drawHeight, 0.0f, color);
                        }
                    }
                    white = !white;
                    i3 = j + 4;
                }
                if ((height / 4) % 2.0f == 0.0f) {
                    white = !white;
                }
                i = i2 + 4;
            } else {
                return;
            }
        }
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovering((getX() + getWidth()) - 18.0f, getY(), 15.0f, FontManager.Bold.get(15.0f).getHeight(), mouseX, mouseY)) {
            this.opened = !this.opened;
        }
        if (this.opened) {
            if (RenderUtil.isHovering(getX() + 4.0f, getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f, getWidth() - 8.0f, (float) (45.0d * this.open.getOutput().doubleValue()), mouseX, mouseY)) {
                this.pickingOthers = true;
            }
            if (RenderUtil.isHovering(getX() + 4.0f, (float) (getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f + (45.0d * this.open.getOutput().doubleValue()) + 4.0d), getWidth() - 8.0f, 6.0f, mouseX, mouseY)) {
                this.pickingHue = true;
            }
            float alphaSliderY = (float) (getY() + FontManager.Bold.get(15.0f).getHeight() + 2.0f + (45.0d * this.open.getOutput().doubleValue()) + 12.0d);
            if (RenderUtil.isHovering(getX() + 4.0f, alphaSliderY, getWidth() - 8.0f, 6.0f, mouseX, mouseY)) {
                this.pickingAlpha = true;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override // qwq.arcane.gui.clickgui.IComponent
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.pickingHue = false;
            this.pickingOthers = false;
            this.pickingAlpha = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override // qwq.arcane.gui.clickgui.Component
    public boolean isVisible() {
        return this.setting.isAvailable();
    }
}
