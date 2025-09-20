package cn.gym.value.impl;

import cn.gym.utils.color.ColorUtil;
import cn.gym.value.Value;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 00:47
 */
@Getter
@Setter
public class ColorValue extends Value<Color> {
    private float hue = 0;
    private float saturation = 1;
    private float brightness = 1;
    private float alpha = 1;
    private boolean rainbow = false;
    public boolean expand;

    public ColorValue(String name, Value.Dependency dependency, Color defaultValue) {
        super(name, dependency);
        set(defaultValue);
    }

    public ColorValue(String name, Color defaultValue) {
        this(name, () -> true, defaultValue);
    }

    public Color get() {
        return ColorUtil.applyOpacity(Color.getHSBColor(hue, saturation, brightness), alpha);
    }

    public void set(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = color.getAlpha() / 255.0f;
    }
}