package com.amaya.module.setting.impl;

import com.amaya.module.setting.Setting;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class NumberSetting extends Setting<Double> {
    public float animatedPercentage;
    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, Dependency dependency, double defaultValue, double min, double max, double step) {
        super(name, dependency);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public NumberSetting(String name, double defaultValue, double min, double max, double step) {
        this(name, () -> true, defaultValue, min, max, step);
    }

    @Override
    public <R> R getConfigValue() {
        return (R) value;
    }

    public Double getMin() {
        return this.min;
    }

    public Double getMax() {
        return this.max;
    }

    public Double getStep() {
        return this.step;
    }

    @Override
    public void setValue(Double value) {
        // 确保值在范围内
        if (value < min) {
            super.setValue(min);
        } else if (value > max) {
            super.setValue(max);
        } else {
            super.setValue(value);
        }
    }
}