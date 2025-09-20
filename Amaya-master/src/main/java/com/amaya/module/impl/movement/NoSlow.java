package com.amaya.module.impl.movement;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.SlowDownEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.combat.KillAura;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.utils.player.InventoryUtil;
import com.amaya.utils.player.Rise.MoveUtil;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.vialoadingbase.ViaLoadingBase;
import org.lwjgl.input.Keyboard;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

import static com.amaya.utils.pack.PacketUtil.sendPacketNoEvent;

@ModuleInfo(name = "NoSlow", category = Category.Movement)
public class NoSlow extends Module {
    private static final int currentSlot = 0;
    public static boolean hasDroppedFood = false;
    private final ModeSetting mode = new ModeSetting("Mode","Grim",new String[]{"Grim", "Watchdog", "Intave", "Polar"});
    private final BooleanSetting bedWarsFood = new BooleanSetting("Food (Bed Wars)", false);
    private final BooleanSetting food = new BooleanSetting("Food",() -> this.mode.is("Grim"), true);
    private final BooleanSetting bow = new BooleanSetting("Bow",() -> this.mode.is("Grim"), true);
    private final BooleanSetting potions = new BooleanSetting("Potions",() -> this.mode.is("Grim"), true);
    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    private boolean lastUsingRestItem = false;
    private int offGroundTicks = 0;
    private boolean send = false;
    private boolean sent = false;

