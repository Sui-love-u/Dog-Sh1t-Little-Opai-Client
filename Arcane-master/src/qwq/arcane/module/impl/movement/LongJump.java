package qwq.arcane.module.impl.movement;

import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.lwjgl.input.Mouse;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.TickEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.fontrender.FontManager;
import qwq.arcane.utils.pack.PacketUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/movement/LongJump.class */
public class LongJump extends Module {
    private int kbCount;
    public static LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();

    public LongJump() {
        super("LongJump", Category.Movement);
        this.kbCount = 0;
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        if (!packets.isEmpty()) {
            packets.forEach(PacketUtil::queue);
            packets.clear();
        }
        this.kbCount = 0;
    }

    @EventTarget
    public void onRender2d(Render2DEvent event) {
        ScaledResolution sr = event.getScaledResolution();
        FontManager.Bold.get(18.0f).drawString("KB Count: " + this.kbCount, (sr.getScaledWidth() / 2) - (FontManager.Bold.get(18.0f).getStringWidth("KB Count: " + this.kbCount) / 2), (sr.getScaledHeight() / 2) - 18, -1);
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if ((packet instanceof S12PacketEntityVelocity) || (packet instanceof C0FPacketConfirmTransaction) || (packet instanceof C00PacketKeepAlive) || (packet instanceof S00PacketKeepAlive)) {
            if (packet instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) packet;
                if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
                    this.kbCount++;
                    packets.add(packet);
                    event.setCancelled(true);
                }
            }
            if (!(packet instanceof S12PacketEntityVelocity)) {
                packets.add(packet);
                event.setCancelled(true);
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (Mouse.isButtonDown(4)) {
            if (!packets.isEmpty()) {
                packets.forEach(PacketUtil::queue);
                packets.clear();
            }
            this.kbCount = 0;
        }
    }
}
