package qwq.arcane.utils.animations.impl;

import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/EaseBackIn.class */
public class EaseBackIn extends Animation {
    private final float easeAmount;

    public EaseBackIn(int ms, double endPoint, float easeAmount) {
        super(ms, endPoint);
        this.easeAmount = easeAmount;
    }

    public EaseBackIn(int ms, double endPoint, float easeAmount, Direction direction) {
        super(ms, endPoint, direction);
        this.easeAmount = easeAmount;
    }

    @Override // qwq.arcane.utils.animations.Animation
    protected boolean correctOutput() {
        return true;
    }

    @Override // qwq.arcane.utils.animations.Animation
    protected double getEquation(double x) {
        float shrink = this.easeAmount + 1.0f;
        return Math.max(0.0d, 1.0d + (shrink * Math.pow(x - 1.0d, 3.0d)) + (this.easeAmount * Math.pow(x - 1.0d, 2.0d)));
    }
}
