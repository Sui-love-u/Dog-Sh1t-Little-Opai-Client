package qwq.arcane.value.impl;

import java.awt.Color;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.value.Value;

/* loaded from: Arcane 8.10.jar:qwq/arcane/value/impl/ColorValue.class */
public class ColorValue extends Value<Color> {
    private float hue;
    private float saturation;
    private float brightness;
    private float alpha;
    private boolean rainbow;
    public boolean expand;

    public void setHue(float hue) {
        this.hue = hue;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public float getHue() {
        return this.hue;
    }

    public float getSaturation() {
        return this.saturation;
    }

    public float getBrightness() {
        return this.brightness;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public boolean isRainbow() {
        return this.rainbow;
    }

    public boolean isExpand() {
        return this.expand;
    }

    public ColorValue(String name, Value.Dependency dependency, Color defaultValue) {
        super(name, dependency);
        this.hue = 0.0f;
        this.saturation = 1.0f;
        this.brightness = 1.0f;
        this.alpha = 1.0f;
        this.rainbow = false;
        set(defaultValue);
    }

    public ColorValue(String name, Color defaultValue) {
        this(name, () -> {
            return true;
        }, defaultValue);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // qwq.arcane.value.Value
    public Color get() {
        return ColorUtil.applyOpacity(Color.getHSBColor(this.hue, this.saturation, this.brightness), this.alpha);
    }

    @Override // qwq.arcane.value.Value
    public void set(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[]) null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.alpha = color.getAlpha() / 255.0f;
    }
}
