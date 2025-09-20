package qwq.arcane.event.impl.events.player;

import qwq.arcane.event.impl.CancellableEvent;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.player.MovementUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/event/impl/events/player/StrafeEvent.class */
public class StrafeEvent extends CancellableEvent implements Instance {
    private float forward;
    private float strafe;
    private float friction;
    private float yaw;

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public StrafeEvent(float forward, float strafe, float friction, float yaw) {
        this.forward = forward;
        this.strafe = strafe;
        this.friction = friction;
        this.yaw = yaw;
    }

    public float getForward() {
        return this.forward;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public float getFriction() {
        return this.friction;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setSpeed(double speed, double motionMultiplier) {
        setFriction((float) ((getForward() == 0.0f || getStrafe() == 0.0f) ? speed : speed * 0.9800000190734863d));
        mc.thePlayer.motionX *= motionMultiplier;
        mc.thePlayer.motionZ *= motionMultiplier;
    }

    public void setSpeed(double speed) {
        setFriction((float) ((getForward() == 0.0f || getStrafe() == 0.0f) ? speed : speed * 0.9800000190734863d));
        MovementUtil.stop();
    }
}
