package qwq.arcane.module.impl.player;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.packet.PacketReceiveSyncEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.combat.KillAura;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.player.InventoryUtil;
import qwq.arcane.utils.time.StopWatch;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/player/InvManager.class */
public class InvManager extends Module {
    private final ModeValue mode;
    private final NumberValue maxDelay;
    private final NumberValue minDelay;
    private final BoolValue dropItems;
    private final BoolValue sortItems;
    private final BoolValue autoArmor;
    private final BoolValue startDelay;
    public final BoolValue display;
    private final BoolValue usingItemCheck;
    private BoolValue slotMachineFix;
    private NumberValue delay;
    private final TimerUtil timer;
    private final int[] bestArmorPieces;
    private final IntSet trash;
    private final int[] bestToolSlots;
    private final IntList gappleStackSlots;
    private final IntList blockSlot;
    private int bestSwordSlot;
    private int bestBowSlot;
    public boolean serverOpen;
    public boolean clientOpen;
    private boolean nextTickCloseInventory;
    public int slot;
    private final LinkedBlockingQueue<Packet<?>> packets;
    public static boolean incontainer = false;
    public final LinkedBlockingQueue<Packet<INetHandlerPlayClient>> setSlots;
    private final StopWatch stopWatch;
    private boolean action;

    public InvManager() {
        super("InvManager", Category.Player);
        this.mode = new ModeValue("Mode", "Open Inventory", new String[]{"Open Inventory", "Spoof"});
        this.maxDelay = new NumberValue("Max Delay", 3.0d, 0.0d, 5.0d, 1.0d);
        this.minDelay = new NumberValue("Min Delay", 1.0d, 0.0d, 5.0d, 1.0d);
        this.dropItems = new BoolValue("Drop Items", true);
        this.sortItems = new BoolValue("Sort Items", true);
        this.autoArmor = new BoolValue("Auto Armor", true);
        this.startDelay = new BoolValue("Start Delay", true);
        this.display = new BoolValue("Display", true);
        this.usingItemCheck = new BoolValue("Using Item Check", true);
        this.slotMachineFix = new BoolValue("SlotMachine Fix", false);
        BoolValue boolValue = this.slotMachineFix;
        Objects.requireNonNull(boolValue);
        this.delay = new NumberValue("Delay", boolValue::get, 1000.0d, 0.0d, 2000.0d, 50.0d);
        this.timer = new TimerUtil();
        this.bestArmorPieces = new int[4];
        this.trash = new IntOpenHashSet();
        this.bestToolSlots = new int[3];
        this.gappleStackSlots = new IntArrayList();
        this.blockSlot = new IntArrayList();
        this.slot = -1;
        this.packets = new LinkedBlockingQueue<>();
        this.setSlots = new LinkedBlockingQueue<>();
        this.stopWatch = new StopWatch();
        this.action = false;
    }

