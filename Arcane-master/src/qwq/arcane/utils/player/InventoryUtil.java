package qwq.arcane.utils.player;

import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.pack.PacketUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/InventoryUtil.class */
public class InventoryUtil implements Instance {
    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int ONLY_HOT_BAR_BEGIN = 36;
    public static final int END = 45;

    /* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/InventoryUtil$BlockAction.class */
    public enum BlockAction {
        PLACE,
        REPLACE,
        PLACE_ON
    }

    /* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/InventoryUtil$ClickType.class */
    public enum ClickType {
        CLICK,
        SHIFT_CLICK,
        SWAP_WITH_HOT_BAR_SLOT,
        PLACEHOLDER,
        DROP_ITEM
    }

    @FunctionalInterface
    /* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/InventoryUtil$SlotConsumer.class */
    public interface SlotConsumer {
        void accept(int i, ItemStack itemStack);
    }

    public static boolean isGoodItem(ItemStack stack) {
        Item item = stack.getItem();
        return (item instanceof ItemEnderPearl) || item == Items.arrow || item == Items.lava_bucket || item == Items.water_bucket;
    }

    public static boolean isRest(Item item) {
        return (item instanceof ItemFood) || (item instanceof ItemPotion);
    }

    public static boolean isInventoryFull() {
        for (int i = 9; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                return false;
            }
        }
        return true;
    }

    private InventoryUtil() {
    }

    public static int findBestBlockStack() {
        int bestSlot = -1;
        int blockCount = -1;
        for (int i = 44; i >= 9; i--) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && (stack.getItem() instanceof ItemBlock) && isGoodBlockStack(stack) && stack.stackSize > blockCount) {
                bestSlot = i;
                blockCount = stack.stackSize;
            }
        }
        return bestSlot;
    }

    public static boolean isGoodBlockStack(ItemStack stack) {
        return stack.stackSize >= 1 && isValidBlock(Block.getBlockFromItem(stack.getItem()), true);
    }

    public static boolean isValidBlock(Block block, boolean toPlace) {
        if ((block instanceof BlockContainer) || (block instanceof BlockTNT) || !block.isFullBlock() || !block.isFullCube()) {
            return false;
        }
        if (toPlace && (block instanceof BlockFalling)) {
            return false;
        }
        Material material = block.getMaterial();
        return !material.isLiquid() && material.isSolid();
    }

    public static boolean isBestSword(EntityPlayerSP player, ItemStack itemStack) {
        double damage = 0.0d;
        ItemStack bestStack = null;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && (stack.getItem() instanceof ItemSword)) {
                double newDamage = getItemDamage(stack);
                if (newDamage > damage) {
                    damage = newDamage;
                    bestStack = stack;
                }
            }
        }
        return bestStack == itemStack || getItemDamage(itemStack) > damage;
    }

    public static boolean isBestSword(ItemStack itemStack) {
        AtomicDouble damage = new AtomicDouble(0.0d);
        AtomicReference<ItemStack> bestStack = new AtomicReference<>(null);
        forEachInventorySlot(9, 45, (slot, stack) -> {
            if (stack.getItem() instanceof ItemSword) {
                double newDamage = getItemDamage(stack);
                if (newDamage > damage.get()) {
                    damage.set(newDamage);
                    bestStack.set(stack);
                }
            }
        });
        return bestStack.get() == itemStack || damage.get() < getItemDamage(itemStack);
    }

    public static void forEachInventorySlot(int begin, int end, SlotConsumer consumer) {
        for (int i = begin; i < end; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null) {
                consumer.accept(i, stack);
            }
        }
    }

    public static Item getHeldItem() {
        if (mc.thePlayer == null || mc.thePlayer.getCurrentEquippedItem() == null) {
            return null;
        }
        return mc.thePlayer.getCurrentEquippedItem().getItem();
    }

    public static int getEnchantment(ItemStack itemStack, Enchantment enchantment) {
        if (itemStack == null || itemStack.getEnchantmentTagList() == null || itemStack.getEnchantmentTagList().hasNoTags()) {
            return 0;
        }
        for (int i = 0; i < itemStack.getEnchantmentTagList().tagCount(); i++) {
            NBTTagCompound tagCompound = itemStack.getEnchantmentTagList().getCompoundTagAt(i);
            if ((tagCompound.hasKey("ench") && tagCompound.getShort("ench") == enchantment.effectId) || (tagCompound.hasKey("id") && tagCompound.getShort("id") == enchantment.effectId)) {
                return tagCompound.getShort("lvl");
            }
        }
        return 0;
    }

    public static int findItem(int startSlot, int endSlot, Item item) {
        for (int i = startSlot; i < endSlot; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static float getSwordStrength(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword) item;
            float sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f;
            float fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5f;
            return sword.getDamageVsEntity() + sharpness + fireAspect;
        }
        return 0.0f;
    }

    public static boolean hasFreeSlots(EntityPlayerSP player) {
        for (int i = 9; i < 45; i++) {
            if (!player.inventoryContainer.getSlot(i).getHasStack()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidStack(ItemStack stack) {
        if ((stack.getItem() instanceof ItemBlock) && isGoodBlockStack(stack)) {
            return true;
        }
        if ((stack.getItem() instanceof ItemPotion) && isBuffPotion(stack)) {
            return true;
        }
        if (((stack.getItem() instanceof ItemFood) && isGoodFood(stack)) || stack.getItem() == Items.arrow) {
            return true;
        }
        return isGoodItem(stack.getItem());
    }

    public static boolean isValidStack(EntityPlayerSP player, ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof ItemArmor) {
            return isBestArmor(player, stack);
        }
        if (item instanceof ItemTool) {
            return isBestTool(player, stack);
        }
        if (item instanceof ItemBow) {
            return isBestBow(player, stack);
        }
        if (item instanceof ItemFood) {
            return isGoodFood(stack);
        }
        if (item instanceof ItemBlock) {
            return isStackValidToPlace(stack);
        }
        if (item instanceof ItemPotion) {
            return isBuffPotion(stack);
        }
        return isGoodItem(item);
    }

    public static void swap(int slot, int switchSlot) {
        Mine.getMinecraft().playerController.windowClick(Mine.getMinecraft().thePlayer.inventoryContainer.windowId, slot, switchSlot, 2, Mine.getMinecraft().thePlayer);
    }

    public static void click(int slot) {
        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    public static void swapSilent(int slot, int switchSlot) {
        short short1 = mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory);
        PacketUtil.sendPacket(new C0EPacketClickWindow(mc.thePlayer.inventoryContainer.windowId, slot, switchSlot, 2, mc.thePlayer.inventory.getStackInSlot(slot), short1));
    }

    public static boolean isGoodItem(Item item) {
        return (item instanceof ItemEnderPearl) || item == Items.snowball || item == Items.egg || item == Items.arrow || item == Items.lava_bucket || item == Items.water_bucket;
    }

    public static boolean isBestArmor(EntityPlayerSP player, ItemStack itemStack) {
        ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
        double reduction = 0.0d;
        ItemStack bestStack = null;
        for (int i = 5; i < 45; i++) {
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && (stack.getItem() instanceof ItemArmor) && !stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.helmetChain") && !stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.leggingsChain")) {
                ItemArmor stackArmor = (ItemArmor) stack.getItem();
                if (stackArmor.armorType == itemArmor.armorType) {
                    double newReduction = getDamageReduction(stack);
                    if (newReduction > reduction) {
                        reduction = newReduction;
                        bestStack = stack;
                    }
                }
            }
        }
        return bestStack == itemStack || getDamageReduction(itemStack) > reduction;
    }

    public static int getToolType(ItemStack stack) {
        ItemTool tool = (ItemTool) stack.getItem();
        if (tool instanceof ItemPickaxe) {
            return 0;
        }
        if (tool instanceof ItemAxe) {
            return 1;
        }
        if (tool instanceof ItemSpade) {
            return 2;
        }
        return -1;
    }

    public static boolean isBestTool(EntityPlayerSP player, ItemStack itemStack) {
        int type = getToolType(itemStack);
        Tool bestTool = new Tool(-1, -1.0d, null);
        for (int i = 9; i < 45; i++) {
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && (stack.getItem() instanceof ItemTool) && type == getToolType(stack)) {
                double efficiency = getToolEfficiency(stack);
                if (efficiency > bestTool.getEfficiency()) {
                    bestTool = new Tool(i, efficiency, stack);
                }
            }
        }
        return bestTool.getStack() == itemStack || ((double) getToolEfficiency(itemStack)) > bestTool.getEfficiency();
    }

    public static boolean isBestBow(EntityPlayerSP player, ItemStack itemStack) {
        double bestBowDmg = -1.0d;
        ItemStack bestBow = null;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && (stack.getItem() instanceof ItemBow)) {
                double damage = getBowDamage(stack);
                if (damage > bestBowDmg) {
                    bestBow = stack;
                    bestBowDmg = damage;
                }
            }
        }
        return itemStack == bestBow || getBowDamage(itemStack) > bestBowDmg;
    }

    public static boolean isBestBow(ItemStack itemStack) {
        AtomicDouble bestBowDmg = new AtomicDouble(-1.0d);
        AtomicReference<ItemStack> bestBow = new AtomicReference<>(null);
        forEachInventorySlot(9, 45, (slot, stack) -> {
            if (stack.getItem() instanceof ItemBow) {
                double damage = getBowDamage(stack);
                if (damage > bestBowDmg.get()) {
                    bestBow.set(stack);
                    bestBowDmg.set(damage);
                }
            }
        });
        return itemStack == bestBow.get() || getBowDamage(itemStack) > bestBowDmg.get();
    }

    public static double getDamageReduction(ItemStack stack) {
        ItemArmor armor = (ItemArmor) stack.getItem();
        double reduction = 0.0d + armor.damageReduceAmount;
        if (stack.isItemEnchanted()) {
            reduction += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25d;
        }
        return reduction;
    }

    public static boolean isBuffPotion(ItemStack stack) {
        ItemPotion potion = (ItemPotion) stack.getItem();
        List<PotionEffect> effects = potion.getEffects(stack);
        for (PotionEffect effect : effects) {
            if (Potion.potionTypes[effect.getPotionID()].isBadEffect()) {
                return false;
            }
        }
        return true;
    }

    public static double getBowDamage(ItemStack stack) {
        double damage = 0.0d;
        if ((stack.getItem() instanceof ItemBow) && stack.isItemEnchanted()) {
            damage = 0.0d + EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        }
        return damage;
    }

    public static boolean isGoodFood(ItemStack stack) {
        ItemFood food = (ItemFood) stack.getItem();
        return (food instanceof ItemAppleGold) || (food.getHealAmount(stack) >= 4 && food.getSaturationModifier(stack) >= 0.3f);
    }

    public static float getToolEfficiency(ItemStack itemStack) {
        ItemTool tool = (ItemTool) itemStack.getItem();
        float efficiency = tool.getToolMaterial().getEfficiencyOnProperMaterial();
        int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
        if (efficiency > 1.0f && lvl > 0) {
            efficiency += (lvl * lvl) + 1;
        }
        return efficiency;
    }

    public static double getItemDamage(ItemStack stack) {
        double damage = 0.0d;
        Multimap<String, AttributeModifier> attributeModifierMap = stack.getAttributeModifiers();
        Iterator it = attributeModifierMap.keySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            String attributeName = (String) it.next();
            if (attributeName.equals("generic.attackDamage")) {
                Iterator<AttributeModifier> attributeModifiers = attributeModifierMap.get(attributeName).iterator();
                if (attributeModifiers.hasNext()) {
                    damage = 0.0d + attributeModifiers.next().getAmount();
                }
            }
        }
        if (stack.isItemEnchanted()) {
            damage = damage + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) + (EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25d);
        }
        return damage;
    }

    public static void windowClick(Mine mc, int slotId, int mouseButtonClicked, ClickType mode) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotId, mouseButtonClicked, mode.ordinal(), mc.thePlayer);
    }

    public static boolean isStackValidToPlace(ItemStack stack) {
        return stack.stackSize >= 1 && validateBlock(Block.getBlockFromItem(stack.getItem()), BlockAction.PLACE);
    }

    public static boolean validateBlock(Block block, BlockAction action) {
        if (block instanceof BlockContainer) {
            return false;
        }
        Material material = block.getMaterial();
        switch (action) {
            case PLACE:
                return !(block instanceof BlockFalling) && block.isFullBlock() && block.isFullCube();
            case REPLACE:
                return material.isReplaceable();
            case PLACE_ON:
                return block.isFullBlock() && block.isFullCube();
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    /* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/InventoryUtil$Tool.class */
    private static class Tool {
        private final int slot;
        private final double efficiency;
        private final ItemStack stack;

        public int getSlot() {
            return this.slot;
        }

        public double getEfficiency() {
            return this.efficiency;
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public Tool(int slot, double efficiency, ItemStack stack) {
            this.slot = slot;
            this.efficiency = efficiency;
            this.stack = stack;
        }
    }

    public static int getGappleSlot() {
        int item = -1;
        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemAppleGold)) {
            return mc.thePlayer.inventory.currentItem;
        }
        for (int i = 36; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null && (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemAppleGold)) {
                item = i - 36;
            }
        }
        return item;
    }
}
