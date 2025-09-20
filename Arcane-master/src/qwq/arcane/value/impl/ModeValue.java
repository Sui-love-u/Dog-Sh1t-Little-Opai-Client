package qwq.arcane.value.impl;

import qwq.arcane.value.Value;

/* loaded from: Arcane 8.10.jar:qwq/arcane/value/impl/ModeValue.class */
public class ModeValue extends Value<String> {
    private final String[] modes;
    public boolean expand;

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public String[] getModes() {
        return this.modes;
    }

    public boolean isExpand() {
        return this.expand;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ModeValue(String name, Value.Dependency dependency, String str, String[] modes) {
        super(name, dependency);
        this.value = str;
        this.modes = modes;
    }

    public ModeValue(String name, String defaultValue, String[] modes) {
        this(name, () -> {
            return true;
        }, defaultValue, modes);
    }

    public boolean is(String sb) {
        return getValue().equalsIgnoreCase(sb);
    }

    public void setMode(String mode) {
        String e;
        String[] arrV = this.modes;
        int n = arrV.length;
        for (int n2 = 0; n2 < n && (e = arrV[n2]) != null; n2++) {
            if (e.equalsIgnoreCase(mode)) {
                setValue(e);
            }
        }
    }
}
