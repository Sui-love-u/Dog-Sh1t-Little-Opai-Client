package com.amaya.module.impl.player;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.world.MotionDisabler;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.player.ItemUtils;
import com.amaya.utils.time.TimerUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;

/**
* @Author: Guyuemang
*/
@ModuleInfo(name = "ChestStealer", category = Category.Player)
public class ChestStealer extends Module {
    private final BooleanSetting postValue = new BooleanSetting("Post", false);
    private final BooleanSetting chest = new BooleanSetting("Chest", true);
    private final BooleanSetting furnace = new BooleanSetting("Furnace", true);
    private final BooleanSetting brewingStand = new BooleanSetting("BrewingStand", true);

    public static final TimerUtil timer = new TimerUtil();
    public static boolean isChest = false;
    public static TimerUtil openChestTimer = new TimerUtil();
    private final NumberSetting delay = new NumberSetting("StealDelay", 100, 0, 1000, 10);
    private final BooleanSetting trash = new BooleanSetting("PickTrash", true);
    public final BooleanSetting silentValue = new BooleanSetting("Silent", true);

    private int nextDelay = 0;

    @EventTarget
    public void onWorld(WorldEvent e){
        this.setState(false);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (isGapple()) return;
        if ((postValue.getValue() && event.isPost()) || (!postValue.getValue() && event.isPre())) {
            if (mc.thePlayer.openContainer == null)
                return;

            if (mc.thePlayer.openContainer instanceof ContainerFurnace && furnace.getValue()) {
                ContainerFurnace container = (ContainerFurnace) mc.thePlayer.openContainer;

                if (isFurnaceEmpty(container) && openChestTimer.delay(100) && timer.delay(100)) {
                    mc.thePlayer.closeScreen();
                    return;
                }

                for (int i = 0; i < container.tileFurnace.getSizeInventory(); ++i) {
                    if (container.tileFurnace.getStackInSlot(i) != null) {
                        if (timer.delay(nextDelay)) {

                            MotionDisabler.fixVia();
                            mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                            nextDelay = (int) (delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                           // timer.reset();
                        }
                    }
                }
            }

            if (mc.thePlayer.openContainer instanceof ContainerBrewingStand && brewingStand.getValue()) {
                ContainerBrewingStand container = (ContainerBrewingStand) mc.thePlayer.openContainer;

                if (isBrewingStandEmpty(container) && openChestTimer.delay(100) && timer.delay(100)) {
                    mc.thePlayer.closeScreen();
                    return;
                }

                for (int i = 0; i < container.tileBrewingStand.getSizeInventory(); ++i) {
                    if (container.tileBrewingStand.getStackInSlot(i) != null) {
                        if (timer.delay(nextDelay)) {

                            MotionDisabler.fixVia();
                            mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                            nextDelay = (int) (delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                           // timer.reset();
                        }
                    }
                }
            }

            if (mc.thePlayer.openContainer instanceof ContainerChest && chest.getValue() && isChest) {
                ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;


                if (isChestEmpty(container) && openChestTimer.delay(100) && timer.delay(100)) {
                    mc.thePlayer.closeScreen();
                    return;
                }

                for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
                    if (container.getLowerChestInventory().getStackInSlot(i) != null) {
                        if (timer.delay(nextDelay) && (isItemUseful(container, i) || trash.getValue())) {

                            MotionDisabler.fixVia();
                            mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                            nextDelay = (int) (delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                          //  timer.reset();
                        }
                    }
                }
            }
        }
    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                if (isItemUseful(c, i) || trash.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < c.tileFurnace.getSizeInventory(); ++i) {
            if (c.tileFurnace.getStackInSlot(i) != null) {
                return false;
            }
        }

        return true;
    }

    private boolean isBrewingStandEmpty(ContainerBrewingStand c) {
        for (int i = 0; i < c.tileBrewingStand.getSizeInventory(); ++i) {
            if (c.tileBrewingStand.getStackInSlot(i) != null) {
                return false;
            }
        }

        return true;
    }

    private boolean isItemUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();

        if (item instanceof ItemAxe || item instanceof ItemPickaxe) {
            return true;
        }

        if (item instanceof ItemFood)
            return true;
        if (item instanceof ItemBow || item == Items.arrow)
            return true;

        if (item instanceof ItemPotion && !ItemUtils.isPotionNegative(itemStack))
            return true;
        if (item instanceof ItemSword && ItemUtils.isBestSword(c, itemStack))
            return true;
        if (item instanceof ItemArmor && ItemUtils.isBestArmor(c, itemStack))
            return true;
        if (item instanceof ItemBlock)
            return true;

        return item instanceof ItemEnderPearl;
    }
}
