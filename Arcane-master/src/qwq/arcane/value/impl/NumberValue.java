package qwq.arcane.value.impl;

import qwq.arcane.value.Value;

/* loaded from: Arcane 8.10.jar:qwq/arcane/value/impl/NumberValue.class */
public class NumberValue extends Value<Double> {
    public float animatedPercentage;
    private final double min;
    private final double max;
    private final double step;

    public void setAnimatedPercentage(float animatedPercentage) {
        this.animatedPercentage = animatedPercentage;
    }

    public float getAnimatedPercentage() {
        return this.animatedPercentage;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public double getStep() {
        return this.step;
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [T, java.lang.Double] */
    public NumberValue(String name, Value.Dependency dependency, double defaultValue, double min, double max, double step) {
        super(name, dependency);
        this.value = Double.valueOf(defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public NumberValue(String name, double defaultValue, double min, double max, double step) {
        this(name, () -> {
            return true;
        }, defaultValue, min, max, step);
    }

    @Override // qwq.arcane.value.Value
    public void setValue(Double value) {
        if (value < min) {
            super.setValue(min);
        } else if (value > max) {
            super.setValue(max);
        } else {
            super.setValue(value);
        }
    }
}
