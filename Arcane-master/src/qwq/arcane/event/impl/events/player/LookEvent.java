package qwq.arcane.event.impl.events.player;

import qwq.arcane.event.impl.Event;
import qwq.arcane.utils.math.Vector2f;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/player/LookEvent.class */
public class LookEvent implements Event {
    private Vector2f rotation;

    public void setRotation(Vector2f rotation) {
        this.rotation = rotation;
    }

    public LookEvent(Vector2f rotation) {
        this.rotation = rotation;
    }

    public Vector2f getRotation() {
        return this.rotation;
    }
}
