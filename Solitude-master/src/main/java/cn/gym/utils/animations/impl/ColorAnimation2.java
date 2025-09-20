package cn.gym.utils.animations.impl;

import cn.gym.utils.animations.AnimationUtils;

import java.awt.*;

public class ColorAnimation2 {
    private Color color;

    private float r;
    private float g;
    private float b;
    private float a;

    public ColorAnimation2(Color color) {
        this.color = color;
    }

    public void animateTo(Color color, float speed) {
        this.r = AnimationUtils.animate(this.r, color.getRed(), speed);
        this.g = AnimationUtils.animate(this.g, color.getGreen(), speed);
        this.b = AnimationUtils.animate(this.b, color.getBlue(), speed);
        this.a = AnimationUtils.animate(this.a, color.getAlpha(), speed);

        this.color = new Color((int) r, (int) g, (int) b, (int) a);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
