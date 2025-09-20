package qwq.arcane.module.impl.misc;

import java.awt.Color;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.player.MovementUtil;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/misc/Timer.class */
public class Timer extends Module {
    final ConcurrentLinkedQueue<Packet<?>> packets;
    final Animation anim;
    private final NumberValue amount;
    public int count;

    public Timer() {
        super("Timer", Category.Misc);
        this.packets = new ConcurrentLinkedQueue<>();
        this.anim = new DecelerateAnimation(250, 1.0d);
        this.amount = new NumberValue("Amount", 1.0d, 1.0d, 10.0d, 0.1d);
        this.count = 0;
    }

    @EventTarget
    public void onSuffix(UpdateEvent event) {
        setsuffix(this.amount.get().toString());
    }

    @EventTarget
    public void onMotion(MotionEvent eventMotion) {
        PacketUtil.sendPacketNoEvent(new C0FPacketConfirmTransaction(MathUtils.getRandom(114514, 191981000), (short) MathUtils.getRandomInRange(114514, 191981000), true));
        if (this.count > 0) {
            mc.timer.timerSpeed = MovementUtil.isMoving() ? this.amount.getValue().floatValue() : 1.0f;
            return;
        }
        mc.timer.timerSpeed = 1.0f;
        if (!this.packets.isEmpty()) {
            this.packets.forEach(packet -> {
                PacketUtil.sendPacketNoEvent(packet);
            });
            this.packets.clear();
        }
    }

    @EventTarget
    public void onPacketSendEvent(PacketSendEvent eventPacket) {
        if ((eventPacket.getPacket() instanceof C03PacketPlayer) && !(eventPacket.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook)) {
            if (!((C03PacketPlayer) eventPacket.getPacket()).isMoving()) {
                this.count += 50;
                eventPacket.setCancelled(true);
            } else if (this.count > 0) {
                this.count -= 50;
            }
        }
        if (eventPacket.getPacket() instanceof C0FPacketConfirmTransaction) {
            eventPacket.setCancelled(true);
            this.packets.add(eventPacket.getPacket());
        }
        if ((eventPacket.getPacket() instanceof C02PacketUseEntity) && ((C02PacketUseEntity) eventPacket.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            toggle();
        }
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        if (!this.packets.isEmpty()) {
            this.packets.forEach(packet -> {
                PacketUtil.sendPacketNoEvent(packet);
            });
            this.packets.clear();
        }
        this.count = 0;
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        renderProgessBar4();
    }

    public void renderProgessBar4() {
        this.anim.setDirection(getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!getState() && this.anim.isDone()) {
            return;
        }
        String string = String.valueOf(this.count);
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        float f = this.anim.getOutput().floatValue();
        String string2 = "§r Grim Timer Balance: §l" + string;
        float f2 = Bold.get(18.0f).getStringWidth(string2);
        float f3 = (f2 + 3 + 6.0f) * f;
        float f4 = (scaledResolution.getScaledWidth() / 2.0f) - (f3 / 2.0f);
        float f5 = scaledResolution.getScaledHeight() - ((scaledResolution.getScaledHeight() / 2.0f) - 20.0f);
        Color color = ColorUtil.applyOpacity(InterFace.color(1), 222.0f);
        Color color2 = ColorUtil.applyOpacity(InterFace.color(6), 222.0f);
        RenderUtil.scissorStart(f4 - 1.5d, f5 - 1.5d, f3 + 3.0f, 20.0f + 23.0f);
        RoundedUtil.drawRound(f4, f5, f3, 20.0f + 20.0f, 4.0f, ColorUtil.tripleColor(20, 0.45f));
        Bold.get(18.0f).drawString(string2, f4 + 2.0f + 3, f5 + 9.5f, -1);
        RoundedUtil.drawRound((f4 + 3.0f) * f, f5 + 25.0f, f3 - (8.0f * f), 5.0f, 2.0f, new Color(166, 164, 164, 81));
        RoundedUtil.drawGradientHorizontal(f4 + 3.0f, f5 + 25.0f, (f3 - 8.0f) * Math.min(Math.max(this.count / 7500.0f, 0.0f), 1.0f), 5.0f, 2.0f, color, color2);
        RenderUtil.scissorEnd();
    }
}
