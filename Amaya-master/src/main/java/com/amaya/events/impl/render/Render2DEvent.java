package com.amaya.events.impl.render;

import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import com.amaya.events.events.callables.EventCancellable;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@Getter
public class Render2DEvent extends EventCancellable{
    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public Render2DEvent(ScaledResolution scaledResolution,float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }
}
