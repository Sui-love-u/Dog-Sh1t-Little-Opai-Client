package cn.gym.events.impl.render;

import cn.gym.events.impl.CancellableEvent;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@Getter
public class Render2DEvent extends CancellableEvent {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public Render2DEvent(ScaledResolution scaledResolution,float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }
}
