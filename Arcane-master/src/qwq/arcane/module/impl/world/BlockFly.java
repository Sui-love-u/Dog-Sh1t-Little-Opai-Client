package qwq.arcane.module.impl.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.TickEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.PlaceEvent;
import qwq.arcane.event.impl.events.player.StrafeEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.math.Vector2f;
import qwq.arcane.utils.player.InventoryUtil;
import qwq.arcane.utils.player.MovementUtil;
import qwq.arcane.utils.player.PlaceRotation;
import qwq.arcane.utils.player.PlayerUtil;
import qwq.arcane.utils.player.Rotation;
import qwq.arcane.utils.player.SlotSpoofComponent;
import qwq.arcane.utils.render.BlockUtil;
import qwq.arcane.utils.render.PlaceInfo;
import qwq.arcane.utils.rotation.RotationUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/world/BlockFly.class */
public class BlockFly extends Module {
    private final Animation anim;
    boolean idk;
    public static double keepYCoord;
    public final BoolValue swing;
    public final BoolValue sprintValue;
    private final NumberValue tellyTicks;
    public final BoolValue eagle;
    public final BoolValue telly;
    public final BoolValue upValue;
    public boolean tip;
    private int direction;
    int idkTick;
    private int slot;
    private PlaceInfo data;
    protected Random rand;
    private boolean canTellyPlace;
    private int prevItem;
    int towerTick;
    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web, Blocks.tnt);
    private static final BoolValue keepYValue = new BoolValue("Keep Y", false);

    public BlockFly() {
        super("BlockFly", Category.World);
        this.anim = new DecelerateAnimation(250, 1.0d);
        this.idk = false;
        this.swing = new BoolValue("Swing", true);
        this.sprintValue = new BoolValue("Sprint", false);
        this.tellyTicks = new NumberValue("TellyTicks", 2.9d, 0.5d, 8.0d, 0.01d);
        this.eagle = new BoolValue("Eagle", false);
        this.telly = new BoolValue("Telly", true);
        this.upValue = new BoolValue("Up", () -> {
            return this.telly.getValue().booleanValue() && !keepYValue.getValue().booleanValue();
        }, false);
        this.tip = false;
        this.idkTick = 0;
        this.rand = new Random();
        this.prevItem = 0;
        this.towerTick = 0;
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        if (mc.thePlayer != null && mc.gameSettings != null) {
            this.idkTick = 5;
            this.prevItem = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.setSprinting(this.sprintValue.getValue().booleanValue() || !this.canTellyPlace);
            mc.gameSettings.keyBindSprint.pressed = this.sprintValue.getValue().booleanValue() || !this.canTellyPlace;
            this.canTellyPlace = false;
            this.tip = false;
            this.data = null;
            this.slot = -1;
        }
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        if (mc.thePlayer == null) {
            return;
        }
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        mc.thePlayer.inventory.currentItem = this.prevItem;
        SlotSpoofComponent.stopSpoofing();
    }

    @EventTarget
    public void onUpdate(MotionEvent event) {
        setsuffix(String.valueOf(this.tellyTicks.get()));
        if (this.idkTick > 0) {
            this.idkTick--;
        }
        if (event.isPre() && this.eagle.getValue().booleanValue() && mc.thePlayer.onGround) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if ((this.upValue.getValue().booleanValue() || keepYValue.getValue().booleanValue()) && mc.thePlayer.onGround && MovementUtil.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.jump();
        }
    }

    @EventTarget
    private void onTick(TickEvent event) {
        if (mc.thePlayer != null && this.slot >= 0 && !this.telly.getValue().booleanValue()) {
            this.canTellyPlace = true;
        }
    }

    @EventTarget
    private void onPlace(PlaceEvent event) {
        this.slot = getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        if (!this.telly.getValue().booleanValue()) {
            mc.thePlayer.setSprinting(this.sprintValue.getValue().booleanValue());
            mc.gameSettings.keyBindSprint.pressed = false;
        }
        event.setCancelled(true);
        place();
        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
    }

    public static double getYLevel() {
        if (keepYValue.getValue().booleanValue()) {
            return !MovementUtil.isMoving() ? mc.thePlayer.posY - 1.0d : keepYCoord;
        }
        return mc.thePlayer.posY - 1.0d;
    }

    @EventTarget
    public void onSetValue(TickEvent e) {
        if (this.telly.getValue().booleanValue()) {
            if (mc.gameSettings.keyBindJump.pressed) {
                this.upValue.set(true);
                keepYValue.set(false);
            } else {
                this.upValue.set(false);
                keepYValue.set(true);
            }
        }
    }

    @EventTarget
    private void onUpdateMotionEvent(UpdateEvent event) {
        double dDoubleValue;
        if (mc.thePlayer.onGround) {
            keepYCoord = Math.floor(mc.thePlayer.posY - 1.0d);
        }
        this.slot = getBlockSlot();
        if (this.slot < 1) {
            return;
        }
        findBlock();
        mc.thePlayer.inventory.currentItem = this.slot;
        SlotSpoofComponent.startSpoofing(this.prevItem);
        if (this.telly.getValue().booleanValue()) {
            if (this.canTellyPlace && !mc.thePlayer.onGround && MovementUtil.isMoving()) {
                mc.thePlayer.setSprinting(false);
            }
            double d = mc.thePlayer.offGroundTicks;
            if (this.upValue.getValue().booleanValue()) {
                dDoubleValue = mc.thePlayer.ticksExisted % 16 == 0 ? 2 : 1;
            } else {
                dDoubleValue = this.tellyTicks.getValue().doubleValue();
            }
            this.canTellyPlace = d >= dDoubleValue;
        }
        if (this.canTellyPlace && this.data != null) {
            float yaw = RotationUtil.getRotationBlock2(this.data.getBlockPos())[0];
            float pitch = RotationUtil.getRotationBlock2(this.data.getBlockPos())[1];
            Client.Instance.getRotationManager().setRotation(new Vector2f(yaw, pitch), 180.0f, true);
            mc.thePlayer.setSprinting(this.sprintValue.getValue().booleanValue());
            if (this.idkTick != 0) {
                this.towerTick = 0;
                return;
            }
            if (this.towerTick > 0) {
                this.towerTick++;
                if (this.towerTick > 6) {
                    idk1(MovementUtil.speed() * 0.05d);
                }
                if (this.towerTick > 16) {
                    this.towerTick = 0;
                }
            }
        }
    }

    public void idk1(double d) {
        float f = MathHelper.wrapAngleTo180_float(((float) Math.toDegrees(Math.atan2(mc.thePlayer.motionZ, mc.thePlayer.motionX))) - 90.0f);
        MovementUtil.setMotion2(d, f);
    }

    @EventTarget
    private void onMotion(MotionEvent event) {
        if (!event.isPre() || getBlockCount() < 1) {
            return;
        }
        if (getBlockCount() <= 0) {
            int spoofSlot = getBestSpoofSlot();
            getBlock(spoofSlot);
        }
        if (this.slot < 0) {
            return;
        }
        mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack();
    }

    private void towerMove() {
        if (mc.thePlayer.onGround) {
            if (this.towerTick == 0 || this.towerTick == 5) {
                float f = mc.thePlayer.rotationYaw * 0.017453292f;
                mc.thePlayer.motionX -= ((MathHelper.sin(f) * 0.2f) * 95.0d) / 100.0d;
                mc.thePlayer.motionY = 0.41999998688697815d;
                mc.thePlayer.motionZ += ((MathHelper.cos(f) * 0.2f) * 95.0d) / 100.0d;
                this.towerTick = 1;
                return;
            }
            return;
        }
        if (mc.thePlayer.motionY > -0.0784000015258789d) {
            int n = (int) Math.round((mc.thePlayer.posY % 1.0d) * 100.0d);
            switch (n) {
                case 0:
                    mc.thePlayer.motionY = -0.0784000015258789d;
                    break;
                case 42:
                    mc.thePlayer.motionY = 0.33d;
                    break;
                case 75:
                    mc.thePlayer.motionY = 1.0d - (mc.thePlayer.posY % 1.0d);
                    this.idk = true;
                    break;
            }
        }
    }

    private void place() {
        if (!this.canTellyPlace) {
            return;
        }
        this.slot = getBlockSlot();
        if (this.slot >= 0 && (PlayerUtil.block(mc.thePlayer.posX, getYLevel(), mc.thePlayer.posZ) instanceof BlockAir) && getBlockCount() >= 1 && this.data != null) {
            boolean normalPlace = this.sprintValue.getValue().booleanValue();
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.data.getBlockPos(), normalPlace ? this.data.getEnumFacing() : mc.objectMouseOver.sideHit, normalPlace ? getVec3(this.data.getBlockPos(), this.data.getEnumFacing()) : mc.objectMouseOver.hitVec)) {
                if (this.swing.getValue().booleanValue()) {
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                }
            }
        }
    }

    private void findBlock() {
        BlockPos blockPosition = new BlockPos(mc.thePlayer.posX, getYLevel(), mc.thePlayer.posZ);
        Mine.getMinecraft().theWorld.getBlockState(blockPosition).getBlock();
        if (BlockUtil.isValidBock(blockPosition)) {
            return;
        }
        if (search(blockPosition, 0 == 0)) {
            return;
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (search(blockPosition.add(x, 0, z), 0 == 0)) {
                    return;
                }
            }
        }
    }

    private double calcStepSize(double range) {
        double accuracy = 6.0d + (6.0d % 2.0d);
        return Math.max(range / accuracy, 0.01d);
    }

    private boolean search(BlockPos blockPosition, boolean checks) {
        if (BlockUtil.isValidBock(blockPosition)) {
            return false;
        }
        Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        PlaceRotation placeRotation = null;
        double xzSSV = calcStepSize(0.5d);
        double ySSV = calcStepSize(0.5d);
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = blockPosition.offset(side);
            if (BlockUtil.isValidBock(neighbor)) {
                Vec3 dirVec = new Vec3(side.getDirectionVec());
                double d = 0.5d - (0.5d / 2.0d);
                while (true) {
                    double xSearch = d;
                    if (xSearch <= 0.5d + (0.5d / 2.0d)) {
                        double d2 = 0.5d - (0.5d / 2.0d);
                        while (true) {
                            double ySearch = d2;
                            if (ySearch <= 0.5d + (0.5d / 2.0d)) {
                                double d3 = 0.5d - (0.5d / 2.0d);
                                while (true) {
                                    double zSearch = d3;
                                    if (zSearch <= 0.5d + (0.5d / 2.0d)) {
                                        Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                                        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                                        Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5d, dirVec.yCoord * 0.5d, dirVec.zCoord * 0.5d));
                                        if (!checks || (eyesPos.squareDistanceTo(hitVec) <= 18.0d && distanceSqPosVec <= eyesPos.squareDistanceTo(posVec.add(dirVec)) && mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) == null)) {
                                            double diffX = hitVec.xCoord - eyesPos.xCoord;
                                            double diffY = hitVec.yCoord - eyesPos.yCoord;
                                            double diffZ = hitVec.zCoord - eyesPos.zCoord;
                                            double diffXZ = MathHelper.sqrt_double((diffX * diffX) + (diffZ * diffZ));
                                            Rotation rotation = new Rotation(MathHelper.wrapAngleTo180_float(((float) Math.toDegrees(Math.atan2(diffZ, diffX))) - 90.0f), MathHelper.wrapAngleTo180_float((float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)))));
                                            Vec3 rotationVector = new Vec3(RotationUtil.getVectorForRotation(rotation).xCoord, RotationUtil.getVectorForRotation(rotation).yCoord, RotationUtil.getVectorForRotation(rotation).zCoord);
                                            Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4.0d, rotationVector.yCoord * 4.0d, rotationVector.zCoord * 4.0d);
                                            MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
                                            if (obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor) && (placeRotation == null || Client.Instance.getRotationManager().getRotationDifference(rotation) < Client.Instance.getRotationManager().getRotationDifference(placeRotation.getRotation()))) {
                                                placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                                            }
                                        }
                                        d3 = zSearch + xzSSV;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (placeRotation == null) {
            return false;
        }
        this.data = placeRotation.getPlaceInfo();
        return true;
    }

    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x = pos.getX() + 0.5d;
        double y = pos.getY() + 0.5d;
        double z = pos.getZ() + 0.5d;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtils.getRandomInRange(0.3d, -0.3d);
            z += MathUtils.getRandomInRange(0.3d, -0.3d);
        } else {
            y += MathUtils.getRandomInRange(0.3d, -0.3d);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtils.getRandomInRange(0.3d, -0.3d);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtils.getRandomInRange(0.3d, -0.3d);
        }
        return new Vec3(x, y, z);
    }

    public int getBlockSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() && (mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock)) {
                return i;
            }
        }
        return -1;
    }

    private PlaceInfo getPlaceInfo(BlockPos pos) {
        if (isPosSolid(pos.add(0, -1, 0))) {
            return new PlaceInfo(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos.add(-1, 0, 0))) {
            return new PlaceInfo(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos.add(1, 0, 0))) {
            return new PlaceInfo(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos.add(0, 0, 1))) {
            return new PlaceInfo(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos.add(0, 0, -1))) {
            return new PlaceInfo(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
        if (isPosSolid(pos1.add(0, -1, 0))) {
            return new PlaceInfo(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos1.add(-1, 0, 0))) {
            return new PlaceInfo(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos1.add(1, 0, 0))) {
            return new PlaceInfo(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos1.add(0, 0, 1))) {
            return new PlaceInfo(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos1.add(0, 0, -1))) {
            return new PlaceInfo(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos2 = pos.add(1, 0, 0);
        if (isPosSolid(pos2.add(0, -1, 0))) {
            return new PlaceInfo(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos2.add(-1, 0, 0))) {
            return new PlaceInfo(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos2.add(1, 0, 0))) {
            return new PlaceInfo(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos2.add(0, 0, 1))) {
            return new PlaceInfo(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos2.add(0, 0, -1))) {
            return new PlaceInfo(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(0, 0, 1);
        if (isPosSolid(pos3.add(0, -1, 0))) {
            return new PlaceInfo(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos3.add(-1, 0, 0))) {
            return new PlaceInfo(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos3.add(1, 0, 0))) {
            return new PlaceInfo(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos3.add(0, 0, 1))) {
            return new PlaceInfo(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos3.add(0, 0, -1))) {
            return new PlaceInfo(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, -1);
        if (isPosSolid(pos4.add(0, -1, 0))) {
            return new PlaceInfo(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos4.add(-1, 0, 0))) {
            return new PlaceInfo(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos4.add(1, 0, 0))) {
            return new PlaceInfo(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos4.add(0, 0, 1))) {
            return new PlaceInfo(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos4.add(0, 0, -1))) {
            return new PlaceInfo(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(-2, 0, 0);
        if (isPosSolid(pos1.add(0, -1, 0))) {
            return new PlaceInfo(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos1.add(-1, 0, 0))) {
            return new PlaceInfo(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos1.add(1, 0, 0))) {
            return new PlaceInfo(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos1.add(0, 0, 1))) {
            return new PlaceInfo(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos1.add(0, 0, -1))) {
            return new PlaceInfo(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(2, 0, 0);
        if (isPosSolid(pos2.add(0, -1, 0))) {
            return new PlaceInfo(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos2.add(-1, 0, 0))) {
            return new PlaceInfo(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos2.add(1, 0, 0))) {
            return new PlaceInfo(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos2.add(0, 0, 1))) {
            return new PlaceInfo(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos2.add(0, 0, -1))) {
            return new PlaceInfo(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, 2);
        if (isPosSolid(pos3.add(0, -1, 0))) {
            return new PlaceInfo(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos3.add(-1, 0, 0))) {
            return new PlaceInfo(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos3.add(1, 0, 0))) {
            return new PlaceInfo(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos3.add(0, 0, 1))) {
            return new PlaceInfo(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos3.add(0, 0, -1))) {
            return new PlaceInfo(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, -2);
        if (isPosSolid(pos4.add(0, -1, 0))) {
            return new PlaceInfo(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos4.add(-1, 0, 0))) {
            return new PlaceInfo(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos4.add(1, 0, 0))) {
            return new PlaceInfo(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos4.add(0, 0, 1))) {
            return new PlaceInfo(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos4.add(0, 0, -1))) {
            return new PlaceInfo(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, -1, 0);
        if (isPosSolid(pos5.add(0, -1, 0))) {
            return new PlaceInfo(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos5.add(-1, 0, 0))) {
            return new PlaceInfo(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos5.add(1, 0, 0))) {
            return new PlaceInfo(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos5.add(0, 0, 1))) {
            return new PlaceInfo(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos5.add(0, 0, -1))) {
            return new PlaceInfo(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos6 = pos5.add(1, 0, 0);
        if (isPosSolid(pos6.add(0, -1, 0))) {
            return new PlaceInfo(pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos6.add(-1, 0, 0))) {
            return new PlaceInfo(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos6.add(1, 0, 0))) {
            return new PlaceInfo(pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos6.add(0, 0, 1))) {
            return new PlaceInfo(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos6.add(0, 0, -1))) {
            return new PlaceInfo(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos7 = pos5.add(-1, 0, 0);
        if (isPosSolid(pos7.add(0, -1, 0))) {
            return new PlaceInfo(pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos7.add(-1, 0, 0))) {
            return new PlaceInfo(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos7.add(1, 0, 0))) {
            return new PlaceInfo(pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos7.add(0, 0, 1))) {
            return new PlaceInfo(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos7.add(0, 0, -1))) {
            return new PlaceInfo(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos8 = pos5.add(0, 0, 1);
        if (isPosSolid(pos8.add(0, -1, 0))) {
            return new PlaceInfo(pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos8.add(-1, 0, 0))) {
            return new PlaceInfo(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos8.add(1, 0, 0))) {
            return new PlaceInfo(pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos8.add(0, 0, 1))) {
            return new PlaceInfo(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos8.add(0, 0, -1))) {
            return new PlaceInfo(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos9 = pos5.add(0, 0, -1);
        if (isPosSolid(pos9.add(0, -1, 0))) {
            return new PlaceInfo(pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos9.add(-1, 0, 0))) {
            return new PlaceInfo(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos9.add(1, 0, 0))) {
            return new PlaceInfo(pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos9.add(0, 0, 1))) {
            return new PlaceInfo(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos9.add(0, 0, -1))) {
            return new PlaceInfo(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    private boolean isPosSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return ((!block.getMaterial().isSolid() && block.isTranslucent() && !block.isVisuallyOpaque() && !(block instanceof BlockLadder) && !(block instanceof BlockCarpet) && !(block instanceof BlockSnow) && !(block instanceof BlockSkull)) || block.getMaterial().isLiquid() || (block instanceof BlockContainer)) ? false : true;
    }

    public int getBlockCount() {
        int n = 0;
        for (int i = 36; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = stack.getItem();
                if ((stack.getItem() instanceof ItemBlock) && isValid(item)) {
                    n += stack.stackSize;
                }
            }
        }
        return n;
    }

    private boolean isValid(Item item) {
        return (item instanceof ItemBlock) && !invalidBlocks.contains(((ItemBlock) item).getBlock());
    }

    private float getYaw() {
        if (mc.gameSettings.keyBindBack.isKeyDown()) {
            return mc.thePlayer.rotationYaw;
        }
        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            return mc.thePlayer.rotationYaw + 90.0f;
        }
        if (mc.gameSettings.keyBindRight.isKeyDown()) {
            return mc.thePlayer.rotationYaw - 90.0f;
        }
        return mc.thePlayer.rotationYaw - 180.0f;
    }

    private void getBlock(int switchSlot) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && (mc.currentScreen == null || (mc.currentScreen instanceof GuiInventory))) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if ((is.getItem() instanceof ItemBlock) && isValid((ItemBlock) is.getItem())) {
                    if (36 + switchSlot != i) {
                        InventoryUtil.swap(i, switchSlot);
                        return;
                    }
                    return;
                }
            }
        }
    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;
        int i = 36;
        while (true) {
            if (i < 45) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    i++;
                } else {
                    spoofSlot = i - 36;
                    break;
                }
            } else {
                break;
            }
        }
        return spoofSlot;
    }

    public int getSlot() {
        return this.slot;
    }
}
