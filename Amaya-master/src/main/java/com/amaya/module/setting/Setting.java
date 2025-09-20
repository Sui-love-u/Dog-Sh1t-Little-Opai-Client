package com.amaya.module.setting;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public abstract class Setting<T> {
    // UI相关属性
    public float animation = 0f;
    @Getter
    public float height = 22f;
    
    // 设置项属性
    @Getter @Setter
    protected final Dependency dependency;
    protected T value;
    protected final String name;

    // 构造方法
    public Setting(String name, Dependency dependency) {
        this.name = name;
        this.dependency = dependency;
    }
    
    public Setting(String name, String description) {
        this(name, () -> true);
    }
    
    public Setting(String name) {
        this(name, () -> true);
    }
    
    public String getName() {
        return this.name;
    }

    public T getValue() {
        return this.value;
    }
    
    public T get() {
        return this.value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public void set(T value) {
        this.value = value;
    }
    
    public abstract <R> R getConfigValue();
    
    public boolean isHidden() {
        return !isAvailable();
    }
    
    public boolean isAvailable() {
        return dependency != null && this.dependency.check();
    }
    
    @FunctionalInterface
    public interface Dependency {
        boolean check();
    }
    
    @Getter @Setter
    private ChangeListener<T> changeListener;
    
    public interface ChangeListener<T> {
        void onChange(T newValue, T oldValue);
    }
    
    protected void fireChangeEvent(T oldValue) {
        if (changeListener != null) {
            changeListener.onChange(value, oldValue);
        }
    }
}