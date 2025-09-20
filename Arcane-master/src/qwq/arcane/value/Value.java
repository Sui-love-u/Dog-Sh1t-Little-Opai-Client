package qwq.arcane.value;

/* loaded from: Arcane 8.10.jar:qwq/arcane/value/Value.class */
public abstract class Value<T> {
    protected final Dependency dependency;
    protected T value;
    protected final String name;

    @FunctionalInterface
    /* loaded from: Arcane 8.10.jar:qwq/arcane/value/Value$Dependency.class */
    public interface Dependency {
        boolean check();
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Dependency getDependency() {
        return this.dependency;
    }

    public T getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public Value(String name, Dependency dependency) {
        this.name = name;
        this.dependency = dependency;
    }

    public Value(String name, String description) {
        this(name, () -> {
            return true;
        });
    }

    public Value(String name) {
        this(name, () -> {
            return true;
        });
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public boolean isAvailable() {
        return this.dependency != null && this.dependency.check();
    }
}
