package qwq.arcane.module.impl.player;

import java.awt.Color;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.TeleportEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.event.impl.events.render.Render2DEvent;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.animations.impl.ContinualAnimation;
import qwq.arcane.utils.math.Vector2f;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.player.PlayerUtil;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.render.RoundedUtil;
import qwq.arcane.utils.rotation.RotationUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/player/BedNuker.class */
public class BedNuker extends Module {
    private final BoolValue movefix;
    public final NumberValue breakRange;
    public final BoolValue breakSurroundings;
    public final BoolValue autoTool;
    public final BoolValue progressText;
    public final BoolValue progressBar;
    public final BoolValue whitelistOwnBed;
    public final BoolValue swap;
    public final BoolValue ignoreSlow;
    public final BoolValue groundSpoof;
    public BlockPos bedPos;
    public boolean rotate;
    private float breakProgress;
    private int delayTicks;
    private Vec3 home;
    private boolean spoofed;
    public ContinualAnimation barAnim;

    public BedNuker() {
        super("BedNuker", Category.Player);
        this.movefix = new BoolValue("Movement Fix", true);
        this.breakRange = new NumberValue("Break Range", 4.0d, 1.0d, 5.0d, 1.0d);
        this.breakSurroundings = new BoolValue("Break Top", true);
        this.autoTool = new BoolValue("Auto Tool", true);
        this.progressText = new BoolValue("Progress Text", true);
        this.progressBar = new BoolValue("Progress Bar", true);
        this.whitelistOwnBed = new BoolValue("Whitelist Own Bed", true);
        this.swap = new BoolValue("Swap", false);
        BoolValue boolValue = this.swap;
        Objects.requireNonNull(boolValue);
        this.ignoreSlow = new BoolValue("Ignore Slow", boolValue::get, false);
        BoolValue boolValue2 = this.swap;
        Objects.requireNonNull(boolValue2);
        this.groundSpoof = new BoolValue("Hypixel Ground Spoof", boolValue2::get, false);
        this.rotate = false;
        this.barAnim = new ContinualAnimation();
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.rotate = false;
        this.bedPos = null;
        this.breakProgress = 0.0f;
        super.onEnable();
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        reset(true);
        super.onDisable();
    }

