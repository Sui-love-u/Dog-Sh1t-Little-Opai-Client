package qwq.arcane.event.impl.events.player;

import qwq.arcane.event.impl.CancellableEvent;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/player/MotionEvent.class */
public final class MotionEvent extends CancellableEvent {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public State state;

    /* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/player/MotionEvent$State.class */
    public enum State {
        PRE,
        POST
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setState(State state) {
        this.state = state;
    }

    public MotionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround, State state) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.state = state;
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

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public State getState() {
        return this.state;
    }

    public MotionEvent(State state) {
        this.state = state;
    }

    public boolean isPre() {
        return this.state.equals(State.PRE);
    }

    public boolean isPost() {
        return this.state.equals(State.POST);
    }
}
