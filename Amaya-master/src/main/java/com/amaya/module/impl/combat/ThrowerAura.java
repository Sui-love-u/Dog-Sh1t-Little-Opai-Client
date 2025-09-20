/*
 * Decompiled with CFR 0.152.
 */
package com.amaya.module.impl.combat;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.misc.AntiBot;
import com.amaya.module.impl.world.Scaffold;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.math.Vector2f;
import com.amaya.utils.player.Rise.MovementFix;
import com.amaya.component.RotationComponent;
import com.amaya.utils.player.Rise.RotationUtils;
import com.amaya.utils.time.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @Author: Guyuemang
 */
@ModuleInfo(name = "ThrowerAura",category = Category.Combat)
public class ThrowerAura
extends Module {
    private final NumberSetting fov = new NumberSetting("Fov", 150.0, 90.0, 180.0, 0.1);
    private final NumberSetting range = new NumberSetting("Range", 5.0, 3.0, 10.0, 0.1);
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil timer2 = new TimerUtil();
    public static final List<EntityPlayer> targets = new ArrayList<EntityPlayer>();
    public static EntityPlayer target;
    public static int tick;
    public static boolean isthrowout;

    @EventTarget
    public void onPre(MotionEvent event) {
        if (isGapple()) return;
        if (event.isPre()) {
            this.start();
        }
    }

    public void start() {
        if (isGapple()) return;
        if (Objects.requireNonNull(Amaya.Instance.moduleManager.getModule(Scaffold.class)).getState() || (Amaya.Instance.moduleManager.getModule(KillAura.class).getState() && KillAura.target != null)) {
            return;
        }
        if (AntiBot.isServerBot(target)) {
            return;
        }
        targets.sort(Comparator.comparingDouble(ThrowerAura.mc.thePlayer::getDistanceToEntity));
        if (!targets.isEmpty()) {
            target = targets.get(0);
        } else {
            target = null;
        }
        for (Entity entity : ThrowerAura.mc.theWorld.getLoadedEntityList()) {
            if (!(entity instanceof EntityFishHook) || !isthrowout || (((EntityFishHook)entity).caughtEntity == null || ((EntityFishHook)entity).caughtEntity != target) && !entity.onGround) continue;
            isthrowout = false;
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(ThrowerAura.mc.thePlayer.inventory.currentItem));
        }
        if (isthrowout || ThrowerAura.findBall() == -1 && (ThrowerAura.findfishRod() == -1 || KillAura.target != null)) {
            return;
        }
        for (Entity entity : ThrowerAura.mc.theWorld.getLoadedEntityList()) {
            if (!(entity instanceof EntityPlayer) || !((double)ThrowerAura.mc.thePlayer.getDistanceToEntity(entity) <= this.range.getValue()) || ThrowerAura.mc.thePlayer == entity) continue;
            targets.add((EntityPlayer)entity);
        }
        Vector2f rotation = RotationUtils.getThrowRotation(target, this.range.getValue());
        if (target != null && (double)ThrowerAura.mc.thePlayer.getDistanceToEntity(target) <= this.range.getValue() && RotationUtils.getRotationDifference(target) <= this.fov.getValue() && ThrowerAura.mc.thePlayer.canEntityBeSeen(target)) {
            RotationComponent.setRotations(rotation, 10.0, MovementFix.NORMAL);
            if (++tick > 3) {
                if (ThrowerAura.findBall() != -1) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(ThrowerAura.findBall() - 36));
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(ThrowerAura.mc.thePlayer.inventory.getStackInSlot(ThrowerAura.findBall() - 36)));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(ThrowerAura.mc.thePlayer.inventory.currentItem));
                } else if (ThrowerAura.findfishRod() != -1 && !isthrowout) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(ThrowerAura.findfishRod() - 36));
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(ThrowerAura.mc.thePlayer.inventory.getStackInSlot(ThrowerAura.findfishRod() - 36)));
                    isthrowout = true;
                    this.timer2.reset();
                }
                target = null;
                targets.clear();
                this.timer.reset();
                tick = 0;
            }
        } else {
            tick = 0;
        }
    }

    public static int findfishRod() {
        for (int i2 = 36; i2 < 45; ++i2) {
            ItemStack itemStack = ThrowerAura.mc.thePlayer.inventoryContainer.getSlot(i2).getStack();
            if (itemStack == null || !itemStack.getItem().equals(Items.fishing_rod)) continue;
            return i2;
        }
        return -1;
    }

    public static int findBall() {
        for (int i2 = 36; i2 < 45; ++i2) {
            ItemStack itemStack = ThrowerAura.mc.thePlayer.inventoryContainer.getSlot(i2).getStack();
            if (itemStack == null || !itemStack.getItem().equals(Items.snowball) && !itemStack.getItem().equals(Items.egg) || itemStack.stackSize <= 0) continue;
            return i2;
        }
        return -1;
    }


    static {
        tick = 0;
        isthrowout = false;
    }
}