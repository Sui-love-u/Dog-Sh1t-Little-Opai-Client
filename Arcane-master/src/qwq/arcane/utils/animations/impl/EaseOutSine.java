package qwq.arcane.utils.animations.impl;

import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/EaseOutSine.class */
public class EaseOutSine extends Animation {
    public EaseOutSine(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseOutSine(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override // qwq.arcane.utils.animations.Animation
    protected boolean correctOutput() {
        return true;
    }

    @Override // qwq.arcane.utils.animations.Animation
    protected double getEquation(double x) {
        return Math.sin(x * 1.5707963267948966d);
    }
}
