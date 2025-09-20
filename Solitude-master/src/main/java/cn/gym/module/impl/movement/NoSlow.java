package cn.gym.module.impl.movement;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.packet.PacketSendEvent;
import cn.gym.events.impl.player.MotionEvent;
import cn.gym.events.impl.player.PlaceEvent;
import cn.gym.events.impl.player.SlowDownEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.combat.KillAura;
import cn.gym.utils.pack.PacketUtil;
import cn.gym.utils.player.Rise.PlayerUtil;
import cn.gym.value.impl.BooleanValue;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 14:18
 */
public class NoSlow extends Module {
    private int offGroundTicks;
    private boolean stop;
    private boolean disable;
    private Packet<?> interactItemPacket;
    private KillAura killAuraModule = null;
    public final BooleanValue slab = new BooleanValue("Slow down on Slabs", true);
    public NoSlow() {
        super("NoSlow", Category.Movement);
    }
    @EventTarget
    public void onPre(MotionEvent preMotionEvent) {
        double d2;
        if (PlayerUtil.blockRelativeToPlayer(0.0, NoSlow.mc.thePlayer.motionY, 0.0) != Blocks.air && !NoSlow.mc.thePlayer.isUsingItem() && ((Boolean)this.slab.getValue()).booleanValue()) {
            this.disable = false;
        }
        if (Math.abs((d2 = preMotionEvent.getY()) - (double)Math.round(d2)) > 0.03 && NoSlow.mc.thePlayer.onGround) {
            this.disable = true;
        }
        if (NoSlow.mc.thePlayer.isUsingItem() && !(NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
            this.offGroundTicks = NoSlow.mc.thePlayer.onGround ? 0 : ++this.offGroundTicks;
            if (this.offGroundTicks >= 2) {
                this.stop = false;
                this.interactItemPacket = null;
            } else if (NoSlow.mc.thePlayer.onGround && !this.disable) {
                preMotionEvent.setY(preMotionEvent.getY() + 0.001);
            }
        }
        if (this.disable && !NoSlow.mc.thePlayer.onGround && NoSlow.mc.thePlayer.isUsingItem() && !(NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
            NoSlow.mc.thePlayer.motionX *= 0.1;
            NoSlow.mc.thePlayer.motionZ *= 0.1;
        }
    };
    @EventTarget
    public void OnRight(PlaceEvent rightClickEvent) {
        if (NoSlow.mc.thePlayer.getHeldItem() == null) {
            return;
        }
        if (NoSlow.mc.thePlayer.isUsingItem() || NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && !ItemPotion.isSplash(NoSlow.mc.thePlayer.getHeldItem().getMetadata()) || NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) {
            if (NoSlow.mc.thePlayer.offGroundTicks < 2 && NoSlow.mc.thePlayer.offGroundTicks != 0 && !this.disable) {
                rightClickEvent.setCancelled(true);
            } else if (NoSlow.mc.thePlayer.onGround) {
                NoSlow.mc.thePlayer.jump();
                rightClickEvent.setCancelled(true);
            }
        }
    };
    @EventTarget
    private void Onpacket(PacketSendEvent packetSendEvent) {
        if (this.killAuraModule == null) {
            this.killAuraModule = this.getModule(KillAura.class);
        }
    };
    @EventTarget
    public void OnSlow(SlowDownEvent slowDownEvent) {
        if (!this.disable || NoSlow.mc.thePlayer.onGround) {
            if (NoSlow.mc.thePlayer.isUsingItem() && NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) {
                slowDownEvent.setCancelled(true);
            }
            if (NoSlow.mc.thePlayer.isUsingItem() && NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && !ItemPotion.isSplash(NoSlow.mc.thePlayer.getHeldItem().getMetadata())) {
                slowDownEvent.setCancelled(true);
            }
            if (NoSlow.mc.thePlayer.isUsingItem() && NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) {
                slowDownEvent.setCancelled(true);
            }
        }
        if (NoSlow.mc.thePlayer.isUsingItem() && NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
            PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            slowDownEvent.setCancelled(true);
        }
    };
}
