package qwq.arcane.event.impl.events.player;

import qwq.arcane.event.impl.Event;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/player/MoveInputEvent.class */
public class MoveInputEvent implements Event {
    private float forward;
    private float strafe;
    private boolean jumping;
    private boolean sneaking;

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public MoveInputEvent(float forward, float strafe, boolean jumping, boolean sneaking) {
        this.forward = forward;
        this.strafe = strafe;
        this.jumping = jumping;
        this.sneaking = sneaking;
    }

    public float getForward() {
        return this.forward;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public boolean isJumping() {
        return this.jumping;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }
}
