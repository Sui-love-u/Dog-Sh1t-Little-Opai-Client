package qwq.arcane.event.impl;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/CancellableEvent.class */
public abstract class CancellableEvent implements Event, Cancellable {
    private boolean cancelled;

    @Override // qwq.arcane.event.impl.Cancellable
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override // qwq.arcane.event.impl.Cancellable
    public void setCancelled() {
        this.cancelled = true;
    }

    @Override // qwq.arcane.event.impl.Cancellable
    public boolean isCancelled() {
        return this.cancelled;
    }
}
