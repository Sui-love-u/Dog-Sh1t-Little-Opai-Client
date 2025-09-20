package com.amaya.module.impl.render;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import lombok.Getter;

/**
 * @Author: Guyuemang
 */
@ModuleInfo(name = "Animations",category = Category.Render)
@Getter
public class Animations extends Module {
    private final BooleanSetting old = new BooleanSetting("Old", false);
    private final ModeSetting type = new ModeSetting("Block Anim", () -> !old.get(), "1.7", new String[]{"Swank", "Swing", "Swang", "Swong", "Swaing", "Punch", "Virtue", "Push", "Stella", "Styles", "Slide", "Interia", "Ethereal", "1.7", "Sigma", "Exhibition", "Old Exhibition", "Smooth", "Moon", "Leaked", "Astolfo", "Small"});
    private final BooleanSetting blockWhenSwing = new BooleanSetting("Block When Swing", false);
    private final ModeSetting hit = new ModeSetting("Hit", ()-> !old.get(),"Vanilla", new String[]{"Vanilla", "Smooth"});
    private final NumberSetting slowdown = new NumberSetting("Slow Down", 0.0, -5.0, 15.0, 1.0);
    private final NumberSetting downscaleFactor = new NumberSetting("Scale", 0.0, 0.0, 0.5, .1);
    private final BooleanSetting rotating = new BooleanSetting("Rotating", ()-> !old.get(),false);
    private final NumberSetting x = new NumberSetting("Item-X", 0.0, -1.0, 1.0, .05);
    private final NumberSetting y = new NumberSetting("Item-Y", 0.0, -1.0, 1.0, .05);
    private final NumberSetting z = new NumberSetting("Item-Z", 0.0, -1.0, 1.0, .05);
    private final NumberSetting bx = new NumberSetting("Block-X", 0.0, -1.0, 1.0, .05);
    private final NumberSetting by = new NumberSetting("Block-Y", 0.0, -1.0, 1.0, .05);
    private final NumberSetting bz = new NumberSetting("Block-Z", 0.0, -1.0, 1.0, .05);
    private final BooleanSetting walking = new BooleanSetting("Funny", false);
    private final BooleanSetting swingWhileUsingItem = new BooleanSetting("Swing While Using Item", false);

    @EventTarget
    private void onUpdate(MotionEvent event) {
        setSuffix(type.get());

    }
}