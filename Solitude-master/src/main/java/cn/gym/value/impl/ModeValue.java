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
public class ModeValue extends Value<String> {
    private final String[] modes;
    public boolean expand;

    public ModeValue(String name, Dependency dependency, String defaultValue, String[] modes) {
        super(name, dependency);
        this.value = defaultValue;
        this.modes = modes;
    }

    public ModeValue(String name, String defaultValue, String[] modes) {
        this(name, () -> true, defaultValue, modes);
    }
    public boolean is(String sb) {
        return this.getValue().equalsIgnoreCase(sb);
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
}