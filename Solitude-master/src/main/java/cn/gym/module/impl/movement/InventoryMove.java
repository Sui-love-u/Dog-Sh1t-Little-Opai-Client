package cn.gym.module.impl.movement;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.player.MoveInputEvent;
import cn.gym.events.impl.player.UpdateEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.utils.player.MovementUtils;
import cn.gym.value.impl.BooleanValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 14:19
 */
public class InventoryMove extends Module {
    private final BooleanValue cancelInventory = new BooleanValue("NoInv", false);
    private final BooleanValue cancelChest = new BooleanValue("No Chest", false);
    private final BooleanValue wdChest = new BooleanValue("Watchdog Chest", false);
    private final BooleanValue wdInv = new BooleanValue("Watchdog Inv", false);
    private final KeyBinding[] keyBindings = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindJump};

    public InventoryMove() {
        super("InventoryMove",Category.Movement);
    }

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
