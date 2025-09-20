package qwq.arcane.module.impl.combat;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.player.InventoryUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/combat/AutoWeapon.class */
public class AutoWeapon extends Module {
    public AutoWeapon() {
        super("AutoWeapon", Category.Combat);
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet2 = (C02PacketUseEntity) packet;
            if (packet2.getAction() == C02PacketUseEntity.Action.ATTACK) {
                int slot = -1;
                double maxDamage = -1.0d;
                for (int i = 0; i <= 8; i++) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                    if (stack != null && (stack.getItem() instanceof ItemSword)) {
                        double damage = ((Double) stack.getAttributeModifiers().get("generic.attackDamage").stream().findFirst().map((v0) -> {
                            return v0.getAmount();
                        }).orElse(Double.valueOf(0.0d))).doubleValue() + (1.25d * InventoryUtil.getEnchantment(stack, Enchantment.sharpness));
                        if (damage > maxDamage) {
                            maxDamage = damage;
                            slot = i;
                        }
                    }
                }
                if (slot == -1 || slot == mc.thePlayer.inventory.currentItem) {
                    return;
                }
                mc.thePlayer.inventory.currentItem = slot;
                mc.playerController.updateController();
                Entity entity = packet2.getEntityFromWorld(mc.theWorld);
                event.setCancelled(true);
                PacketUtil.sendPacketNoEvent(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            }
        }
    }
}
