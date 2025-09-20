package com.amaya.utils.animations.impl;

import com.amaya.utils.animations.AnimationUtils;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.StencilUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RippleAnimation {
    public final List<Ripple> ripples = new ArrayList<Ripple>();

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

    public static class Ripple {
        public float x;
        public float y;
        public float topRadius;
        public float speed;
        public float alpha;
        public float progress;
        public boolean complete;

        public Ripple(float x, float y, float rad, float speed) {
            this.x = x;
            this.y = y;
            this.alpha = 200.0f;
            this.topRadius = rad;
            this.speed = speed;
        }
    }
}

