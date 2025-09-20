package cn.gym.module.impl.render;

import cn.gym.Solitude;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.WorldEvent;
import cn.gym.events.impl.render.Render2DEvent;
import cn.gym.events.impl.render.Render3DEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.utils.color.ColorUtil;
import cn.gym.utils.fontrender.FontManager;
import cn.gym.utils.math.MathUtils;
import cn.gym.utils.render.GLUtil;
import cn.gym.utils.render.RenderUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ColorValue;
import cn.gym.value.impl.NumberValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4ub;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glVertex3i;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 17:14
 */
public class ESP extends Module {
    public ESP() {
        super("ESP", Category.Render);
    }

    public final BooleanValue fontTags = new BooleanValue("Font Tags", true);
    public final BooleanValue fonttagsBackground = new BooleanValue("TagsBackground", fontTags::get, true);
    public final BooleanValue fonttagsHealth = new BooleanValue("TagsHealth", fontTags::get, true);
    public final BooleanValue esp2d = new BooleanValue("2D ESP", true);
    public final BooleanValue box = new BooleanValue("Box", esp2d::get, true);
    public final BooleanValue boxSyncColor = new BooleanValue("Box Sync Color", () -> esp2d.get() && box.get(), false);
    public final ColorValue boxColor = new ColorValue("Box Color", () -> esp2d.get() && box.get() && !boxSyncColor.get(), Color.RED);
    public final BooleanValue healthBar = new BooleanValue("Health Bar", esp2d::get, true);
    public final BooleanValue healthBarSyncColor = new BooleanValue("Health Bar Sync Color", () -> esp2d.get() && healthBar.get(),false);
    public final ColorValue absorptionColor = new ColorValue("Absorption Color", () -> esp2d.get() && healthBar.get() && !healthBarSyncColor.get(), new Color(255, 255, 50));
    public final BooleanValue armorBar = new BooleanValue("Armor Bar", esp2d::get,true);
    public final ColorValue armorBarColor = new ColorValue("Armor Bar Color", () -> esp2d.get() && armorBar.get(), new Color(50, 255, 255));
    public final BooleanValue skeletons = new BooleanValue("Skeletons", esp2d::get,true);
    public final NumberValue skeletonWidth = new NumberValue("Skeletons Width", () -> esp2d.get() && skeletons.get(), 0.5f, 0.5f, 5, 0.5f);
    public final ColorValue skeletonsColor = new ColorValue("Skeletons Color", () -> esp2d.get() && skeletons.get(), Color.WHITE);
    public final Map<EntityPlayer, float[][]> playerRotationMap = new HashMap<>();
    private final Map<EntityPlayer, float[]> entityPosMap = new HashMap<>();

