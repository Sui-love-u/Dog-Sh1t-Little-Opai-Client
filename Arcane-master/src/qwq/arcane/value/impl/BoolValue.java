package qwq.arcane.value.impl;

import qwq.arcane.value.Value;

/* loaded from: Arcane 8.10.jar:qwq/arcane/value/impl/BoolValue.class */
public class BoolValue extends Value<Boolean> {
    /* JADX WARN: Type inference failed for: r1v2, types: [T, java.lang.Boolean] */
    public BoolValue(String name, Value.Dependency dependency, boolean defaultValue) {
        super(name, dependency);
        this.value = Boolean.valueOf(defaultValue);
    }

    public BoolValue(String name, boolean defaultValue) {
        this(name, () -> {
            return true;
        }, defaultValue);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v6, types: [T, java.lang.Boolean] */
    public void toggle() {
        this.value = Boolean.valueOf(!((Boolean) this.value).booleanValue());
    }
}
