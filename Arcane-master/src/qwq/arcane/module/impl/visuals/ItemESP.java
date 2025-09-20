package qwq.arcane.module.impl.visuals;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/ItemESP.class */
public class ItemESP extends Module {
    public ItemESP() {
        super("ItemESP", Category.Visuals);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        mc.getRenderManager();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) entity;
                String enhancement = "";
                if (EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, entityItem.getEntityItem()) != 0) {
                    enhancement = EnumChatFormatting.AQUA + " Protection:" + EnumChatFormatting.RED + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, entityItem.getEntityItem());
                }
                if (EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, entityItem.getEntityItem()) != 0) {
                    enhancement = EnumChatFormatting.AQUA + " Sharpness:" + EnumChatFormatting.RED + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, entityItem.getEntityItem());
                }
                if (entityItem.getEntityItem().getItem() == Items.golden_apple && entityItem.getEntityItem().getItem().hasEffect(entityItem.getEntityItem())) {
                    enhancement = EnumChatFormatting.RED + " Enchanted";
                }
                String var3 = entityItem.getEntityItem().stackSize > 1 ? EnumChatFormatting.RESET + " x" + entityItem.getEntityItem().stackSize : "";
                if (checkItem(entityItem.getEntityItem().getItem())) {
                    double interpolatedX = entityItem.lastTickPosX + ((entityItem.posX - entityItem.lastTickPosX) * event.partialTicks());
                    double interpolatedY = entityItem.lastTickPosY + ((entityItem.posY - entityItem.lastTickPosY) * event.partialTicks());
                    double interpolatedZ = entityItem.lastTickPosZ + ((entityItem.posZ - entityItem.lastTickPosZ) * event.partialTicks());
                    double diffX = (mc.thePlayer.lastTickPosX + ((mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.partialTicks())) - interpolatedX;
                    double diffY = (mc.thePlayer.lastTickPosY + ((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.partialTicks())) - interpolatedY;
                    double diffZ = (mc.thePlayer.lastTickPosZ + ((mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.partialTicks())) - interpolatedZ;
                    double dist = MathHelper.sqrt_double((diffX * diffX) + (diffY * diffY) + (diffZ * diffZ));
                    GlStateManager.pushMatrix();
                    drawText(entityItem.getEntityItem().getDisplayName() + var3 + enhancement, -1, interpolatedX, interpolatedY, interpolatedZ, dist);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    public boolean checkItem(Item item) {
        return (item instanceof ItemAppleGold) || (item instanceof ItemSword) || (item instanceof ItemBow) || (item instanceof ItemBucketMilk) || (item instanceof ItemPotion) || item == Items.diamond || item == Items.gold_ingot || item == Items.gold_nugget || item == Items.iron_ingot || (item instanceof ItemEnchantedBook) || item == Items.apple || item == Items.skull || item == Items.diamond_sword || item == Items.diamond_boots || item == Items.diamond_helmet || item == Items.diamond_leggings || item == Items.iron_leggings;
    }

    public void drawText(String value, int textColor, double posY, double posX, double posZ, double dist) {
        mc.getRenderManager();
        double posY2 = posY - RenderManager.viewerPosX;
        mc.getRenderManager();
        double posX2 = posX - RenderManager.viewerPosY;
        mc.getRenderManager();
        double posZ2 = posZ - RenderManager.viewerPosZ;
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posY2, ((float) posX2) + 0.3d, (float) posZ2);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((mc.gameSettings.thirdPersonView == 2 ? -1 : 1) * mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        float scale = Math.min(Math.max(0.02266667f, (float) (0.001500000013038516d * dist)), 0.07f);
        GlStateManager.scale(-scale, -scale, -scale);
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        mc.fontRendererObj.drawOutlinedString(value, (-(mc.fontRendererObj.getStringWidth(value) / 2.0f)) + (scale * 3.5f), -((123.805f * scale) - 2.47494f), 0.5f, textColor, Color.BLACK.getRGB());
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
}
