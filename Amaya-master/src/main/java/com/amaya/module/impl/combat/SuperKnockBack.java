package com.amaya.module.impl.combat;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.AttackEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.utils.pack.PacketUtil;
import net.minecraft.network.play.client.C0BPacketEntityAction;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "SuperKnockBack",category = Category.Combat)
public class SuperKnockBack extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", "LegitFast",new String[]{"LegitFast", "Balance", "PacketKB"});

    @EventTarget
    public void onAttack(AttackEvent event) {
        switch (mode.getValue()) {
            case "LegitFast": {
                if (mc.thePlayer.isSprinting()) {
                    mc.thePlayer.setSprinting(false);
                    mc.thePlayer.setSprinting(true);
                }
                break;
            }
            case "Balance":
                if (mc.thePlayer.isSprinting()) {
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.thePlayer.setSprinting(false);
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.setSprinting(true);
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.thePlayer.setSprinting(false);
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.setSprinting(true);
                }
                break;
            case "PacketKB": {
                PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            break;
        }
    }
}
