package qwq.arcane.utils.player;

import net.minecraft.item.ItemStack;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.utils.Instance;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/SlotSpoofComponent.class */
public class SlotSpoofComponent implements Instance {
    private static int spoofedSlot;
    private static boolean spoofing;

    public static boolean isSpoofing() {
        return spoofing;
    }

    public static void startSpoofing(int slot) {
        spoofing = true;
        spoofedSlot = slot;
    }

    public static void stopSpoofing() {
        spoofing = false;
    }

    public static int getSpoofedSlot() {
        return spoofing ? spoofedSlot : mc.thePlayer.inventory.currentItem;
    }

    public static ItemStack getSpoofedStack() {
        return spoofing ? mc.thePlayer.inventory.getStackInSlot(spoofedSlot) : mc.thePlayer.inventory.getCurrentItem();
    }

    @EventTarget
    public void onWorld(WorldLoadEvent event) {
        stopSpoofing();
    }
}
