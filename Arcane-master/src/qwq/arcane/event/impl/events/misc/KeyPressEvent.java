package qwq.arcane.event.impl.events.misc;

import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/misc/KeyPressEvent.class */
public class KeyPressEvent extends CancellableEvent {
    private final int key;

    public int getKey() {
        return this.key;
    }

    public KeyPressEvent(int key) {
        this.key = key;
    }
}