    @EventTarget
    public void onPacketSend(PacketSendEvent event) {
        if (((Scaffold) INSTANCE.getModuleManager().getModule(Scaffold.class)).getState()) {
            return;
        }
        if (KillAura.target == null && mc.thePlayer != null) {
            if (this.usingItemCheck.get().booleanValue() && mc.thePlayer.isUsingItem()) {
                return;
            }
            Packet<?> packet = event.getPacket();
            if (this.slotMachineFix.get().booleanValue()) {
                if (incontainer) {
                    if ((packet instanceof C0EPacketClickWindow) || (packet instanceof C03PacketPlayer) || (packet instanceof C0FPacketConfirmTransaction) || (packet instanceof C0DPacketCloseWindow)) {
                        event.setCancelled(true);
                        this.packets.add(event.getPacket());
                    }
                } else if (!this.packets.isEmpty()) {
                    LinkedBlockingQueue<Packet<?>> linkedBlockingQueue = this.packets;
                    NetHandlerPlayClient netHandler = mc.getNetHandler();
                    Objects.requireNonNull(netHandler);
                    linkedBlockingQueue.forEach(netHandler::addToSendQueueUnregistered);
                    this.packets.clear();
                }
                if ((packet instanceof S2DPacketOpenWindow) || ((packet instanceof C16PacketClientStatus) && ((C16PacketClientStatus) packet).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)) {
                    incontainer = true;
                }
                if ((packet instanceof S2EPacketCloseWindow) || (packet instanceof C0DPacketCloseWindow)) {
                    incontainer = false;
                }
            }
            if (packet instanceof C16PacketClientStatus) {
                C16PacketClientStatus clientStatus = (C16PacketClientStatus) packet;
                if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                    if (this.startDelay.get().booleanValue() && this.slotMachineFix.get().booleanValue()) {
                        this.stopWatch.reset();
                        this.action = true;
                    }
                    this.clientOpen = true;
                    this.serverOpen = true;
                    this.timer.reset();
                }
            } else if (packet instanceof C0DPacketCloseWindow) {
                C0DPacketCloseWindow packetCloseWindow = (C0DPacketCloseWindow) packet;
                if (packetCloseWindow.windowId == mc.thePlayer.inventoryContainer.windowId) {
                    this.clientOpen = false;
                    this.serverOpen = false;
                    this.slot = -1;
                }
            }
            if (packet instanceof S2DPacketOpenWindow) {
                this.clientOpen = false;
                this.serverOpen = false;
            }
        }
    }

    private boolean dropItem(IntSet listOfSlots) {
        if (this.dropItems.get().booleanValue() && !listOfSlots.isEmpty()) {
            IntIterator iter = listOfSlots.iterator();
            int slot = iter.nextInt();
            if (this.slotMachineFix.get().booleanValue()) {
                this.stopWatch.reset();
                this.action = true;
            }
            windowClick(slot, 1, 4);
            iter.remove();
            this.timer.reset();
            return true;
        }
        return false;
    }

    @EventTarget
    public void onWorld(WorldLoadEvent e) {
        incontainer = false;
        setState(false);
    }

    /* JADX WARN: Removed duplicated region for block: B:57:0x0190  */
    @qwq.arcane.event.annotations.EventTarget
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onUpdate(qwq.arcane.event.impl.events.player.UpdateEvent r6) {
        /*
            Method dump skipped, instructions count: 714
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: qwq.arcane.module.impl.player.InvManager.onUpdate(qwq.arcane.event.impl.events.player.UpdateEvent):void");
    }

    private boolean sortItems(boolean moveItems) {
        int type;
        if (this.sortItems.get().booleanValue()) {
            if (this.bestSwordSlot != -1 && this.bestSwordSlot != 36) {
                if (moveItems) {
                    putItemInSlot(36, this.bestSwordSlot);
                    this.bestSwordSlot = 36;
                    return true;
                }
                return true;
            }
            if (this.bestBowSlot != -1 && this.bestBowSlot != 38) {
                if (moveItems) {
                    putItemInSlot(38, this.bestBowSlot);
                    this.bestBowSlot = 38;
                    return true;
                }
                return true;
            }
            if (!this.gappleStackSlots.isEmpty()) {
                this.gappleStackSlots.sort(Comparator.comparingInt(slot -> {
                    return mc.thePlayer.inventoryContainer.getSlot(slot.intValue()).getStack().stackSize;
                }));
                int bestGappleSlot = this.gappleStackSlots.getInt(0);
                if (bestGappleSlot != 37) {
                    if (moveItems) {
                        putItemInSlot(37, bestGappleSlot);
                        this.gappleStackSlots.set(0, 37);
                        return true;
                    }
                    return true;
                }
            }
            if (!this.blockSlot.isEmpty()) {
                this.blockSlot.sort(Comparator.comparingInt(slot2 -> {
                    return -mc.thePlayer.inventoryContainer.getSlot(slot2.intValue()).getStack().stackSize;
                }));
                int blockSlot = this.blockSlot.getInt(0);
                if (blockSlot != 42) {
                    if (moveItems) {
                        putItemInSlot(42, blockSlot);
                        this.blockSlot.set(0, 42);
                        return true;
                    }
                    return true;
                }
            }
            int[] toolSlots = {39, 40, 41};
            for (int toolSlot : this.bestToolSlots) {
                if (toolSlot != -1 && (type = InventoryUtil.getToolType(mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack())) != -1 && toolSlot != toolSlots[type]) {
                    if (moveItems) {
                        putToolsInSlot(type, toolSlots);
                        return true;
                    }
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean equipArmor(boolean moveItems) {
        if (this.autoArmor.get().booleanValue()) {
            for (int i = 0; i < this.bestArmorPieces.length; i++) {
                int piece = this.bestArmorPieces[i];
                if (piece != -1) {
                    int armorPieceSlot = i + 5;
                    ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot).getStack();
                    if (stack == null) {
                        if (moveItems && this.slotMachineFix.get().booleanValue()) {
                            this.stopWatch.reset();
                            this.action = true;
                        }
                        windowClick(piece, 0, 1);
                        this.timer.reset();
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    public void windowClick(int slotId, int mouseButtonClicked, int mode) {
        this.slot = slotId;
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotId, mouseButtonClicked, mode, mc.thePlayer);
        if (this.slotMachineFix.get().booleanValue()) {
            this.stopWatch.reset();
            this.action = true;
        }
        this.timer.reset();
    }

    private void putItemInSlot(int slot, int slotIn) {
        if (this.slotMachineFix.get().booleanValue()) {
            this.stopWatch.reset();
            this.action = true;
        }
        windowClick(slotIn, slot - 36, 2);
        this.timer.reset();
    }

    private void putToolsInSlot(int tool, int[] toolSlots) {
        int toolSlot = toolSlots[tool];
        if (this.slotMachineFix.get().booleanValue()) {
            this.stopWatch.reset();
            this.action = true;
        }
        windowClick(this.bestToolSlots[tool], toolSlot - 36, 2);
        this.timer.reset();
        this.bestToolSlots[tool] = toolSlot;
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.clientOpen = mc.currentScreen instanceof GuiInventory;
        this.serverOpen = this.clientOpen;
        this.slot = -1;
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        close();
        clear();
    }

    private void open() {
        if (!this.clientOpen && !this.serverOpen) {
            PacketUtil.sendPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.serverOpen = true;
        }
    }

    private void close() {
        if (!this.clientOpen && this.serverOpen) {
            PacketUtil.sendPacket(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
            this.serverOpen = false;
            this.slot = -1;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @EventTarget
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (this.slotMachineFix.getValue().booleanValue()) {
            if (this.action) {
                if ((event.getPacket() instanceof S2FPacketSetSlot) || (event.getPacket() instanceof S30PacketWindowItems) || (event.getPacket() instanceof C0EPacketClickWindow)) {
                    event.setCancelled(true);
                    setSlots.add((Packet<INetHandlerPlayClient>) event.getPacket());
                }
                if (this.stopWatch.hasTimePassed(this.delay.get().longValue())) {
                    slotMachineReset();
                }
            }
            if ((event.getPacket() instanceof S2DPacketOpenWindow) || (event.getPacket() instanceof S2EPacketCloseWindow)) {
                slotMachineReset();
            }
        }
    }

    private void slotMachineReset() {
        while (!this.setSlots.isEmpty()) {
            try {
                this.setSlots.poll().processPacket(mc.getNetHandler());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        this.action = false;
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