    @Override
    public void onDisable() {
        entityPosMap.clear();
        playerRotationMap.clear();
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        entityPosMap.clear();
        playerRotationMap.clear();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        for (EntityPlayer player : entityPosMap.keySet()) {
            if ((player.getDistanceToEntity(mc.thePlayer) < 1.0F && mc.gameSettings.thirdPersonView == 0) ||
                    !RenderUtil.isInViewFrustum(player))
                continue;
            if (player == mc.thePlayer) continue;
            final float[] positions = entityPosMap.get(player);
            final float x = positions[0];
            final float y = positions[1];
            final float x2 = positions[2];
            final float y2 = positions[3];

            final float health = player.getHealth();
            final float maxHealth = player.getMaxHealth();
            final float healthPercentage = health / maxHealth;

            if (fontTags.get()) {
                final String healthString = fonttagsHealth.get() ? EnumChatFormatting.WHITE + "" + EnumChatFormatting.BOLD + " " + (MathUtils.roundToHalf(player.getHealth())) + EnumChatFormatting.RESET + "" : "";
                final String name = player.getDisplayName().getFormattedText() + healthString;
                float halfWidth = (float) FontManager.Semibold.get(15).getStringWidth(name) * 0.5f;
                final float xDif = x2 - x;
                final float middle = x + (xDif / 2);
                final float textHeight = FontManager.Semibold.get(15).getHeight() * 0.5f;
                float renderY = y - textHeight - 2;

                final float left = middle - halfWidth - 1;
                final float right = middle + halfWidth + 1;

                if (fonttagsBackground.get()) {
                    Gui.drawRect(left, renderY - 4, right, renderY + textHeight + 1, new Color(0, 0, 0, 50).getRGB());
                }

                FontManager.Semibold.get(15).drawStringWithShadow(name, middle - halfWidth, renderY - 1.5f, 0.5, -1);
            }

            if (esp2d.get()) {
                glDisable(GL_TEXTURE_2D);
                GLUtil.startBlend();

                if (armorBar.get()) {
                    final float armorPercentage = player.getTotalArmorValue() / 20.0F;
                    final float armorBarWidth = (x2 - x) * armorPercentage;

                    glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);
                    glBegin(GL_QUADS);

                    // Background
                    {
                        glVertex2f(x, y2 + 0.5F);
                        glVertex2f(x, y2 + 2.5F);

                        glVertex2f(x2, y2 + 2.5F);
                        glVertex2f(x2, y2 + 0.5F);
                    }

                    if (armorPercentage > 0) {
                        color(armorBarColor.get().getRGB());

                        // Bar
                        {
                            glVertex2f(x + 0.5F, y2 + 1);
                            glVertex2f(x + 0.5F, y2 + 2);

                            glVertex2f(x + armorBarWidth - 0.5F, y2 + 2);
                            glVertex2f(x + armorBarWidth - 0.5F, y2 + 1);
                        }
                        resetColor();
                    }

                    if (!healthBar.get())
                        glEnd();
                }

                if (healthBar.get()) {
                    float healthBarLeft = x - 2.5F;
                    float healthBarRight = x - 0.5F;

                    glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);

                    if (!armorBar.get())
                        glBegin(GL_QUADS);

                    // Background
                    {
                        glVertex2f(healthBarLeft, y);
                        glVertex2f(healthBarLeft, y2);

                        glVertex2f(healthBarRight, y2);
                        glVertex2f(healthBarRight, y);
                    }

                    healthBarLeft += 0.5F;
                    healthBarRight -= 0.5F;

                    final float heightDif = y - y2;
                    final float healthBarHeight = heightDif * healthPercentage;

                    final float topOfHealthBar = y2 + 0.5F + healthBarHeight;

                    if (healthBarSyncColor.get()) {
                        final int syncedcolor = Solitude.Instance.getModuleManager().getModule(Interface.class).color(0);

                        color(syncedcolor);
                    } else {
                        final int color = ColorUtil.getColorFromPercentage(healthPercentage);

                        color(color);
                    }
                    // Bar
                    {
                        glVertex2f(healthBarLeft, topOfHealthBar);
                        glVertex2f(healthBarLeft, y2 - 0.5F);

                        glVertex2f(healthBarRight, y2 - 0.5F);
                        glVertex2f(healthBarRight, topOfHealthBar);
                    }

                    resetColor();


                    final float absorption = player.getAbsorptionAmount();

                    final float absorptionPercentage = Math.min(1.0F, absorption / 20.0F);

                    final int absorptionColor = this.absorptionColor.get().getRGB();

                    final float absorptionHeight = heightDif * absorptionPercentage;

                    final float topOfAbsorptionBar = y2 + 0.5F + absorptionHeight;

                    if (healthBarSyncColor.get()) {
                        color(Solitude.Instance.getModuleManager().getModule(Interface.class).FirstColor.get().getRGB());
                    } else {
                        color(absorptionColor);
                    }

                    // Absorption Bar
                    {
                        glVertex2f(healthBarLeft, topOfAbsorptionBar);
                        glVertex2f(healthBarLeft, y2 - 0.5F);

                        glVertex2f(healthBarRight, y2 - 0.5F);
                        glVertex2f(healthBarRight, topOfAbsorptionBar);
                    }

                    resetColor();

                    if (!box.get())
                        glEnd();
                }

                if (box.get()) {
                    glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);
                    if (!healthBar.get())
                        glBegin(GL_QUADS);

                    // Background
                    {
                        // Left
                        glVertex2f(x, y);
                        glVertex2f(x, y2);
                        glVertex2f(x + 1.5F, y2);
                        glVertex2f(x + 1.5F, y);

                        // Right
                        glVertex2f(x2 - 1.5F, y);
                        glVertex2f(x2 - 1.5F, y2);
                        glVertex2f(x2, y2);
                        glVertex2f(x2, y);

                        // Top
                        glVertex2f(x + 1.5F, y);
                        glVertex2f(x + 1.5F, y + 1.5F);
                        glVertex2f(x2 - 1.5F, y + 1.5F);
                        glVertex2f(x2 - 1.5F, y);

                        // Bottom
                        glVertex2f(x + 1.5F, y2 - 1.5F);
                        glVertex2f(x + 1.5F, y2);
                        glVertex2f(x2 - 1.5F, y2);
                        glVertex2f(x2 - 1.5F, y2 - 1.5F);
                    }

                    if (boxSyncColor.get()) {
                        color(Solitude.Instance.getModuleManager().getModule(Interface.class).FirstColor.get().getRGB());
                    } else {
                        color(boxColor.get().getRGB());
                    }

                    // Box
                    {
                        // Left
                        glVertex2f(x + 0.5F, y + 0.5F);
                        glVertex2f(x + 0.5F, y2 - 0.5F);
                        glVertex2f(x + 1, y2 - 0.5F);
                        glVertex2f(x + 1, y + 0.5F);

                        // Right
                        glVertex2f(x2 - 1, y + 0.5F);
                        glVertex2f(x2 - 1, y2 - 0.5F);
                        glVertex2f(x2 - 0.5F, y2 - 0.5F);
                        glVertex2f(x2 - 0.5F, y + 0.5F);

                        // Top
                        glVertex2f(x + 0.5F, y + 0.5F);
                        glVertex2f(x + 0.5F, y + 1);
                        glVertex2f(x2 - 0.5F, y + 1);
                        glVertex2f(x2 - 0.5F, y + 0.5F);

                        // Bottom
                        glVertex2f(x + 0.5F, y2 - 1);
                        glVertex2f(x + 0.5F, y2 - 0.5F);
                        glVertex2f(x2 - 0.5F, y2 - 0.5F);
                        glVertex2f(x2 - 0.5F, y2 - 1);
                    }

                    resetColor();

                    glEnd();
                }

