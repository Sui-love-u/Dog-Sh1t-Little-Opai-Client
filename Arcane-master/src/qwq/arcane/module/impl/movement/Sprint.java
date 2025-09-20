package qwq.arcane.module.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.player.MovementUtil;
import qwq.arcane.value.impl.BoolValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/movement/Sprint.class */
public class Sprint extends Module {
    private final BoolValue omni;
    public static boolean keepSprinting = false;

    public Sprint() {
        super("Sprint", Category.Movement);
        this.omni = new BoolValue("Omni", false);
    }

    @EventTarget
    public void onSuffix(UpdateEvent event) {
        setsuffix("Omni " + this.omni.get().toString());
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        mc.thePlayer.omniSprint = false;
        keepSprinting = false;
        super.onDisable();
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (!keepSprinting) {
            if (!isEnabled(Scaffold.class)) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            }
        } else {
            keepSprinting = false;
        }
        if (this.omni.get().booleanValue()) {
            mc.thePlayer.omniSprint = MovementUtil.isMoving();
        }
    }
}
