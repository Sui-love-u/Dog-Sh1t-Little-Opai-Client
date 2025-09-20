package qwq.arcane.value.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import qwq.arcane.value.Value;

/* loaded from: Arcane 8.10.jar:qwq/arcane/value/impl/MultiBooleanValue.class */
public class MultiBooleanValue extends Value {
    public List<BoolValue> options;
    public int index;

    public MultiBooleanValue(String name, Value.Dependency dependency, List<BoolValue> options) {
        super(name, dependency);
        this.options = options;
        this.index = options.size();
    }

    public MultiBooleanValue(String name, List<BoolValue> options) {
        super(name);
        this.options = options;
        this.index = options.size();
    }

    public boolean isEnabled(String name) {
        return ((BoolValue) Objects.requireNonNull(this.options.stream().filter(option -> {
            return option.getName().equalsIgnoreCase(name);
        }).findFirst().orElse(null))).get().booleanValue();
    }

    public void set(String name, boolean value) {
        ((BoolValue) Objects.requireNonNull(this.options.stream().filter(option -> {
            return option.getName().equalsIgnoreCase(name);
        }).findFirst().orElse(null))).set(Boolean.valueOf(value));
    }

    public List<BoolValue> getToggled() {
        return (List) this.options.stream().filter((v0) -> {
            return v0.get();
        }).collect(Collectors.toList());
    }

    public String isEnabled() {
        List<String> includedOptions = new ArrayList<>();
        for (BoolValue option : this.options) {
            if (option.get().booleanValue()) {
                includedOptions.add(option.getName());
            }
        }
        return String.join(", ", includedOptions);
    }

    public void set(int index, boolean value) {
        this.options.get(index).set(Boolean.valueOf(value));
    }

    public boolean isEnabled(int index) {
        return this.options.get(index).get().booleanValue();
    }

    public List<BoolValue> getValues() {
        return this.options;
    }
}
