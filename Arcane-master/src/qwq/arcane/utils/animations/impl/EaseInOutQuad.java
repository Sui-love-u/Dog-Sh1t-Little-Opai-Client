package qwq.arcane.utils.animations.impl;

import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/EaseInOutQuad.class */
public class EaseInOutQuad extends Animation {
    public EaseInOutQuad(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseInOutQuad(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override // qwq.arcane.utils.animations.Animation
    protected double getEquation(double x) {
        return x < 0.5d ? 2.0d * Math.pow(x, 2.0d) : 1.0d - (Math.pow(((-2.0d) * x) + 2.0d, 2.0d) / 2.0d);
    }
}
