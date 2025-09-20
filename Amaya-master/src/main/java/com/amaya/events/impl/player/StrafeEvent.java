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

import lombok.Getter;
import lombok.Setter;
import com.amaya.events.events.callables.EventCancellable;

@Setter
@Getter
public class StrafeEvent extends EventCancellable{
    public float strafe;
    public float forward;
    public float friction;
    public float yaw;

    public StrafeEvent(float Strafe, float Forward, float Friction, float Yaw) {
        this.strafe = Strafe;
        this.forward = Forward;
        this.friction = Friction;
        this.yaw = Yaw;
    }
}