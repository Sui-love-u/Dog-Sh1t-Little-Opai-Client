package qwq.arcane.module.impl.world;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.StrafeEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.movement.Sprint;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.color.ColorUtil;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.math.Vector2f;
import qwq.arcane.utils.player.MovementUtil;
import qwq.arcane.utils.player.PlaceData;
import qwq.arcane.utils.player.ScaffoldUtil;
import qwq.arcane.utils.player.SlotSpoofComponent;
import qwq.arcane.utils.render.BlockUtil;
import qwq.arcane.utils.render.PlaceInfo;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.rotation.RotationUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/world/Scaffold.class */
public class Scaffold extends Module {
    public ModeValue mode;
    private final NumberValue minTellyTicks;
    private final NumberValue maxTellyTicks;
    public final BoolValue swing;
    public final BoolValue sprint;
    public BoolValue rotation;
    public NumberValue rotationspeed;
    public ModeValue modeValue;
    public static BoolValue rayCastValue = new BoolValue("RayCast", true);
    public BoolValue movefix;
    public final BoolValue esp;
    public PlaceData data;
    public BlockPos previousBlock;
    public int slot;
    private int prevItem;
    private TimerUtil timerUtil;
    private double onGroundY;
    private float[] smoothRotation;
    private boolean canPlace;
    private int tellyTicks;
    private float[] previousRotation;
    private boolean tellyStage;
    private float[] rotations;
    private static final float NORMAL_PITCH = 82.5f;

    public Scaffold() {
        super("Scaffold", Category.World);
        this.mode = new ModeValue("Mode", "Normal", new String[]{"Normal", "Telly"});
        this.minTellyTicks = new NumberValue("Min Telly Ticks", () -> {
            return this.mode.is("Telly");
        }, 2.0d, 1.0d, 5.0d, 1.0d);
        this.maxTellyTicks = new NumberValue("Max Telly Ticks", () -> {
            return this.mode.is("Telly");
        }, 4.0d, 1.0d, 5.0d, 1.0d);
        this.swing = new BoolValue("Swing", true);
        this.sprint = new BoolValue("sprint", true);
        this.rotation = new BoolValue("Rotation", true);
        this.rotationspeed = new NumberValue("RotationSpeed", () -> {
            return this.rotation.get().booleanValue();
        }, 180.0d, 1.0d, 360.0d, 1.0d);
        this.modeValue = new ModeValue("RotationMode", "Normal", new String[]{"Normal", "Telly"});
        this.movefix = new BoolValue("MoveFix", true);
        this.esp = new BoolValue("ESP", true);
        this.prevItem = 0;
        this.timerUtil = new TimerUtil();
        this.smoothRotation = new float[]{0.0f, 85.0f};
        this.canPlace = true;
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.timerUtil.reset();
        if (mc.thePlayer != null) {
            this.prevItem = mc.thePlayer.inventory.currentItem;
            this.onGroundY = mc.thePlayer.getEntityBoundingBox().minY;
            this.previousRotation = new float[]{mc.thePlayer.rotationYaw + 180.0f, 82.0f};
        }
        this.slot = -1;
        if (this.mode.is("Telly")) {
            this.smoothRotation = new float[]{mc.thePlayer.rotationYaw, 85.0f};
        }
        this.canPlace = true;
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        this.timerUtil.reset();
        this.tellyTicks = 0;
        this.rotations = null;
        this.previousRotation = null;
        mc.thePlayer.inventory.currentItem = this.prevItem;
        SlotSpoofComponent.stopSpoofing();
    }

