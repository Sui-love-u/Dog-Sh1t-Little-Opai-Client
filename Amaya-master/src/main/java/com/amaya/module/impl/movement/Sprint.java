package com.amaya.module.impl.movement;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.StrafeEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@ModuleInfo(name = "Sprint",category = Category.Movement)
public class Sprint extends Module {
    public Sprint(){
        this.setState(true);
    }

    @EventTarget
    public void onStrafe(StrafeEvent event){
        mc.gameSettings.keyBindSprint.setPressed(true);
    }
}
