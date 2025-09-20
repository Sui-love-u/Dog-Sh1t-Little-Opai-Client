package qwq.arcane.module.impl.world;

import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import qwq.arcane.event.annotations.EventPriority;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.packet.HigherPacketEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.player.InvManager;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/world/Disabler.class */
public class Disabler extends Module {
    public final BoolValue oldPostValue;
    public final BoolValue digValue;
    public final BoolValue blockValue;
    private final BoolValue badPacketsA;
    private final BoolValue fakePingValue;
    public final BoolValue fastBreak;
    public final BoolValue debug;
    private final HashMap<Packet<?>, Long> packetsMap;
    int lastSlot;
    boolean lastSprinting;
    static Disabler INSTANCE;
    private boolean S08;
    private boolean c16;
    private boolean c0d;
    public static final BoolValue invmove = new BoolValue("Dog Invmove", true);
    public static final BoolValue postValue = new BoolValue("Post", true);
    public static final BoolValue badPacketsF = new BoolValue("BadPacketsF", true);

    public Disabler() {
        super("Disabler", Category.World);
        this.oldPostValue = new BoolValue("OldPost", false);
        this.digValue = new BoolValue("Digging", true);
        this.blockValue = new BoolValue("Cancel Blocking Packet", false);
        this.badPacketsA = new BoolValue("BadPacketsA", true);
        this.fakePingValue = new BoolValue("FakePing", false);
        this.fastBreak = new BoolValue("FastBreak", true);
        this.debug = new BoolValue("Debug", true);
        this.packetsMap = new HashMap<>();
        this.lastSlot = -1;
        this.S08 = false;
        INSTANCE = this;
    }

    @EventPriority(9)
    @EventTarget
    public void onupdate(UpdateEvent event) {
        setsuffix(String.valueOf(invmove.getName()));
        if (invmove.get().booleanValue()) {
            this.c16 = false;
            this.c0d = false;
            if ((mc.currentScreen instanceof GuiInventory) || (((InvManager) getModule(InvManager.class)).isEnabled() && ((InvManager) getModule(InvManager.class)).clientOpen)) {
                if (mc.thePlayer.ticksExisted % (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 3 : 4) == 0) {
                    PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow());
                } else {
                    if (mc.thePlayer.ticksExisted % (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 3 : 4) == 1) {
                        PacketUtil.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    }
                }
            }
        }
        if (this.fakePingValue.getValue().booleanValue()) {
            try {
                HashMap<Packet<?>, Long> hashMap = this.packetsMap;
                synchronized (hashMap) {
                    Iterator<Map.Entry<Packet<?>, Long>> iterator = this.packetsMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Packet<?>, Long> entry = iterator.next();
                        if (entry.getValue().longValue() < System.currentTimeMillis()) {
                            mc.getNetHandler().addToSendQueue(entry.getKey());
                            iterator.remove();
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (this.S08) {
            this.S08 = false;
        }
    }

    public static void onS08() {
        INSTANCE.S08 = true;
    }

    @EventTarget
    public void onWorld(WorldLoadEvent event) {
        this.lastSlot = -1;
        this.lastSprinting = false;
    }

    @EventPriority(0)
    @EventTarget
    public void onHigherPacket(HigherPacketEvent event) {
        Packet packet = event.getPacket();
        if (mc.thePlayer == null || mc.thePlayer.isDead) {
            return;
        }
        if ((this.blockValue.getValue().booleanValue() || this.digValue.getValue().booleanValue()) && mc.thePlayer.getHeldItem() != null && this.blockValue.getValue().booleanValue() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) && (event.getPacket() instanceof C08PacketPlayerBlockPlacement)) {
            C08PacketPlayerBlockPlacement c08 = (C08PacketPlayerBlockPlacement) packet;
            if (this.debug.getValue().booleanValue()) {
            }
            if ((c08.getPosition().getX() == -1 || c08.getFacingX() == -1.0f) && c08.getFacingZ() == -1.0f) {
                if (this.debug.getValue().booleanValue()) {
                }
                event.setCancelled(true);
            }
        }
        if (this.digValue.getValue().booleanValue() && (event.getPacket() instanceof C07PacketPlayerDigging)) {
            C07PacketPlayerDigging c07 = (C07PacketPlayerDigging) packet;
            if (mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) && c07.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                if (this.debug.getValue().booleanValue()) {
                }
                event.setCancelled(true);
                mc.thePlayer.sendQueue.addToSendQueue(new C0EPacketClickWindow(0, 36, 0, 2, new ItemStack(Block.getBlockById(166)), (short) 0));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem % 8) + 1));
                mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("test", new PacketBuffer(Unpooled.buffer())));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        Packet packet = event.getPacket();
        if (invmove.get().booleanValue()) {
            if (event.getPacket() instanceof C16PacketClientStatus) {
                if (this.c16) {
                    event.setCancelled(true);
                }
                this.c16 = true;
            }
            if (event.getPacket() instanceof C0DPacketCloseWindow) {
                if (this.c0d) {
                    event.setCancelled(true);
                }
                this.c0d = true;
            }
        }
        if (mc.thePlayer == null || mc.thePlayer.isDead) {
            return;
        }
        if (badPacketsF.getValue().booleanValue() && (packet instanceof C0BPacketEntityAction)) {
            if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = true;
            } else if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = false;
            }
        }
        if (this.oldPostValue.getValue().booleanValue() && mc.getCurrentServerData() != null && ((packet instanceof C0APacketAnimation) || (packet instanceof C02PacketUseEntity) || (packet instanceof C0EPacketClickWindow) || (packet instanceof C08PacketPlayerBlockPlacement) || (packet instanceof C07PacketPlayerDigging))) {
            PacketUtil.sendPacket(new C0FPacketConfirmTransaction(114, (short) 514, true));
        }
        if (this.fastBreak.getValue().booleanValue() && (packet instanceof C07PacketPlayerDigging) && ((C07PacketPlayerDigging) packet).getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, ((C07PacketPlayerDigging) packet).getPosition(), ((C07PacketPlayerDigging) packet).getFacing()));
        }
        if (this.badPacketsA.getValue().booleanValue() && (packet instanceof C09PacketHeldItemChange)) {
            int slot = ((C09PacketHeldItemChange) packet).getSlotId();
            if (slot == this.lastSlot && slot != -1) {
                event.setCancelled(true);
            }
            this.lastSlot = ((C09PacketHeldItemChange) packet).getSlotId();
        }
        if (this.fakePingValue.getValue().booleanValue()) {
            if (((packet instanceof C00PacketKeepAlive) || (packet instanceof C16PacketClientStatus)) && mc.thePlayer.getHealth() > 0.0f && !this.packetsMap.containsKey(packet)) {
                event.setCancelled(true);
                HashMap<Packet<?>, Long> hashMap = this.packetsMap;
                synchronized (hashMap) {
                    this.packetsMap.put(packet, Long.valueOf(System.currentTimeMillis() + TimerUtil.randomDelay(199999, 9999999)));
                }
            }
        }
    }

    public static boolean getGrimPost() {
        return mc.thePlayer != null && mc.theWorld != null && INSTANCE != null && INSTANCE.getState() && postValue.getValue().booleanValue() && mc.thePlayer.ticksExisted > 30;
    }
}
