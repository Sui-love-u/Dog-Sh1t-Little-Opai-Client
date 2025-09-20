/*
 * Decompiled with CFR 0.152.
 */
package com.amaya.module.impl.player;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.combat.Gapple;
import com.amaya.module.impl.combat.KillAura;
import com.amaya.module.impl.world.MotionDisabler;
import com.amaya.module.impl.world.Scaffold;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.player.InventoryUtil;
import com.amaya.utils.time.TimerUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@ModuleInfo(name = "InvManager", category = Category.Player)
public class InvManager extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Spoof",new String[]{"Spoof", "OpenInv"});
    private final NumberSetting delay = new NumberSetting("SlotDelay", 5.0, 0, 300.0, 10.0);
    private final NumberSetting armorDelay = new NumberSetting("Delay", 20.0, 0, 300.0, 10.0);
    public final NumberSetting sword = new NumberSetting("Weapon", 1.0, 1.0, 9.0, 1.0);
    public final NumberSetting bow = new NumberSetting("Bow", 6.0, 1.0, 9.0, 1.0);
    public final NumberSetting pearl = new NumberSetting("Pearl", 8.0, 1.0, 9.0, 1.0);
    public final NumberSetting block = new NumberSetting("Block", 7.0, 1.0, 9.0, 1.0);
    public final NumberSetting gApple = new NumberSetting("Gapple", 4.0, 1.0, 9.0, 1.0);
    public final NumberSetting ball = new NumberSetting("Ball", 9.0, 1.0, 9.0, 1.0);
    public final String[] serverItems = new String[]{"\u9009\u62e9\u6e38\u620f", "\u52a0\u5165\u6e38\u620f", "\u804c\u4e1a\u9009\u62e9\u83dc\u5355", "\u79bb\u5f00\u5bf9\u5c40", "\u518d\u6765\u4e00\u5c40", "selector", "tracking compass", "(right click)", "tienda ", "perfil", "salir", "shop", "collectibles", "game", "profil", "lobby", "show all", "hub", "friends only", "cofre", "(click", "teleport", "play", "exit", "hide all", "jeux", "gadget", " (activ", "emote", "amis", "bountique", "choisir", "choose "};
    private final int[] bestArmorPieces = new int[4];
    private final List<Integer> trash = new ArrayList<Integer>();
    private final int[] bestToolSlots = new int[3];
    private final List<Integer> gappleStackSlots = new ArrayList<Integer>();
    private int bestSwordSlot;
    private int bestPearlSlot;
    private int bestballSlot;
    private int bestBowSlot;
    private boolean serverOpen;
    private boolean clientOpen;
    private int ticksSinceLastClick;
    private boolean nextTickCloseInventory;
    private final TimerUtil timer = new TimerUtil();

    @EventTarget
    private void onPacket(PacketReceiveEvent event) {
        if (Amaya.Instance.moduleManager.getModule(Gapple.class).getState()){
            return;
        }
        Packet<?> packet = event.getPacket();
        if (packet instanceof S2DPacketOpenWindow) {
            this.clientOpen = false;
            this.serverOpen = false;
        }
    }

    @EventTarget
    private void onPacketSend(PacketSendEvent event) {
        if (Amaya.Instance.moduleManager.getModule(Gapple.class).getState()){
            return;
        }
        Packet<?> packet = event.getPacket();
        if (packet instanceof C16PacketClientStatus) {
            C16PacketClientStatus clientStatus = (C16PacketClientStatus)packet;
            if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                this.clientOpen = true;
                this.serverOpen = true;
            }
        } else if (packet instanceof C0DPacketCloseWindow) {
            C0DPacketCloseWindow packetCloseWindow = (C0DPacketCloseWindow)packet;
            if (packetCloseWindow.windowId == InvManager.mc.thePlayer.inventoryContainer.windowId) {
                this.clientOpen = false;
                this.serverOpen = false;
            }
        } else if (packet instanceof C0EPacketClickWindow && !InvManager.mc.thePlayer.isUsingItem()) {
            this.ticksSinceLastClick = 0;
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        this.setState(false);
    }

    private boolean dropItem(List<Integer> listOfSlots) {
        if (!listOfSlots.isEmpty()) {
            int slot = listOfSlots.remove(0);
            MotionDisabler.fixVia();
            InventoryUtil.windowClick(mc, slot, 1, InventoryUtil.ClickType.DROP_ITEM);
            return true;
        }
        return false;
    }

    @EventTarget
    private void onMotion(MotionEvent event) {
        if (Amaya.Instance.moduleManager.getModule(Gapple.class).getState()){
            return;
        }
        if (!(InvManager.mc.thePlayer.isOnLadder() || InvManager.mc.thePlayer.isUsingItem() || InvManager.mc.currentScreen != null && !(InvManager.mc.currentScreen instanceof GuiChat) && !(InvManager.mc.currentScreen instanceof GuiInventory) && InvManager.mc.thePlayer.isSpectator() && !(InvManager.mc.currentScreen instanceof GuiIngameMenu) || KillAura.target != null)) {
            ++this.ticksSinceLastClick;
            if ((double)this.ticksSinceLastClick < Math.floor(this.delay.getValue() / 50.0)) {
                return;
            }
            if (this.clientOpen || InvManager.mc.currentScreen == null && !this.mode.getValue().equals("OpenInv")) {
                boolean waitUntilNextTick;
                boolean busy;
                this.clear();
                for (int slot = 5; slot < 45; ++slot) {
                    ItemStack stack = InvManager.mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                    if (stack == null) continue;
                    if (stack.getItem() instanceof ItemSword && InventoryUtil.isBestSword(stack)) {
                        this.bestSwordSlot = slot;
                        continue;
                    }
                    if (stack.getItem() instanceof ItemTool && InventoryUtil.isBestTool(InvManager.mc.thePlayer, stack)) {
                        int toolType = InventoryUtil.getToolType(stack);
                        if (toolType == -1 || slot == this.bestToolSlots[toolType]) continue;
                        this.bestToolSlots[toolType] = slot;
                        continue;
                    }
                    Item toolType = stack.getItem();
                    if (toolType instanceof ItemArmor) {
                        ItemArmor armor = (ItemArmor)toolType;
                        if (InventoryUtil.isBestArmor(InvManager.mc.thePlayer, stack)) {
                            int pieceSlot = this.bestArmorPieces[armor.armorType];
                            if (pieceSlot != -1 && slot == pieceSlot) continue;
                            this.bestArmorPieces[armor.armorType] = slot;
                            continue;
                        }
                    }
                    if (stack.getItem() instanceof ItemBow && InventoryUtil.isBestBow(InvManager.mc.thePlayer, stack)) {
                        if (slot == this.bestBowSlot) continue;
                        this.bestBowSlot = slot;
                        continue;
                    }
                    if (stack.getItem() instanceof ItemAppleGold) {
                        this.gappleStackSlots.add(slot);
                        continue;
                    }
                    if (stack.getItem() instanceof ItemEnderPearl) {
                        this.bestPearlSlot = slot;
                        continue;
                    }
                    if (stack.getItem() instanceof ItemSnowball) {
                        this.bestballSlot = slot;
                        continue;
                    }
                    if (stack.getItem() instanceof ItemEgg) {
                        this.bestballSlot = slot;
                        continue;
                    }
                    if (this.trash.contains(slot) || InvManager.isValidStack(stack)) continue;
                    if (Arrays.stream(this.serverItems).anyMatch(stack.getDisplayName()::contains) || stack.getItem() instanceof ItemSkull) continue;
                    this.trash.add(slot);
                }
                boolean bl = busy = !this.trash.isEmpty() || this.equipArmor(false) || this.sortItems(false);
                if (!busy) {
                    if (this.nextTickCloseInventory) {
                        if (this.mode.is("Spoof")) {
                            this.close();
                        }
                        this.nextTickCloseInventory = false;
                    } else {
                        this.nextTickCloseInventory = true;
                    }
                    return;
                }
                boolean bl2 = waitUntilNextTick = !this.serverOpen;
                if (this.mode.is("Spoof")) {
                    this.open();
                }
                if (this.nextTickCloseInventory) {
                    this.nextTickCloseInventory = false;
                }
                if (waitUntilNextTick) {
                    return;
                }
                if (this.timer.hasTimeElapsed(this.armorDelay.getValue().longValue()) && this.equipArmor(true)) {
                    return;
                }
                if (this.dropItem(this.trash)) {
                    return;
                }
                this.sortItems(true);
            }
        }
    }

    private boolean sortItems(boolean moveItems) {
        block16: {
            int mostBlocksSlot;
            int goodBlockSlot;
            block17: {
                int goodSwordSlot = this.sword.getValue().intValue() + 35;
                if (this.bestSwordSlot != -1 && this.bestSwordSlot != goodSwordSlot) {
                    if (moveItems) {
                        this.putItemInSlot(goodSwordSlot, this.bestSwordSlot);
                        this.bestSwordSlot = goodSwordSlot;
                    }
                    return true;
                }
                int goodBowSlot = this.bow.getValue().intValue() + 35;
                if (this.bestBowSlot != -1 && this.bestBowSlot != goodBowSlot) {
                    if (moveItems) {
                        this.putItemInSlot(goodBowSlot, this.bestBowSlot);
                        this.bestBowSlot = goodBowSlot;
                    }
                    return true;
                }
                int goodGappleSlot = this.gApple.getValue().intValue() + 35;
                if (!this.gappleStackSlots.isEmpty()) {
                    this.gappleStackSlots.sort(Comparator.comparingInt(slot -> InvManager.mc.thePlayer.inventoryContainer.getSlot((int)slot.intValue()).getStack().stackSize));
                    int bestGappleSlot = this.gappleStackSlots.get(0);
                    if (bestGappleSlot != goodGappleSlot) {
                        if (moveItems) {
                            this.putItemInSlot(goodGappleSlot, bestGappleSlot);
                            this.gappleStackSlots.set(0, goodGappleSlot);
                        }
                        return true;
                    }
                }
                goodBlockSlot = this.block.getValue().intValue() + 35;
                mostBlocksSlot = this.getMostBlocks();
                if (mostBlocksSlot == -1 || mostBlocksSlot == goodBlockSlot) break block16;
                Slot dss = InvManager.mc.thePlayer.inventoryContainer.getSlot(goodBlockSlot);
                ItemStack dsis = dss.getStack();
                if (dsis == null || !(dsis.getItem() instanceof ItemBlock) || dsis.stackSize < InvManager.mc.thePlayer.inventoryContainer.getSlot((int)mostBlocksSlot).getStack().stackSize) break block17;
                if (Arrays.stream(this.serverItems).noneMatch(dsis.getDisplayName().toLowerCase()::contains)) break block16;
            }
            this.putItemInSlot(goodBlockSlot, mostBlocksSlot);
        }
        int goodPearlSlot = this.pearl.getValue().intValue() + 35;
        if (this.bestPearlSlot != -1 && this.bestPearlSlot != goodPearlSlot) {
            if (moveItems) {
                this.putItemInSlot(goodPearlSlot, this.bestPearlSlot);
                this.bestPearlSlot = goodPearlSlot;
            }
            return true;
        }
        int goodballSlot = this.ball.getValue().intValue() + 35;
        if (this.bestballSlot != -1 && this.bestballSlot != goodballSlot) {
            if (moveItems) {
                this.putItemInSlot(goodballSlot, this.bestballSlot);
                this.bestballSlot = goodballSlot;
            }
            return true;
        }
        return false;
    }

    public int getMostBlocks() {
        int stack = 0;
        int biggestSlot = -1;
        for (int i2 = 9; i2 < 45; ++i2) {
            Slot slot = InvManager.mc.thePlayer.inventoryContainer.getSlot(i2);
            ItemStack is = slot.getStack();
            if (is == null || !(is.getItem() instanceof ItemBlock) || is.stackSize <= stack) continue;
            if (!Arrays.stream(this.serverItems).noneMatch(is.getDisplayName().toLowerCase()::contains)) continue;
            stack = is.stackSize;
            biggestSlot = i2;
        }
        return biggestSlot;
    }

    private boolean equipArmor(boolean moveItems) {
        for (int i2 = 0; i2 < this.bestArmorPieces.length; ++i2) {
            int armorPieceSlot;
            ItemStack stack;
            int piece = this.bestArmorPieces[i2];
            if (piece == -1 || (stack = InvManager.mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot = i2 + 5).getStack()) != null) continue;
            if (moveItems) {
                MotionDisabler.fixVia();
                InventoryUtil.windowClick(mc, piece, 0, InventoryUtil.ClickType.SHIFT_CLICK);
            }
            return true;
        }
        return false;
    }

    private void putItemInSlot(int slot, int slotIn) {
        MotionDisabler.fixVia();
        InventoryUtil.windowClick(mc, slotIn, slot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private void putToolsInSlot(int tool, int[] toolSlots) {
        int toolSlot = toolSlots[tool];
        MotionDisabler.fixVia();
        InventoryUtil.windowClick(mc, this.bestToolSlots[tool], toolSlot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
        this.bestToolSlots[tool] = toolSlot;
    }

    private static boolean isValidStack(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            if (InventoryUtil.isStackValidToPlace(stack)) {
                return true;
            }
        } else if (item instanceof ItemPotion) {
            if (InventoryUtil.isBuffPotion(stack)) {
                return true;
            }
        } else if (item instanceof ItemFood) {
            if (InventoryUtil.isGoodFood(stack)) {
                return true;
            }
        } else {
            if (item != null) {
                // empty if block
            }
            return InventoryUtil.isGoodItem(stack.getItem());
        }
        return false;
    }

    @Override
    public void onEnable() {
        this.ticksSinceLastClick = 0;
        this.serverOpen = this.clientOpen = InvManager.mc.currentScreen instanceof GuiInventory;
    }

    @Override
    public void onDisable() {
        this.close();
        this.clear();
    }

    private void open() {
        if (!this.clientOpen && !this.serverOpen) {
            InvManager.mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.serverOpen = true;
        }
    }

    private void close() {
        if (!this.clientOpen && this.serverOpen) {
            InvManager.mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(InvManager.mc.thePlayer.inventoryContainer.windowId));
            this.serverOpen = false;
        }
    }

    private void clear() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.gappleStackSlots.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }
}

