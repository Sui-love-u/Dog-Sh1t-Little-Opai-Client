/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package cn.gym.value.impl;

import cn.gym.value.Value;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

@Getter
@Setter
public class TextValue extends Value {
    private String text;
    private boolean onlyNumber;

    public TextValue(String name, String text, Dependency dependency) {
        super(name, dependency);
        this.text = text;
        this.onlyNumber = false;
    }

    public TextValue(String name, String text) {
        super(name, () -> true);
        this.text = text;
    }

    public TextValue(String name, String text, boolean onlyNumber, Dependency dependency) {
        super(name, dependency);
        this.text = text;
        this.onlyNumber = onlyNumber;
    }

    public TextValue(String name, String text, boolean onlyNumber) {
        super(name, () -> true);
        this.text = text;
        this.onlyNumber = onlyNumber;
    }

    public String get() {
        return text;
    }
}
