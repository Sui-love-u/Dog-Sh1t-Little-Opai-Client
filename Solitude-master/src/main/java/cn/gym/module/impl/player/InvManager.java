package cn.gym.module.impl.player;

import cn.gym.Solitude;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.WorldEvent;
import cn.gym.events.impl.packet.PacketSendEvent;
import cn.gym.events.impl.player.UpdateEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.misc.Disabler;
import cn.gym.module.impl.movement.Scaffold;
import cn.gym.utils.math.MathUtils;
import cn.gym.utils.player.InventoryUtil;
import cn.gym.utils.time.TimerUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ModeValue;
import cn.gym.value.impl.NumberValue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

import static cn.gym.utils.pack.PacketUtil.sendPacket;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 14:18
 */
public class InvManager extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Open Inventory", new String[]{"Open Inventory", "Spoof"});
    private final NumberValue maxDelay = new NumberValue("Max Delay", 3, 0, 5, 1);
    private final NumberValue minDelay = new NumberValue("Min Delay", 1, 0, 5, 1);
    private final BooleanValue dropItems = new BooleanValue("Drop Items", true);
    private final BooleanValue sortItems = new BooleanValue("Sort Items", true);
    private final BooleanValue autoArmor = new BooleanValue("Auto Armor", true);
    private final BooleanValue startDelay = new BooleanValue("Start Delay", true);
    public final BooleanValue display = new BooleanValue("Display", true);
    private final BooleanValue usingItemCheck = new BooleanValue("Using Item Check", true);
    private final TimerUtil timer = new TimerUtil();
    private final int[] bestArmorPieces = new int[4];
    private final IntSet trash = new IntOpenHashSet();
    private final int[] bestToolSlots = new int[3];
    private final IntList gappleStackSlots = new IntArrayList();
    private final IntList blockSlot = new IntArrayList();
    private int bestSwordSlot;
    private int bestBowSlot;
    public boolean serverOpen;
    public boolean clientOpen;
    private boolean nextTickCloseInventory;
    public int slot = -1;
    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    public static boolean incontainer = false;

    public InvManager() {
        super("InvManager", Category.Player);
    }

    @EventTarget
    public void onPacketSend(PacketSendEvent event) {
        if (usingItemCheck.get() && mc.thePlayer.isUsingItem()) return;
        final Packet<?> packet = event.getPacket();

        if (incontainer) {
            if (packet instanceof C0EPacketClickWindow || packet instanceof C03PacketPlayer || packet instanceof C0FPacketConfirmTransaction || packet instanceof C0DPacketCloseWindow) {
                event.setCancelled(true);
                packets.add(event.getPacket());
            }
        } else if (!packets.isEmpty()) {
            packets.forEach(mc.getNetHandler()::addToSendQueueUnregistered);
            packets.clear();
        }

        if (packet instanceof S2DPacketOpenWindow || (packet instanceof C16PacketClientStatus && ((C16PacketClientStatus) packet).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)) {
            incontainer = true;
        }
        if (packet instanceof S2EPacketCloseWindow || packet instanceof C0DPacketCloseWindow) {
            incontainer = false;
        }

        if (packet instanceof C16PacketClientStatus clientStatus) {

            if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                if (startDelay.get())
                    Disabler.fixVia();
                this.clientOpen = true;
                this.serverOpen = true;
            }
        } else if (packet instanceof C0DPacketCloseWindow packetCloseWindow) {

            if (packetCloseWindow.windowId == mc.thePlayer.inventoryContainer.windowId) {
                this.clientOpen = false;
                this.serverOpen = false;
                slot = -1;
            }
        }
        if (packet instanceof S2DPacketOpenWindow) {
            this.clientOpen = false;
            this.serverOpen = false;
        }
    }

    private boolean dropItem(final IntSet listOfSlots) {
        if (this.dropItems.get() && !listOfSlots.isEmpty()) {
            final var iter = listOfSlots.iterator();
            int slot = iter.nextInt();
            Disabler.fixVia();
            windowClick(slot, 1, 4);
            iter.remove();
            return true;
        }
        return false;
    }
    @EventTarget
    public void onWorld(WorldEvent e){
        incontainer = false;
        this.setState(false);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setSuffix(String.valueOf(maxDelay.get()));
        if (usingItemCheck.get() && mc.thePlayer.isUsingItem()) return;
        final long delay = (MathUtils.nextInt((int) minDelay.get().intValue(), (int) maxDelay.get().intValue()) * 50L);
        if ((this.clientOpen || (mc.currentScreen == null && !Objects.equals(this.mode.get(), "Open Inventory"))) && !Solitude.Instance.getModuleManager().getModule(Scaffold.class).getState()
        ) {
            if ((this.timer.hasTimeElapsed(delay) || delay == 0)) {
                this.clear();

                for (int slot = InventoryUtil.INCLUDE_ARMOR_BEGIN; slot < InventoryUtil.END; slot++) {
                    final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();

                    if (stack != null) {
                        // Find Best Sword
                        if (stack.getItem() instanceof ItemSword && InventoryUtil.isBestSword(stack)) {
                            this.bestSwordSlot = slot;
                        }
                        //Find Best Bow
                        else if (stack.getItem() instanceof ItemBow && InventoryUtil.isBestBow(stack)) {
                            this.bestBowSlot = slot;
                        }
                        // Find Best Tools
                        else if (stack.getItem() instanceof ItemTool && InventoryUtil.isBestTool(mc.thePlayer, stack)) {
                            final int toolType = InventoryUtil.getToolType(stack);
                            if (toolType != -1 && slot != this.bestToolSlots[toolType])
                                this.bestToolSlots[toolType] = slot;
                        }
                        // Find Best Armor
                        else if (stack.getItem() instanceof ItemArmor armor && InventoryUtil.isBestArmor(mc.thePlayer, stack)) {

                            final int pieceSlot = this.bestArmorPieces[armor.armorType];

                            if (pieceSlot == -1 || slot != pieceSlot)
                                this.bestArmorPieces[armor.armorType] = slot;
                        } else if (stack.getItem() instanceof ItemBlock) {
                            if (slot == InventoryUtil.findBestBlockStack()) {
                                this.blockSlot.add(slot);
                            } else if (blockSlot.contains(slot)) {
                                this.blockSlot.removeInt(slot);
                            }
                        } else if (stack.getItem() instanceof ItemAppleGold) {
                            this.gappleStackSlots.add(slot);
                        } else if (!this.trash.contains(slot) && !InventoryUtil.isValidStack(stack)) {
                            this.trash.add(slot);
                        }
                    }
                }

                final boolean busy = (!this.trash.isEmpty() && this.dropItems.get()) || this.equipArmor(false) || this.sortItems(false);

                if (!busy) {
                    if (this.nextTickCloseInventory) {
                        this.close();
                        this.nextTickCloseInventory = false;
                    } else {
                        this.nextTickCloseInventory = true;
                    }
                    return;
                } else {
                    boolean waitUntilNextTick = !this.serverOpen;

                    this.open();

                    if (this.nextTickCloseInventory)
                        this.nextTickCloseInventory = false;

                    if (waitUntilNextTick) return;
                }

                if (this.equipArmor(true)) return;
                if (this.dropItem(this.trash)) return;
                this.sortItems(true);
                slot = -1;
                Disabler.fixVia();
            }
        }
    }

    private boolean sortItems(final boolean moveItems) {
        if (this.sortItems.get()) {

            if (this.bestSwordSlot != -1) {
                if (this.bestSwordSlot != 36) {
                    if (moveItems) {
                        this.putItemInSlot(36,this.bestSwordSlot);
                        this.bestSwordSlot = 36;
                    }
                    return true;
                }
            }

            if (this.bestBowSlot != -1) {
                if (this.bestBowSlot != 38) {
                    if (moveItems) {
                        this.putItemInSlot(38,this.bestBowSlot);
                        this.bestBowSlot = 38;
                    }
                    return true;
                }
            }

            if (!this.gappleStackSlots.isEmpty()) {
                this.gappleStackSlots.sort(Comparator.comparingInt(slot -> mc.thePlayer.inventoryContainer.getSlot(slot).getStack().stackSize));

                final int bestGappleSlot = this.gappleStackSlots.getInt(0);

                if (bestGappleSlot != 37) {
                    if (moveItems) {
                        this.putItemInSlot(37, bestGappleSlot);
                        this.gappleStackSlots.set(0, 37);
                    }
                    return true;
                }
            }

            if (!this.blockSlot.isEmpty()) {
                this.blockSlot.sort(Comparator.comparingInt(slot -> -mc.thePlayer.inventoryContainer.getSlot(slot).getStack().stackSize));

                final int blockSlot = this.blockSlot.getInt(0);

                if (blockSlot != 42) {
                    if (moveItems) {
                        this.putItemInSlot(42, blockSlot);
                        this.blockSlot.set(0, 42);
                    }
                    return true;
                }
            }

            final int[] toolSlots = {39, 40, 41};

            for (final int toolSlot : this.bestToolSlots) {
                if (toolSlot != -1) {
                    final int type = InventoryUtil.getToolType(mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack());

                    if (type != -1) {
                        if (toolSlot != toolSlots[type]) {
                            if (moveItems) {
                                this.putToolsInSlot(type, toolSlots);
                            }
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean equipArmor(boolean moveItems) {
        if (this.autoArmor.get()) {
            for (int i = 0; i < this.bestArmorPieces.length; i++) {
                final int piece = this.bestArmorPieces[i];

                if (piece != -1) {
                    int armorPieceSlot = i + 5;
                    final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot).getStack();
                    if (stack != null)
                        continue;

                    if (moveItems)
                        Disabler.fixVia();
                    windowClick(piece, 0, 1);

                    return true;
                }
            }
        }

        return false;
    }

    public void windowClick(int slotId, int mouseButtonClicked, int mode) {
        slot = slotId;
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotId, mouseButtonClicked, mode, mc.thePlayer);
        Disabler.fixVia();
    }

    private void putItemInSlot(final int slot, final int slotIn) {
        Disabler.fixVia();
        windowClick(slotIn, slot - 36, 2);
    }

    private void putToolsInSlot(final int tool, final int[] toolSlots) {
        final int toolSlot = toolSlots[tool];

        Disabler.fixVia();
        windowClick(this.bestToolSlots[tool],
                toolSlot - 36,
                2);
        this.bestToolSlots[tool] = toolSlot;
    }

    @Override
    public void onEnable() {
        this.clientOpen = mc.currentScreen instanceof GuiInventory;
        this.serverOpen = this.clientOpen;
        this.slot = -1;
    }

    @Override
    public void onDisable() {
        this.close();
        this.clear();
    }

    private void open() {
        if (!this.clientOpen && !this.serverOpen) {
            sendPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.serverOpen = true;
        }
    }

    private void close() {
        if (!this.clientOpen && this.serverOpen) {
            sendPacket(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
            this.serverOpen = false;
            this.slot = -1;
        }
    }

    private void clear() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.gappleStackSlots.clear();
        this.blockSlot.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
        this.slot = -1;
    }
}
