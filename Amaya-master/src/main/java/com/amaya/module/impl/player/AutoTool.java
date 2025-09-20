package com.amaya.module.impl.player;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.PreUpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "AutoTool", category = Category.Player)
public class AutoTool extends Module {
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