package com.amaya.module.impl.movement;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MoveInputEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.utils.player.MovementUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

/**
 * @Author: Guyuemang
 * 2025/4/30
 */
@ModuleInfo(name = "InventoryMove",category = Category.Movement)
public class InventoryMove extends Module {
    private final BooleanSetting cancelInventory = new BooleanSetting("NoInv", false);
    private final BooleanSetting cancelChest = new BooleanSetting("No Chest", false);
    private final BooleanSetting wdChest = new BooleanSetting("Watchdog Chest", false);
    private final BooleanSetting wdInv = new BooleanSetting("Watchdog Inv", false);
    private final KeyBinding[] keyBindings = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindJump};

    @Override
    public void onDisable() {
        for (KeyBinding keyBinding : this.keyBindings) {
            KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
        }
    }
    @EventTarget
    public void onMoveInput(MoveInputEvent event) {
        if (wdChest.get() && mc.currentScreen instanceof GuiChest)
            event.setJumping(false);
        if (wdInv.get() && mc.currentScreen instanceof GuiInventory)
            event.setJumping(false);
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (!(mc.currentScreen instanceof GuiChat) && !(mc.currentScreen instanceof GuiIngameMenu)) {
            if (cancelInventory.get() && (mc.currentScreen instanceof GuiContainer))
                return;

            if (cancelChest.get() && mc.currentScreen instanceof GuiChest)
                return;

            for (KeyBinding keyBinding : this.keyBindings) {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), GameSettings.isKeyDown(keyBinding));
            }

            if (wdChest.get() && mc.currentScreen instanceof GuiChest)
                MovementUtils.stopXZ();

            if (wdInv.get() && mc.currentScreen instanceof GuiInventory)
                MovementUtils.stopXZ();

        }
    }
}
