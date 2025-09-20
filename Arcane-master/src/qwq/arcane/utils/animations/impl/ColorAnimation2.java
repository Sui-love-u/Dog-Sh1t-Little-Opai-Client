package qwq.arcane.utils.animations.impl;

import java.awt.Color;
import qwq.arcane.utils.animations.AnimationUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/ColorAnimation2.class */
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
        this.color = new Color((int) this.r, (int) this.g, (int) this.b, (int) this.a);
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
