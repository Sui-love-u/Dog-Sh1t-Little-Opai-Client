package qwq.arcane.module.impl.visuals;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.render.GLUtil;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/ESP.class */
public final class ESP extends Module {
    public static final BoolValue fontTags = new BoolValue("TagsName", true);
    public static final BoolValue fonttagsBackground;
    public static final BoolValue fonttagsHealth;
    public static final BoolValue esp2d;
    public static final BoolValue box;
    public static final BoolValue boxSyncColor;
    public static final ColorValue boxColor;
    public static final BoolValue healthBar;
    public static final BoolValue healthBarSyncColor;
    public static final ColorValue absorptionColor;
    public static final BoolValue armorBar;
    public static final ColorValue armorBarColor;
    public final Map<EntityPlayer, float[][]> playerRotationMap;
    private final Map<EntityPlayer, float[]> entityPosMap;

    public ESP() {
        super("ESP", Category.Visuals);
        this.playerRotationMap = new HashMap();
        this.entityPosMap = new HashMap();
    }

    static {
        BoolValue boolValue = fontTags;
        Objects.requireNonNull(boolValue);
        fonttagsBackground = new BoolValue("TagsBackground", boolValue::get, true);
        BoolValue boolValue2 = fontTags;
        Objects.requireNonNull(boolValue2);
        fonttagsHealth = new BoolValue("TagsHealth", boolValue2::get, true);
        esp2d = new BoolValue("2DESP", true);
        BoolValue boolValue3 = esp2d;
        Objects.requireNonNull(boolValue3);
        box = new BoolValue("Box", boolValue3::get, true);
        boxSyncColor = new BoolValue("BoxSyncColor", () -> {
            return esp2d.get().booleanValue() && box.get().booleanValue();
        }, false);
        boxColor = new ColorValue("BoxColor", () -> {
            return esp2d.get().booleanValue() && box.get().booleanValue() && !boxSyncColor.get().booleanValue();
        }, Color.RED);
        BoolValue boolValue4 = esp2d;
        Objects.requireNonNull(boolValue4);
        healthBar = new BoolValue("Health", boolValue4::get, true);
        healthBarSyncColor = new BoolValue("HealthColor", () -> {
            return esp2d.get().booleanValue() && healthBar.get().booleanValue();
        }, false);
        absorptionColor = new ColorValue("AbsorptionColor", () -> {
            return esp2d.get().booleanValue() && healthBar.get().booleanValue() && !healthBarSyncColor.get().booleanValue();
        }, new Color(255, 255, 50));
        BoolValue boolValue5 = esp2d;
        Objects.requireNonNull(boolValue5);
        armorBar = new BoolValue("Armor", boolValue5::get, true);
        armorBarColor = new ColorValue("ArmorColor", () -> {
            return esp2d.get().booleanValue() && armorBar.get().booleanValue();
        }, new Color(50, 255, 255));
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        this.entityPosMap.clear();
        this.playerRotationMap.clear();
    }

