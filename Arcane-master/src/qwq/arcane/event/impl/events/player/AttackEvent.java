package qwq.arcane.event.impl.events.player;

import net.minecraft.entity.Entity;
import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/player/AttackEvent.class */
public final class AttackEvent extends CancellableEvent {
    private final Entity targetEntity;

    public AttackEvent(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Entity getTargetEntity() {
        return this.targetEntity;
    }
}
