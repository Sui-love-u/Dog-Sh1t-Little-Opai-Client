package qwq.arcane.module.impl.movement;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import qwq.arcane.event.annotations.EventPriority;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.JumpEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.combat.KillAura;
import qwq.arcane.utils.player.MovementUtil;
import qwq.arcane.utils.player.PlayerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/movement/TargetStrafe.class */
public class TargetStrafe extends Module {
    private final NumberValue range;
    private final BoolValue jump;
    private final BoolValue behind;
    public float yaw;
    private boolean left;
    private boolean colliding;
    public boolean active;
    public EntityLivingBase target;

    public TargetStrafe() {
        super("TargetStrafe", Category.Movement);
        this.range = new NumberValue("Range", 1.0d, 0.1d, 6.0d, 0.1d);
        this.jump = new BoolValue("Jump", true);
        this.behind = new BoolValue("behind", false);
    }

    @EventPriority(3)
    @EventTarget
    public void onJump(JumpEvent eventJump) {
        if (this.active && this.target != null) {
            eventJump.setYaw(this.yaw);
        }
    }

    @EventPriority(3)
    @EventTarget
    public void onUpdate(UpdateEvent eventUpdate) {
        float yaw;
        if ((this.jump.getValue().booleanValue() && mc.gameSettings.keyBindJump.isPressed()) || mc.gameSettings.keyBindForward.isKeyDown()) {
            this.active = false;
            this.target = null;
            return;
        }
        this.target = KillAura.target;
        if (this.target == null) {
            this.active = false;
            return;
        }
        if (mc.thePlayer.isCollidedVertically || PlayerUtil.isBlockUnder(5.0d, false)) {
            if (!this.colliding) {
                MovementUtil.strafe(0.45d);
                this.left = !this.left;
            }
            this.colliding = true;
        }
        this.colliding = false;
        this.active = true;
        if (this.behind.getValue().booleanValue()) {
            yaw = this.target.rotationYaw + 180.0f;
        } else {
            yaw = getYaw(mc.thePlayer, new Vec3(this.target.posX, this.target.posY, this.target.posZ)) + (135 * (this.left ? -1 : 1));
        }
        float f = yaw;
        double d = this.range.getValue().floatValue() + (Math.random() / 100.0d);
        double d2 = ((-MathHelper.sin((float) Math.toRadians(f))) * d) + this.target.posX;
        double d3 = (MathHelper.cos((float) Math.toRadians(f)) * d) + this.target.posZ;
        this.yaw = getYaw(mc.thePlayer, new Vec3(d2, this.target.posY, d3));
    }

    public static float getYaw(EntityPlayer entityPlayer, Vec3 vec3) {
        return entityPlayer.rotationYaw + MathHelper.wrapAngleTo180_float((((float) Math.toDegrees(Math.atan2(vec3.zCoord - entityPlayer.posZ, vec3.xCoord - entityPlayer.posX))) - 90.0f) - entityPlayer.rotationYaw);
    }
}
