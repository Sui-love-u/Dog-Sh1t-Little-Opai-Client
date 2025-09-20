package qwq.arcane.module.impl.visuals;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.render.RenderUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/Projectile.class */
public class Projectile extends Module {
    float yaw;
    float pitch;

    public Projectile() {
        super("Projectile", Category.Visuals);
    }

    @EventTarget
    public void onMotion(MotionEvent e) {
        if (e.isPost()) {
            return;
        }
        this.yaw = e.getYaw();
        this.pitch = e.getPitch();
    }

    @EventTarget
    public void onR3D(Render3DEvent e) {
        float gravity;
        float size;
        double motionY;
        double d;
        double d2;
        MovingObjectPosition possibleEntityLanding;
        boolean isBow = false;
        float pitchDifference = 0.0f;
        float motionFactor = 1.5f;
        float motionSlowdown = 0.99f;
        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            Item heldItem = mc.thePlayer.getCurrentEquippedItem().getItem();
            if (heldItem instanceof ItemBow) {
                isBow = true;
                gravity = 0.05f;
                size = 0.3f;
                float power = mc.thePlayer.getItemInUseDuration() / 20.0f;
                float f = ((power * power) + (power * 2.0f)) / 3.0f;
                float power2 = f;
                if (f < 0.1d) {
                    return;
                }
                if (power2 > 1.0f) {
                    power2 = 1.0f;
                }
                motionFactor = power2 * 3.0f;
            } else if (heldItem instanceof ItemFishingRod) {
                gravity = 0.04f;
                size = 0.25f;
                motionSlowdown = 0.92f;
            } else if (ItemPotion.isSplash(mc.thePlayer.getCurrentEquippedItem().getMetadata())) {
                gravity = 0.05f;
                size = 0.25f;
                pitchDifference = -20.0f;
                motionFactor = 0.5f;
            } else {
                if (!(heldItem instanceof ItemSnowball) && !(heldItem instanceof ItemEnderPearl) && !(heldItem instanceof ItemEgg) && !heldItem.equals(Item.getItemById(46))) {
                    return;
                }
                gravity = 0.03f;
                size = 0.25f;
            }
            double posX = RenderManager.renderPosX - (MathHelper.cos((this.yaw / 180.0f) * 3.1415927f) * 0.16f);
            double posY = (RenderManager.renderPosY + mc.thePlayer.getEyeHeight()) - 0.10000000149011612d;
            double posZ = RenderManager.renderPosZ - (MathHelper.sin((this.yaw / 180.0f) * 3.1415927f) * 0.16f);
            double motionX = (-MathHelper.sin((this.yaw / 180.0f) * 3.1415927f)) * MathHelper.cos((this.pitch / 180.0f) * 3.1415927f) * (isBow ? 1.0d : 0.4d);
            double motionY2 = (-MathHelper.sin(((this.pitch + pitchDifference) / 180.0f) * 3.1415927f)) * (isBow ? 1.0d : 0.4d);
            double motionZ = MathHelper.cos((this.yaw / 180.0f) * 3.1415927f) * MathHelper.cos((this.pitch / 180.0f) * 3.1415927f) * (isBow ? 1.0d : 0.4d);
            float distance = MathHelper.sqrt_double((motionX * motionX) + (motionY2 * motionY2) + (motionZ * motionZ));
            double motionX2 = (motionX / distance) * motionFactor;
            double motionY3 = (motionY2 / distance) * motionFactor;
            double motionZ2 = (motionZ / distance) * motionFactor;
            MovingObjectPosition landingPosition = null;
            boolean hasLanded = false;
            boolean hitEntity = false;
            RenderUtil.enableRender3D(true);
            RenderUtil.color(ColorUtil.getRainbow().getRGB());
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            while (!hasLanded && posY > 0.0d) {
                Vec3 posBefore = new Vec3(posX, posY, posZ);
                Vec3 posAfter = new Vec3(posX + motionX2, posY + motionY3, posZ + motionZ2);
                landingPosition = mc.theWorld.rayTraceBlocks(posBefore, posAfter, false, true, false);
                Vec3 posBefore2 = new Vec3(posX, posY, posZ);
                Vec3 posAfter2 = new Vec3(posX + motionX2, posY + motionY3, posZ + motionZ2);
                if (landingPosition != null) {
                    hasLanded = true;
                    posAfter2 = new Vec3(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
                }
                AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
                List<?> entityList = getEntitiesWithinAABB(arrowBox.addCoord(motionX2, motionY3, motionZ2).expand(1.0d, 1.0d, 1.0d));
                for (Object o : entityList) {
                    Entity var18 = (Entity) o;
                    if (var18.canBeCollidedWith() && var18 != mc.thePlayer && (possibleEntityLanding = var18.getEntityBoundingBox().expand(size, size, size).calculateIntercept(posBefore2, posAfter2)) != null) {
                        hitEntity = true;
                        hasLanded = true;
                        landingPosition = possibleEntityLanding;
                    }
                }
                double d3 = posX + motionX2;
                posX = d3;
                double d4 = posY + motionY3;
                posY = d3;
                double d5 = posZ + motionZ2;
                posZ = d3;
                BlockPos var20 = new BlockPos(d3, d4, d5);
                Block var21 = mc.theWorld.getBlockState(var20).getBlock();
                if (var21.getMaterial() == Material.water) {
                    motionX2 *= 0.6d;
                    motionY = motionY3 * 0.6d;
                    d = motionZ2;
                    d2 = 0.6d;
                } else {
                    motionX2 *= motionSlowdown;
                    motionY = motionY3 * motionSlowdown;
                    d = motionZ2;
                    d2 = motionSlowdown;
                }
                motionZ2 = d * d2;
                motionY3 = motionY - gravity;
                GL11.glVertex3d(posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ);
            }
            GL11.glEnd();
            GL11.glPushMatrix();
            GL11.glTranslated(posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ);
            if (landingPosition != null) {
                int side = landingPosition.sideHit.getIndex();
                if (side == 1 && (heldItem instanceof ItemEnderPearl)) {
                    RenderUtil.color(ColorUtil.getRainbow().getRGB());
                } else if (side == 2 || side == 3) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                } else if (side == 4 || side == 5) {
                    GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                }
                if (hitEntity) {
                    RenderUtil.color(ColorUtil.getRainbow().getRGB());
                }
            }
            renderPoint();
            GL11.glPopMatrix();
            RenderUtil.disableRender3D(true);
        }
    }

    private void renderPoint() {
        GL11.glBegin(1);
        GL11.glVertex3d(-0.5d, 0.0d, 0.0d);
        GL11.glVertex3d(0.0d, 0.0d, 0.0d);
        GL11.glVertex3d(0.0d, 0.0d, -0.5d);
        GL11.glVertex3d(0.0d, 0.0d, 0.0d);
        GL11.glVertex3d(0.5d, 0.0d, 0.0d);
        GL11.glVertex3d(0.0d, 0.0d, 0.0d);
        GL11.glVertex3d(0.0d, 0.0d, 0.5d);
        GL11.glVertex3d(0.0d, 0.0d, 0.0d);
        GL11.glEnd();
        Cylinder c = new Cylinder();
        GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.5f, 0.5f, 0.0f, 256, 27);
    }

    private List<?> getEntitiesWithinAABB(AxisAlignedBB axisalignedBB) {
        ArrayList<Entity> list = new ArrayList<>();
        int chunkMinX = MathHelper.floor_double((axisalignedBB.minX - 2.0d) / 16.0d);
        int chunkMaxX = MathHelper.floor_double((axisalignedBB.maxX + 2.0d) / 16.0d);
        int chunkMinZ = MathHelper.floor_double((axisalignedBB.minZ - 2.0d) / 16.0d);
        int chunkMaxZ = MathHelper.floor_double((axisalignedBB.maxZ + 2.0d) / 16.0d);
        for (int x = chunkMinX; x <= chunkMaxX; x++) {
            for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                if (mc.theWorld.getChunkProvider().chunkExists(x, z)) {
                    mc.theWorld.getChunkFromChunkCoords(x, z).getEntitiesWithinAABBForEntity(mc.thePlayer, axisalignedBB, list, EntitySelectors.selectAnything);
                }
            }
        }
        return list;
    }
}
