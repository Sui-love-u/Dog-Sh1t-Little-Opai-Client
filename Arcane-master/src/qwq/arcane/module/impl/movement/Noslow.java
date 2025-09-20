package qwq.arcane.module.impl.movement;

import com.viaversion.viarewind.protocol.v1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketReceiveEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.SlowDownEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.utils.pack.BlinkComponent;
import qwq.arcane.utils.player.MovementUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/movement/Noslow.class */
public class Noslow extends Module {
    private final ModeValue mode;
    private final BoolValue bedWarsFood;
    private final BoolValue food;
    public final BoolValue bow;
    private final BoolValue potions;
    private final LinkedBlockingQueue<Packet<?>> packets;
    boolean usingItem;
    private boolean lastUsingRestItem;
    private static final int currentSlot = 0;
    public static boolean hasDroppedFood = false;
    private boolean sent;

    public Noslow() {
        super("Noslow", Category.Movement);
        this.mode = new ModeValue("Mode", "Blink", new String[]{"Blink", "Grim", "Prediction"});
        this.bedWarsFood = new BoolValue("Food (Bed Wars)", () -> {
            return this.mode.is("Grim");
        }, false);
        this.food = new BoolValue("Food", () -> {
            return this.mode.is("Grim");
        }, true);
        this.bow = new BoolValue("Bow", () -> {
            return this.mode.is("Grim");
        }, true);
        this.potions = new BoolValue("Potions", () -> {
            return this.mode.is("Grim");
        }, true);
        this.packets = new LinkedBlockingQueue<>();
        this.lastUsingRestItem = false;
        this.sent = false;
    }

