package qwq.arcane.module.impl.movement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import org.lwjgl.input.Keyboard;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.MoveInputEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.player.InvManager;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/movement/GuiMove.class */
public class GuiMove extends Module {
    public final ModeValue mode;
    public final BoolValue noChestValue;
    public final BoolValue noInventoryValue;
    int tick;
    double dist;
    private boolean c16;
    private boolean c0d;
    private boolean OpenInventory;
    C0DPacketCloseWindow pc;
    C16PacketClientStatus c16C;
    public static List<Packet<?>> InvPacketList = new ArrayList();

    public GuiMove() {
        super("GuiMove", Category.Movement);
        this.mode = new ModeValue("Mode", "Basic", new String[]{"Basic", "Hypixel", "LastPacket"});
        this.noChestValue = new BoolValue("Chest", false);
        this.noInventoryValue = new BoolValue("Inventory", false);
        this.tick = 0;
        this.dist = 0.0d;
        this.c16 = false;
        this.c0d = false;
        this.OpenInventory = false;
        this.pc = null;
        this.c16C = null;
    }

    @EventTarget
    private void onPacket(PacketSendEvent event) {
        if (this.mode.is("LastPacket")) {
            if (isHypixelLobby()) {
                return;
            }
            Packet<?> packet = event.getPacket();
            if (packet instanceof C16PacketClientStatus) {
                C16PacketClientStatus p = (C16PacketClientStatus) packet;
                if (this.c16C == null && p.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                    this.c16C = p;
                }
            }
            Packet<?> packet2 = event.getPacket();
            if (packet2 instanceof C0EPacketClickWindow) {
                C0EPacketClickWindow p2 = (C0EPacketClickWindow) packet2;
                if (this.pc == null) {
                    InvPacketList.add(p2);
                    event.setCancelled(true);
                }
            }
            if ((event.getPacket() instanceof C0DPacketCloseWindow) && this.pc == null) {
                this.pc = (C0DPacketCloseWindow) event.getPacket();
                event.setCancelled(true);
            }
        }
        if (this.mode.is("Hypixel")) {
            if ((event.getPacket() instanceof C03PacketPlayer) && (mc.currentScreen instanceof GuiChest) && this.tick > 0) {
                InvPacketList.add(event.getPacket());
                event.setCancelled(true);
            }
            Packet<?> packet3 = event.getPacket();
            if (packet3 instanceof C16PacketClientStatus) {
                C16PacketClientStatus p3 = (C16PacketClientStatus) packet3;
                if (this.c16 && p3.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                    event.setCancelled(true);
                }
                this.c16 = true;
            }
            Packet<?> packet4 = event.getPacket();
            if (packet4 instanceof C0EPacketClickWindow) {
                C0EPacketClickWindow p4 = (C0EPacketClickWindow) packet4;
                if (this.tick > 0 || this.OpenInventory) {
                    InvPacketList.add(p4);
                    event.setCancelled(true);
                }
            }
            if (event.getPacket() instanceof C0DPacketCloseWindow) {
                if (this.c0d && this.tick <= 1 && this.OpenInventory) {
                    event.setCancelled(true);
                } else if (!InvPacketList.isEmpty()) {
                    event.setCancelled(true);
                    for (Packet<?> p5 : InvPacketList) {
                        mc.getNetHandler().addToSendQueueUnregistered(p5);
                    }
                    InvPacketList.clear();
                    mc.getNetHandler().addToSendQueueUnregistered(event.getPacket());
                }
                this.c0d = true;
            }
        }
    }

