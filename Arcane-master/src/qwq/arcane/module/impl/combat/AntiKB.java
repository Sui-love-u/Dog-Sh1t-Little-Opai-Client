package qwq.arcane.module.impl.combat;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.fixes.AttackOrder;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldSettings;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.packet.PacketReceiveEvent;
import qwq.arcane.event.impl.events.player.AttackEvent;
import qwq.arcane.event.impl.events.player.StrafeEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.utils.math.Vector2f;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/combat/AntiKB.class */
public class AntiKB extends Module {
    private final ModeValue mode;
    private final ModeValue jumpResetMode;
    private final NumberValue jumpResetHurtTime;
    private final NumberValue jumpResetChance;
    private final NumberValue hitsUntilJump;
    private final NumberValue ticksUntilJump;
    private final BoolValue flagCheckValue;
    public NumberValue flagTicksValue;
    public NumberValue attackCountValue;
    public final NumberValue chance;
    private final BoolValue fireCheckValue;
    private final BoolValue waterCheckValue;
    private final BoolValue fallCheckValue;
    private final BoolValue consumecheck;
    private final BoolValue raycastValue;
    private boolean state;
    private int hitsCount;
    private int ticksCount;
    private boolean veloPacket;
    private boolean isFallDamage;
    private final Random random;
    private TimerUtil timer;
    private TimerUtil flagtimer;
    public boolean velocityInput;
    private boolean grim_1_17Velocity;
    private boolean attacked;
    private double reduceXZ;
    private int flags;
    private boolean reducing;
    boolean enable;

