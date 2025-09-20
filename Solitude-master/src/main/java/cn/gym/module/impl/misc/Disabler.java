package cn.gym.module.impl.misc;

import cn.gym.Solitude;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.WorldEvent;
import cn.gym.events.impl.packet.HigherPacketEvent;
import cn.gym.events.impl.packet.PacketReceiveEvent;
import cn.gym.events.impl.packet.PacketReceiveSyncEvent;
import cn.gym.events.impl.packet.PacketSendEvent;
import cn.gym.events.impl.player.MotionEvent;
import cn.gym.events.impl.player.UpdateEvent;
import cn.gym.events.impl.render.Render2DEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.movement.Speed;
import cn.gym.module.impl.player.InvManager;
import cn.gym.utils.player.PlayerUtil;
import cn.gym.utils.render.RoundedUtil;
import cn.gym.utils.time.StopWatch;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ModeValue;
import cn.gym.value.impl.NumberValue;
import com.viaversion.viarewind.protocol.v1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.Protocol1_20To1_20_2;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.packet.ServerboundPackets1_20_2;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.Potion;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static cn.gym.utils.pack.PacketUtil.sendPacketNoEvent;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 14:20
 */
public class Disabler extends Module {
    private ModeValue mode = new ModeValue("Mode", "Watchdog", new String[]{"Watchdog"});
    private BooleanValue c0fFix = new BooleanValue("C0FFix", false);
    private BooleanValue lowhop = new BooleanValue("Lowhop", false);
    private BooleanValue slotMachineFix = new BooleanValue("SlotMachine Fix", false);
    private NumberValue delay = new NumberValue("Delay", 1000, 0, 2000, 50);

    public Disabler() {
        super("Disabler", Category.Misc);
        INSTANCE = this;
    }

    public final LinkedBlockingQueue<Packet<INetHandlerPlayClient>> setSlots = new LinkedBlockingQueue<>();

    private final StopWatch stopWatch = new StopWatch();
    private boolean action = false;
    public int testTicks;
    private WorldClient lastWorld = null;
    private int lastSlot;
    public boolean disabled;
    static Disabler INSTANCE;
    private boolean stuck = false;
    private boolean jump = false;
    private long lastLoadWorldTime = 0L;
    private boolean lastTickSentC0F = false;
    private boolean spoofed = false;
    private boolean lastGround = true;
    private boolean c16 = false;
    private boolean c0d = false;
    private final StopWatch lowhopFinishTimer = new StopWatch();
    private boolean shouldRegisterProtocol = false;
    private double x, y, z, motionX, motionY, motionZ;
    private boolean flag1 = false;

    public static ArrayList<C0FPacketConfirmTransaction> c0fStorage = new ArrayList<>();

    @Override
    public void onEnable() {
        jump = false;
        testTicks = 0;
    }

    @EventTarget
    public void onWorldLoad(WorldEvent event) {
        c0fStorage.clear();
        lastLoadWorldTime = System.currentTimeMillis();
        stuck = false;
        spoofed = false;
        jump = true;
        testTicks = 0;
        shouldRegisterProtocol = false;
        lowhopFinishTimer.reset();
    }
    ;

    public boolean checkCompass() {
        boolean compass = false;
        for (int i = 0; i < 9; i++) {
            final ItemStack stackInSlot = mc.thePlayer.inventory.getStackInSlot(i);

            if (stackInSlot != null && stackInSlot.getUnlocalizedName().toLowerCase().contains("compass")) {
                compass = true;
            }
        }
        return compass;
    }

    public static void fixVia() {
        INSTANCE.stopWatch.reset();
        INSTANCE.action = true;
    }

