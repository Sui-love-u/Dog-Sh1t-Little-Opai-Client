package qwq.arcane.utils.render;

import java.awt.Color;
import qwq.arcane.utils.color.ColorUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/Particle.class */
public class Particle {
    public float x;
    public float y;
    public float adjustedX;
    public float adjustedY;
    public float deltaX;
    public float deltaY;
    public float size;
    public int opacity;
    public Color color;

    public void render2D() {
        RenderUtil.drawGoodCircle(this.x + this.adjustedX + (this.size / 2.0f), this.y + this.adjustedY + (this.size / 2.0f), this.size / 2.0f, ColorUtil.applyOpacity(this.color, this.opacity / 255.0f).getRGB());
    }

    public void updatePosition() {
        for (int i = 1; i <= 2; i++) {
            this.adjustedX += this.deltaX;
            this.adjustedY += this.deltaY;
            this.deltaY *= 0.97f;
            this.deltaX *= 0.97f;
            this.opacity = (int) (this.opacity - 1.0f);
            if (this.opacity < 1) {
                this.opacity = 1;
            }
        }
    }

    public void init(float x, float y, float deltaX, float deltaY, float size, int color) {
        this.x = x;
        this.y = y;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.size = size;
        this.opacity = 254;
        this.color = new Color(color);
    }
}
