package qwq.arcane.event.impl.events.render;

import net.minecraft.client.gui.ScaledResolution;
import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/render/Render2DEvent.class */
public class Render2DEvent extends CancellableEvent {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public Render2DEvent(ScaledResolution scaledResolution, float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }
}
