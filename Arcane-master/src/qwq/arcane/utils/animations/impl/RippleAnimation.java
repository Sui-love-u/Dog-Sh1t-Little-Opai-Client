package qwq.arcane.utils.animations.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import qwq.arcane.utils.animations.AnimationUtils;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.StencilUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/RippleAnimation.class */
public class RippleAnimation {
    public final List<Ripple> ripples = new ArrayList();

    public void addRipple(float x, float y, float radius, float speed) {
        this.ripples.add(new Ripple(x, y, radius, speed));
    }

    public void mouseClicked(float mouseX, float mouseY) {
        this.ripples.add(new Ripple(mouseX, mouseY, 100.0f, 1.0f));
    }

    public void draw(float x, float y, float width, float height) {
        if (!this.ripples.isEmpty()) {
            StencilUtils.initStencilToWrite();
            RenderUtil.drawRect(x, y, width, height, -1);
            StencilUtils.readStencilBuffer(528);
            for (Ripple c : this.ripples) {
                c.progress = AnimationUtils.animateSmooth(c.progress, c.topRadius, c.speed / 10.0f);
                RenderUtil.drawCircleCGUI(c.x, c.y, c.progress, new Color(1.0f, 1.0f, 1.0f, (1.0f - Math.min(1.0f, Math.max(0.0f, c.progress / c.topRadius))) / 2.0f).getRGB());
            }
            StencilUtils.endStencilBuffer();
        }
    }

    public void draw(Runnable context) {
        if (!this.ripples.isEmpty()) {
            StencilUtils.initStencilToWrite();
            context.run();
            StencilUtils.readStencilBuffer(528);
            for (Ripple c : this.ripples) {
                c.progress = AnimationUtils.animateSmooth(c.progress, c.topRadius, c.speed / 10.0f);
                RenderUtil.drawCircleCGUI(c.x, c.y, c.progress, new Color(1.0f, 1.0f, 1.0f, (1.0f - Math.min(1.0f, Math.max(0.0f, c.progress / c.topRadius))) / 2.0f).getRGB());
            }
            StencilUtils.endStencilBuffer();
        }
    }

    /* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/RippleAnimation$Ripple.class */
    public static class Ripple {
        public float x;
        public float y;
        public float topRadius;
        public float speed;
        public float alpha = 200.0f;
        public float progress;
        public boolean complete;

        public Ripple(float x, float y, float rad, float speed) {
            this.x = x;
            this.y = y;
            this.topRadius = rad;
            this.speed = speed;
        }
    }
}
