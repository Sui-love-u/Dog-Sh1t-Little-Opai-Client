package cn.gym.module.impl.movement;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.player.StrafeEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 14:19
 */
public class Sprint extends Module {
    public Sprint() {
        super("Sprint",Category.Movement);
    }

    @EventTarget
    public void onStrafe(StrafeEvent event){
        mc.gameSettings.keyBindSprint.setPressed(true);
    }
}
