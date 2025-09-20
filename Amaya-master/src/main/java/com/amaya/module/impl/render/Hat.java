/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package com.amaya.module.impl.render;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.render.Render3DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ColorSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.math.MathUtils;
import com.amaya.utils.render.ColorUtil;
import com.amaya.utils.render.GLUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Hat", category = Category.Render)
public class Hat extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", "Sexy", new String[]{"Astolfo", "Sexy", "Fade", "Dynamic"});
    public final NumberSetting points = new NumberSetting("Points", 30.0, 3.0, 180.0,1.0);
    public final NumberSetting size = new NumberSetting("Size", 0.5, 0.1, 3.0, 0.1);
    private final NumberSetting offSetValue = new NumberSetting("Off Set", 2000.0, 0.0, 5000.0, 100.0);
    public final ColorSetting colorValue = new ColorSetting("Color", () -> mode.is("Fade") || mode.is("Dynamic"),new Color(255, 255, 255));
    public final ColorSetting secondColorValue = new ColorSetting("Second Color", () -> mode.is("Fade"),new Color(0, 0, 0));
    public final BooleanSetting target = new BooleanSetting("TargetHUD", true);
    private final double[][] positions = new double[(int) (points.getMax() + 1)][2];
    private int lastPoints;
    private double lastSize;

    private void computeChineseHatPoints(final int points, final double radius) {
        for (int i = 0; i <= points; i++) {
            final double circleX = radius * StrictMath.cos(i * Math.PI * 2 / points);
            final double circleZ = radius * StrictMath.sin(i * Math.PI * 2 / points);

            this.positions[i][0] = circleX;
            this.positions[i][1] = circleZ;
        }
    }

    private void addCircleVertices(final int points, final Color[] colors, final int alpha) {
        for (int i = 0; i <= points; i++) {
            final double[] pos = this.positions[i];
            final Color clr = colors[i];
            GL11.glColor4f(clr.getRed() / 255.0f, clr.getGreen() / 255.0f, clr.getBlue() / 255.0f, alpha / 255.0f);
            glVertex3d(pos[0], 0, pos[1]);
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {

        if (this.lastSize != this.size.get() || this.lastPoints != this.points.get()) {
            this.lastSize = this.size.get();
            this.computeChineseHatPoints(this.lastPoints = (int) this.points.get().doubleValue(), this.lastSize);
        }

        drawHat(event, mc.thePlayer);
    }

    public void drawHat(Render3DEvent event, EntityPlayer player) {
        if (player == mc.thePlayer && this.mc.gameSettings.thirdPersonView == 0)
            return;
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);
        GLUtil.startBlend();

        final float partialTicks = event.partialTicks;
        final RenderManager render = this.mc.getRenderManager();

        final double rx = render.renderPosX;
        final double ry = render.renderPosY;
        final double rz = render.renderPosZ;

        glTranslated(-rx, -ry, -rz);

        final double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        final double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        final double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        final int points = (int) this.points.get().doubleValue();
        final double radius = this.size.get();

        Color[] colors = new Color[181];
        Color[] colorMode = new Color[0];
        colorMode = switch (this.mode.get()) {
            case "Astolfo" -> new Color[]{new Color(252, 106, 140), new Color(252, 106, 213),
                    new Color(218, 106, 252), new Color(145, 106, 252), new Color(106, 140, 252),
                    new Color(106, 213, 252), new Color(106, 213, 252), new Color(106, 140, 252),
                    new Color(145, 106, 252), new Color(218, 106, 252), new Color(252, 106, 213),
                    new Color(252, 106, 140)};
            case "Sexy" ->
                    new Color[]{new Color(255, 150, 255), new Color(255, 132, 199), new Color(211, 101, 187), new Color(160, 80, 158), new Color(120, 63, 160), new Color(123, 65, 168), new Color(104, 52, 152), new Color(142, 74, 175), new Color(160, 83, 179), new Color(255, 110, 189), new Color(255, 150, 255)};
            case "Fade" ->
                    new Color[]{this.colorValue.get(), this.secondColorValue.get(), this.colorValue.get()};
            case "Dynamic" ->
                    new Color[]{this.colorValue.get(), new Color(ColorUtil.darker(colorValue.get().getRGB(), 0.25F)), this.colorValue.get()};
            default -> colorMode;
        };

        for (int i = 0; i < colors.length; ++i) {
            colors[i] = this.fadeBetween(colorMode, (this.offSetValue.get()), (double) i * ((double) (this.offSetValue.get()) / this.points.get()));
        }

        glPushMatrix();

        // Position
        {
            glTranslated(x, y + 1.9, z);
            if (player.isSneaking())
                glTranslated(0, -0.2, 0);
        }

        // Yaw
        {
            glRotatef(MathUtils.interpolateFloat(player.prevRotationYawHead, player.rotationYawHead, partialTicks), 0, -1, 0);
        }

        // Pitch
        {
            final float pitch = MathUtils.interpolateFloat(player.prevRotationPitchHead, player.rotationPitchHead, partialTicks);
            glRotatef(pitch / 3.f, 1, 0, 0);
            glTranslated(0, 0, pitch / 270.f);
        }

        // Outline
        {
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glLineWidth(2.0f);

            glBegin(GL_LINE_LOOP);
            {
                this.addCircleVertices(points - 1, colors, 0xFF);
            }
            glEnd();

            glDisable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        }

        // Cone
        {
            glBegin(GL_TRIANGLE_FAN);
            {
                glVertex3d(0, radius / 2, 0);

                this.addCircleVertices(points, colors, 0x80);
            }
            glEnd();
        }

        glPopMatrix();

        glTranslated(rx, ry, rz);

        GLUtil.endBlend();
        glDepthMask(true);
        glShadeModel(GL_FLAT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
    }

    public Color fadeBetween(final Color[] table, final double speed, final double offset) {
        return this.fadeBetween(table, (System.currentTimeMillis() + offset) % speed / speed);
    }

    public Color fadeBetween(final Color[] table, final double progress) {
        final int i = table.length;
        if (progress == 1.0) {
            return table[0];
        }
        if (progress == 0.0) {
            return table[i - 1];
        }
        final double max = Math.max(0.0, (1.0 - progress) * (i - 1));
        final int min = (int) max;
        return this.fadeBetween(table[min], table[min + 1], max - min);
    }

    public Color fadeBetween(final Color start, final Color end, double progress) {
        if (progress > 1.0) {
            progress = 1.0 - progress % 1.0;
        }
        return this.gradient(start, end, progress);
    }

    public Color gradient(final Color start, final Color end, final double progress) {
        final double invert = 1.0 - progress;
        return new Color((int) (start.getRed() * invert + end.getRed() * progress), (int) (start.getGreen() * invert + end.getGreen() * progress), (int) (start.getBlue() * invert + end.getBlue() * progress), (int) (start.getAlpha() * invert + end.getAlpha() * progress));
    }
}
