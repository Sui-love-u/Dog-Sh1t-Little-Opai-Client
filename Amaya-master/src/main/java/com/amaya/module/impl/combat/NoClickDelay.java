package com.amaya.module.impl.combat;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "NoClickDelay",category = Category.Combat)
public class NoClickDelay extends Module {
    private final BooleanSetting leftClick = new BooleanSetting("Left Click", true);
    private final BooleanSetting rightClick = new BooleanSetting("Right Click", true);

    @EventTarget
    public void onTick(TickEvent event) {
        if (!getState() || mc.thePlayer == null) {
            return;
        }

        // 移除左键点击延迟
        if (leftClick.get()) {
            mc.leftClickCounter = 0;
        }

        // 移除右键点击延迟
        if (rightClick.getValue()) {
            mc.rightClickDelayTimer = 0;
        }
    }
}
