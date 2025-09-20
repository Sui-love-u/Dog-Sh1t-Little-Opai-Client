package qwq.arcane.event.impl.events.player;

import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/player/JumpEvent.class */
public class JumpEvent extends CancellableEvent {
    private float yaw;

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public JumpEvent(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return this.yaw;
    }
}
