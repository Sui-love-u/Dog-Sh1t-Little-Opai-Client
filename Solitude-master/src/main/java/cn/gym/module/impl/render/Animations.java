package cn.gym.module.impl.render;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.player.MotionEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ModeValue;
import cn.gym.value.impl.NumberValue;
import lombok.Getter;

/**
 * @Author: Guyuemang
 */
@Getter
public class Animations extends Module {
    private final BooleanValue old = new BooleanValue("Old", false);
    private final ModeValue type = new ModeValue("Block Anim", () -> !old.get(), "1.7", new String[]{"Swank", "Swing", "Swang", "Swong", "Swaing", "Punch", "Virtue", "Push", "Stella", "Styles", "Slide", "Interia", "Ethereal", "1.7", "Sigma", "Exhibition", "Old Exhibition", "Smooth", "Moon", "Leaked", "Astolfo", "Small"});
    private final BooleanValue blockWhenSwing = new BooleanValue("Block Swing", false);
    private final ModeValue hit = new ModeValue("Hit", ()-> !old.get(),"Vanilla", new String[]{"Vanilla", "Smooth"});
    private final NumberValue slowdown = new NumberValue("Slow Down", 0.0, -5.0, 15.0, 1.0);
    private final NumberValue downscaleFactor = new NumberValue("Scale", 0.0, 0.0, 0.5, .1);
    private final BooleanValue rotating = new BooleanValue("Rotating", ()-> !old.get(),false);
    private final NumberValue x = new NumberValue("Item-X", 0.0, -1.0, 1.0, .05);
    private final NumberValue y = new NumberValue("Item-Y", 0.0, -1.0, 1.0, .05);
    private final NumberValue z = new NumberValue("Item-Z", 0.0, -1.0, 1.0, .05);
    private final NumberValue bx = new NumberValue("Block-X", 0.0, -1.0, 1.0, .05);
    private final NumberValue by = new NumberValue("Block-Y", 0.0, -1.0, 1.0, .05);
    private final NumberValue bz = new NumberValue("Block-Z", 0.0, -1.0, 1.0, .05);
    private final BooleanValue walking = new BooleanValue("Funny", false);
    private final BooleanValue swingWhileUsingItem = new BooleanValue("Swing Using Item", false);

    public Animations() {
        super("Animations",Category.Render);
    }

    @EventTarget
    private void onUpdate(MotionEvent event) {
        setSuffix(type.get());

    }
}