                glEnable(GL_TEXTURE_2D);
                GLUtil.endBlend();
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        final boolean skeletons = this.skeletons.get();
        final boolean project2D = esp2d.get();
        if (project2D && !entityPosMap.isEmpty())
            entityPosMap.clear();

        if (skeletons) {
            glLineWidth(skeletonWidth.get().floatValue());
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);
            color(skeletonsColor.get().getRGB());
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_TEXTURE_2D);
            glDepthMask(false);
            resetColor();
        }

        final float partialTicks = event.partialTicks();

        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (isHypixelSpecialEntity(player)) {
                continue;
            }
            if (player == mc.thePlayer) continue;
            if (project2D) {

                final double posX = (MathUtils.interpolate(player.prevPosX, player.posX, partialTicks) -
                        mc.getRenderManager().viewerPosX);
                final double posY = (MathUtils.interpolate(player.prevPosY, player.posY, partialTicks) -
                        mc.getRenderManager().viewerPosY);
                final double posZ = (MathUtils.interpolate(player.prevPosZ, player.posZ, partialTicks) -
                        mc.getRenderManager().viewerPosZ);

                final double halfWidth = player.width / 2.0D;
                final AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth,
                        posX + halfWidth, posY + player.height + (player.isSneaking() ? -0.2D : 0.1D), posZ + halfWidth).expand(0.1, 0.1, 0.1);

                final double[][] vectors = {{bb.minX, bb.minY, bb.minZ},
                        {bb.minX, bb.maxY, bb.minZ},
                        {bb.minX, bb.maxY, bb.maxZ},
                        {bb.minX, bb.minY, bb.maxZ},
                        {bb.maxX, bb.minY, bb.minZ},
                        {bb.maxX, bb.maxY, bb.minZ},
                        {bb.maxX, bb.maxY, bb.maxZ},
                        {bb.maxX, bb.minY, bb.maxZ}};

                float[] projection;
                final float[] position = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F};

                for (final double[] vec : vectors) {
                    projection = GLUtil.project2D((float) vec[0], (float) vec[1], (float) vec[2], event.scaledResolution().getScaleFactor());
                    if (projection != null && projection[2] >= 0.0F && projection[2] < 1.0F) {
                        final float pX = projection[0];
                        final float pY = projection[1];
                        position[0] = Math.min(position[0], pX);
                        position[1] = Math.min(position[1], pY);
                        position[2] = Math.max(position[2], pX);
                        position[3] = Math.max(position[3], pY);
                    }
                }

                entityPosMap.put(player, position);
            }

            if (skeletons) {
                drawAdvancedSkeleton(partialTicks, player);
            }
        }

        if (skeletons) {
            glDepthMask(true);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_LINE_SMOOTH);
            glEnable(GL_DEPTH_TEST);
        }
    }

    private void drawAdvancedSkeleton(float partialTicks, EntityPlayer player) {
        RenderManager renderManager = mc.getRenderManager();
        Render renderer = renderManager.getEntityRenderObject(player);
        if (renderer == null || !(renderer instanceof RenderPlayer)) {
            return;
        }
        RenderPlayer playerRenderer = (RenderPlayer)renderer;
        ModelPlayer model = playerRenderer.getMainModel();
        updatePlayerRotations(player, model);
        float x = (float) (MathUtils.interpolate(player.prevPosX, player.posX, partialTicks) -
                mc.getRenderManager().renderPosX);
        float y = (float) (MathUtils.interpolate(player.prevPosY, player.posY, partialTicks) -
                mc.getRenderManager().renderPosY);
        float z = (float) (MathUtils.interpolate(player.prevPosZ, player.posZ, partialTicks) -
                mc.getRenderManager().renderPosZ);

        boolean sneaking = player.isSneaking();
        float yOffset = sneaking ? 0.6F : 0.75F;

        glPushMatrix();
        glTranslated(x, y, z);
        float yaw = MathUtils.interpolateFloat(player.prevRenderYawOffset, player.renderYawOffset, partialTicks);
        glRotatef(-yaw, 0.0F, 1.0F, 0.0F);
        drawHead(player, model, partialTicks, yOffset);
        drawBody(yOffset);
        drawArms(player, model, partialTicks, yOffset);
        drawLegs(player, model, partialTicks, yOffset);
        glPopMatrix();
    }
    private boolean isHypixelSpecialEntity(EntityPlayer player) {
        if (mc.getCurrentServerData() == null || !mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
            return false;
        }
        if (player.getDisplayName().getUnformattedText().startsWith("[NPC] ") ||
                player.getDisplayName().getUnformattedText().startsWith("NPC ")) {
            return true;
        }
        if (player.getDisplayName().getUnformattedText().toLowerCase().contains("helper") ||
                player.getDisplayName().getUnformattedText().toLowerCase().contains("mod") ||
                player.getDisplayName().getUnformattedText().toLowerCase().contains("admin")) {
            return true;
        }
        if (player.getDisplayName().getUnformattedText().startsWith("BOT ") ||
                player.getDisplayName().getUnformattedText().endsWith(" BOT") ||
                player.getDisplayName().getUnformattedText().contains("Robot")) {
            return true;
        }
        if (player.getScore() == -9999 || player.getScore() == 0) {
            return true;
        }
        if (player.posX == player.prevPosX && player.posZ == player.prevPosZ &&
                player.rotationYaw == player.prevRotationYaw) {
            return true;
        }

        return false;
    }
    public boolean isValid(Entity entity) {
        if (entity instanceof EntityPlayer player) {

            if (!player.isEntityAlive()) {
                return false;
            }

            if (player == mc.thePlayer) {
                return false;
            }

            return RenderUtil.isBBInFrustum(entity.getEntityBoundingBox()) && mc.theWorld.playerEntities.contains(player);
        }

        return false;
    }
    public void addEntity(EntityPlayer e, ModelPlayer model) {
        playerRotationMap.put(e, new float[][]{
                {model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ},
                {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ},
                {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ},
                {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ},
                {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}
        });
    }
    public static void resetColor() {
        color(1, 1, 1, 1);
    }
    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(int color) {
        glColor4ub(
                (byte) (color >> 16 & 0xFF),
                (byte) (color >> 8 & 0xFF),
                (byte) (color & 0xFF),
                (byte) (color >> 24 & 0xFF));
    }
    private void drawHead(EntityPlayer player, ModelPlayer model, float partialTicks, float yOffset) {
        glPushMatrix();
        glTranslatef(0.0F, yOffset + 0.55F, 0.0F);
        float headYaw = MathUtils.interpolateFloat(player.prevRotationYawHead, player.rotationYawHead, partialTicks) -
                MathUtils.interpolateFloat(player.prevRenderYawOffset, player.renderYawOffset, partialTicks);
        float headPitch = MathUtils.interpolateFloat(player.prevRotationPitch, player.rotationPitch, partialTicks);

        glRotatef(headYaw, 0.0F, 1.0F, 0.0F);
        glRotatef(headPitch, 1.0F, 0.0F, 0.0F);
        glBegin(GL_LINE_STRIP);
        glVertex3f(0.0F, 0.0F, 0.0F);
        glVertex3f(0.0F, 0.3F, 0.0F);
        glEnd();

        glPopMatrix();
    }

    private void drawBody(float yOffset) {
        glPushMatrix();
        glBegin(GL_LINE_STRIP);
        glVertex3f(0.0F, yOffset, 0.0F);
        glVertex3f(0.0F, yOffset + 0.55F, 0.0F);
        glEnd();
        glBegin(GL_LINE_STRIP);
        glVertex3f(-0.375F, yOffset + 0.55F, 0.0F);
        glVertex3f(0.375F, yOffset + 0.55F, 0.0F);
        glEnd();
        glBegin(GL_LINE_STRIP);
        glVertex3f(-0.125F, yOffset, 0.0F);
        glVertex3f(0.125F, yOffset, 0.0F);
        glEnd();

        glPopMatrix();
    }

    private void drawArms(EntityPlayer player, ModelPlayer model, float partialTicks, float yOffset) {
        glPushMatrix();
        glTranslatef(-0.375F, yOffset + 0.55F, 0.0F);
        float[][] rotations = playerRotationMap.get(player);
        if (rotations != null && rotations.length > 1) {
            glRotatef(rotations[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            glRotatef(rotations[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            glRotatef(-rotations[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        glBegin(GL_LINE_STRIP);
        glVertex3f(0.0F, 0.0F, 0.0F);
        glVertex3f(0.0F, -0.5F, 0.0F);
        glEnd();
        glPopMatrix();
        glPushMatrix();
        glTranslatef(0.375F, yOffset + 0.55F, 0.0F);

        if (rotations != null && rotations.length > 2) {
            glRotatef(rotations[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            glRotatef(rotations[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            glRotatef(-rotations[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        glBegin(GL_LINE_STRIP);
        glVertex3f(0.0F, 0.0F, 0.0F);
        glVertex3f(0.0F, -0.5F, 0.0F);
        glEnd();
        glPopMatrix();
    }

    private void drawLegs(EntityPlayer player, ModelPlayer model, float partialTicks, float yOffset) {
        glPushMatrix();
        glTranslatef(-0.125F, yOffset, 0.0F);

        float[][] rotations = playerRotationMap.get(player);
        if (rotations != null && rotations.length > 3) {
            glRotatef(rotations[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            glRotatef(rotations[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            glRotatef(rotations[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        glBegin(GL_LINE_STRIP);
        glVertex3f(0.0F, 0.0F, 0.0F);
        glVertex3f(0.0F, -yOffset, 0.0F);
        glEnd();
        glPopMatrix();

        glPushMatrix();
        glTranslatef(0.125F, yOffset, 0.0F);

        if (rotations != null && rotations.length > 4) {
            glRotatef(rotations[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            glRotatef(rotations[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            glRotatef(rotations[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        glBegin(GL_LINE_STRIP);
        glVertex3f(0.0F, 0.0F, 0.0F);
        glVertex3f(0.0F, -yOffset, 0.0F);
        glEnd();
        glPopMatrix();
    }

    private void updatePlayerRotations(EntityPlayer player, ModelPlayer model) {
        if (model == null) return;
        playerRotationMap.put(player, new float[][]{
                {model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ},
                {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ},
                {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ},
                {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ},
                {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}
        });
    }
}