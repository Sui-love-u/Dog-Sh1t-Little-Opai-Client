package com.amaya.component;

import com.amaya.utils.client.InstanceAccess;
import lombok.Getter;
import net.minecraft.item.ItemStack;

/**
 * @Author KuChaZi
 * @Date 2024/7/16 12:16
 * @ClassName: SlotSpoofComponent
 */
public class SlotSpoofComponent implements InstanceAccess {
    private int spoofedSlot;

    @Getter
    private boolean spoofing;

    public void startSpoofing(int slot) {
        this.spoofing = true;
        this.spoofedSlot = slot;
    }

    public void stopSpoofing() {
        this.spoofing = false;
    }

    public int getSpoofedSlot() {
        return spoofing ? spoofedSlot : mc.thePlayer.inventory.currentItem;
    }

    public ItemStack getSpoofedStack() {
        return spoofing ? mc.thePlayer.inventory.getStackInSlot(spoofedSlot) : mc.thePlayer.inventory.getCurrentItem();
    }
}
