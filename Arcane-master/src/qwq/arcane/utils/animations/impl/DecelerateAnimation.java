package qwq.arcane.utils.animations.impl;

import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/DecelerateAnimation.class */
public class DecelerateAnimation extends Animation {
    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override // qwq.arcane.utils.animations.Animation
    protected double getEquation(double x) {
        return 1.0d - ((x - 1.0d) * (x - 1.0d));
    }
}
