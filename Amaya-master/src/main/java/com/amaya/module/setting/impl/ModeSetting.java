package com.amaya.module.setting.impl;

import com.amaya.module.setting.Setting;
import lombok.Getter;

import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class ModeSetting extends Setting<String> {
    private final String[] modes;
    public boolean expand;

    public ModeSetting(String name, Dependency dependency, String defaultValue, String[] modes) {
        super(name, dependency);
        this.value = defaultValue;
        this.modes = modes;
    }

    public ModeSetting(String name, String defaultValue, String[] modes) {
        this(name, () -> true, defaultValue, modes);
    }
    public boolean is(String sb) {
        return this.getValue().equalsIgnoreCase(sb);
    }

    public String[] getModes() {
        return this.modes;
    }
    public void setMode(String mode) {
        String[] arrV = this.modes;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            String e = arrV[n2];
            if (e == null)
                return;
            if (e.equalsIgnoreCase(mode)) {
                this.setValue(e);
            }
            ++n2;
        }
    }
    @Override
    public <R> R getConfigValue() {
        return (R) value;
    }
}