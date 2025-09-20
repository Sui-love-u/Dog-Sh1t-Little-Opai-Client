package qwq.arcane.utils.animations.impl;

import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/SmoothStepAnimation.class */
public class SmoothStepAnimation extends Animation {
    public SmoothStepAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public SmoothStepAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override // qwq.arcane.utils.animations.Animation
    protected double getEquation(double x) {
        return ((-2.0d) * Math.pow(x, 3.0d)) + (3.0d * Math.pow(x, 2.0d));
    }
}
