package qwq.arcane.utils.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import qwq.arcane.module.Mine;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/HYTUtils.class */
public class HYTUtils {
    private static final Mine mc = Mine.getMinecraft();

    public static boolean isInLobby() {
        if (mc.theWorld == null) {
            return false;
        }
        return mc.theWorld.playerEntities.stream().anyMatch(e -> {
            return e.getName().contains("问题反馈");
        });
    }

    public static boolean isHoldingGodAxe(EntityPlayer player) {
        ItemStack holdingItem = player.getEquipmentInSlot(0);
        return isGodAxe(holdingItem);
    }

    public static boolean isGodAxe(ItemStack stack) {
        NBTTagList enchantmentTagList;
        if (stack == null || stack.getItem() != Items.golden_axe) {
            return false;
        }
        int durability = stack.getMaxDamage() - stack.getItemDamage();
        if (durability > 2 || (enchantmentTagList = stack.getEnchantmentTagList()) == null) {
            return false;
        }
        for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
            NBTTagCompound nbt = (NBTTagCompound) enchantmentTagList.get(i);
            if (nbt.hasKey("id") && nbt.hasKey("lvl") && nbt.getInteger("id") == 16 && nbt.getInteger("lvl") >= 666) {
                return true;
            }
        }
        return false;
    }

    public static boolean isKBBall(ItemStack stack) {
        NBTTagList enchantmentTagList;
        if (stack == null || stack.getItem() != Items.slime_ball || (enchantmentTagList = stack.getEnchantmentTagList()) == null) {
            return false;
        }
        for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
            NBTTagCompound nbt = (NBTTagCompound) enchantmentTagList.get(i);
            if (nbt.hasKey("id") && nbt.hasKey("lvl") && nbt.getInteger("id") == 19 && nbt.getInteger("lvl") >= 2) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFireEnchantBall(ItemStack stack) {
        NBTTagList enchantmentTagList;
        if (stack == null || stack.getItem() != Items.magma_cream || (enchantmentTagList = stack.getEnchantmentTagList()) == null) {
            return false;
        }
        for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
            NBTTagCompound nbt = (NBTTagCompound) enchantmentTagList.get(i);
            if (nbt.hasKey("id") && nbt.hasKey("lvl") && nbt.getInteger("id") == 20 && nbt.getInteger("lvl") >= 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHoldingEnchantedGoldenApple(EntityPlayer player) {
        ItemStack holdingItem = player.getEquipmentInSlot(0);
        if (holdingItem == null || holdingItem.getItem() != Items.golden_apple) {
            return false;
        }
        return holdingItem.hasEffect();
    }

    public static int hasEatenGoldenApple(EntityPlayer player) {
        PotionEffect regenPotion = player.getActivePotionEffect(Potion.regeneration);
        if (regenPotion == null || regenPotion.getAmplifier() < 4) {
            return -1;
        }
        return regenPotion.getDuration();
    }

    public static int isRegen(EntityPlayer player) {
        PotionEffect regenPotion = player.getActivePotionEffect(Potion.regeneration);
        if (regenPotion == null) {
            return -1;
        }
        return regenPotion.getDuration();
    }

    public static int isStrength(EntityPlayer player) {
        PotionEffect strengthPotion = player.getActivePotionEffect(Potion.damageBoost);
        if (strengthPotion == null) {
            return -1;
        }
        return strengthPotion.getDuration();
    }
}
