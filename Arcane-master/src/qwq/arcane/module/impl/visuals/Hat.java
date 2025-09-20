package qwq.arcane.module.impl.visuals;

import java.awt.Color;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.render.GLUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/Hat.class */
public class Hat extends Module {
    public final ModeValue mode;
    public final NumberValue points;
    public final NumberValue size;
    private final NumberValue offSetValue;
    public final ColorValue colorValue;
    public final ColorValue secondColorValue;
    public final BoolValue target;
    private final double[][] positions;
    private int lastPoints;
    private double lastSize;

    public Hat() {
        super("Hat", Category.Visuals);
        this.mode = new ModeValue("Mode", "Sexy", new String[]{"Astolfo", "Sexy", "Fade", "Dynamic"});
        this.points = new NumberValue("Points", 30.0d, 3.0d, 180.0d, 1.0d);
        this.size = new NumberValue("Size", 0.5d, 0.10000000149011612d, 3.0d, 0.10000000149011612d);
        this.offSetValue = new NumberValue("Off Set", 2000.0d, 0.0d, 5000.0d, 100.0d);
        this.colorValue = new ColorValue("Color", () -> {
            return this.mode.is("Fade") || this.mode.is("Dynamic");
        }, new Color(255, 255, 255));
        this.secondColorValue = new ColorValue("Second Color", () -> {
            return this.mode.is("Fade");
        }, new Color(0, 0, 0));
        this.target = new BoolValue("Target", true);
        this.positions = new double[((int) this.points.getMax()) + 1][2];
    }

    private void computeChineseHatPoints(int points, double radius) {
        for (int i = 0; i <= points; i++) {
            double circleX = radius * StrictMath.cos(((i * 3.141592653589793d) * 2.0d) / points);
            double circleZ = radius * StrictMath.sin(((i * 3.141592653589793d) * 2.0d) / points);
            this.positions[i][0] = circleX;
            this.positions[i][1] = circleZ;
        }
    }

    private void addCircleVertices(int points, Color[] colors, int alpha) {
        for (int i = 0; i <= points; i++) {
            double[] pos = this.positions[i];
            Color clr = colors[i];
            GL11.glColor4f(clr.getRed() / 255.0f, clr.getGreen() / 255.0f, clr.getBlue() / 255.0f, alpha / 255.0f);
            GL11.glVertex3d(pos[0], 0.0d, pos[1]);
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (this.lastSize != this.size.get().doubleValue() || this.lastPoints != this.points.get().doubleValue()) {
            this.lastSize = this.size.get().doubleValue();
            int iIntValue = this.points.get().intValue();
            this.lastPoints = iIntValue;
            computeChineseHatPoints(iIntValue, this.lastSize);
        }
        drawHat(event, mc.thePlayer);
    }

    public void drawHat(Render3DEvent event, EntityPlayer player) {
        Color[] colorMode;
        Color[] colorArr;
        if (player == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) {
            return;
        }
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glShadeModel(7425);
        GLUtil.startBlend();
        float partialTicks = event.partialTicks();
        mc.getRenderManager();
        double rx = RenderManager.renderPosX;
        double ry = RenderManager.renderPosY;
        double rz = RenderManager.renderPosZ;
        GL11.glTranslated(-rx, -ry, -rz);
        double x = player.lastTickPosX + ((player.posX - player.lastTickPosX) * event.partialTicks());
        double y = player.lastTickPosY + ((player.posY - player.lastTickPosY) * event.partialTicks());
        double z = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * event.partialTicks());
        int points = this.points.get().intValue();
        double radius = this.size.get().doubleValue();
        Color[] colors = new Color[181];
        colorMode = new Color[0];
        switch (this.mode.get()) {
            case "Astolfo":
                colorArr = new Color[]{new Color(252, 106, 140), new Color(252, 106, 213), new Color(218, 106, 252), new Color(145, 106, 252), new Color(106, 140, 252), new Color(106, 213, 252), new Color(106, 213, 252), new Color(106, 140, 252), new Color(145, 106, 252), new Color(218, 106, 252), new Color(252, 106, 213), new Color(252, 106, 140)};
                break;
            case "Sexy":
                colorArr = new Color[]{new Color(255, 150, 255), new Color(255, 132, 199), new Color(211, 101, 187), new Color(160, 80, 158), new Color(120, 63, 160), new Color(123, 65, 168), new Color(104, 52, 152), new Color(142, 74, 175), new Color(160, 83, 179), new Color(255, 110, 189), new Color(255, 150, 255)};
                break;
            case "Fade":
                colorArr = new Color[]{this.colorValue.get(), this.secondColorValue.get(), this.colorValue.get()};
                break;
            case "Dynamic":
                colorArr = new Color[]{this.colorValue.get(), new Color(ColorUtil.darker(this.colorValue.get().getRGB(), 0.25f)), this.colorValue.get()};
                break;
            default:
                colorArr = colorMode;
                break;
        }
        Color[] colorMode2 = colorArr;
        for (int i = 0; i < colors.length; i++) {
            colors[i] = fadeBetween(colorMode2, this.offSetValue.get().doubleValue(), i * (this.offSetValue.get().doubleValue() / this.points.get().doubleValue()));
        }
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 1.9d, z);
        if (player.isSneaking()) {
            GL11.glTranslated(0.0d, -0.2d, 0.0d);
        }
        GL11.glRotatef(MathUtils.interpolate(player.prevRotationYawHead, player.rotationYawHead, partialTicks).floatValue(), 0.0f, -1.0f, 0.0f);
        float pitch = MathUtils.interpolate(player.prevRotationPitchHead, player.rotationPitchHead, partialTicks).floatValue();
        GL11.glRotatef(pitch / 3.0f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslated(0.0d, 0.0d, pitch / 270.0f);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(2.0f);
        GL11.glBegin(2);
        addCircleVertices(points - 1, colors, 255);
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glBegin(6);
        GL11.glVertex3d(0.0d, radius / 2.0d, 0.0d);
        addCircleVertices(points, colors, 128);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glTranslated(rx, ry, rz);
        GLUtil.endBlend();
        GL11.glDepthMask(true);
        GL11.glShadeModel(7424);
        GL11.glEnable(2929);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
    }

    public Color fadeBetween(Color[] table, double speed, double offset) {
        return fadeBetween(table, ((System.currentTimeMillis() + offset) % speed) / speed);
    }

    public Color fadeBetween(Color[] table, double progress) {
        int i = table.length;
        if (progress == 1.0d) {
            return table[0];
        }
        if (progress == 0.0d) {
            return table[i - 1];
        }
        double max = Math.max(0.0d, (1.0d - progress) * (i - 1));
        int min = (int) max;
        return fadeBetween(table[min], table[min + 1], max - min);
    }

    public Color fadeBetween(Color start, Color end, double progress) {
        if (progress > 1.0d) {
            progress = 1.0d - (progress % 1.0d);
        }
        return gradient(start, end, progress);
    }

    public Color gradient(Color start, Color end, double progress) {
        double invert = 1.0d - progress;
        return new Color((int) ((start.getRed() * invert) + (end.getRed() * progress)), (int) ((start.getGreen() * invert) + (end.getGreen() * progress)), (int) ((start.getBlue() * invert) + (end.getBlue() * progress)), (int) ((start.getAlpha() * invert) + (end.getAlpha() * progress)));
    }
}