    @EventTarget
    public void onTeleport(TeleportEvent event) {
        if (this.whitelistOwnBed.get().booleanValue()) {
            double distance = mc.thePlayer.getDistance(event.getPosX(), event.getPosY(), event.getPosZ());
            if (distance > 40.0d) {
                this.home = new Vec3(event.getPosX(), event.getPosY(), event.getPosZ());
            }
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setsuffix(this.swap.get().booleanValue() ? "Swap" : "Vanilla");
        if (((Scaffold) Client.Instance.getModuleManager().getModule(Scaffold.class)).isEnabled() && ((Scaffold) Client.INSTANCE.getModuleManager().getModule(Scaffold.class)).data == null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
            reset(true);
            return;
        }
        getBedPos();
        if (this.bedPos != null) {
            if (this.rotate) {
                float[] rot = RotationUtil.getRotations(this.bedPos);
                Client.INSTANCE.getRotationManager().setRotation(new Vector2f(rot[0], rot[1]), 180.0f, this.movefix.get().booleanValue());
                this.rotate = false;
            }
            mine(this.bedPos);
            return;
        }
        reset(true);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.isPost()) {
            return;
        }
        if (this.bedPos != null && this.groundSpoof.get().booleanValue() && !mc.thePlayer.onGround) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                mc.timer.timerSpeed = 0.5f;
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                this.spoofed = true;
                return;
            } else {
                mc.timer.timerSpeed = 1.0f;
                this.spoofed = false;
                return;
            }
        }
        if (this.spoofed) {
            mc.timer.timerSpeed = 1.0f;
            this.spoofed = false;
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (this.progressText.get().booleanValue() && this.bedPos != null) {
            RenderUtil.renderBlock(this.bedPos, ((InterFace) getModule(InterFace.class)).color(), true, true);
            if (this.breakProgress == 0.0f) {
                return;
            }
            mc.getRenderManager();
            double n = (this.bedPos.getX() + 0.5d) - RenderManager.viewerPosX;
            mc.getRenderManager();
            double n2 = (this.bedPos.getY() + 0.5d) - RenderManager.viewerPosY;
            mc.getRenderManager();
            double n3 = (this.bedPos.getZ() + 0.5d) - RenderManager.viewerPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) n, (float) n2, (float) n3);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-0.02266667f, -0.02266667f, -0.02266667f);
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            String progressStr = ((int) (100.0d * (this.breakProgress / 1.0d))) + "%";
            mc.fontRendererObj.drawString(progressStr, (-mc.fontRendererObj.getStringWidth(progressStr)) / 2, -3.0f, -1, true);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (!this.progressBar.get().booleanValue() || this.bedPos == null || this.breakProgress == 0.0f) {
            return;
        }
        ScaledResolution resolution = event.getScaledResolution();
        int x = resolution.getScaledWidth() / 2;
        int y = (resolution.getScaledHeight() / 2) + 30;
        this.barAnim.animate(100.0f * this.breakProgress, 40);
        RoundedUtil.drawRound(x - 50, y, 100.0f, 6.0f, 3.0f, new Color(((InterFace) getModule(InterFace.class)).color(), true));
        float output = this.barAnim.getOutput();
        Color color = new Color(((InterFace) getModule(InterFace.class)).color());
        RoundedUtil.drawGradientHorizontal(x - 50, y, output, 6.0f, 3.0f, color, new Color(InterFace.color(90).getRGB()));
        String progressStr = ((int) (100.0d * (this.breakProgress / 1.0d))) + "%";
        Bold.get(12.0f).drawCenteredStringWithShadow(progressStr, x, y + 1, -1);
    }

    private void getBedPos() {
        if (this.home != null && mc.thePlayer.getDistanceSq(this.home.xCoord, this.home.yCoord, this.home.zCoord) < 1225.0d && this.whitelistOwnBed.get().booleanValue()) {
            return;
        }
        this.bedPos = null;
        double range = this.breakRange.getValue().doubleValue();
        double d = mc.thePlayer.posX - range;
        while (true) {
            double x = d;
            if (x <= mc.thePlayer.posX + range) {
                double eyeHeight = (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) - range;
                while (true) {
                    double y = eyeHeight;
                    if (y <= mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + range) {
                        double d2 = mc.thePlayer.posZ - range;
                        while (true) {
                            double z = d2;
                            if (z <= mc.thePlayer.posZ + range) {
                                BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
                                if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockBed) || mc.theWorld.getBlockState(pos).getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD) {
                                    d2 = z + 1.0d;
                                } else if (this.breakSurroundings.get().booleanValue() && isBedCovered(pos)) {
                                    this.bedPos = pos.add(0, 1, 0);
                                } else {
                                    this.bedPos = pos;
                                }
                            }
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    private void mine(BlockPos blockPos) {
        if (this.delayTicks > 0) {
            this.delayTicks--;
            return;
        }
        IBlockState blockState = mc.theWorld.getBlockState(blockPos);
        if (blockState.getBlock() instanceof BlockAir) {
            return;
        }
        if (this.breakProgress == 0.0f) {
            this.rotate = true;
            if (this.autoTool.get().booleanValue() && !this.swap.get().booleanValue()) {
                doAutoTool(blockPos);
            }
            mc.thePlayer.swingItem();
            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, this.bedPos, EnumFacing.UP));
        } else {
            if (this.breakProgress >= 1.0f) {
                this.rotate = true;
                if (this.autoTool.get().booleanValue() && this.swap.get().booleanValue()) {
                    doAutoTool(blockPos);
                }
                mc.thePlayer.swingItem();
                PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.bedPos, EnumFacing.UP));
                reset(false);
                return;
            }
            if (!this.swap.get().booleanValue()) {
                this.rotate = true;
            }
            if (this.autoTool.get().booleanValue() && !this.swap.get().booleanValue()) {
                doAutoTool(blockPos);
            }
            mc.thePlayer.swingItem();
        }
        if (this.swap.get().booleanValue()) {
            this.breakProgress += getBlockHardness(this.bedPos, PlayerUtil.findTool(this.bedPos) != -1 ? mc.thePlayer.inventory.getStackInSlot(PlayerUtil.findTool(this.bedPos)) : mc.thePlayer.getHeldItem(), this.ignoreSlow.get().booleanValue(), this.groundSpoof.get().booleanValue());
        } else {
            this.breakProgress += mc.theWorld.getBlockState(this.bedPos).getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.bedPos);
        }
        mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), this.bedPos, (int) (this.breakProgress * 10.0f));
    }

    private void reset(boolean resetRotate) {
        if (this.bedPos != null) {
            mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), this.bedPos, -1);
        }
        this.breakProgress = 0.0f;
        this.delayTicks = 5;
        this.bedPos = null;
        this.rotate = !resetRotate;
    }

    private void doAutoTool(BlockPos pos) {
        if (PlayerUtil.findTool(pos) != -1) {
            mc.thePlayer.inventory.currentItem = PlayerUtil.findTool(pos);
        }
    }

    private boolean isBedCovered(BlockPos headBlockBedPos) {
        BlockPos headBlockBedPosOffSet1 = headBlockBedPos.add(1, 0, 0);
        BlockPos headBlockBedPosOffSet2 = headBlockBedPos.add(-1, 0, 0);
        BlockPos headBlockBedPosOffSet3 = headBlockBedPos.add(0, 0, 1);
        BlockPos headBlockBedPosOffSet4 = headBlockBedPos.add(0, 0, -1);
        if (!isBlockCovered(headBlockBedPos)) {
            return false;
        }
        if ((mc.theWorld.getBlockState(headBlockBedPosOffSet1).getBlock() instanceof BlockBed) && mc.theWorld.getBlockState(headBlockBedPosOffSet1).getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            return isBlockCovered(headBlockBedPosOffSet1);
        }
        if ((mc.theWorld.getBlockState(headBlockBedPosOffSet2).getBlock() instanceof BlockBed) && mc.theWorld.getBlockState(headBlockBedPosOffSet2).getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            return isBlockCovered(headBlockBedPosOffSet2);
        }
        if ((mc.theWorld.getBlockState(headBlockBedPosOffSet3).getBlock() instanceof BlockBed) && mc.theWorld.getBlockState(headBlockBedPosOffSet3).getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            return isBlockCovered(headBlockBedPosOffSet3);
        }
        if ((mc.theWorld.getBlockState(headBlockBedPosOffSet4).getBlock() instanceof BlockBed) && mc.theWorld.getBlockState(headBlockBedPosOffSet4).getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            return isBlockCovered(headBlockBedPosOffSet4);
        }
        return false;
    }

    private boolean isBlockCovered(BlockPos blockPos) {
        BlockPos[] directions = {blockPos.add(0, 1, 0), blockPos.add(1, 0, 0), blockPos.add(-1, 0, 0), blockPos.add(0, 0, 1), blockPos.add(0, 0, -1)};
        for (BlockPos pos : directions) {
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            if ((block instanceof BlockAir) || (block.getMaterial() instanceof MaterialLiquid)) {
                return false;
            }
        }
        return true;
    }

    public static float getBlockHardness(BlockPos blockPos, ItemStack itemStack, boolean ignoreSlow, boolean ignoreGround) {
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        float getBlockHardness = block.getBlockHardness(mc.theWorld, null);
        if (getBlockHardness < 0.0f) {
            return 0.0f;
        }
        return (block.getMaterial().isToolNotRequired() || (itemStack != null && itemStack.canHarvestBlock(block))) ? (getToolDigEfficiency(itemStack, block, ignoreSlow, ignoreGround) / getBlockHardness) / 30.0f : (getToolDigEfficiency(itemStack, block, ignoreSlow, ignoreGround) / getBlockHardness) / 100.0f;
    }

    public static float getToolDigEfficiency(ItemStack itemStack, Block block, boolean ignoreSlow, boolean ignoreGround) {
        float n2;
        int getEnchantmentLevel;
        float n = itemStack == null ? 1.0f : itemStack.getItem().getStrVsBlock(itemStack, block);
        if (n > 1.0f && (getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack)) > 0 && itemStack != null) {
            n += (getEnchantmentLevel * getEnchantmentLevel) + 1;
        }
        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            n *= 1.0f + ((mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2f);
        }
        if (!ignoreSlow) {
            if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
                switch (mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                    case 0:
                        n2 = 0.3f;
                        break;
                    case 1:
                        n2 = 0.09f;
                        break;
                    case 2:
                        n2 = 0.0027f;
                        break;
                    default:
                        n2 = 8.1E-4f;
                        break;
                }
                n *= n2;
            }
            if (mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) {
                n /= 5.0f;
            }
            if (!mc.thePlayer.onGround && !ignoreGround) {
                n /= 5.0f;
            }
        }
        return n;
    }
}
