package cn.gym.module.impl.movement;

import cn.gym.Solitude;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.misc.TickEvent;
import cn.gym.events.impl.player.*;
import cn.gym.manager.RotationComponent;
import cn.gym.manager.SlotSpoofComponent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.math.MathUtils;
import cn.gym.utils.math.Rotation;
import cn.gym.utils.math.Vector2f;
import cn.gym.utils.misc.BlockUtil;
import cn.gym.utils.player.InventoryUtil;
import cn.gym.utils.player.PlaceInfo;
import cn.gym.utils.player.PlaceRotation;
import cn.gym.utils.player.RayCastUtil;
import cn.gym.utils.player.Rise.MoveUtil;
import cn.gym.utils.player.Rise.MovementFix;
import cn.gym.utils.player.Rise.PlayerUtil;
import cn.gym.utils.player.Rise.RotationUtils;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ModeValue;
import cn.gym.value.impl.NumberValue;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 15:56
 */
public class Scaffold extends Module {
    private final Animation anim = new DecelerateAnimation(250, 1.0);
    boolean idk = false;
    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web, Blocks.tnt);
    public final ModeValue modeValue = new ModeValue("Mode", "Normal", new String[]{"Normal", "Legit"});
    public static double keepYCoord;
    public final BooleanValue swing = new BooleanValue("Swing", true);
    public final BooleanValue sprintValue = new BooleanValue("Sprint", false);
    public final BooleanValue watchdogValue = new BooleanValue("Watchdog", false);
    public final BooleanValue adStrafe = new BooleanValue("ADStrafe", false);
    private static final BooleanValue keepYValue;
    public final BooleanValue tower = new BooleanValue("Tower", false);
    private final NumberValue tellyTicks = new NumberValue("TellyTicks", 2.9, 0.5, 8.0, 0.01);
    public final BooleanValue safeValue = new BooleanValue("Safe walk", false);
    public final BooleanValue telly = new BooleanValue("Telly", true);
    public final BooleanValue upValue = new BooleanValue("Up", () -> this.telly.getValue() && !keepYValue.getValue(), false);
    public boolean tip = false;
    int idkTick = 0;
    private int slot;
    private PlaceInfo data;
    protected Random rand = new Random();
    private boolean canTellyPlace;
    private int prevItem = 0;
    int towerTick = 0;

    public Scaffold() {
        super("Scaffold", Category.Movement);
    }

    @Override
    public void onEnable() {
        this.idkTick = 5;
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        this.prevItem = Scaffold.mc.thePlayer.inventory.currentItem;
        Scaffold.mc.thePlayer.setSprinting(this.sprintValue.getValue() || !this.canTellyPlace);
        Scaffold.mc.gameSettings.keyBindSprint.pressed = this.sprintValue.getValue() || !this.canTellyPlace;
        this.canTellyPlace = false;
        this.tip = false;
        this.data = null;
        this.slot = -1;
    }

    @Override
    public void onDisable() {
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        KeyBinding.setKeyBindState(Scaffold.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        if (this.adStrafe.getValue().booleanValue()) {
            if (Scaffold.mc.gameSettings.keyBindLeft.isKeyDown()) {
                Scaffold.mc.gameSettings.keyBindLeft.setPressed(false);
            } else if (Scaffold.mc.gameSettings.keyBindRight.isKeyDown()) {
                Scaffold.mc.gameSettings.keyBindRight.setPressed(false);
            }
        }
        Scaffold.mc.thePlayer.inventory.currentItem = this.prevItem;
        SlotSpoofComponent.stopSpoofing();
    }

    @EventTarget
    public void onUpdate(MotionEvent event) {
        if (this.idkTick > 0) {
            --this.idkTick;
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if ((this.upValue.getValue().booleanValue() || keepYValue.getValue().booleanValue()) && Scaffold.mc.thePlayer.onGround && MoveUtil.isMoving() && !Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
            Scaffold.mc.thePlayer.jump();
        }
    }

    @EventTarget
    private void onTick(TickEvent event) {
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        if (this.slot == -1) {
            return;
        }
        if (!this.telly.getValue().booleanValue()) {
            this.canTellyPlace = true;
        }
    }

    @EventTarget
    private void onMove(MoveEvent event) {
        if (Scaffold.mc.thePlayer.onGround && this.safeValue.getValue().booleanValue()) {
            Scaffold.mc.thePlayer.safeWalk = true;
        }
        if (this.watchdogValue.getValue().booleanValue() && this.sprintValue.getValue().booleanValue() && MoveUtil.isMoving()) {
            if (!Keyboard.isKeyDown(Scaffold.mc.gameSettings.keyBindJump.getKeyCode())) {
                if (Scaffold.mc.thePlayer.onGround) {
                    MoveUtil.strafe(event, 0.2805, MoveUtil.getDirection1());
                    Scaffold.mc.thePlayer.setSprinting(true);
                }
            } else {
                Scaffold.mc.thePlayer.setSprinting(false);
            }
        }
    }

    @EventTarget
    private void onPlace(PlaceEvent event) {
        this.slot = this.getBlockSlot();
        if (this.slot == -1) {
            return;
        }
        if (!this.telly.getValue().booleanValue()) {
            Scaffold.mc.thePlayer.setSprinting(this.sprintValue.getValue());
            Scaffold.mc.gameSettings.keyBindSprint.pressed = false;
        }
        event.setCancelled(true);
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        this.place();
        mc.sendClickBlockToController(Scaffold.mc.currentScreen == null && Scaffold.mc.gameSettings.keyBindAttack.isKeyDown() && Scaffold.mc.inGameHasFocus);
    }

    public static double getYLevel() {
        if (!keepYValue.getValue().booleanValue()) {
            return Scaffold.mc.thePlayer.posY - 1.0;
        }
        return !MoveUtil.isMoving() ? Scaffold.mc.thePlayer.posY - 1.0 : keepYCoord;
    }

    @EventTarget
    public void onSetValue(TickEvent e) {
        if (this.telly.getValue().booleanValue()) {
            if (Scaffold.mc.gameSettings.keyBindJump.pressed) {
                this.upValue.set(true);
                keepYValue.set(false);
            } else {
                this.upValue.set(false);
                keepYValue.set(true);
            }
        }
    }

    @EventTarget
    private void onMotione(MotionEvent event) {
        if (Scaffold.mc.thePlayer.onGround) {
            keepYCoord = Math.floor(Scaffold.mc.thePlayer.posY - 1.0);
        }
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        Scaffold.mc.thePlayer.inventory.currentItem = this.slot;
        SlotSpoofComponent.startSpoofing(this.prevItem);
        this.findBlock();
        if (this.telly.getValue().booleanValue()) {
            if (this.canTellyPlace && !Scaffold.mc.thePlayer.onGround && MoveUtil.isMoving()) {
                Scaffold.mc.thePlayer.setSprinting(false);
            }
            this.canTellyPlace = (double)Scaffold.mc.thePlayer.offGroundTicks >= (this.upValue.getValue() ? (double)(Scaffold.mc.thePlayer.ticksExisted % 16 == 0 ? 2 : 1) : (Double)this.tellyTicks.getValue());
        }
        if (!this.canTellyPlace) {
            return;
        }
        if (this.data != null) {
            float yaw = RotationUtils.calculate(Scaffold.getVec3(this.data.getBlockPos(), this.data.getEnumFacing())).getX();
            float pitch = RotationUtils.calculate(Scaffold.getVec3(this.data.getBlockPos(), this.data.getEnumFacing())).getY();
            if (this.telly.getValue().booleanValue()) {
                yaw = RotationUtils.getRotationBlock(this.data.getBlockPos())[0];
                pitch = RotationUtils.getRotationBlock(this.data.getBlockPos())[1];
            }
            if (!this.watchdogValue.getValue().booleanValue()) {
                RotationComponent.setRotations(new cn.gym.utils.math.Vector2f(yaw, pitch), 180.0f, !this.watchdogValue.getValue() ? MovementFix.NORMAL : MovementFix.OFF);
                Scaffold.mc.thePlayer.setSprinting(this.sprintValue.getValue());
            }
            if (this.idkTick != 0) {
                this.towerTick = 0;
                return;
            }
            if (this.towerTick > 0) {
                ++this.towerTick;
                if (this.towerTick > 6) {
                    this.idk1(MoveUtil.speed() * 0.05);
                }
                if (this.towerTick > 16) {
                    this.towerTick = 0;
                }
            }
            if (this.isTowering()) {
                this.towerMove();
            }
        }
    }

    public void idk1(double d) {
        float f = MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(Scaffold.mc.thePlayer.motionZ, Scaffold.mc.thePlayer.motionX)) - 90.0f);
        MoveUtil.setMotion2(d, f);
    }

    private boolean isTowering() {
        return this.tower.getValue() && Keyboard.isKeyDown(Scaffold.mc.gameSettings.keyBindJump.getKeyCode());
    }

    private void pickBlock() {
        if (this.getBlockSlot() <= 0) {
            return;
        }
        Scaffold.mc.thePlayer.inventory.currentItem = this.getBlockSlot();
    }

    @EventTarget
    private void onMotion(MotionEvent event) {
        if (event.isPre()) {
            if (this.getBlockCount() < 1) {
                return;
            }
            if (this.getBlockCount() <= 0) {
                int spoofSlot = this.getBestSpoofSlot();
                this.getBlock(spoofSlot);
            }
            if (this.slot == -1) {

                return;
            }
            Scaffold.mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack();
            if (this.data != null && this.watchdogValue.getValue().booleanValue()) {
                event.setYaw(MoveUtil.getDirection(Scaffold.mc.thePlayer.rotationYaw) - 180.0f);
                event.setPitch(Scaffold.mc.gameSettings.keyBindJump.isKeyDown() ? 85.0f : 80.0f);
                RotationUtils.setVisualRotations(MoveUtil.getDirection(Scaffold.mc.thePlayer.rotationYaw) - 180.0f, Scaffold.mc.gameSettings.keyBindJump.isKeyDown() ? 85.0f : 80.0f);
            }
        }
    }

    private void towerMove() {
        if (Scaffold.mc.thePlayer.onGround) {
            if (this.towerTick == 0 || this.towerTick == 5) {
                float f = Scaffold.mc.thePlayer.rotationYaw * ((float)Math.PI / 180);
                Scaffold.mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.2f) * 95.0 / 100.0;
                Scaffold.mc.thePlayer.motionY = 0.42f;
                Scaffold.mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.2f) * 95.0 / 100.0;
                this.towerTick = 1;
            }
        } else if (Scaffold.mc.thePlayer.motionY > -0.0784000015258789) {
            int n = (int)Math.round(Scaffold.mc.thePlayer.posY % 1.0 * 100.0);
            switch (n) {
                case 42: {
                    Scaffold.mc.thePlayer.motionY = 0.33;
                    break;
                }
                case 75: {
                    Scaffold.mc.thePlayer.motionY = 1.0 - Scaffold.mc.thePlayer.posY % 1.0;
                    this.idk = true;
                    break;
                }
                case 0: {
                    Scaffold.mc.thePlayer.motionY = -0.0784000015258789;
                }
            }
        }
    }

    private void place() {
        if (!this.canTellyPlace) {
            return;
        }
        this.slot = this.getBlockSlot();
        if (this.slot == -1) {
            return;
        }
        if (PlayerUtil.block(Scaffold.mc.thePlayer.posX, Scaffold.getYLevel(), Scaffold.mc.thePlayer.posZ) instanceof BlockAir) {
            if (this.getBlockCount() < 1) {
                return;
            }
            if (this.data != null) {
                boolean normalPlace = this.modeValue.getValue().equals("Normal") || this.sprintValue.getValue();
                if (Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, Scaffold.mc.thePlayer.getCurrentEquippedItem(), this.data.getBlockPos(), normalPlace ? this.data.getEnumFacing() : Scaffold.mc.objectMouseOver.sideHit, normalPlace ? Scaffold.getVec3(this.data.getBlockPos(), this.data.getEnumFacing()) : Scaffold.mc.objectMouseOver.hitVec)) {
                    if (this.swing.getValue().booleanValue()) {
                        Scaffold.mc.thePlayer.swingItem();
                    } else {
                        Scaffold.mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                    }
                }
            }
        }
    }

    private void findBlock() {
        boolean shouldGoDown = false;
        BlockPos blockPosition = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.getYLevel(), Scaffold.mc.thePlayer.posZ);
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(blockPosition).getBlock();
        if (BlockUtil.isValidBock(blockPosition) || this.search(blockPosition, !shouldGoDown)) {
            return;
        }
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                if (!this.search(blockPosition.add(x, 0, z), !shouldGoDown)) continue;
                return;
            }
        }
    }

    private double calcStepSize(double range) {
        double accuracy = 6.0;
        accuracy += accuracy % 2.0;
        return Math.max(range / accuracy, 0.01);
    }

    private boolean search(BlockPos blockPosition, boolean checks) {
        if (BlockUtil.isValidBock(blockPosition)) {
            return false;
        }
        Vec3 eyesPos = new Vec3(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.getEntityBoundingBox().minY + (double)Scaffold.mc.thePlayer.getEyeHeight(), Scaffold.mc.thePlayer.posZ);
        PlaceRotation placeRotation = null;
        double xzRV = 0.5;
        double yRV = 0.5;
        double xzSSV = this.calcStepSize(xzRV);
        double ySSV = this.calcStepSize(xzRV);
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = blockPosition.offset(side);
            if (!BlockUtil.isValidBock(neighbor)) continue;
            Vec3 dirVec = new Vec3(side.getDirectionVec());
            for (double xSearch = 0.5 - xzRV / 2.0; xSearch <= 0.5 + xzRV / 2.0; xSearch += xzSSV) {
                for (double ySearch = 0.5 - yRV / 2.0; ySearch <= 0.5 + yRV / 2.0; ySearch += ySSV) {
                    for (double zSearch = 0.5 - xzRV / 2.0; zSearch <= 0.5 + xzRV / 2.0; zSearch += xzSSV) {
                        Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));
                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || Scaffold.mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null)) continue;
                        double diffX = hitVec.xCoord - eyesPos.xCoord;
                        double diffY = hitVec.yCoord - eyesPos.yCoord;
                        double diffZ = hitVec.zCoord - eyesPos.zCoord;
                        double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
                        Rotation rotation = new Rotation(MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)))));
                        Vec3 rotationVector = new Vec3(RotationUtils.getVectorForRotation(rotation).xCoord, RotationUtils.getVectorForRotation(rotation).yCoord, RotationUtils.getVectorForRotation(rotation).zCoord);
                        Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4.0, rotationVector.yCoord * 4.0, rotationVector.zCoord * 4.0);
                        MovingObjectPosition obj = Scaffold.mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
                        if (obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || !obj.getBlockPos().equals(neighbor) || placeRotation != null && !(RotationComponent.getRotationDifference(rotation) < RotationComponent.getRotationDifference(placeRotation.getRotation()))) continue;
                        placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
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
        double x = (double)pos.getX() + 0.5;
        double y = (double)pos.getY() + 0.5;
        double z = (double)pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
            z += MathUtils.getRandomInRange(0.3, -0.3);
        } else {
            y += MathUtils.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtils.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    public int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() || !(Scaffold.mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock)) continue;
            return i;
        }
        return -1;
    }

    private PlaceInfo getPlaceInfo(BlockPos pos) {
        if (this.isPosSolid(pos.add(0, -1, 0))) {
            return new PlaceInfo(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0))) {
            return new PlaceInfo(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(1, 0, 0))) {
            return new PlaceInfo(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(0, 0, 1))) {
            return new PlaceInfo(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, 0, -1))) {
            return new PlaceInfo(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
        if (this.isPosSolid(pos1.add(0, -1, 0))) {
            return new PlaceInfo(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos1.add(-1, 0, 0))) {
            return new PlaceInfo(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos1.add(1, 0, 0))) {
            return new PlaceInfo(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos1.add(0, 0, 1))) {
            return new PlaceInfo(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos1.add(0, 0, -1))) {
            return new PlaceInfo(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos2 = pos.add(1, 0, 0);
        if (this.isPosSolid(pos2.add(0, -1, 0))) {
            return new PlaceInfo(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos2.add(-1, 0, 0))) {
            return new PlaceInfo(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos2.add(1, 0, 0))) {
            return new PlaceInfo(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos2.add(0, 0, 1))) {
            return new PlaceInfo(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos2.add(0, 0, -1))) {
            return new PlaceInfo(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(0, 0, 1);
        if (this.isPosSolid(pos3.add(0, -1, 0))) {
            return new PlaceInfo(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos3.add(-1, 0, 0))) {
            return new PlaceInfo(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos3.add(1, 0, 0))) {
            return new PlaceInfo(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos3.add(0, 0, 1))) {
            return new PlaceInfo(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos3.add(0, 0, -1))) {
            return new PlaceInfo(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, -1);
        if (this.isPosSolid(pos4.add(0, -1, 0))) {
            return new PlaceInfo(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos4.add(-1, 0, 0))) {
            return new PlaceInfo(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos4.add(1, 0, 0))) {
            return new PlaceInfo(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos4.add(0, 0, 1))) {
            return new PlaceInfo(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos4.add(0, 0, -1))) {
            return new PlaceInfo(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(-2, 0, 0);
        if (this.isPosSolid(pos1.add(0, -1, 0))) {
            return new PlaceInfo(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos1.add(-1, 0, 0))) {
            return new PlaceInfo(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos1.add(1, 0, 0))) {
            return new PlaceInfo(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos1.add(0, 0, 1))) {
            return new PlaceInfo(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos1.add(0, 0, -1))) {
            return new PlaceInfo(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(2, 0, 0);
        if (this.isPosSolid(pos2.add(0, -1, 0))) {
            return new PlaceInfo(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos2.add(-1, 0, 0))) {
            return new PlaceInfo(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos2.add(1, 0, 0))) {
            return new PlaceInfo(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos2.add(0, 0, 1))) {
            return new PlaceInfo(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos2.add(0, 0, -1))) {
            return new PlaceInfo(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, 2);
        if (this.isPosSolid(pos3.add(0, -1, 0))) {
            return new PlaceInfo(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos3.add(-1, 0, 0))) {
            return new PlaceInfo(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos3.add(1, 0, 0))) {
            return new PlaceInfo(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos3.add(0, 0, 1))) {
            return new PlaceInfo(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos3.add(0, 0, -1))) {
            return new PlaceInfo(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        pos.add(0, 0, -2);
        if (this.isPosSolid(pos4.add(0, -1, 0))) {
            return new PlaceInfo(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos4.add(-1, 0, 0))) {
            return new PlaceInfo(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos4.add(1, 0, 0))) {
            return new PlaceInfo(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos4.add(0, 0, 1))) {
            return new PlaceInfo(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos4.add(0, 0, -1))) {
            return new PlaceInfo(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, -1, 0);
        if (this.isPosSolid(pos5.add(0, -1, 0))) {
            return new PlaceInfo(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos5.add(-1, 0, 0))) {
            return new PlaceInfo(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos5.add(1, 0, 0))) {
            return new PlaceInfo(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos5.add(0, 0, 1))) {
            return new PlaceInfo(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos5.add(0, 0, -1))) {
            return new PlaceInfo(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos6 = pos5.add(1, 0, 0);
        if (this.isPosSolid(pos6.add(0, -1, 0))) {
            return new PlaceInfo(pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos6.add(-1, 0, 0))) {
            return new PlaceInfo(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos6.add(1, 0, 0))) {
            return new PlaceInfo(pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos6.add(0, 0, 1))) {
            return new PlaceInfo(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos6.add(0, 0, -1))) {
            return new PlaceInfo(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos7 = pos5.add(-1, 0, 0);
        if (this.isPosSolid(pos7.add(0, -1, 0))) {
            return new PlaceInfo(pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos7.add(-1, 0, 0))) {
            return new PlaceInfo(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos7.add(1, 0, 0))) {
            return new PlaceInfo(pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos7.add(0, 0, 1))) {
            return new PlaceInfo(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos7.add(0, 0, -1))) {
            return new PlaceInfo(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos8 = pos5.add(0, 0, 1);
        if (this.isPosSolid(pos8.add(0, -1, 0))) {
            return new PlaceInfo(pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos8.add(-1, 0, 0))) {
            return new PlaceInfo(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos8.add(1, 0, 0))) {
            return new PlaceInfo(pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos8.add(0, 0, 1))) {
            return new PlaceInfo(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos8.add(0, 0, -1))) {
            return new PlaceInfo(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos9 = pos5.add(0, 0, -1);
        if (this.isPosSolid(pos9.add(0, -1, 0))) {
            return new PlaceInfo(pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos9.add(-1, 0, 0))) {
            return new PlaceInfo(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos9.add(1, 0, 0))) {
            return new PlaceInfo(pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos9.add(0, 0, 1))) {
            return new PlaceInfo(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos9.add(0, 0, -1))) {
            return new PlaceInfo(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    private boolean isPosSolid(BlockPos pos) {
        Block block = Scaffold.mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isVisuallyOpaque() || block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow || block instanceof BlockSkull) && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
    }

    public int getBlockCount() {
        int n = 0;
        for (int i = 36; i < 45; ++i) {
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack stack = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = stack.getItem();
            if (!(stack.getItem() instanceof ItemBlock) || !this.isValid(item)) continue;
            n += stack.stackSize;
        }
        return n;
    }

    private boolean isValid(Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock)item).getBlock());
    }

    private float getYaw() {
        if (Scaffold.mc.gameSettings.keyBindBack.isKeyDown()) {
            return Scaffold.mc.thePlayer.rotationYaw;
        }
        if (Scaffold.mc.gameSettings.keyBindLeft.isKeyDown()) {
            return Scaffold.mc.thePlayer.rotationYaw + 90.0f;
        }
        if (Scaffold.mc.gameSettings.keyBindRight.isKeyDown()) {
            return Scaffold.mc.thePlayer.rotationYaw - 90.0f;
        }
        return Scaffold.mc.thePlayer.rotationYaw - 180.0f;
    }

    private void getBlock(int switchSlot) {
        for (int i = 9; i < 45; ++i) {
            ItemBlock block;
            ItemStack is;
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || Scaffold.mc.currentScreen != null && !(Scaffold.mc.currentScreen instanceof GuiInventory) || !((is = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemBlock) || !this.isValid(block = (ItemBlock)is.getItem())) continue;
            if (36 + switchSlot == i) break;
            InventoryUtil.swap(i, switchSlot);
            break;
        }
    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; ++i) {
            if (Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            spoofSlot = i - 36;
            break;
        }
        return spoofSlot;
    }

    public int getSlot() {
        return this.slot;
    }

    static {
        keepYValue = new BooleanValue("Keep Y", false);
    }
}
