package qwq.arcane.event.impl.events.player;

import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/player/PlaceEvent.class */
public class PlaceEvent extends CancellableEvent {
    private boolean shouldRightClick;
    private int slot;

    public void setShouldRightClick(boolean shouldRightClick) {
        this.shouldRightClick = shouldRightClick;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean isShouldRightClick() {
        return this.shouldRightClick;
    }

    public int getSlot() {
        return this.slot;
    }

    public PlaceEvent(int slot) {
        this.slot = slot;
    }
}