    public static boolean hasSword() {
        return Mine.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public static boolean isRest(Item item) {
        return (item instanceof ItemFood) || (item instanceof ItemPotion);
    }

    public static ItemStack getHeldItem() {
        InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
        return getRenderHeldItem();
    }

    public static ItemStack getRenderHeldItem() {
        InventoryPlayer inventory = mc.thePlayer.inventory;
        if (inventory.currentItem >= 9 || inventory.currentItem < 0) {
            return null;
        }
        return inventory.mainInventory[inventory.currentItem];
    }

    public static int getCurrentSlot() {
        return mc.thePlayer.inventory.currentItem;
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.sent = false;
        super.onEnable();
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        this.packets.forEach(packet -> {
            mc.getNetHandler().addToSendQueueUnregistered(packet);
        });
        this.packets.clear();
        this.lastUsingRestItem = false;
        super.onDisable();
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setsuffix(String.valueOf(this.mode.get()));
        if (isGapple()) {
            return;
        }
        if (this.mode.is("Blink")) {
            if (mc.thePlayer.isUsingItem() && this.usingItem) {
                mc.thePlayer.setJumping(true);
            }
            if (mc.thePlayer.getCurrentEquippedItem() == null) {
                return;
            }
            Item item = mc.thePlayer.getCurrentEquippedItem().getItem();
            if ((mc.thePlayer.isUsingItem() && (item instanceof ItemFood)) || (item instanceof ItemPotion) || (item instanceof ItemBow) || (item instanceof ItemSword)) {
                BlinkComponent.blinking = true;
                BlinkComponent.dispatch();
            } else {
                this.usingItem = true;
                BlinkComponent.blinking = false;
            }
        }
        if (this.mode.is("Grim") && !mc.isSingleplayer()) {
            if (event.isPre()) {
                if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.getHeldItem() == null) {
                    return;
                }
                if (mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) && this.food.getValue().booleanValue()) {
                    Mine.getMinecraft().rightClickDelayTimer = 4;
                    if (mc.thePlayer.isUsingItem() && !hasDroppedFood && mc.thePlayer.getHeldItem().stackSize > 1) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        hasDroppedFood = true;
                    } else {
                        hasDroppedFood = false;
                    }
                }
                if (Mine.getMinecraft().thePlayer.inventory.getCurrentItem() != null) {
                    if (mc.thePlayer.isBlocking() || (mc.thePlayer.isUsingItem() && hasSword())) {
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem % 8) + 1));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer())));
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                    if ((mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) && mc.thePlayer.isUsingItem() && this.bow.getValue().booleanValue() && !mc.thePlayer.isSneaking()) {
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem % 8) + 1));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer())));
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                }
            }
            if (!event.isPost() || mc.thePlayer.getHeldItem() == null) {
                return;
            }
            if (mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) && mc.thePlayer.isUsingItem()) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
            if ((mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) && mc.thePlayer.isUsingItem() && this.bow.getValue().booleanValue()) {
                PacketWrapper useItem = PacketWrapper.create(29, (ByteBuf) null, (UserConnection) Via.getManager().getConnectionManager().getConnections().iterator().next());
                useItem.write(Types.VAR_INT, 1);
                useItem.sendToServer(Protocol1_9To1_8.class);
                PacketWrapper useItem2 = PacketWrapper.create(29, (ByteBuf) null, (UserConnection) Via.getManager().getConnectionManager().getConnections().iterator().next());
                useItem2.write(Types.VAR_INT, 0);
                useItem2.sendToServer(Protocol1_9To1_8.class);
            }
        }
    }

    @EventTarget
    public void onSlowDown(SlowDownEvent event) {
        if (isGapple()) {
        }
        if (this.mode.is("Prediction")) {
            if (mc.thePlayer.onGround && mc.thePlayer.offGroundTicks % 2 != 0) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
        switch (this.mode.get()) {
            case "Grim":
                if (mc.thePlayer.getHeldItem() == null || ((!(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) && (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion) || ItemPotion.isSplash(mc.thePlayer.getHeldItem().getMetadata()))) || !this.sent)) {
                    event.setCancelled(true);
                }
                if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.getHeldItem() != null) {
                    if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) || !this.food.getValue().booleanValue()) {
                        if (mc.thePlayer.getHeldItem() != null && (((mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) || ((mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) && this.bow.getValue().booleanValue())) && mc.thePlayer.isUsingItem())) {
                            event.setCancelled(true);
                        }
                        if (!mc.thePlayer.isSprinting() && !mc.thePlayer.isSneaking() && MovementUtil.isMoving()) {
                            mc.thePlayer.setSprinting(true);
                            break;
                        }
                    }
                }
                break;
            case "Watchdog":
                Item item = mc.thePlayer.getCurrentEquippedItem().getItem();
                if (mc.thePlayer.isUsingItem() && this.usingItem) {
                    if ((item instanceof ItemFood) || (item instanceof ItemPotion) || (item instanceof ItemBow) || (item instanceof ItemSword)) {
                        event.setCancelled();
                        event.setForward(1.0f);
                        event.setStrafe(1.0f);
                        event.setSprinting(false);
                        break;
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onReceive(PacketReceiveEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (!isGapple() && this.mode.is("Grim")) {
            Packet<?> packet = event.getPacket();
            if (packet instanceof C08PacketPlayerBlockPlacement) {
                C08PacketPlayerBlockPlacement wrapped = (C08PacketPlayerBlockPlacement) packet;
                if (wrapped.getPlacedBlockDirection() == 255 && wrapped.getPosition().equals(new BlockPos(-1, -1, -1)) && !this.sent) {
                    mc.thePlayer.sendChatMessage("/lizi open");
                    this.sent = true;
                }
            }
        }
    }

    @EventTarget
    public void onPacketSend(PacketSendEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (isGapple()) {
            return;
        }
        Packet<?> packet = event.getPacket();
        if (mc.thePlayer == null) {
            return;
        }
        if (this.mode.is("Blink")) {
            if (packet instanceof C08PacketPlayerBlockPlacement) {
                C08PacketPlayerBlockPlacement status = (C08PacketPlayerBlockPlacement) packet;
                if (status.getPlacedBlockDirection() >= 4) {
                    this.usingItem = true;
                    this.packets.add(status);
                }
            } else if (!this.packets.isEmpty()) {
                LinkedBlockingQueue<Packet<?>> linkedBlockingQueue = this.packets;
                NetHandlerPlayClient netHandler = mc.getNetHandler();
                Objects.requireNonNull(netHandler);
                linkedBlockingQueue.forEach(netHandler::addToSendQueueUnregistered);
                this.packets.clear();
            }
        }
        if (this.mode.is("Grim") && this.mode.is("Grim") && this.bedWarsFood.getValue().booleanValue()) {
            if (mc.thePlayer.getHeldItem() != null && ((mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) || ((mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion) && !ItemPotion.isSplash(mc.thePlayer.getHeldItem().getMetadata())))) {
                if (packet instanceof C08PacketPlayerBlockPlacement) {
                    C08PacketPlayerBlockPlacement wrapped = (C08PacketPlayerBlockPlacement) packet;
                    if (wrapped.getPlacedBlockDirection() == 255 && wrapped.getPosition().equals(new BlockPos(-1, -1, -1)) && !this.sent) {
                        mc.thePlayer.sendChatMessage("/lizi open");
                        this.sent = true;
                        return;
                    }
                    return;
                }
                return;
            }
            this.sent = false;
        }
    }
}
