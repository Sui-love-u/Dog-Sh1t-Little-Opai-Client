package qwq.arcane.utils.animations.impl;

import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/impl/ElasticAnimation.class */
public class ElasticAnimation extends Animation {
    float easeAmount;
    float smooth;
    boolean reallyElastic;

    public ElasticAnimation(int ms, double endPoint, float elasticity, float smooth, boolean moreElasticity) {
        super(ms, endPoint);
        this.easeAmount = elasticity;
        this.smooth = smooth;
        this.reallyElastic = moreElasticity;
    }

    public ElasticAnimation(int ms, double endPoint, float elasticity, float smooth, boolean moreElasticity, Direction direction) {
        super(ms, endPoint, direction);
        this.easeAmount = elasticity;
        this.smooth = smooth;
        this.reallyElastic = moreElasticity;
    }

    @Override // qwq.arcane.utils.animations.Animation
    protected double getEquation(double x) {
        double x2 = Math.pow(x, this.smooth);
        double elasticity = this.easeAmount * 0.1f;
        return (Math.pow(2.0d, (-10.0d) * (this.reallyElastic ? Math.sqrt(x2) : x2)) * Math.sin((x2 - (elasticity / 4.0d)) * (6.283185307179586d / elasticity))) + 1.0d;
    }
}
