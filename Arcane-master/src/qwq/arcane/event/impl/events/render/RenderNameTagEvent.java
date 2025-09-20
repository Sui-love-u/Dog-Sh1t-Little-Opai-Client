package qwq.arcane.event.impl.events.render;

import net.minecraft.entity.EntityLivingBase;
import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/render/RenderNameTagEvent.class */
public final class RenderNameTagEvent extends CancellableEvent {
    private final EntityLivingBase entityLivingBase;

    public RenderNameTagEvent(EntityLivingBase entityLivingBase) {
        this.entityLivingBase = entityLivingBase;
    }

    public EntityLivingBase getEntityLivingBase() {
        return this.entityLivingBase;
    }
}
