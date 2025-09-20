package qwq.arcane.module.impl.visuals;

import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/Animations.class */
public class Animations extends Module {
    private final BoolValue old;
    private final ModeValue type;
    private final BoolValue blockWhenSwing;
    private final ModeValue hit;
    private final NumberValue slowdown;
    private final NumberValue downscaleFactor;
    private final BoolValue rotating;
    private final BoolValue swingWhileUsingItem;
    private final NumberValue x;
    private final NumberValue y;
    private final NumberValue z;
    private final NumberValue bx;
    private final NumberValue by;
    private final NumberValue bz;

    public BoolValue getOld() {
        return this.old;
    }

    public ModeValue getType() {
        return this.type;
    }

    public BoolValue getBlockWhenSwing() {
        return this.blockWhenSwing;
    }

    public ModeValue getHit() {
        return this.hit;
    }

    public NumberValue getSlowdown() {
        return this.slowdown;
    }

    public NumberValue getDownscaleFactor() {
        return this.downscaleFactor;
    }

    public BoolValue getRotating() {
        return this.rotating;
    }

    public BoolValue getSwingWhileUsingItem() {
        return this.swingWhileUsingItem;
    }

    public NumberValue getX() {
        return this.x;
    }

    public NumberValue getY() {
        return this.y;
    }

    public NumberValue getZ() {
        return this.z;
    }

    public NumberValue getBx() {
        return this.bx;
    }

    public NumberValue getBy() {
        return this.by;
    }

    public NumberValue getBz() {
        return this.bz;
    }

    public Animations() {
        super("Animations", Category.Visuals);
        this.old = new BoolValue("Old", false);
        this.type = new ModeValue("Block Anim", () -> {
            return !this.old.get().booleanValue();
        }, "Sigma", new String[]{"Swank", "Swing", "Swang", "Swong", "Swaing", "Punch", "Virtue", "Push", "Stella", "Styles", "Slide", "Interia", "Ethereal", "1.7", "Sigma", "Exhibition", "Old Exhibition", "Smooth", "Moon", "Leaked", "Astolfo", "Small"});
        this.blockWhenSwing = new BoolValue("Block Swing", false);
        this.hit = new ModeValue("Hit", () -> {
            return !this.old.get().booleanValue();
        }, "Vanilla", new String[]{"Vanilla", "Smooth"});
        this.slowdown = new NumberValue("Slow Down", 0.0d, -5.0d, 15.0d, 1.0d);
        this.downscaleFactor = new NumberValue("Scale", 0.0d, 0.0d, 0.5d, 0.1d);
        this.rotating = new BoolValue("Rotating", () -> {
            return !this.old.get().booleanValue();
        }, false);
        this.swingWhileUsingItem = new BoolValue("Swing Using Item", false);
        this.x = new NumberValue("Item-X", 0.0d, -1.0d, 1.0d, 0.05d);
        this.y = new NumberValue("Item-Y", 0.0d, -1.0d, 1.0d, 0.05d);
        this.z = new NumberValue("Item-Z", 0.0d, -1.0d, 1.0d, 0.05d);
        this.bx = new NumberValue("Block-X", 0.0d, -1.0d, 1.0d, 0.05d);
        this.by = new NumberValue("Block-Y", 0.0d, -1.0d, 1.0d, 0.05d);
        this.bz = new NumberValue("Block-Z", 0.0d, -1.0d, 1.0d, 0.05d);
    }

    @EventTarget
    private void onUpdate(MotionEvent event) {
        setsuffix(this.type.get());
    }
}
