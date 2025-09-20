package qwq.arcane.module.impl.visuals;

import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import org.lwjgl.opengl.GL11;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketReceiveEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.utils.render.OGLUtils;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/Hitmarkers.class */
public final class Hitmarkers extends Module {
    private final ColorValue hitColorProperty;
    private final ColorValue killColorProperty;
    private final NumberValue xOffsetProperty;
    private final NumberValue lengthProperty;
    private final NumberValue hitMarkerThicknessProperty;
    private final BoolValue soundsProperty;
    public final NumberValue volumeProperty;
    private final ModeValue soundTypeProperty;
    private final TimerUtil attackTimeOut;
    private final TimerUtil killTimeOut;
    private int color;
    private double progress;
    private int lastAttackedEntity;
    private int toBeKilledEntity;

    public Hitmarkers() {
        super("Hitmarkers", Category.Visuals);
        this.hitColorProperty = new ColorValue("Hit Color", new Color(-1));
        this.killColorProperty = new ColorValue("Kill Color", new Color(-65536));
        this.xOffsetProperty = new NumberValue("X Offset", 2.0d, 0.5d, 10.0d, 0.5d);
        this.lengthProperty = new NumberValue("Length", 4.0d, 0.5d, 10.0d, 0.5d);
        this.hitMarkerThicknessProperty = new NumberValue("Thickness", 1.0d, 0.5d, 3.0d, 0.5d);
        this.soundsProperty = new BoolValue("Sounds", true);
        this.volumeProperty = new NumberValue("Volume", () -> {
            return this.soundsProperty.get().booleanValue();
        }, 100.0d, 0.0d, 100.0d, 1.0d);
        this.soundTypeProperty = new ModeValue("Sound Type", () -> {
            return this.soundsProperty.get().booleanValue();
        }, "SKEET", new String[]{"BASIC", "RIFK", "SKEET"});
        this.attackTimeOut = new TimerUtil();
        this.killTimeOut = new TimerUtil();
    }

    @EventTarget
    public void onRender(Render2DEvent event) {
        if (this.progress > 0.0d) {
            this.progress = RenderUtil.linearAnimation(this.progress, 0.0d, 0.02d);
            ScaledResolution resolution = event.getScaledResolution();
            double xMiddle = resolution.getScaledWidth() / 2.0d;
            double yMiddle = resolution.getScaledHeight() / 2.0d;
            GL11.glPushMatrix();
            GL11.glEnable(2881);
            OGLUtils.enableBlending();
            GL11.glDisable(3553);
            GL11.glTranslated(xMiddle, yMiddle, 0.0d);
            GL11.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
            OGLUtils.color(RenderUtil.fadeTo(removeAlphaComponent(this.color), this.color, (float) this.progress));
            for (int i = 0; i < 4; i++) {
                drawHitMarker(this.xOffsetProperty.getValue().doubleValue(), this.lengthProperty.getValue().doubleValue(), this.hitMarkerThicknessProperty.getValue().doubleValue());
                if (i != 3) {
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                }
            }
            GL11.glDisable(3042);
            GL11.glDisable(2881);
            GL11.glEnable(3553);
            GL11.glPopMatrix();
        }
    }

    @EventTarget
    public void onPacketSendEvent(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof C02PacketUseEntity) {
            C02PacketUseEntity packetUseEntity = (C02PacketUseEntity) packet;
            if (packetUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
                this.lastAttackedEntity = packetUseEntity.getEntityId();
                this.attackTimeOut.reset2();
                return;
            }
            return;
        }
        if ((packet instanceof C03PacketPlayer) && this.lastAttackedEntity != -1 && this.attackTimeOut.hasElapsed(500L)) {
            this.lastAttackedEntity = -1;
        }
    }

    @EventTarget
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus packetEntityStatus = (S19PacketEntityStatus) packet;
            int entityId = packetEntityStatus.getEntityId();
            if (entityId == this.lastAttackedEntity || (!this.killTimeOut.hasElapsed(50L) && entityId == this.toBeKilledEntity)) {
                switch (packetEntityStatus.getOpCode()) {
                    case 2:
                        this.color = this.hitColorProperty.get().getRGB();
                        this.progress = 1.0d;
                        this.killTimeOut.reset2();
                        this.toBeKilledEntity = this.lastAttackedEntity;
                        if (this.soundsProperty.getValue().booleanValue()) {
                            playSound();
                            break;
                        }
                        break;
                    case 3:
                        this.color = this.killColorProperty.get().getRGB();
                        this.progress = 1.0d;
                        this.toBeKilledEntity = -1;
                        break;
                }
                this.lastAttackedEntity = -1;
            }
        }
    }

    private static int removeAlphaComponent(int color) {
        int r = (color >> 16) & 255;
        int g = (color >> 8) & 255;
        int b = color & 255;
        return ((r & 255) << 16) | ((g & 255) << 8) | (b & 255) | 0;
    }

    private static void drawHitMarker(double xOffset, double length, double width) {
        double halfWidth = width * 0.5d;
        GL11.glBegin(7);
        GL11.glVertex2d(-(xOffset + length), -halfWidth);
        GL11.glVertex2d(-(xOffset + length), halfWidth);
        GL11.glVertex2d(-xOffset, halfWidth);
        GL11.glVertex2d(-xOffset, -halfWidth);
        GL11.glEnd();
    }

    private void playSound() {
        switch (this.soundTypeProperty.getValue()) {
            case "SKEET":
                Mine.getMinecraft().getSoundHandler().playSoundFromFile("skeet.ogg", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                break;
            case "NEKO":
                Mine.getMinecraft().getSoundHandler().playSoundFromFile("neko.ogg", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                break;
            case "RIFK":
                Mine.getMinecraft().getSoundHandler().playSoundFromFile("rifk.ogg", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                break;
            case "BASIC":
                Mine.getMinecraft().getSoundHandler().playSoundFromFile("basic.ogg", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                break;
        }
    }
}