    @EventTarget
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (slotMachineFix.getValue()) {
            if (action) {
                if (event.getPacket() instanceof S2FPacketSetSlot || event.getPacket() instanceof S30PacketWindowItems || event.getPacket() instanceof C0EPacketClickWindow) {
                    event.setCancelled(true);
                    setSlots.add((Packet<INetHandlerPlayClient>) event.getPacket());
                }
                if (stopWatch.hasTimePassed(delay.get().longValue())) slotMachineReset();
            }

            if (event.getPacket() instanceof S2DPacketOpenWindow || event.getPacket() instanceof S2EPacketCloseWindow)
                slotMachineReset();
        }
    }

    private void slotMachineReset() {
        while (!setSlots.isEmpty()) {
            try {
                setSlots.poll().processPacket(mc.getNetHandler());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        action = false;
    }

    public static boolean shouldProcess() {
        return true;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        lastTickSentC0F = false;
        if (slotMachineFix.get()) {
            c16 = false;
            c0d = false;
            if (mc.currentScreen instanceof GuiInventory || getModule(InvManager.class).serverOpen) {
                if (mc.thePlayer.ticksExisted % (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 3 : 4) == 0) {
                    sendPacketNoEvent(new C0DPacketCloseWindow());
                } else if (mc.thePlayer.ticksExisted % (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 3 : 4) == 1) {
                    sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                }
            }
        }
    }

    ;

    @EventTarget
    public void onPacketSend(HigherPacketEvent event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            PacketWrapper blockPlace = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
            blockPlace.write(Types.VAR_INT, 1);
            try {
                blockPlace.sendToServer(Protocol1_9To1_8.class, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @EventTarget
    public void onPacketSend(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (slotMachineFix.get()) {
            if (event.getPacket() instanceof C16PacketClientStatus) {
                if (c16) {
                    event.setCancelled(true);
                }
                c16 = true;
            }

            if (event.getPacket() instanceof C0DPacketCloseWindow) {
                if (c0d) {
                    event.setCancelled(true);
                }
                c0d = true;
            }
        }
        if (packet instanceof C09PacketHeldItemChange) {
            if (lastSlot == ((C09PacketHeldItemChange) packet).getSlotId()) {
                event.setCancelled(true);
            }
            lastSlot = ((C09PacketHeldItemChange) packet).getSlotId();
        }
        if (c0fFix.getValue() && System.currentTimeMillis() - lastLoadWorldTime >= 2000) {
            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                if (!lastTickSentC0F) {
                    if (!c0fStorage.isEmpty()) {
                        c0fStorage.add((C0FPacketConfirmTransaction) event.getPacket());
                        event.setCancelled(true);
                        mc.getNetHandler().addToSendQueue(c0fStorage.get(0));
                        c0fStorage.remove(0);
                        lastTickSentC0F = true;
                    }
                } else {
                    c0fStorage.add((C0FPacketConfirmTransaction) event.getPacket());
                    event.setCancelled(true);
                }
            }
        }
        if (mode.is("Watchdog")) {
            if (event.getPacket() instanceof C03PacketPlayer && (!lowhop.getValue())) {
                final C03PacketPlayer wrapper = ((C03PacketPlayer) event.getPacket());

                if (!wrapper.isMoving() && !wrapper.rotating && wrapper.isOnGround() && lastGround) {
                    event.setCancelled(true);
                }
                lastGround = wrapper.isOnGround();
            }
        }
    }

    ;

    @EventTarget
    public void onPacketReceive(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S09PacketHeldItemChange) {
            lastSlot = ((S09PacketHeldItemChange) packet).getHeldItemHotbarIndex();
        }
        if (checkCompass()) return;
        if (mode.is("Watchdog") && lowhop.getValue() && disabled) {
            if (mc.thePlayer.ticksExisted <= 200) {
                if (mc.thePlayer.ticksExisted == 4) {
                    mc.thePlayer.motionY = mc.thePlayer.motionZ = mc.thePlayer.motionX = 0;
                }
            }

            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                Protocol1_20To1_20_2 protocol20_2 = Via.getManager().getProtocolManager().getProtocol(Protocol1_20To1_20_2.class);
                if (protocol20_2 != null) {
                    protocol20_2.registerServerbound(ServerboundPackets1_20_2.MOVE_PLAYER_POS, new PacketHandlers() {
                        public void register() {
                            this.map(Types.DOUBLE); // X
                            this.map(Types.DOUBLE); // Y
                            this.map(Types.DOUBLE); // Z
                            this.map(Types.FLOAT);  // Yaw
                            this.map(Types.FLOAT);  // Pitch
                            this.map(Types.BOOLEAN);// On Ground
                            this.map(Types.INT);
                        }
                    });
                }
                testTicks++;
                if (testTicks == 25) {
                    mc.thePlayer.jump();
                    disabled = false;
                    testTicks = 0;
                }
                mc.thePlayer.motionY = mc.thePlayer.motionZ = mc.thePlayer.motionX = 0;
            }
        }
    }

    public static boolean isHypixel() {
        return !mc.isSingleplayer() && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.contains("hypixel.net");
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setSuffix(mode.get());

        if (checkCompass()) return;
        if (lowhop.getValue() && mode.is("Watchdog")) {
            if (mc.thePlayer.onGround && jump) {
                mc.thePlayer.jump();
                mc.thePlayer.jumpTicks = 0;
                shouldRegisterProtocol = true; // 开始lowhop时设置标志
            } else if (jump) {
                jump = false;
                disabled = true;
                lowhopFinishTimer.reset(); // 重置计时器
            } else if (disabled && mc.thePlayer.offGroundTicks >= 10) {
                if ((!isHypixel()) && jump) {
                    jump = false;
                    disabled = false;
                    testTicks = 0;
                    return;
                }
                if (mc.thePlayer.offGroundTicks % 2 == 0) {
                    event.setX(event.getX() + 0.095);
                    PlayerUtil.stop();
                }
                if (Solitude.Instance.getModuleManager().getModule(Speed.class).getState()) {
                    Solitude.Instance.getModuleManager().getModule(Speed.class).setState(false);
                }
                mc.thePlayer.motionY = 0;
            }
        }
    }

    ;

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (checkCompass()) return;
        if (lowhop.getValue() && mode.is("Watchdog") && disabled && testTicks < 25) {
            int width = event.getScaledResolution().getScaledWidth();
            int height = event.getScaledResolution().getScaledHeight();

            int barWidth = 100;
            int barHeight = 5;
            int x = width / 2 - barWidth / 2;
            int y = height / 2 + 20;

            float progress = testTicks / 25f;

            RoundedUtil.drawRound(x, y, barWidth, barHeight, 2, new Color(0x80000000));

            RoundedUtil.drawRound(x, y, (int) (barWidth * progress), barHeight, 2, new Color(0xFF00FF00));

            mc.fontRendererObj.drawStringWithShadow(
                    "Disabling: " + testTicks + "/25",
                    x,
                    y - 10,
                    0xFFFFFFFF
            );
        }
    }
}
