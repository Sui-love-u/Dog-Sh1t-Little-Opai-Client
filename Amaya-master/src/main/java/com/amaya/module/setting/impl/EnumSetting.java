package com.amaya.module.setting.impl;

import com.amaya.module.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class EnumSetting extends Setting {
    public List<BooleanSetting> options;
    public int index;

    public EnumSetting(String name,Dependency dependency, List<BooleanSetting> options) {
        super(name,dependency);
        this.options = options;
        index = options.size();
    }

    public EnumSetting(String name, List<BooleanSetting> options) {
        super(name);
        this.options = options;
        index = options.size();
    }

    public boolean isEnabled(String name) {
        return Objects.requireNonNull(this.options.stream().filter((option) -> option.getName().equalsIgnoreCase(name)).findFirst().orElse(null)).get();
    }

    public void set(String name, boolean value) {
        Objects.requireNonNull(this.options.stream().filter((option) -> option.getName().equalsIgnoreCase(name)).findFirst().orElse(null)).set(value);
    }

    public List<BooleanSetting> getToggled() {
        return this.options.stream().filter(BooleanSetting::get).collect(Collectors.toList());
    }

    public String isEnabled() {
        List<String> includedOptions = new ArrayList<>();
        for (BooleanSetting option : options) {
            if (option.get()) {
                includedOptions.add(option.getName());
            }
        }
        return String.join(", ", includedOptions);
    }

    public void set(int index, boolean value) {
        this.options.get(index).set(value);
    }

    public boolean isEnabled(int index) {
        return this.options.get(index).get();
    }

    public List<BooleanSetting> getValues() {
        return this.options;
    }

    @Override
    public List<BooleanSetting> getConfigValue() {
        return options;
    }
}