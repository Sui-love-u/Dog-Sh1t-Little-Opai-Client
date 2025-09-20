package com.amaya.module.impl.combat;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.events.impl.player.EventMoveMath;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.player.SlowDownEvent;
import com.amaya.events.impl.render.Render2DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.utils.animations.AnimationUtils;
import com.amaya.utils.animations.ContinualAnimation;
import com.amaya.utils.animations.impl.RippleAnimation;
import com.amaya.utils.client.ChatUtil;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.pack.PacketUtil;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.render.RoundedUtil;
import com.amaya.utils.render.shader.ShaderElement;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.*;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "Gapple",category = Category.Combat)
public class Gapple extends Module {
    public static boolean eating = false;
    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    @Getter
    private int movingPackets = 0;
    private int slot = 0;
    private boolean start;


    @Override
    public void onEnable() {

        packets.clear();
        slot = -1;
        movingPackets = 0;
        eating = false;
        if (mc.thePlayer != null) {
            mc.gameSettings.keyBindSprint.pressed = false;
        }

        start = true;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eating = false;
        release();
        super.onDisable();
    }
    @EventTarget
    public void onMoveMathEvent(EventMoveMath e) {
            if (eating && mc.thePlayer.positionUpdateTicks < 20)
                e.setCancelled(true);
    }
    @EventTarget
    public void onWorldEvent(WorldEvent e) {
        eating = false;
        release();
    }

    @EventTarget
    public void onMotionEvent(MotionEvent event) {
        if (mc.isSingleplayer()) {
            return;
        }
        if (!event.isPre()) {
            if (eating) {
                ++movingPackets;
                packets.add(new C01PacketChatMessage("release"));
            }

        }
        if (!event.isPost()) {
            if (mc.thePlayer.getHealth() <= 0.01) {
                toggle();
            }

            if (mc.thePlayer != null && mc.thePlayer.isEntityAlive()) {
                if (start && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem)));
                    start = false;
                }

                if (mc.playerController.getCurrentGameType().isSurvivalOrAdventure()) {
                    slot = getGApple();
                    if (slot != -1 && !(mc.thePlayer.getHealth() >= 40.0F)) {
                        eating = true;
                        if (movingPackets >= 33) {
                            mc.gameSettings.keyBindSprint.pressed = false;
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(slot)));
                            release();
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem)));
                        } else {
                            Packet<?> packet;
                            if (mc.thePlayer.ticksExisted % 5 == 0) {
                                for (; !packets.isEmpty();
                                     PacketUtil.sendPacketNoEvent(packet)) {
                                    packet = packets.poll();
                                    if (packet instanceof C01PacketChatMessage) {
                                        break;
                                    }

                                    if (packet instanceof C03PacketPlayer) {
                                        --movingPackets;
                                    }
                                }
                            }
                        }
                    } else if (eating) {
                        eating = false;
                        release();
                    }

                } else {
                    eating = false;
                    release();
                }
            } else {
                eating = false;
                packets.clear();
            }
        }
    }

    @EventTarget
    public void onTickEvent(TickEvent event) {
        mc.thePlayer.setSprinting(false);
    }


    @EventTarget
    public void onPacketSendEvent(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (mc.thePlayer != null && mc.playerController.getCurrentGameType().isSurvivalOrAdventure()) {
            if (eating && packet instanceof C07PacketPlayerDigging dig) {
                if (dig.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                    event.setCancelled(true);
                }
            }

            if (!(packet instanceof C00Handshake) && !(packet instanceof C00PacketLoginStart) && !(packet instanceof C00PacketServerQuery) && !(packet instanceof C01PacketPing) && !(packet instanceof C01PacketEncryptionResponse) && !(packet instanceof C01PacketChatMessage)) {
                if (!(packet instanceof C09PacketHeldItemChange) && !(packet instanceof C0EPacketClickWindow) && !(packet instanceof C16PacketClientStatus) && !(packet instanceof C0DPacketCloseWindow) && eating) {
                    event.setCancelled(true);
                    packets.add(packet);
                }

            }
        }
    }


    private void release() {
        if (mc.getNetHandler() != null) {
            while (!packets.isEmpty()) {
                Packet<?> packet = packets.poll();
                if (!(packet instanceof C01PacketChatMessage) && !(packet instanceof C08PacketPlayerBlockPlacement) && !(packet instanceof C07PacketPlayerDigging)) {
                    PacketUtil.sendPacketNoEvent(packet);
                }
            }

            movingPackets = 0;
        }
    }

    private int getGApple() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemAppleGold) {
                return i;
            }
        }

        toggle();
        return -1;
    }
    private float x;
    private float y;
    private float width;
    private double progressRender;
    private final RippleAnimation rippleAnimation = new RippleAnimation();

    @EventTarget
    public void onRender2DEvent(Render2DEvent event) {
        if (mc.thePlayer.getHeldItem() != null && eating) {
            this.x = AnimationUtils.animate((float) event.getScaledResolution().getScaledWidth() / 2.0f - 42.0f, this.x, 0.5f);
            this.y = AnimationUtils.animate(!eating ? (float) (event.getScaledResolution().getScaledHeight() - 20) : (float) event.getScaledResolution().getScaledHeight() / 2.0f + 20.0f, this.y, 0.5f);
            this.width = AnimationUtils.animate(eating ? 86.0f : 20.0f, this.width, 0.5f);
            this.progressRender = AnimationUtils.animate(movingPackets * 1.78f, this.progressRender, 0.2f);
            ShaderElement.addBlurTask(() -> RoundedUtil.drawRound(this.x - 2.0f, this.y - 2.0f, this.width, 20.0f, 4.0f, false, new Color(0, 0, 0, 255)));
            ShaderElement.addBloomTask(() -> RoundedUtil.drawRound(this.x - 2.0f, this.y - 2.0f, this.width, 20.0f, 4.0f, false, new Color(0, 0, 0, 255)));
            this.rippleAnimation.draw(() -> RoundedUtil.drawRound(this.x - 2.0f, this.y - 2.0f, this.width, 20.0f, 4.0f, false, new Color(0, 0, 0, 100)));
            RoundedUtil.drawRound(this.x - 2.0f, this.y - 2.0f, this.width, 20.0f, 4.0f, false, new Color(0, 0, 0, 100));
            if (eating) {
                RoundedUtil.drawRound(this.x + 20.0f, this.y + 3.0f, 60.0f, 10.0f, 4.0f, false, new Color(0, 0, 0, 60));
                RoundedUtil.drawRound(this.x + 20.0f, this.y + 3.0f, (float) this.progressRender, 10.0f, 4.0f, false, Color.WHITE);
            }
            RenderUtil.drawItemStack(mc.thePlayer.getHeldItem(), (int) this.x, (int) this.y);
        }
    }
}
