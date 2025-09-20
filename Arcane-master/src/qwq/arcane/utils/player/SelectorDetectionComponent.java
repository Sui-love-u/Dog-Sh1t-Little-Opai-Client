package qwq.arcane.utils.player;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.utils.Instance;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/SelectorDetectionComponent.class */
public final class SelectorDetectionComponent implements Instance {
    private static boolean selector;

    public static boolean selector() {
        return selector;
    }

    public static boolean selector(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (itemStack == mc.thePlayer.inventory.getItemStack()) {
            return selector();
        }
        return !trueName(itemStack).contains(itemStack.getDisplayName());
    }

    public static boolean selector(int itemSlot) {
        return selector(mc.thePlayer.inventory.getStackInSlot(itemSlot));
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (getItemStack() != null) {
            ItemStack itemStack = getItemStack();
            selector = !trueName(itemStack).contains(itemStack.getDisplayName());
        } else {
            selector = false;
        }
    }

    public ItemStack getItemStack() {
        if (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null) {
            return null;
        }
        return mc.thePlayer.inventoryContainer.getSlot(getItemIndex() + 36).getStack();
    }

    public int getItemIndex() {
        InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
        return (!inventoryPlayer.alternativeSlot || inventoryPlayer.breakNotNative) ? inventoryPlayer.currentItem : inventoryPlayer.alternativeCurrentItem;
    }

    public static String trueName(ItemStack itemStack) {
        String name = StatCollector.translateToLocal(itemStack.getUnlocalizedName() + ".name").trim();
        String s1 = EntityList.getStringFromID(itemStack.getMetadata());
        if (s1 != null) {
            name = name + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
        }
        return name;
    }
}
