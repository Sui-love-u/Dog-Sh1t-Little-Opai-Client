package com.amaya.module.setting.impl;

import com.amaya.module.setting.Setting;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, Dependency dependency, boolean defaultValue) {
        super(name, dependency);
        this.value = defaultValue;
    }

    public BooleanSetting(String name, boolean defaultValue) {
        this(name, () -> true, defaultValue);
    }

    @Override
    public <R> R getConfigValue() {
        return (R) value;
    }

    // 添加切换方法
    public void toggle() {
        value = !value;
    }
}