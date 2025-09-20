package qwq.arcane.module.impl.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.PlaceEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.combat.Gapple;
import qwq.arcane.module.impl.combat.KillAura;
import qwq.arcane.module.impl.player.Blink;
import qwq.arcane.module.impl.player.Stealer;
import qwq.arcane.utils.math.Vector2f;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.rotation.RotationUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/world/ChestAura.class */
public class ChestAura extends Module {
    private final NumberValue range;
    public TimerUtil waitBoxOpenTimer;
    public TimerUtil delayAfterOpenTimer;
    private BlockPos globalPos;
    Entity entity;
    public static boolean isWaitingOpen = false;
    public static List<BlockPos> list = new ArrayList();

    public ChestAura() {
        super("ContainerAura", Category.World);
        this.range = new NumberValue("Range", 4.0d, 0.0d, 15.0d, 0.1d);
        this.waitBoxOpenTimer = new TimerUtil();
        this.delayAfterOpenTimer = new TimerUtil();
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        list.clear();
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        list.clear();
    }

    @EventTarget
    private void onPre(UpdateEvent event) {
        setsuffix(String.valueOf(this.range.get()));
        GuiScreen guiScreen = mc.currentScreen;
        if (mc.thePlayer.isOnLadder() || (guiScreen instanceof GuiChest) || Gapple.eating || KillAura.target != null || mc.thePlayer.isUsingItem() || ((Scaffold) getModule(Scaffold.class)).getState() || ((BlockFly) getModule(BlockFly.class)).getState() || ((Blink) getModule(Blink.class)).getState() || Gapple.eating || !this.delayAfterOpenTimer.hasTimeElapsed(500L)) {
            return;
        }
        this.globalPos = null;
        if (mc.thePlayer.ticksExisted % 20 == 0 || KillAura.target != null || (mc.currentScreen instanceof GuiContainer) || mc.thePlayer.isUsingItem()) {
            return;
        }
        float radius = this.range.getValue().floatValue();
        float f = radius;
        while (true) {
            float y = f;
            if (y >= (-radius)) {
                float f2 = -radius;
                while (true) {
                    float x = f2;
                    if (x <= radius) {
                        float f3 = -radius;
                        while (true) {
                            float z = f3;
                            if (z <= radius) {
                                BlockPos pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                                Block block = mc.theWorld.getBlockState(pos).getBlock();
                                if (mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ()) >= mc.playerController.getBlockReachDistance() || ((!(block instanceof BlockChest) && !(block instanceof BlockFurnace) && !(block instanceof BlockBrewingStand)) || list.contains(pos))) {
                                    f3 = z + 1.0f;
                                } else {
                                    float[] rotations = RotationUtil.getBlockRotations(pos.getX(), pos.getY(), pos.getZ());
                                    Client.Instance.getRotationManager().setRotation(new Vector2f(rotations[0], rotations[1]), 360.0f, true);
                                    C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(pos, 1, mc.thePlayer.getCurrentEquippedItem(), 0.0f, 0.0f, 0.0f);
                                    mc.thePlayer.sendQueue.addToSendQueue(packet);
                                    PacketUtil.sendPacket(new C0APacketAnimation());
                                    this.globalPos = pos;
                                    this.delayAfterOpenTimer.reset();
                                    return;
                                }
                            }
                        }
                    }
                    f2 = x + 1.0f;
                }
            } else {
                return;
            }
        }
    }

    @EventTarget
    public void onPostMotion(MotionEvent event) {
        GuiScreen guiScreen = mc.currentScreen;
        if (!mc.thePlayer.isOnLadder() && !(guiScreen instanceof GuiChest) && !Gapple.eating && event.isPost() && KillAura.target == null && !mc.thePlayer.isUsingItem() && !((Scaffold) getModule(Scaffold.class)).getState() && !((Blink) getModule(Blink.class)).getState() && !Gapple.eating && isWaitingOpen) {
            if (this.waitBoxOpenTimer.hasTimeElapsed(600.0d)) {
                isWaitingOpen = false;
            } else if (this.globalPos != null && (mc.thePlayer.openContainer instanceof ContainerChest)) {
                list.add(this.globalPos);
                this.globalPos = null;
                isWaitingOpen = false;
            }
        }
    }

    @EventTarget
    public void onPlace(PlaceEvent event) {
        GuiScreen guiScreen = mc.currentScreen;
        if (!mc.thePlayer.isOnLadder() && !(guiScreen instanceof GuiChest) && !Gapple.eating && ((Stealer) Client.Instance.getModuleManager().getModule(Stealer.class)).getState() && !((Scaffold) Client.Instance.getModuleManager().getModule(Scaffold.class)).getState() && this.globalPos != null && !(mc.currentScreen instanceof GuiContainer) && list.size() < 50 && !isWaitingOpen && !list.contains(this.globalPos)) {
            PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
            event.setShouldRightClick(false);
            list.add(this.globalPos);
        }
    }

    @EventTarget
    public void onRender(Render3DEvent event) {
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if ((tileEntity instanceof TileEntityChest) || (tileEntity instanceof TileEntityBrewingStand) || (tileEntity instanceof TileEntityFurnace)) {
                Color color = list.contains(tileEntity.getPos()) ? new Color(255, 0, 0, 60) : new Color(25, 255, 0, 120);
                if (mc.thePlayer.getDistance(tileEntity.getPos()) < 20.0d) {
                    RenderUtil.drawBlockBox(tileEntity.getPos(), color, false);
                }
            }
        }
    }

    @EventTarget
    public void onWorld(WorldLoadEvent e2) {
        list.clear();
    }
}
