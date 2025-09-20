package cn.gym.manager;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.WorldEvent;
import cn.gym.utils.Instance;
import lombok.Getter;
import net.minecraft.item.ItemStack;

public class SlotSpoofComponent implements Instance {
    private static int spoofedSlot;

    @Getter
    private static boolean spoofing;

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
    public void onWorld(WorldEvent event){
        stopSpoofing();
    }
}
