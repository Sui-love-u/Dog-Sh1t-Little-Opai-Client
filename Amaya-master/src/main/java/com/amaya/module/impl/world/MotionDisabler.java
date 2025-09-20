package com.amaya.module.impl.world;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.packet.PacketReceiveSyncEvent;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.events.impl.render.Render2DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.movement.Speed;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.player.PlayerUtil;
import com.amaya.utils.render.RoundedUtil;
import com.amaya.utils.time.StopWatch;
import com.amaya.utils.time.TimerUtil;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.*;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@ModuleInfo(name = "MDisabler",category = Category.World)
public class MotionDisabler extends Module {
    private ModeSetting mode = new ModeSetting("Mode","Watchdog",new String[]{"Watchdog"});
    private BooleanSetting c0fFix = new BooleanSetting("C0FFix", false);
    private BooleanSetting lowhop = new BooleanSetting("Lowhop", false);
    private BooleanSetting slotMachineFix = new BooleanSetting("SlotMachine Fix", false);
    private NumberSetting delay = new NumberSetting("Delay",1000, 0, 2000, 50);
    public MotionDisabler() {
        INSTANCE = this;
    }
    public final LinkedBlockingQueue<Packet<INetHandlerPlayClient>> setSlots = new LinkedBlockingQueue<>();

    private final StopWatch stopWatch = new StopWatch();

    public String getSuffix() {
        return "WatchDog";
    }

    private boolean action = false;
    private int testTicks;
    private WorldClient lastWorld = null;
    private int lastSlot;
    private boolean disabled;
    static MotionDisabler INSTANCE;
    private boolean stuck = false;
    private boolean jump = false;
    private long lastLoadWorldTime = 0L;
    private boolean lastTickSentC0F = false;
    private boolean spoofed = false;
    private boolean lastGround = true;

    private double storageX = 0.0;
    private double storageY = 0.0;
    private double storageZ = 0.0;
    private boolean flagStop = false;

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
    };

    public static void fixVia() {
        INSTANCE.stopWatch.reset();
        INSTANCE.action = true;
    }

    @EventTarget
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (slotMachineFix.getValue()) {
            if (action) {
                if (event.getPacket() instanceof S2FPacketSetSlot || event.getPacket() instanceof S30PacketWindowItems) {
                    event.setCancelled(true);
                    setSlots.add((Packet<INetHandlerPlayClient>) event.getPacket());
                }
                if (stopWatch.hasTimePassed(delay.get().longValue())) slotMachineReset();
            }

            if (event.getPacket() instanceof S2DPacketOpenWindow || event.getPacket() instanceof S2EPacketCloseWindow) slotMachineReset();
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
    };

    @EventTarget
   public void onPacket(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
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
    };
    @EventTarget
    public void sdjai(PacketReceiveEvent event){
        Packet<?> packet = event.getPacket();
        if (packet instanceof S09PacketHeldItemChange) {
            lastSlot = ((S09PacketHeldItemChange) packet).getHeldItemHotbarIndex();
        }
        if(mode.is("Watchdog") && lowhop.getValue() && disabled){
            if(mc.thePlayer.ticksExisted <= 200) {
                if(mc.thePlayer.ticksExisted == 4) {
                    mc.thePlayer.motionY = mc.thePlayer.motionZ = mc.thePlayer.motionX = 0;
                }
            }
            if(event.getPacket() instanceof S08PacketPlayerPosLook) {
                testTicks++;
                if(testTicks == 25) {
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
        if (lowhop.getValue() && mode.is("Watchdog")) {
            if(mc.thePlayer.onGround && jump) {
                mc.thePlayer.jump();
                mc.thePlayer.jumpTicks = 0;
            } else if (jump) {
                jump = false;
                disabled = true;
            } else if(disabled && mc.thePlayer.offGroundTicks >= 10) {
                if ((!isHypixel()) && jump) {
                    jump = false;
                    disabled = false;
                    testTicks = 0;
                    return;
                }
                if(mc.thePlayer.offGroundTicks % 2 == 0) {
                    event.setX(event.getX() + 0.095);
                    PlayerUtil.stop();
                }
                if(Amaya.Instance.moduleManager.getModule(Speed.class).getState()) {
                    Amaya.Instance.moduleManager.getModule(Speed.class).setState(false);
                }

//                if(Faiths.moduleManager.getModule(ModuleBHop.class).getState()) {
//                    Faiths.notificationManager.pop("Disabler","Disabled module Speed due to disabler is working", 3000, NotificationType.INFO);
//                    Faiths.moduleManager.getModule(ModuleBHop.class).setState(false);
//                }
                mc.thePlayer.motionY = 0;
            }
        }
    };

    @EventTarget
    public void onRender2D(Render2DEvent event){
        //RoundedUtil.drawRound(event.getScaledResolution().getScaledWidth() / 2,event);
    }
}