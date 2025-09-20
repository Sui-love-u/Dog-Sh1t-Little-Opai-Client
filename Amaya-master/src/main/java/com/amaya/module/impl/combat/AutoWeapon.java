package com.amaya.module.impl.combat;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.events.impl.player.AttackEvent;
import com.amaya.events.impl.player.UpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.player.ItemUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

import java.util.Objects;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "AutoWeapon",category = Category.Combat)
public class AutoWeapon extends Module {
    private final BooleanSetting silentValue = new BooleanSetting("SpoofItem",false);
    private final NumberSetting ticksValue = new NumberSetting("SpoofTicks",10, 1, 20,1);
    private final BooleanSetting itemTool = new BooleanSetting("ItemTool",true);
    private boolean attackEnemy = false;
    private int spoofedSlot = 0;

    @EventTarget
    public void onAttack(AttackEvent event){
        attackEnemy = true;
    }

    @EventTarget
    public void onPacket(PacketSendEvent event){
        if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK && attackEnemy) {
            attackEnemy = false;

            // Find best weapon in hotbar (Kotlin Style)
            int slot = -1;
            double maxDamage = 0;

            for (int i = 0; i < 9; i++) {
                if (mc.thePlayer.inventory.getStackInSlot(i) != null
                        && (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSword
                        || (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemTool && itemTool.getValue()))) {
                    double damage = (mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null) != null
                            ? Objects.requireNonNull(mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null)).getAmount() : 0) +
                            1.25 * ItemUtils.getEnchantment(mc.thePlayer.inventory.getStackInSlot(i), Enchantment.sharpness);

                    if (damage > maxDamage) {
                        maxDamage = damage;
                        slot = i;
                    }
                }
            }

            if (slot == mc.thePlayer.inventory.currentItem || slot == -1) // If in hand no need to swap
                return;

            // Switch to best weapon
            if (silentValue.getValue()) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slot));
                spoofedSlot = ticksValue.getValue().intValue();
            } else {
                mc.thePlayer.inventory.currentItem = slot;
                mc.playerController.updateController();
            }

            // Resend attack packet
            mc.getNetHandler().addToSendQueue(event.getPacket());
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event){
        // Switch back to old item after some time
        if (spoofedSlot > 0) {
            if (spoofedSlot == 1)
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            spoofedSlot--;
        }
    }
}
