package qwq.arcane.event.impl.events.player;

import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/player/MoveEvent.class */
public class MoveEvent extends CancellableEvent {
    public double x;
    public double y;
    public double z;

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
