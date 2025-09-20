package com.amaya.component;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;

import static com.amaya.utils.client.InstanceAccess.mc;

public final class FallDistanceManager {

    public float distance;
    private float lastDistance;

    @EventTarget
    private void onMotion(MotionEvent event) {
        if (event.isPre()) {
            final float fallDistance = mc.thePlayer.fallDistance;

            if (fallDistance == 0) {
                distance = 0;
            }

            distance += fallDistance - lastDistance;
            lastDistance = fallDistance;
        }
    }
}
