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
package com.amaya.events.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import com.amaya.events.events.callables.EventCancellable;

@Getter
@AllArgsConstructor
public class Render3DEvent extends EventCancellable{
    public final float partialTicks;
    private final ScaledResolution scaledResolution;
}