    public static boolean hasSword() {
        return Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public static boolean isRest(Item item) {
        return item instanceof ItemFood || item instanceof ItemPotion;
    }

    public static ItemStack getHeldItem() {
        final InventoryPlayer inventory = mc.thePlayer.inventory;
        if (currentSlot != 0)
            return currentSlot < 9 && currentSlot >= 0 ? inventory.mainInventory[currentSlot] : null;
        return getRenderHeldItem();
    }

    public static ItemStack getRenderHeldItem() {
        final InventoryPlayer inventory = mc.thePlayer.inventory;
        return inventory.currentItem < 9 && inventory.currentItem >= 0 ? inventory.mainInventory[inventory.currentItem] : null;
    }

    public static int getCurrentSlot() {
        if (currentSlot != 0)
            return currentSlot;
        return mc.thePlayer.inventory.currentItem;
    }

    @Override
    public void onEnable() {
        sent = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        packets.forEach(packet -> mc.getNetHandler().addToSendQueueUnregistered(packet));
        packets.clear();
        lastUsingRestItem = false;
        super.onDisable();
    }

    @EventTarget
    public void onSlow(SlowDownEvent event) {
        if (this.isGapple()) {
            return;
        }
        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.getHeldItem() == null) return;
        if (this.potions.getValue() && mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion) {
            event.setCancelled(true);
        }
        switch (mode.getValue()) {
            case "Grim":
                if (mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || (mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && !ItemPotion.isSplash(mc.thePlayer.getHeldItem().getMetadata())))) {
                    if (!sent) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
                if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.getHeldItem() == null || mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && food.getValue())
                    return;
                if (mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && bow.getValue())) && mc.thePlayer.isUsingItem())
                    event.setCancelled(true);
                if (!mc.thePlayer.isSprinting() && !mc.thePlayer.isSneaking() && MoveUtil.isMoving()) {
                    mc.thePlayer.setSprinting(true);
                }
                break;
            case "Intave":
            case "Polar":
                if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow))
                    event.setCancelled(true);
                if (!mc.thePlayer.isSprinting() && !mc.thePlayer.isSneaking() && MoveUtil.isMoving()) {
                    mc.thePlayer.setSprinting(true);
                }
                break;
            case "Watchdog":
                if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.getHeldItem() == null) return;
                if (mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)) && mc.thePlayer.isUsingItem())
                    event.setCancelled(true);
                if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && mc.thePlayer.isUsingItem()) {
                    event.setCancelled(true);
                    mc.timer.timerSpeed = 0.85F;
                } else {
                    mc.timer.timerSpeed = 1.0F;
                }
                if (!mc.thePlayer.isSprinting() && !mc.thePlayer.isSneaking() && MoveUtil.isMoving()) {
                    mc.thePlayer.setSprinting(true);
                }
                break;
        }
    }

    @EventTarget
    public void onReceive(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement wrapped = (C08PacketPlayerBlockPlacement) packet;

            if (wrapped.getPlacedBlockDirection() == 255 && wrapped.getPosition().equals(new BlockPos(-1, -1, -1))) {
                if (!sent) {
                    mc.thePlayer.sendChatMessage("/lizi open");
                    sent = true;
                }
            }
        }
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (mode.is("Grim") && bedWarsFood.getValue()) {
            if (mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || (mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && !ItemPotion.isSplash(mc.thePlayer.getHeldItem().getMetadata())))) {
                if (packet instanceof C08PacketPlayerBlockPlacement) {
                    C08PacketPlayerBlockPlacement wrapped = (C08PacketPlayerBlockPlacement) packet;

                    if (wrapped.getPlacedBlockDirection() == 255 && wrapped.getPosition().equals(new BlockPos(-1, -1, -1))) {
                        if (!sent) {
                            mc.thePlayer.sendChatMessage("/lizi open");
                            sent = true;
                        }
                    }
                }
            } else {
                sent = false;
            }

        }
        if (mode.is("Watchdog")) {
            if (packet instanceof C08PacketPlayerBlockPlacement) {
                C08PacketPlayerBlockPlacement blockPlacement = (C08PacketPlayerBlockPlacement) packet;

                if (!mc.thePlayer.isUsingItem()) {
                    if (getHeldItem() != null && blockPlacement.getPlacedBlockDirection() == 255
                            && InventoryUtil.isRest(getHeldItem().getItem()) && offGroundTicks < 2) {

                        if (mc.thePlayer.onGround && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                            mc.thePlayer.jump();
                        }

                        send = true;
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventTarget
    public void onMotion(MotionEvent e) {
        setSuffix(mode.getValue());
        if (this.isGapple()) {
            return;
        }
        switch (this.mode.getValue()) {
            case "Watchdog":
                if (!mc.thePlayer.isUsingItem() || getHeldItem() == null) return;

                if (getHeldItem().getItem() instanceof ItemSword) {
                    final int slot = getCurrentSlot();

                    com.amaya.utils.pack.PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    com.amaya.utils.pack.PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot < 8 ? slot + 1 : 0));
                    com.amaya.utils.pack.PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                    if (mc.thePlayer.isUsingItem() && mc.thePlayer.isBlocking() && mc.thePlayer.ticksExisted % 3 == 0) {
                        com.amaya.utils.pack.PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                }

                if (mc.thePlayer.onGround) {
                    offGroundTicks = 0;
                } else {
                    offGroundTicks++;
                }

                final ItemStack item = getHeldItem();
                if (offGroundTicks == 4 && send) {
                    send = false;
                    com.amaya.utils.pack.PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(
                            new BlockPos(-1, -1, -1),
                            255, item,
                            0, 0, 0
                    ));

                } else if (item != null && mc.thePlayer.isUsingItem()) {
                    e.setY(e.getY() + 1E-14);
                }
                break;
            case "Grim":
                if (!mc.isSingleplayer()) {
                    if (e.isPre()) {
                        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.getHeldItem() == null) return;
                        //F00d N0Sl0w
                        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && food.getValue()) {
                            Minecraft.getMinecraft().rightClickDelayTimer = 4;
                            if (mc.thePlayer.isUsingItem() && !hasDroppedFood && mc.thePlayer.getHeldItem().stackSize > 1) {
                                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                hasDroppedFood = true;
                            } else {
                                hasDroppedFood = false;
                            }
                        }
                        if (Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null) {
                            if (mc.thePlayer.isBlocking() || mc.thePlayer.isUsingItem() && hasSword()) {
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                                mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer())));
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(NoSlow.mc.thePlayer.inventory.currentItem));
                            }
                            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem() && bow.getValue() && !mc.thePlayer.isSneaking()) {
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                                mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer())));
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            }
                        }
                    }
                    if (e.isPost()) {
                        if (mc.thePlayer.getHeldItem() == null) return;
                        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.isUsingItem()) {
                            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(NoSlow.mc.thePlayer.getHeldItem()));
                        }
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem() && bow.getValue()) {
                            PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem.write(Type.VAR_INT, 1);
                            PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                            PacketWrapper useItem2 = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem2.write(Type.VAR_INT, 0);
                            PacketUtil.sendToServer(useItem2, Protocol1_8To1_9.class, true, true);
                        }
                    }
                }
                break;
            case "Intave":
                final Item item3 = mc.thePlayer != null ? mc.thePlayer.getHeldItem().getItem() : null;
                if (!e.isPre()) return;
                if (!mc.thePlayer.isUsingItem()) {
                    lastUsingRestItem = false;
                    return;
                }

                if (!MoveUtil.isMoving()) return;
                if (isRest(item3)) {
                    if (!lastUsingRestItem) {
                        com.amaya.utils.pack.PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
                    }
                    lastUsingRestItem = true;
                } else {
                    lastUsingRestItem = false;

                    if (item3 instanceof ItemSword) {
                        com.amaya.utils.pack.PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    }
                }
                break;
            case "Polar":
                final Item item2 = mc.thePlayer != null ? mc.thePlayer.getHeldItem().getItem() : null;
                if (!e.isPre()) return;
                if (!mc.thePlayer.isUsingItem()) {
                    lastUsingRestItem = false;
                    return;
                }

                if (isRest(item2)) {
                    if (!lastUsingRestItem) {
                        com.amaya.utils.pack.PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
                    }
                    com.amaya.utils.pack.PacketUtil.send(new C0CPacketInput(0, 0.82f, false, false));
                    lastUsingRestItem = true;
                } else {
                    lastUsingRestItem = false;

                    if (item2 instanceof ItemSword) {
                        com.amaya.utils.pack.PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        com.amaya.utils.pack.PacketUtil.send(new C0CPacketInput(0, 0.82f, false, false));
                    }
                }
                break;
        }
    }
}