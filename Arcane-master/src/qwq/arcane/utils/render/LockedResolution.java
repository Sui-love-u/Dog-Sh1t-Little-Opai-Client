package qwq.arcane.utils.render;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/LockedResolution.class */
public final class LockedResolution {
    public static final int SCALE_FACTOR = 2;
    private final int width;
    private final int height;

    public LockedResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
