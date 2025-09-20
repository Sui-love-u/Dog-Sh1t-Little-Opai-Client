package cn.gym.module.impl.player;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.player.MotionEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 15:57
 */
public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool",Category.Player);
    }

    private int oldSlot;
    private int tick;

    @EventTarget
    public void onClick(MotionEvent event) {
        if(event.isPre()){
            if (mc.playerController.isBreakingBlock()) {
                tick++;

                if (tick == 1) {
                    oldSlot = mc.thePlayer.inventory.currentItem;
                }

                mc.thePlayer.updateTool(mc.objectMouseOver.getBlockPos());
            } else if (tick > 0) {
                mc.thePlayer.inventory.currentItem = oldSlot;

                tick = 0;
            }
        }
    }
}
