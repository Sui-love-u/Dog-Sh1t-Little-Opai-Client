package com.amaya.module.setting.impl;

import com.amaya.module.setting.Setting;
import com.amaya.utils.render.ColorUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@Getter
@Setter
public class ColorSetting extends Setting<Color> {
    private float hue = 0;
    private float saturation = 1;
    private float brightness = 1;
    private float alpha = 1;
    private boolean rainbow = false;
    public boolean expand;

    public ColorSetting(String name, Dependency dependency, Color defaultValue) {
        super(name, dependency);
        set(defaultValue);
    }
    public ColorSetting(String name, Color defaultValue) {
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
    @Override
    public <R> R getConfigValue() {
        return null;
    }
}