    public AntiKB() {
        super("AntiKB", Category.Combat);
        this.mode = new ModeValue("Mode", "Prediction", new String[]{"Watchdog", "Grim", "Prediction", "Jump Reset"});
        this.jumpResetMode = new ModeValue("Jump Reset Mode", () -> {
            return this.mode.is("Jump Reset");
        }, "Packet", new String[]{"Hurt Time", "Packet", "Advanced"});
        this.jumpResetHurtTime = new NumberValue("Jump Reset Hurt Time", () -> {
            return this.mode.is("Jump Reset") && (this.jumpResetMode.is("Hurt Time") || this.jumpResetMode.is("Advanced"));
        }, 9.0d, 1.0d, 10.0d, 1.0d);
        this.jumpResetChance = new NumberValue("Jump Reset Chance", () -> {
            return this.mode.is("Jump Reset") && this.jumpResetMode.is("Advanced");
        }, 100.0d, 0.0d, 100.0d, 1.0d);
        this.hitsUntilJump = new NumberValue("Hits Until Jump", () -> {
            return this.mode.is("Jump Reset") && this.jumpResetMode.is("Advanced");
        }, 2.0d, 1.0d, 10.0d, 1.0d);
        this.ticksUntilJump = new NumberValue("Ticks Until Jump", () -> {
            return this.mode.is("Jump Reset") && this.jumpResetMode.is("Advanced");
        }, 2.0d, 1.0d, 20.0d, 1.0d);
        this.flagCheckValue = new BoolValue("Flag Check", false);
        this.flagTicksValue = new NumberValue("Flag Ticks", 6.0d, 0.0d, 30.0d, 1.0d);
        this.attackCountValue = new NumberValue("Attack Counts", 12.0d, 1.0d, 16.0d, 1.0d);
        this.chance = new NumberValue("Prediction Chance", 1.0d, 0.0d, 1.0d, 0.01d);
        this.fireCheckValue = new BoolValue("FireCheck", false);
        this.waterCheckValue = new BoolValue("WaterCheck", false);
        this.fallCheckValue = new BoolValue("FallCheck", false);
        this.consumecheck = new BoolValue("ConsumableCheck", false);
        this.raycastValue = new BoolValue("Ray cast", false);
        this.hitsCount = 0;
        this.ticksCount = 0;
        this.veloPacket = false;
        this.random = new Random();
        this.timer = new TimerUtil();
        this.flagtimer = new TimerUtil();
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.velocityInput = false;
        this.attacked = false;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setsuffix(this.mode.is("Grim") ? ViaLoadingBase.getInstance().getTargetVersion().getVersion() >= 755 ? "Grim1.17+" : "Reduce" : this.mode.getValue());
        switch (this.mode.get()) {
            case "Watchdog":
                if (mc.thePlayer.onGround) {
                    this.state = false;
                    break;
                }
                break;
            case "Grim":
                if (this.grim_1_17Velocity) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(mc.thePlayer).up(), EnumFacing.DOWN));
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.thePlayer).up(), EnumFacing.DOWN));
                    this.grim_1_17Velocity = false;
                }
                if (this.flagCheckValue.getValue().booleanValue() && this.flags > 0) {
                    this.flags--;
                }
                if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47) {
                    if (this.velocityInput) {
                        if (this.attacked) {
                            mc.thePlayer.motionX *= this.reduceXZ;
                            mc.thePlayer.motionZ *= this.reduceXZ;
                            this.attacked = false;
                        }
                        if (mc.thePlayer.hurtTime == 0) {
                            this.velocityInput = false;
                            break;
                        }
                    }
                } else if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround) {
                    mc.thePlayer.addVelocity(-1.3E-10d, -1.3E-10d, -1.3E-10d);
                    mc.thePlayer.setSprinting(false);
                    break;
                }
                break;
            case "Jump Reset":
                if (this.jumpResetMode.is("Advanced")) {
                    if (mc.thePlayer.hurtTime == 9) {
                        this.hitsCount++;
                    }
                    this.ticksCount++;
                    break;
                }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketReceiveEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        S12PacketEntityVelocity s12;
        Packet<?> packet = event.getPacket();
        if (!this.mode.is("Grim") && (packet instanceof S12PacketEntityVelocity)) {
            S12PacketEntityVelocity s122 = (S12PacketEntityVelocity) packet;
            if (s122.getEntityID() == mc.thePlayer.getEntityId()) {
                switch (this.mode.get()) {
                    case "Jump Reset":
                        if (this.jumpResetMode.is("Packet")) {
                            this.veloPacket = true;
                            break;
                        } else if (this.jumpResetMode.is("Advanced")) {
                            double velocityX = s122.getMotionX() / 8000.0d;
                            double velocityY = s122.getMotionY() / 8000.0d;
                            double velocityZ = s122.getMotionZ() / 8000.0d;
                            this.isFallDamage = velocityX == 0.0d && velocityZ == 0.0d && velocityY < 0.0d;
                            break;
                        }
                        break;
                    case "Watchdog":
                        if (!mc.thePlayer.onGround && !this.state) {
                            event.setCancelled(true);
                            this.state = true;
                            return;
                        } else {
                            s122.motionX = (int) (mc.thePlayer.motionX * 8000.0d);
                            s122.motionZ = (int) (mc.thePlayer.motionZ * 8000.0d);
                            break;
                        }
                }
            }
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            this.flagtimer.reset();
            if (this.flagCheckValue.getValue().booleanValue()) {
                this.flags = this.flagTicksValue.getValue().intValue();
            }
        }
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            if (this.mode.is("Grim")) {
                if (this.flags != 0 || mc.thePlayer.isDead || (mc.currentScreen instanceof GuiGameOver) || mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR || mc.thePlayer.isOnLadder()) {
                    return;
                }
                if (mc.thePlayer.isBurning() && this.fireCheckValue.getValue().booleanValue()) {
                    return;
                }
                if (mc.thePlayer.isInWater() && this.waterCheckValue.getValue().booleanValue()) {
                    return;
                }
                if (mc.thePlayer.fallDistance > 1.5d && this.fallCheckValue.getValue().booleanValue()) {
                    return;
                }
                if (this.flagCheckValue.getValue().booleanValue() && !this.flagtimer.hasTimeElapsed(1000L)) {
                    return;
                }
                if ((mc.thePlayer.isEatingOrDrinking() && this.consumecheck.getValue().booleanValue()) || soulSandCheck()) {
                    return;
                }
            }
            if (((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                s12 = (S12PacketEntityVelocity) event.getPacket();
                this.attacked = false;
                switch (this.mode.getValue()) {
                    case "Grim":
                        if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() >= 755) {
                            event.setCancelled(true);
                            this.grim_1_17Velocity = true;
                            break;
                        } else {
                            double horizontalStrength = new Vector2f(s12.getMotionX(), s12.getMotionZ()).length();
                            if (horizontalStrength <= 1000.0d) {
                                return;
                            }
                            MovingObjectPosition mouse = mc.objectMouseOver;
                            this.velocityInput = true;
                            Entity entity = null;
                            this.reduceXZ = 1.0d;
                            if (mouse.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && (mouse.entityHit instanceof EntityLivingBase) && mc.thePlayer.getClosestDistanceToEntity(mouse.entityHit) <= 3.0f) {
                                entity = mouse.entityHit;
                            }
                            if (entity == null && !this.raycastValue.getValue().booleanValue()) {
                                Entity target = KillAura.target;
                                if (target != null && KillAura.shouldAttack()) {
                                    entity = KillAura.target;
                                }
                            }
                            boolean state = mc.thePlayer.serverSprintState;
                            if (entity != null) {
                                if (!state) {
                                    PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                                }
                                Client.Instance.getEventManager().call(new AttackEvent(entity));
                                int count = this.attackCountValue.get().intValue();
                                for (int i = 1; i <= count; i++) {
                                    AttackOrder.sendFixedAttackByPacket(entity);
                                }
                                if (!state) {
                                    PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                                }
                                this.attacked = true;
                                this.reduceXZ = 0.07776d;
                                break;
                            }
                        }
                        break;
                }
            }
        }
        if ((packet instanceof S27PacketExplosion) && ViaLoadingBase.getInstance().getTargetVersion().getVersion() >= 755) {
            event.setCancelled(true);
            this.grim_1_17Velocity = true;
        }
    }

    public static boolean soulSandCheck() {
        AxisAlignedBB par1AxisAlignedBB = Mine.getMinecraft().thePlayer.getEntityBoundingBox().contract(0.001d, 0.001d, 0.001d);
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0d);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0d);
        int var8 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var9 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0d);
        for (int var11 = var4; var11 < var5; var11++) {
            for (int var12 = var6; var12 < var7; var12++) {
                for (int var13 = var8; var13 < var9; var13++) {
                    BlockPos pos = new BlockPos(var11, var12, var13);
                    Block var14 = Mine.getMinecraft().theWorld.getBlockState(pos).getBlock();
                    if (var14 instanceof BlockSoulSand) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if (this.mode.is("Jump Reset")) {
            boolean shouldJump = false;
            if (this.jumpResetMode.is("Packet") && this.veloPacket) {
                shouldJump = true;
            } else if (this.jumpResetMode.is("Hurt Time") && mc.thePlayer.hurtTime >= this.jumpResetHurtTime.getValue().doubleValue()) {
                shouldJump = true;
            } else if (this.jumpResetMode.is("Advanced")) {
                if (this.random.nextInt(100) > this.jumpResetChance.getValue().doubleValue()) {
                    return;
                }
                boolean hitsCondition = ((double) this.hitsCount) >= this.hitsUntilJump.getValue().doubleValue();
                boolean ticksCondition = ((double) this.ticksCount) >= this.ticksUntilJump.getValue().doubleValue();
                shouldJump = mc.thePlayer.hurtTime == 9 && mc.thePlayer.isSprinting() && !this.isFallDamage && (hitsCondition || ticksCondition);
            }
            if (shouldJump && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !checks()) {
                mc.thePlayer.jump();
                this.veloPacket = false;
                this.hitsCount = 0;
                this.ticksCount = 0;
            }
        }
    }

    @EventTarget
    public void onUpdates(UpdateEvent event) {
        if (this.mode.is("Prediction")) {
            Mine mine = mc;
            if (Mine.getCurrentScreen() != null || KillAura.target == null) {
                return;
            }
            if (mc.thePlayer.hurtTime == 10) {
                this.enable = MathHelper.getRandomDoubleInRange(new Random(), 0.0d, 1.0d) <= this.chance.getValue().doubleValue();
            }
            if (this.enable) {
                if (mc.thePlayer.hurtTime >= 8) {
                    mc.gameSettings.keyBindJump.pressed = true;
                } else if (mc.thePlayer.hurtTime > 6) {
                    mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
                    mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
                }
            }
        }
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        if (this.mode.is("Prediction")) {
            mc.gameSettings.keyBindJump.pressed = false;
            mc.gameSettings.keyBindForward.pressed = false;
        }
    }

    @EventTarget
    public void onWorldEvent(WorldLoadEvent event) {
        if (this.mode.is("Prediction")) {
            mc.gameSettings.keyBindJump.pressed = false;
            mc.gameSettings.keyBindForward.pressed = false;
        }
    }

    private boolean checks() {
        return mc.thePlayer.isInWeb || mc.thePlayer.isInLava() || mc.thePlayer.isBurning() || mc.thePlayer.isInWater();
    }
}