    @EventTarget
    public void onWorld(WorldLoadEvent event) {
        this.entityPosMap.clear();
        this.playerRotationMap.clear();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        String str;
        for (EntityPlayer player : this.entityPosMap.keySet()) {
            if (player.getDistanceToEntity(mc.thePlayer) >= 1.0f || mc.gameSettings.thirdPersonView != 0) {
                if (RenderUtil.isInViewFrustum(player)) {
                    float[] positions = this.entityPosMap.get(player);
                    float x = positions[0];
                    float y = positions[1];
                    float x2 = positions[2];
                    float y2 = positions[3];
                    float health = player.getHealth();
                    float maxHealth = player.getMaxHealth();
                    float healthPercentage = health / maxHealth;
                    if (fontTags.get().booleanValue()) {
                        if (fonttagsHealth.get().booleanValue()) {
                            EnumChatFormatting enumChatFormatting = EnumChatFormatting.RED;
                            EnumChatFormatting enumChatFormatting2 = EnumChatFormatting.BOLD;
                            double dRoundToHalf = MathUtils.roundToHalf(player.getHealth());
                            EnumChatFormatting enumChatFormatting3 = EnumChatFormatting.RESET;
                            str = " |" + enumChatFormatting + enumChatFormatting2 + " " + dRoundToHalf + "❤" + enumChatFormatting;
                        } else {
                            str = "";
                        }
                        String healthString = str;
                        String name = player.getDisplayName().getFormattedText() + healthString;
                        float halfWidth = mc.fontRendererObj.getStringWidth(name) * 0.5f;
                        float xDif = x2 - x;
                        float middle = x + (xDif / 2.0f);
                        float textHeight = mc.fontRendererObj.FONT_HEIGHT * 0.5f;
                        float renderY = (y - textHeight) - 2.0f;
                        float left = (middle - halfWidth) - 1.0f;
                        float right = middle + halfWidth + 1.0f;
                        if (fonttagsBackground.get().booleanValue()) {
                            Gui.drawRect(left, renderY - 6.0f, right, renderY + textHeight + 1.0f, new Color(0, 0, 0, 50).getRGB());
                        }
                        mc.fontRendererObj.drawStringWithShadow(name, middle - halfWidth, renderY - 4.0f, -1);
                    }
                    if (esp2d.get().booleanValue()) {
                        GL11.glDisable(3553);
                        GLUtil.startBlend();
                        if (armorBar.get().booleanValue()) {
                            float armorPercentage = player.getTotalArmorValue() / 20.0f;
                            float armorBarWidth = (x2 - x) * armorPercentage;
                            GL11.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) -106);
                            GL11.glBegin(7);
                            GL11.glVertex2f(x, y2 + 0.5f);
                            GL11.glVertex2f(x, y2 + 2.5f);
                            GL11.glVertex2f(x2, y2 + 2.5f);
                            GL11.glVertex2f(x2, y2 + 0.5f);
                            if (armorPercentage > 0.0f) {
                                color(armorBarColor.get().getRGB());
                                GL11.glVertex2f(x + 0.5f, y2 + 1.0f);
                                GL11.glVertex2f(x + 0.5f, y2 + 2.0f);
                                GL11.glVertex2f((x + armorBarWidth) - 0.5f, y2 + 2.0f);
                                GL11.glVertex2f((x + armorBarWidth) - 0.5f, y2 + 1.0f);
                                resetColor();
                            }
                            if (!healthBar.get().booleanValue()) {
                                GL11.glEnd();
                            }
                        }
                        if (healthBar.get().booleanValue()) {
                            float healthBarLeft = x - 2.5f;
                            float healthBarRight = x - 0.5f;
                            GL11.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) -106);
                            if (!armorBar.get().booleanValue()) {
                                GL11.glBegin(7);
                            }
                            GL11.glVertex2f(healthBarLeft, y);
                            GL11.glVertex2f(healthBarLeft, y2);
                            GL11.glVertex2f(healthBarRight, y2);
                            GL11.glVertex2f(healthBarRight, y);
                            float healthBarLeft2 = healthBarLeft + 0.5f;
                            float healthBarRight2 = healthBarRight - 0.5f;
                            float heightDif = y - y2;
                            float healthBarHeight = heightDif * healthPercentage;
                            float topOfHealthBar = y2 + 0.5f + healthBarHeight;
                            if (healthBarSyncColor.get().booleanValue()) {
                                int syncedcolor = InterFace.color(0).getRGB();
                                color(syncedcolor);
                            } else {
                                int color = ColorUtil.getColorFromPercentage(healthPercentage);
                                color(color);
                            }
                            GL11.glVertex2f(healthBarLeft2, topOfHealthBar);
                            GL11.glVertex2f(healthBarLeft2, y2 - 0.5f);
                            GL11.glVertex2f(healthBarRight2, y2 - 0.5f);
                            GL11.glVertex2f(healthBarRight2, topOfHealthBar);
                            resetColor();
                            float absorption = player.getAbsorptionAmount();
                            float absorptionPercentage = Math.min(1.0f, absorption / 20.0f);
                            int absorptionColor2 = absorptionColor.get().getRGB();
                            float absorptionHeight = heightDif * absorptionPercentage;
                            float topOfAbsorptionBar = y2 + 0.5f + absorptionHeight;
                            if (healthBarSyncColor.get().booleanValue()) {
                                color(InterFace.color(1).getRGB());
                            } else {
                                color(absorptionColor2);
                            }
                            GL11.glVertex2f(healthBarLeft2, topOfAbsorptionBar);
                            GL11.glVertex2f(healthBarLeft2, y2 - 0.5f);
                            GL11.glVertex2f(healthBarRight2, y2 - 0.5f);
                            GL11.glVertex2f(healthBarRight2, topOfAbsorptionBar);
                            resetColor();
                            if (!box.get().booleanValue()) {
                                GL11.glEnd();
                            }
                        }
                        if (box.get().booleanValue()) {
                            GL11.glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) -106);
                            if (!healthBar.get().booleanValue()) {
                                GL11.glBegin(7);
                            }
                            GL11.glVertex2f(x, y);
                            GL11.glVertex2f(x, y2);
                            GL11.glVertex2f(x + 1.5f, y2);
                            GL11.glVertex2f(x + 1.5f, y);
                            GL11.glVertex2f(x2 - 1.5f, y);
                            GL11.glVertex2f(x2 - 1.5f, y2);
                            GL11.glVertex2f(x2, y2);
                            GL11.glVertex2f(x2, y);
                            GL11.glVertex2f(x + 1.5f, y);
                            GL11.glVertex2f(x + 1.5f, y + 1.5f);
                            GL11.glVertex2f(x2 - 1.5f, y + 1.5f);
                            GL11.glVertex2f(x2 - 1.5f, y);
                            GL11.glVertex2f(x + 1.5f, y2 - 1.5f);
                            GL11.glVertex2f(x + 1.5f, y2);
                            GL11.glVertex2f(x2 - 1.5f, y2);
                            GL11.glVertex2f(x2 - 1.5f, y2 - 1.5f);
                            if (boxSyncColor.get().booleanValue()) {
                                color(InterFace.color(7).getRGB());
                            } else {
                                color(boxColor.get().getRGB());
                            }
                            GL11.glVertex2f(x + 0.5f, y + 0.5f);
                            GL11.glVertex2f(x + 0.5f, y2 - 0.5f);
                            GL11.glVertex2f(x + 1.0f, y2 - 0.5f);
                            GL11.glVertex2f(x + 1.0f, y + 0.5f);
                            GL11.glVertex2f(x2 - 1.0f, y + 0.5f);
                            GL11.glVertex2f(x2 - 1.0f, y2 - 0.5f);
                            GL11.glVertex2f(x2 - 0.5f, y2 - 0.5f);
                            GL11.glVertex2f(x2 - 0.5f, y + 0.5f);
                            GL11.glVertex2f(x + 0.5f, y + 0.5f);
                            GL11.glVertex2f(x + 0.5f, y + 1.0f);
                            GL11.glVertex2f(x2 - 0.5f, y + 1.0f);
                            GL11.glVertex2f(x2 - 0.5f, y + 0.5f);
                            GL11.glVertex2f(x + 0.5f, y2 - 1.0f);
                            GL11.glVertex2f(x + 0.5f, y2 - 0.5f);
                            GL11.glVertex2f(x2 - 0.5f, y2 - 0.5f);
                            GL11.glVertex2f(x2 - 0.5f, y2 - 1.0f);
                            resetColor();
                            GL11.glEnd();
                        }
                        GL11.glEnable(3553);
                        GLUtil.endBlend();
                    }
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @EventTarget
    public void onRender3D(Render3DEvent event) {
        boolean project2D = esp2d.get().booleanValue();
        if (project2D && !this.entityPosMap.isEmpty()) {
            this.entityPosMap.clear();
        }
        float partialTicks = event.partialTicks();

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
        }
    }

    private boolean isHypixelSpecialEntity(EntityPlayer player) {
        if (mc.getCurrentServerData() == null || !mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
            return false;
        }
        if (player.getDisplayName().getUnformattedText().startsWith("[NPC] ") || player.getDisplayName().getUnformattedText().startsWith("NPC ") || player.getDisplayName().getUnformattedText().toLowerCase().contains("helper") || player.getDisplayName().getUnformattedText().toLowerCase().contains("mod") || player.getDisplayName().getUnformattedText().toLowerCase().contains("admin") || player.getDisplayName().getUnformattedText().startsWith("BOT ") || player.getDisplayName().getUnformattedText().endsWith(" BOT") || player.getDisplayName().getUnformattedText().contains("Robot") || player.getScore() == -9999 || player.getScore() == 0) {
            return true;
        }
        if (player.posX == player.prevPosX && player.posZ == player.prevPosZ && player.rotationYaw == player.prevRotationYaw) {
            return true;
        }
        return false;
    }

    public boolean isValid(Entity entity) {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer) entity;
        return player.isEntityAlive() && RenderUtil.isBBInFrustum(entity.getEntityBoundingBox()) && mc.theWorld.playerEntities.contains(player);
    }

    public static void resetColor() {
        color(1.0d, 1.0d, 1.0d, 1.0d);
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(int color) {
        GL11.glColor4ub((byte) ((color >> 16) & 255), (byte) ((color >> 8) & 255), (byte) (color & 255), (byte) ((color >> 24) & 255));
    }
}
