package qwq.arcane.module.impl.world;

import net.minecraft.item.ItemBlock;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/world/FastPlace.class */
public class FastPlace extends Module {
    public final NumberValue speed;

    public FastPlace() {
        super("FastPlace", Category.World);
        this.speed = new NumberValue("Speed", 1.0d, 0.0d, 4.0d, 1.0d);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setsuffix(String.valueOf(this.speed.get()));
        if ((mc.thePlayer != null || mc.theWorld != null) && mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
            mc.rightClickDelayTimer = this.speed.getValue().intValue();
        }
    }
}
