package com.amaya.module.setting.impl;

import com.amaya.module.setting.Setting;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class StringSetting extends Setting<String> {
   @Getter @Setter
   private String text;
    @Getter @Setter
   private boolean onlyNumber;

    public StringSetting(String name, Dependency dependency, String text) {
        super(name, dependency);
        this.text = text;
        this.value = text;
    }

    public StringSetting(String name, String defaultValue) {
        this(name, () -> true, defaultValue);
    }

    @Override
    public <R> R getConfigValue() {
        return (R) value;
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }
}