    @EventTarget
    public void Tickevent(UpdateEvent event) {
        this.slot = getBlockSlot();
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if (mc.thePlayer.onGround && this.mode.is("Telly") && !mc.thePlayer.isJumping && MovementUtil.isMoving()) {
            this.tellyStage = !this.tellyStage;
            mc.thePlayer.jump();
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        this.slot = getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        mc.thePlayer.inventory.currentItem = this.slot;
        SlotSpoofComponent.startSpoofing(this.prevItem);
        if (this.sprint.get().booleanValue()) {
            Sprint.keepSprinting = true;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        } else {
            Sprint.keepSprinting = false;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
            mc.thePlayer.setSprinting(false);
        }
        if (this.mode.is("Telly") && mc.thePlayer.onGround) {
            this.tellyTicks = MathUtils.randomizeInt(this.minTellyTicks.getValue().intValue(), this.maxTellyTicks.getValue().intValue());
        }
        this.data = null;
        if (mc.thePlayer.onGround) {
            this.onGroundY = mc.thePlayer.getEntityBoundingBox().minY;
        }
        double posY = mc.thePlayer.getEntityBoundingBox().minY;
        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
            posY = this.onGroundY;
        }
        double posX = mc.thePlayer.posX;
        double posZ = mc.thePlayer.posZ;
        this.previousBlock = new BlockPos(posX, posY, posZ).offset(EnumFacing.DOWN);
        this.data = ScaffoldUtil.getPlaceData(this.previousBlock);
        this.canPlace = (this.mode.is("Telly") && mc.thePlayer.offGroundTicks >= this.tellyTicks && this.data != null) || (this.mode.is("Normal") && this.data != null);
        if (!this.canPlace) {
            this.rotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        }
        place();
        if (this.mode.is("Telly") && mc.thePlayer.onGround) {
            this.tellyTicks = MathUtils.randomizeInt(this.minTellyTicks.getValue().intValue(), this.maxTellyTicks.getValue().intValue());
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        float yaw;
        setsuffix(String.valueOf(this.mode.get()));
        if ((this.data != null && this.rotation.get().booleanValue() && this.mode.is("Normal")) || (this.mode.is("Telly") && this.canPlace && this.rotation.get().booleanValue())) {
            switch (this.modeValue.get()) {
                case "Normal":
                    this.rotations = RotationUtil.getRotations(getVec3(this.data));
                    break;
                case "Telly":
                    float yaw2 = MovementUtil.getRawDirection();
                    float pitch = RotationUtil.getRotations(getVec3(this.data))[1];
                    boolean shouldTurn = this.mode.is("Telly") ? this.tellyStage : mc.thePlayer.offGroundTicks % 8 < 4;
                    if (shouldTurn) {
                        yaw = yaw2 + mc.thePlayer.rotationYaw + 180.0f + RandomUtils.nextInt(0, 5);
                    } else {
                        yaw = yaw2 - ((mc.thePlayer.rotationYaw + 180.0f) + RandomUtils.nextInt(0, 5));
                    }
                    this.rotations = new float[]{yaw, pitch};
                    break;
            }
        }
        if (this.canPlace) {
            Client.Instance.getRotationManager().setRotation(new Vector2f(this.rotations[0], this.rotations[1]), this.rotationspeed.get().intValue(), this.movefix.get().booleanValue());
        }
    }

    private void place() {
        if (this.slot >= 0 && this.data != null) {
            if (rayCastValue.get().booleanValue()) {
                MovingObjectPosition ray = Client.Instance.getRotationManager().rayTrace(mc.playerController.getBlockReachDistance(), 1.0f);
                if (ray != null && ray.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.data.getBlockPos(), this.data.getFacing(), getVec3(this.data))) {
                    if (this.swing.getValue().booleanValue()) {
                        mc.thePlayer.swingItem();
                        return;
                    } else {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        return;
                    }
                }
                return;
            }
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.data.getBlockPos(), this.data.getFacing(), getVec3(this.data))) {
                if (this.swing.getValue().booleanValue()) {
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                }
            }
        }
    }

    public int getBlockSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() && (mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock)) {
                return i;
            }
        }
        return -1;
    }

    public static Vec3 getVec3(PlaceData data) {
        BlockPos pos = data.blockPos;
        EnumFacing face = data.facing;
        double x = pos.getX() + 0.5d;
        double y = pos.getY() + 0.5d;
        double z = pos.getZ() + 0.5d;
        return new Vec3(x + (face.getFrontOffsetX() / 2.0d), y + (face.getFrontOffsetY() / 2.0d), z + (face.getFrontOffsetZ() / 2.0d));
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (this.data == null) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            BlockPos blockPos = this.data.getBlockPos();
            PlaceInfo placeInfo = PlaceInfo.get(blockPos);
            if (BlockUtil.isValidBock(blockPos) && placeInfo != null && this.esp.get().booleanValue()) {
                RenderUtil.drawBlockBox(blockPos, ColorUtil.applyOpacity(InterFace.color(1), 0.5f), false);
                return;
            }
        }
    }
}
