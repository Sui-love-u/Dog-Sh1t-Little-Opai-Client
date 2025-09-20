/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package com.amaya.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.amaya.events.events.callables.EventCancellable;

@Getter
@Setter
@AllArgsConstructor
public class MoveEvent extends EventCancellable{
    public double x, y, z;
    private boolean isSafeWalk = false;
    public boolean isSafeWalk() {
        return isSafeWalk;
    }
    public MoveEvent(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}