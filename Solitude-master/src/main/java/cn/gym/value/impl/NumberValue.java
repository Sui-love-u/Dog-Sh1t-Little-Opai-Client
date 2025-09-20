package cn.gym.value.impl;

import cn.gym.value.Value;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 00:47
 */
@Getter
@Setter
public class NumberValue extends Value<Double> {
    public float animatedPercentage;
    private final double min;
    private final double max;
    private final double step;

    public NumberValue(String name, Dependency dependency, double defaultValue, double min, double max, double step) {
        super(name, dependency);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public NumberValue(String name, double defaultValue, double min, double max, double step) {
        this(name, () -> true, defaultValue, min, max, step);
    }

    @Override
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