package qwq.arcane.module.impl.combat;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.TickEvent;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.MoveMathEvent;
import qwq.arcane.event.impl.events.player.SlowDownEvent;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.animations.AnimationUtils;
import qwq.arcane.utils.animations.impl.ContinualAnimation;
import qwq.arcane.utils.animations.impl.RippleAnimation;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.utils.render.shader.ShaderElement;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/combat/Gapple.class */
public class Gapple extends Module {
    public BoolValue render;
    public ModeValue renderMode;
    public int eattick;
    public static boolean isS12;
    private final LinkedBlockingQueue<Packet<?>> packets;
    private final ContinualAnimation anim;
    public static int i;
    private float x;
    private float y;
    private float width;
    private double progressRender;
    private final RippleAnimation rippleAnimation;
    public static boolean eating = false;

    public Gapple() {
        super("Gapple", Category.Combat);
        this.render = new BoolValue("Render", true);
        this.renderMode = new ModeValue("RenderMode", "SouthSide", new String[]{"Client", "SouthSide", "Old", "Naven"});
        this.anim = new ContinualAnimation();
        this.rippleAnimation = new RippleAnimation();
        this.eattick = 0;
        this.packets = new LinkedBlockingQueue<>();
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.eattick = 0;
        this.packets.clear();
        eating = false;
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        eating = false;
        releaseall();
    }

