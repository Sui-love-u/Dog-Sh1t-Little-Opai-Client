package qwq.arcane.value.impl;

import qwq.arcane.value.Value;

/* loaded from: Arcane 8.10.jar:qwq/arcane/value/impl/TextValue.class */
public class TextValue extends Value {
    private String text;
    private boolean onlyNumber;

    public void setText(String text) {
        this.text = text;
    }

    public void setOnlyNumber(boolean onlyNumber) {
        this.onlyNumber = onlyNumber;
    }

    public String getText() {
        return this.text;
    }

    public boolean isOnlyNumber() {
        return this.onlyNumber;
    }

    public TextValue(String name, String text, Value.Dependency dependency) {
        super(name, dependency);
        this.text = text;
        this.onlyNumber = false;
    }

    public TextValue(String name, String text) {
        super(name, () -> {
            return true;
        });
        this.text = text;
    }

    public TextValue(String name, String text, boolean onlyNumber, Value.Dependency dependency) {
        super(name, dependency);
        this.text = text;
        this.onlyNumber = onlyNumber;
    }

    public TextValue(String name, String text, boolean onlyNumber) {
        super(name, () -> {
            return true;
        });
        this.text = text;
        this.onlyNumber = onlyNumber;
    }

    @Override // qwq.arcane.value.Value
    public String get() {
        return this.text;
    }
}
