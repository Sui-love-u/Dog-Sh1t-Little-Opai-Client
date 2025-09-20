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
public class MoveInputEvent extends EventCancellable{
    private float forward;
    private float strafe;
    private boolean jumping;
    private boolean sneaking;
}