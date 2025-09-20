package com.amaya.component;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.events.impl.player.MoveEvent;
import com.amaya.utils.client.InstanceAccess;

public final class MovementComponent implements InstanceAccess {
    public static final MovementComponent INSTANCE = new MovementComponent();
    public static boolean cancelMove = false;
    public static boolean forceStuck = false;

    public static void cancelMove() {
        cancelMove(false);
    }

    public static void cancelMove(boolean force) {
        if (mc.thePlayer == null) {
            return;
        }
        if (cancelMove) {
            return;
        }
        forceStuck = force;
        cancelMove = true;
    }

    public static void resetMove() {
        cancelMove = false;
        mc.theWorld.skiptick = 0;
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if (cancelMove) {
            if (forceStuck) {
                return;
            }
            if (mc.theWorld.skiptick > 0) {
                return;
            }
            mc.theWorld.skiptick = 20;
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (cancelMove && forceStuck) {
            mc.theWorld.skiptick = 20;
        }
    }
//    @Listener
//
//    public void onPacketReceive(PacketReceiveEvent event) {
//        if (event.getPacket() instanceof S12PacketEntityVelocity s12 && s12.getEntityID() == mc.thePlayer.getEntityId() && cancelMove && !forceStuck) {
//            if (mc.theWorld.skiptick <= 0) {
//                mc.theWorld.skiptick--;
//                return;
//            }
//            mc.theWorld.skiptick = 0;
//        }
//    }
}