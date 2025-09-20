package qwq.arcane.utils.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import qwq.arcane.event.impl.events.player.StrafeEvent;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.math.Vector2f;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/Rotation.class */
public final class Rotation {
    public float yaw;
    public float pitch;
    public double distanceSq;

    public void toPlayer(EntityPlayer player) {
        float var2 = this.yaw;
        if (!Float.isNaN(var2)) {
            float var22 = this.pitch;
            if (!Float.isNaN(var22)) {
                fixedSensitivity(Mine.getMinecraft().gameSettings.mouseSensitivity);
                player.rotationYaw = this.yaw;
                player.rotationPitch = this.pitch;
            }
        }
    }

    public void fixedSensitivity(float sensitivity) {
        float f = (sensitivity * 0.6f) + 0.2f;
        float gcd = f * f * f * 1.2f;
        this.yaw -= this.yaw % gcd;
        this.pitch -= this.pitch % gcd;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float var1) {
        this.yaw = var1;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float var1) {
        this.pitch = var1;
    }

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vector2f toVec2f() {
        return new Vector2f(this.yaw, this.pitch);
    }

    public Rotation copy(float yaw, float pitch) {
        return new Rotation(yaw, pitch);
    }

    public void applyStrafeToPlayer(StrafeEvent event) {
        EntityPlayerSP player = Mine.getMinecraft().thePlayer;
        int dif = (int) ((MathHelper.wrapAngleTo180_float(((player.rotationYaw - this.yaw) - 23.5f) - 135.0f) + 180.0f) / 45.0f);
        float yaw = this.yaw;
        float strafe = event.getStrafe();
        float forward = event.getForward();
        float friction = event.getFriction();
        float calcForward = 0.0f;
        float calcStrafe = 0.0f;
        switch (dif) {
            case 0:
                calcForward = forward;
                calcStrafe = strafe;
                break;
            case 1:
                float calcForward2 = 0.0f + forward;
                float calcStrafe2 = 0.0f - forward;
                calcForward = calcForward2 + strafe;
                calcStrafe = calcStrafe2 + strafe;
                break;
            case 2:
                calcForward = strafe;
                calcStrafe = -forward;
                break;
            case 3:
                float calcForward3 = 0.0f - forward;
                float calcStrafe3 = 0.0f - forward;
                calcForward = calcForward3 + strafe;
                calcStrafe = calcStrafe3 - strafe;
                break;
            case 4:
                calcForward = -forward;
                calcStrafe = -strafe;
                break;
            case 5:
                float calcForward4 = 0.0f - forward;
                float calcStrafe4 = 0.0f + forward;
                calcForward = calcForward4 - strafe;
                calcStrafe = calcStrafe4 - strafe;
                break;
            case 6:
                calcForward = -strafe;
                calcStrafe = forward;
                break;
            case 7:
                float calcForward5 = 0.0f + forward;
                float calcStrafe5 = 0.0f + forward;
                calcForward = calcForward5 - strafe;
                calcStrafe = calcStrafe5 + strafe;
                break;
        }
        if (calcForward > 1.0f || ((calcForward < 0.9f && calcForward > 0.3f) || calcForward < -1.0f || (calcForward > -0.9f && calcForward < -0.3f))) {
            calcForward *= 0.5f;
        }
        if (calcStrafe > 1.0f || ((calcStrafe < 0.9f && calcStrafe > 0.3f) || calcStrafe < -1.0f || (calcStrafe > -0.9f && calcStrafe < -0.3f))) {
            calcStrafe *= 0.5f;
        }
        float d = (calcStrafe * calcStrafe) + (calcForward * calcForward);
        if (d >= 1.0E-4f) {
            float d2 = MathHelper.sqrt_float(d);
            if (d2 < 1.0f) {
                d2 = 1.0f;
            }
            float d3 = friction / d2;
            float calcStrafe6 = calcStrafe * d3;
            float calcForward6 = calcForward * d3;
            float yawSin = MathHelper.sin((float) ((yaw * 3.141592653589793d) / 180.0d));
            float yawCos = MathHelper.cos((float) ((yaw * 3.141592653589793d) / 180.0d));
            player.motionX += (calcStrafe6 * yawCos) - (calcForward6 * yawSin);
            player.motionZ += (calcForward6 * yawCos) + (calcStrafe6 * yawSin);
        }
    }

    public String toString() {
        return "Rotation(yaw=" + this.yaw + ", pitch=" + this.pitch + ")";
    }
}
