package com.amaya.module.impl.movement;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.JumpEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.combat.KillAura;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.player.Rise.MoveUtil;
import com.amaya.utils.player.Rise.PlayerUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "TargetStrafe", category = Category.Player)
public class TargetStrafe
        extends Module {
    private final NumberSetting range = new NumberSetting("Range", 1.0, 0.1, 6.0, 0.1);
    private final BooleanSetting jump = new BooleanSetting("Jump", true);
    private final BooleanSetting behind = new BooleanSetting("behind", false);
    public float yaw;
    private boolean left;
    private boolean colliding;
    public boolean active;
    public EntityLivingBase target;

    @EventTarget
    public void onJump(JumpEvent eventJump) {
        if (this.active && this.target != null) {
            eventJump.setYaw(this.yaw);
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent eventUpdate) {
        if (((Boolean)this.jump.getValue()).booleanValue() && TargetStrafe.mc.gameSettings.keyBindJump.isPressed() || TargetStrafe.mc.gameSettings.keyBindForward.isKeyDown()) {
            this.active = false;
            this.target = null;
            return;
        }
        this.target = KillAura.target;
        if (this.target == null) {
            this.active = false;
            return;
        }
        if (TargetStrafe.mc.thePlayer.isCollidedVertically || PlayerUtil.isBlockUnder(5.0, false)) {
            if (!this.colliding) {
                MoveUtil.strafe(0.45);
                this.left = !this.left;
            }
            this.colliding = true;
        }
        this.colliding = false;
        this.active = true;
        float f = (Boolean)this.behind.getValue() != false ? this.target.rotationYaw + 180.0f : TargetStrafe.getYaw(TargetStrafe.mc.thePlayer, new Vec3(this.target.posX, this.target.posY, this.target.posZ)) + (float)(135 * (this.left ? -1 : 1));
        double d = (double)(this.range.getValue()).floatValue() + Math.random() / 100.0;
        double d2 = (double)(-MathHelper.sin((float)Math.toRadians(f))) * d + this.target.posX;
        double d3 = (double)MathHelper.cos((float)Math.toRadians(f)) * d + this.target.posZ;
        this.yaw = f = TargetStrafe.getYaw(TargetStrafe.mc.thePlayer, new Vec3(d2, this.target.posY, d3));
    }

    public static float getYaw(EntityPlayer entityPlayer, Vec3 vec3) {
        return entityPlayer.rotationYaw + MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(vec3.zCoord - entityPlayer.posZ, vec3.xCoord - entityPlayer.posX)) - 90.0f - entityPlayer.rotationYaw);
    }
}
