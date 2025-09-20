package cn.gym.module.impl.player;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.WorldEvent;
import cn.gym.events.impl.player.MotionEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.misc.Disabler;
import cn.gym.utils.player.ItemUtils;
import cn.gym.utils.time.TimerUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.NumberValue;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 13:37
 */
public class Stealer extends Module {
    private final BooleanValue postValue = new BooleanValue("Post", false);
    private final BooleanValue chest = new BooleanValue("Chest", true);
    private final BooleanValue furnace = new BooleanValue("Furnace", true);
    private final BooleanValue brewingStand = new BooleanValue("BrewingStand", true);
    public static final TimerUtil timer = new TimerUtil();
    public static boolean isChest = false;
    public static TimerUtil openChestTimer = new TimerUtil();
    private final NumberValue delay = new NumberValue("StealDelay", 100, 0, 1000, 10);
    private final BooleanValue trash = new BooleanValue("PickTrash", true);
    public final BooleanValue silentValue = new BooleanValue("Silent", true);

    private int nextDelay = 0;

    public Stealer() {
        super("Stealer",Category.Player);
    }

    @EventTarget
    public void onWorld(WorldEvent e){
        this.setState(false);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
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

                            Disabler.fixVia();
                            mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                            nextDelay = (int) (delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                            timer.reset();
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

                            Disabler.fixVia();
                            mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                            nextDelay = (int) (delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                            timer.reset();
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

                            Disabler.fixVia();
                            mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                            nextDelay = (int) (delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                            timer.reset();
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