    @EventTarget
    public void onMoveMath(MoveMathEvent event) {
        if (((Gapple) Client.Instance.getModuleManager().getModule(Gapple.class)).getState()) {
            if ((Mine.getMinecraft().thePlayer.positionUpdateTicks >= 19 || isS12) && isS12) {
                isS12 = false;
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent e) {
        setsuffix(this.renderMode.get());
        mc.thePlayer.setSprinting(false);
    }

    @EventTarget
    public void onMotion(MotionEvent e) {
        if (e.isPost()) {
            this.packets.add(new C01PacketChatMessage("cnm"));
        }
        if (e.isPre()) {
            if (mc.thePlayer == null || !mc.thePlayer.isEntityAlive()) {
                setState(false);
                return;
            }
            if (findgapple() == -100) {
                setState(false);
                return;
            }
            eating = true;
            if (this.eattick >= 33) {
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(findgapple()));
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                releaseall();
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                this.eattick = 0;
                return;
            }
            if (mc.thePlayer.ticksExisted % 5 == 0) {
                while (!this.packets.isEmpty()) {
                    Packet<?> packet = this.packets.poll();
                    if (!(packet instanceof C01PacketChatMessage)) {
                        if (packet instanceof C03PacketPlayer) {
                            this.eattick--;
                        }
                        mc.getNetHandler().addToSendQueueUnregistered(packet);
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public int findgapple() {
        for (int i2 = 0; i2 < 9; i2++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i2 + 36).getStack();
            if (stack != null && (stack.getItem() instanceof ItemAppleGold)) {
                return i2;
            }
        }
        return -100;
    }

    private void releaseall() {
        if (mc.getNetHandler() == null) {
            return;
        }
        while (!this.packets.isEmpty()) {
            Packet<?> packet = this.packets.poll();
            if (!(packet instanceof C01PacketChatMessage) && !(packet instanceof C07PacketPlayerDigging) && !(packet instanceof C0EPacketClickWindow) && !(packet instanceof C0DPacketCloseWindow)) {
                mc.getNetHandler().addToSendQueueUnregistered(packet);
            }
        }
        this.eattick = 0;
    }

    @EventTarget
    public void onPacket(PacketSendEvent e) {
        Packet<?> packet = e.getPacket();
        if ((packet instanceof C00Handshake) || (packet instanceof C00PacketLoginStart) || (packet instanceof C00PacketServerQuery) || (packet instanceof C01PacketPing) || (packet instanceof C01PacketEncryptionResponse) || (packet instanceof C01PacketChatMessage)) {
            return;
        }
        if (packet instanceof C03PacketPlayer) {
            this.eattick++;
        }
        if ((packet instanceof C07PacketPlayerDigging) || (packet instanceof C09PacketHeldItemChange) || (packet instanceof C0EPacketClickWindow) || (packet instanceof C0DPacketCloseWindow)) {
            e.setCancelled(true);
        } else if (!(packet instanceof C08PacketPlayerBlockPlacement) && eating) {
            this.packets.add(packet);
            e.setCancelled(true);
        }
    }

    @EventTarget
    public void onSlow(SlowDownEvent eventSlowDown) {
        eventSlowDown.setCancelled(false);
        eventSlowDown.setForward(0.2f);
        eventSlowDown.setStrafe(0.2f);
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (this.render.getValue().booleanValue()) {
            switch (this.renderMode.getValue()) {
                case "Client":
                    if (mc.thePlayer.getHeldItem() != null && eating) {
                        this.x = AnimationUtils.animate((event.getScaledResolution().getScaledWidth() / 2.0f) - 42.0f, this.x, 0.5f);
                        this.y = AnimationUtils.animate(!eating ? event.getScaledResolution().getScaledHeight() - 20 : (event.getScaledResolution().getScaledHeight() / 2.0f) + 20.0f, this.y, 0.5f);
                        this.width = AnimationUtils.animate(eating ? 86.0f : 20.0f, this.width, 0.5f);
                        this.progressRender = AnimationUtils.animate(this.eattick * 1.78f, this.progressRender, 0.20000000298023224d);
                        ShaderElement.addBlurTask(() -> {
                            RoundedUtil.drawRound(this.x - 2.0f, this.y - 2.0f, this.width, 20.0f, 4.0f, false, new Color(0, 0, 0, 255));
                        });
                        ShaderElement.addBloomTask(() -> {
                            RoundedUtil.drawRound(this.x - 2.0f, this.y - 2.0f, this.width, 20.0f, 4.0f, false, new Color(0, 0, 0, 255));
                        });
                        this.rippleAnimation.draw(() -> {
                            RoundedUtil.drawRound(this.x - 2.0f, this.y - 2.0f, this.width, 20.0f, 4.0f, false, new Color(0, 0, 0, 100));
                        });
                        RoundedUtil.drawRound(this.x - 2.0f, this.y - 2.0f, this.width, 20.0f, 4.0f, false, new Color(0, 0, 0, 100));
                        if (eating) {
                            RoundedUtil.drawRound(this.x + 20.0f, this.y + 3.0f, 60.0f, 10.0f, 4.0f, false, new Color(0, 0, 0, 60));
                            RoundedUtil.drawRound(this.x + 20.0f, this.y + 3.0f, (float) this.progressRender, 10.0f, 4.0f, false, Color.WHITE);
                        }
                        ItemStack goldenAppleStack = new ItemStack(Item.getItemById(322));
                        RenderUtil.drawItemStack(goldenAppleStack, (int) this.x, (int) this.y);
                        break;
                    }
                    break;
                case "SouthSide":
                    ScaledResolution resolution = new ScaledResolution(mc);
                    int x = resolution.getScaledWidth() / 2;
                    int y = resolution.getScaledHeight() - 75;
                    float percentage = ((float) (120.0d * (this.eattick / 34.0d))) * 1.0f;
                    AnimationUtils.animate(98.0f * percentage, 40.0f, 1.0f);
                    RoundedUtil.drawRound((x - 50) - 1, (y - 1) - 12, 101.0f, ((int) (5.0f + 1.0f)) + 12 + 3, 2.0f, new Color(17, 17, 17, 215));
                    RoundedUtil.drawRound((x - 50) - 1, y - 1, 101.0f, (int) (5.0f + 1.0f), 2.0f, new Color(17, 17, 17, 215));
                    RoundedUtil.drawGradientHorizontal(x - 50, y + 1, Math.min(percentage, 100.0f), (int) 5.0f, 2.0f, new Color(128, 255, 255), new Color(128, 128, 255));
                    Bold.get(22.0f).drawString("Time", x - 15, (y - 1) - 10, Color.WHITE.getRGB());
                    Bold.get(22.0f).drawString(new DecimalFormat("0.0").format(percentage * 0.9d) + "%", x - 11, y + 1.5f, new Color(207, 207, 207).getRGB());
                    break;
                case "Old":
                    ScaledResolution sr = event.getScaledResolution();
                    int startX = (sr.getScaledWidth() / 2) - 58;
                    int startY = (sr.getScaledHeight() / 2) + 50;
                    this.anim.animate(Math.min(3.75f * i, 120.0f), 20);
                    float target = ((float) (120.0d * (this.eattick / 34.0d))) * 1.0f;
                    GlStateManager.disableAlpha();
                    RoundedUtil.drawRound(startX - 38, startY, 170.0f, 28.0f, 8.0f, new Color(0, 0, 0, 80));
                    RoundedUtil.drawRound(startX, (float) (startY + 7.5d), 124.0f, 11.0f, 5.0f, new Color(0, 0, 0, 80));
                    RoundedUtil.drawGradientRound(startX, startY + 7.5f, Math.min(target, 120.0f), 11.0f, 5.0f, InterFace.color(1), InterFace.color(7), InterFace.color(14), InterFace.color(21));
                    Bold.get(18.0f).drawString("Gapple", startX - 34, startY + 9.0f, -1);
                    GlStateManager.disableAlpha();
                    break;
                case "Naven":
                    ScaledResolution sr2 = new ScaledResolution(mc);
                    float progress = Math.min(this.eattick / 34.0f, 1.0f);
                    int centerX = sr2.getScaledWidth() / 2;
                    int centerY = sr2.getScaledHeight() / 2;
                    float startX2 = centerX - (80.0f / 2.0f);
                    float startY2 = centerY - 30;
                    int textWidth = Bold.get(18.0f).getStringWidth("Eating Ticks");
                    Bold.get(18.0f).drawString("Eating Ticks", centerX - (textWidth / 2.0f), startY2 - 3.0f, -1);
                    RoundedUtil.drawGradientRound(startX2, startY2 + 7.5f, 80.0f, 2.0f, 3.0f, new Color(0, 0, 0, 200), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150), new Color(0, 0, 0, 150));
                    float target2 = 80.0f * progress;
                    RoundedUtil.drawGradientRound(startX2, startY2 + 7.5f, target2, 2.0f, 3.0f, new Color(143, 49, 46, 220), new Color(143, 49, 46, 220), new Color(143, 49, 46, 220), new Color(143, 49, 46, 220));
                    break;
            }
        }
    }
}
