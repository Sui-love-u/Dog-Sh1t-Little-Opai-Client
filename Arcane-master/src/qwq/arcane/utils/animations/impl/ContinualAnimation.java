package qwq.arcane.utils.animations.impl;

import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/ContinualAnimation.class */
public class ContinualAnimation {
    private float output;
    private float endpoint;
    private Animation animation = new SmoothStepAnimation(0, 0.0d, Direction.BACKWARDS);

    public void setOutput(float output) {
        this.output = output;
    }

    public void setEndpoint(float endpoint) {
        this.endpoint = endpoint;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public void animate(float destination, int ms) {
        this.output = (float) (this.endpoint - this.animation.getOutput().doubleValue());
        this.endpoint = destination;
        if (this.output != this.endpoint - destination) {
            this.animation = new SmoothStepAnimation(ms, this.endpoint - this.output, Direction.BACKWARDS);
        }
    }

    public boolean isDone() {
        return this.output == this.endpoint || this.animation.isDone();
    }

    public float getOutput() {
        this.output = (float) (this.endpoint - this.animation.getOutput().doubleValue());
        return this.output;
    }
}
