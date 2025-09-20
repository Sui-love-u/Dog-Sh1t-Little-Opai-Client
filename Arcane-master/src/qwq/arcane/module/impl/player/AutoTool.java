package qwq.arcane.module.impl.player;

import net.minecraft.util.MovingObjectPosition;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.TickEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.player.PlayerUtil;
import qwq.arcane.utils.player.SlotSpoofComponent;
import qwq.arcane.value.impl.BoolValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/player/AutoTool.class */
public class AutoTool extends Module {
    public final BoolValue ignoreUsingItem;
    public final BoolValue spoof;
    public final BoolValue switchBack;
    private int oldSlot;
    public boolean wasDigging;

    public AutoTool() {
        super("AutoTool", Category.Player);
        this.ignoreUsingItem = new BoolValue("Ignore Using Item", false);
        this.spoof = new BoolValue("Spoof", false);
        this.switchBack = new BoolValue("Switch Back", () -> {
            return !this.spoof.get().booleanValue();
        }, true);
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        if (this.wasDigging) {
            mc.thePlayer.inventory.currentItem = this.oldSlot;
            this.wasDigging = false;
        }
        SlotSpoofComponent.stopSpoofing();
    }

    @EventTarget
    public void onTick(TickEvent event) {
        setsuffix("Spoof");
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        if (mc.gameSettings.keyBindAttack.isKeyDown() && (((this.ignoreUsingItem.get().booleanValue() && !mc.thePlayer.isUsingItem()) || !this.ignoreUsingItem.get().booleanValue()) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && PlayerUtil.findTool(mc.objectMouseOver.getBlockPos()) != -1)) {
            if (!this.wasDigging) {
                this.oldSlot = mc.thePlayer.inventory.currentItem;
                if (this.spoof.get().booleanValue()) {
                    SlotSpoofComponent.startSpoofing(this.oldSlot);
                }
            }
            mc.thePlayer.inventory.currentItem = PlayerUtil.findTool(mc.objectMouseOver.getBlockPos());
            this.wasDigging = true;
            return;
        }
        if (this.wasDigging && (this.switchBack.get().booleanValue() || this.spoof.get().booleanValue())) {
            mc.thePlayer.inventory.currentItem = this.oldSlot;
            SlotSpoofComponent.stopSpoofing();
            this.wasDigging = false;
            return;
        }
        this.oldSlot = mc.thePlayer.inventory.currentItem;
    }
}
