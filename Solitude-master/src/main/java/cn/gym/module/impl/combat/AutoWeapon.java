package cn.gym.module.impl.combat;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.packet.PacketSendEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.utils.player.InventoryUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;

import static cn.gym.utils.pack.PacketUtil.sendPacketNoEvent;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 15:54
 */
public class AutoWeapon extends Module {
    public AutoWeapon() {
        super("AutoWeapon", Category.Combat);
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity packet && packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
            int slot = -1;
            double maxDamage = -1.0;

            for (int i = 0; i <= 8; i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemSword) {
                    double damage = stack.getAttributeModifiers().get("generic.attackDamage").stream().findFirst().map(AttributeModifier::getAmount).orElse(0.0)
                            + 1.25 * InventoryUtil.getEnchantment(stack, Enchantment.sharpness);
                    if (damage > maxDamage) {
                        maxDamage = damage;
                        slot = i;
                    }
                }
            }

            if (slot == -1 || slot == mc.thePlayer.inventory.currentItem)
                return;

            mc.thePlayer.inventory.currentItem = slot;
            mc.playerController.updateController();
            Entity entity = packet.getEntityFromWorld(mc.theWorld);
            event.setCancelled(true);
            sendPacketNoEvent(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }
    }
}
