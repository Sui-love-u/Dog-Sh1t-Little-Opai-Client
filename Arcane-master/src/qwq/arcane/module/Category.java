package qwq.arcane.module;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/Category.class */
public enum Category {
    Combat("A"),
    Movement("B"),
    Misc("C"),
    Player("D"),
    World("E"),
    Visuals("F"),
    Display("G");

    public String icon;

    Category(String icon) {
        this.icon = icon;
    }
}