    public static boolean isHypixelLobby() {
        String[] strings = {"CLICK TO PLAY"};
        for (Entity entity : Mine.getMinecraft().theWorld.playerEntities) {
            if (entity.getName().startsWith("§e§l")) {
                for (String string : strings) {
                    if (entity.getName().equals("§e§l" + string)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @EventTarget
    public void onUpdate(MotionEvent e) {
        setsuffix(this.mode.getValue());
        if (this.mode.is("LastPacket") && e.isPre()) {
            if (isHypixelLobby()) {
                return;
            }
            if (mc.currentScreen == null) {
                if (this.pc != null) {
                    if (this.c16C != null && !InvPacketList.isEmpty()) {
                        mc.getNetHandler().addToSendQueueUnregistered(this.c16C);
                    }
                    if (!InvPacketList.isEmpty()) {
                        for (Packet<?> p : InvPacketList) {
                            mc.getNetHandler().addToSendQueue(p);
                        }
                    }
                    if (this.c16C == null || !InvPacketList.isEmpty()) {
                        mc.getNetHandler().addToSendQueueUnregistered(this.pc);
                    }
                }
                InvPacketList.clear();
                this.c16C = null;
                this.pc = null;
            }
        }
        if (this.mode.is("Hypixel") && e.isPre()) {
            this.c16 = false;
            this.c0d = false;
            this.OpenInventory = false;
            if (!(mc.currentScreen instanceof GuiInventory) && (!isEnabled(InvManager.class) || !((InvManager) getModule(InvManager.class)).clientOpen)) {
                if (mc.currentScreen instanceof GuiChest) {
                    double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                    double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                    double lastDist = Math.sqrt((xDist * xDist) + (zDist * zDist));
                    this.tick++;
                    this.dist += lastDist;
                    return;
                }
                this.tick = 0;
                this.dist = 0.0d;
                return;
            }
            double xDist2 = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist2 = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            double lastDist2 = Math.sqrt((xDist2 * xDist2) + (zDist2 * zDist2));
            this.OpenInventory = true;
            if (this.tick == 1) {
                mc.getNetHandler().addToSendQueueUnregistered(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            }
            if (this.dist / this.tick > 0.0d) {
                if (this.tick == (this.dist / ((double) this.tick) > 0.45d ? 2 : 3) && !InvPacketList.isEmpty()) {
                    for (Packet<?> p2 : InvPacketList) {
                        mc.getNetHandler().addToSendQueueUnregistered(p2);
                    }
                    InvPacketList.clear();
                }
                if (this.tick > (this.dist / ((double) this.tick) > 0.45d ? 2 : 3)) {
                    mc.getNetHandler().addToSendQueueUnregistered(new C0DPacketCloseWindow());
                    this.tick = 0;
                    this.dist = 0.0d;
                }
            } else if (this.tick > 0) {
                if (!InvPacketList.isEmpty()) {
                    for (Packet<?> p3 : InvPacketList) {
                        mc.getNetHandler().addToSendQueueUnregistered(p3);
                    }
                    InvPacketList.clear();
                }
                this.tick = 1;
                this.dist = 0.0d;
            }
            this.tick++;
            this.dist += lastDist2;
        }
    }

    @EventTarget
    public final void onUpdate(UpdateEvent event) {
        if (mc.currentScreen == null || mc.currentScreen.GuiInvMove()) {
            return;
        }
        KeyBinding[] moveKeys = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump};
        for (KeyBinding bind : moveKeys) {
            KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
        }
    }

    @EventTarget
    public final void onMoveInput(MoveInputEvent event) {
        if (mc.currentScreen == null || mc.currentScreen.GuiInvMove()) {
            return;
        }
        float moveStrafe = 0.0f;
        float moveForward = 0.0f;
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            moveForward = 0.0f + 1.0f;
        }
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            moveForward -= 1.0f;
        }
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
            moveStrafe = 0.0f + 1.0f;
        }
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
            moveStrafe -= 1.0f;
        }
        if (this.mode.is("Hypixel") && (mc.currentScreen instanceof GuiChest)) {
            if (this.dist / this.tick > 0.05d) {
                if (!InvPacketList.isEmpty()) {
                    for (Packet<?> p : InvPacketList) {
                        mc.getNetHandler().addToSendQueueUnregistered(p);
                    }
                    InvPacketList.clear();
                }
                this.tick = 0;
                this.dist = 0.0d;
            } else if (this.tick > 0) {
                if (!InvPacketList.isEmpty()) {
                    for (Packet<?> p2 : InvPacketList) {
                        mc.getNetHandler().addToSendQueueUnregistered(p2);
                    }
                    InvPacketList.clear();
                }
                this.tick = 1;
                this.dist = 0.0d;
            }
        }
        event.setForward(moveForward);
        event.setStrafe(moveStrafe);
    }
}
