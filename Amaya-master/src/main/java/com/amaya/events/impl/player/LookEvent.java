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

import com.amaya.utils.math.Vector2f;
import lombok.Getter;
import lombok.Setter;
import com.amaya.events.events.callables.EventCancellable;

@Getter
@Setter
public class LookEvent extends EventCancellable{
    private Vector2f rotation;
    public LookEvent(Vector2f rotation) {
        this.rotation = rotation;
    }
}
