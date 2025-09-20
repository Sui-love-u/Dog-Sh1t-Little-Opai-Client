package qwq.arcane.module.impl.visuals;

import org.lwjgl.input.Mouse;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/FreeLook.class */
public class FreeLook extends Module {
    private boolean released;
    public static ModeValue modeValue = new ModeValue("Mode", "Middle", new String[]{"Middle", "Right"});

    public FreeLook() {
        super("FreeLook", Category.Visuals);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.isPost()) {
            if (Mouse.isButtonDown(modeValue.is("Middle") ? 2 : 1)) {
                mc.gameSettings.thirdPersonView = 1;
                this.released = false;
            } else if (!this.released) {
                mc.gameSettings.thirdPersonView = 0;
                this.released = true;
            }
        }
    }
}
