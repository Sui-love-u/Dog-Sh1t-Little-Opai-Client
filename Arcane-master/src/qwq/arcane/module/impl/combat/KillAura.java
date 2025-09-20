package qwq.arcane.module.impl.combat;

import com.viaversion.viarewind.protocol.v1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import de.florianmichael.viamcp.fixes.AttackOrder;
import io.netty.buffer.ByteBuf;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.player.AttackEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.movement.Sprint;
import qwq.arcane.module.impl.player.Blink;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.utils.math.MathUtils;
import qwq.arcane.utils.math.Vector2f;
import qwq.arcane.utils.pack.BlinkComponent;
import qwq.arcane.utils.player.BlinkUtils;
import qwq.arcane.utils.player.PlayerUtil;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.utils.rotation.RotationUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.MultiBooleanValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/combat/KillAura.class */
public class KillAura extends Module {
    public ModeValue modeValue;
    public NumberValue switchdelay;
    public NumberValue max;
    public NumberValue min;
    public BoolValue keepsprint;
    public BoolValue autoblock;
    public NumberValue blockrange;
    public final ModeValue blockmode;
    public BoolValue rotation;
    public NumberValue Rotationrange;
    public NumberValue rotationspeed;
    private final ModeValue rotationmode;
    public BoolValue movefix;
    public BoolValue strictValue;
    private final ModeValue priority;
    public MultiBooleanValue sorttargets;
    private final MultiBooleanValue auraESP;
    private final ColorValue customColor;
    public List<EntityLivingBase> targets;
    public static EntityLivingBase target;
    public EntityLivingBase blockTarget;
    public EntityLivingBase rotationTarget;
    public boolean blocking;
    public TimerUtil switchTimer;
    public TimerUtil attacktimer;
    private int index;
    private int cps;
    private Entity auraESPTarget;
    private final Animation auraESPAnim;
    private final ScaledResolution sr;
    public static NumberValue range = new NumberValue("Range", 3.0d, 1.0d, 6.0d, 0.1d);
    public static BoolValue rayCastValue = new BoolValue("RayCast", false);
    public static BoolValue noscaffold = new BoolValue("NoScaffold", false);

    public KillAura() {
        super("KillAura", Category.Combat);
        this.modeValue = new ModeValue("AttackMode", "Switch", new String[]{"Single", "Multi", "Switch"});
        this.switchdelay = new NumberValue("SwitchDelay", () -> {
            return this.modeValue.getValue().equals("Switch");
        }, 10.0d, 1.0d, 20.0d, 1.0d);
        this.max = new NumberValue("MaxDelay", 10.0d, 1.0d, 20.0d, 1.0d);
        this.min = new NumberValue("MinDelay", 10.0d, 1.0d, 20.0d, 1.0d);
        this.keepsprint = new BoolValue("KeepSprint", false);
        this.autoblock = new BoolValue("AutoBlock", false);
        this.blockrange = new NumberValue("BlockRange", () -> {
            return this.autoblock.get().booleanValue();
        }, 3.0d, 1.0d, 6.0d, 0.1d);
        this.blockmode = new ModeValue("BlockMode", () -> {
            return this.autoblock.get().booleanValue();
        }, "Fake", new String[]{"Fake", "Grim", "Blink", "LEGIT", "Interact"});
        this.rotation = new BoolValue("Rotation", false);
        this.Rotationrange = new NumberValue("RotationRange", () -> {
            return this.rotation.get().booleanValue();
        }, 3.0d, 1.0d, 6.0d, 0.1d);
        this.rotationspeed = new NumberValue("RotationSpeed", () -> {
            return this.rotation.get().booleanValue();
        }, 180.0d, 1.0d, 180.0d, 1.0d);
        this.rotationmode = new ModeValue("RotationMode", () -> {
            return this.rotation.get().booleanValue();
        }, "Normal", new String[]{"Normal", "HvH", "Smart"});
        this.movefix = new BoolValue("MoveFix", false);
        this.strictValue = new BoolValue("FollowTarget", () -> {
            return this.movefix.getValue().booleanValue();
        }, false);
        this.priority = new ModeValue("Priority", "Range", new String[]{"Range", "Armor", "Health", "HurtTime"});
        this.sorttargets = new MultiBooleanValue("Targets", Arrays.asList(new BoolValue("Animals", false), new BoolValue("Players", true), new BoolValue("Mobs", false), new BoolValue("Dead", false), new BoolValue("Invisible", false), new BoolValue("Teams", false)));
        this.auraESP = new MultiBooleanValue("TargetHUD ESP", Arrays.asList(new BoolValue("Circle", true), new BoolValue("Tracer", false), new BoolValue("Box", false), new BoolValue("Custom Color", false)));
        this.customColor = new ColorValue("Custom Color", Color.WHITE);
        this.targets = new ArrayList();
        this.switchTimer = new TimerUtil();
        this.attacktimer = new TimerUtil();
        this.auraESPAnim = new DecelerateAnimation(300, 1.0d);
        this.sr = new ScaledResolution(mc);
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        StopAutoBlock();
        this.blocking = false;
        this.index = 0;
        this.cps = 0;
        this.switchTimer.reset();
        this.attacktimer.reset();
        this.targets.clear();
        target = null;
        this.blockTarget = null;
        this.rotationTarget = null;
        super.onEnable();
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        StopAutoBlock();
        this.blocking = false;
        this.index = 0;
        this.cps = 0;
        this.switchTimer.reset();
        this.attacktimer.reset();
        this.targets.clear();
        target = null;
        this.blockTarget = null;
        this.rotationTarget = null;
        super.onDisable();
    }

    @EventTarget
    public void onWorldLoad(WorldLoadEvent event) {
        setState(false);
    }

    @EventTarget
    public void UpdateEvent(UpdateEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        setsuffix(this.modeValue.get());
        this.targets = setTargets();
        if (!this.targets.isEmpty()) {
            if (this.switchTimer.hasTimeElapsed((long) (this.switchdelay.get().doubleValue() * 100.0d)) && this.targets.size() > 1) {
                this.index++;
                this.switchTimer.reset();
            }
            if (this.index >= this.targets.size()) {
                this.index = 0;
                this.switchTimer.reset();
            }
            if (this.attacktimer.delay(this.cps)) {
                if (this.keepsprint.get().booleanValue()) {
                    Sprint.keepSprinting = true;
                }
                if (!mc.gameSettings.keyBindUseItem.isKeyDown() && this.autoblock.get().booleanValue() && this.blockmode.is("LEGIT")) {
                    switch (this.modeValue.get()) {
                        case "Multi":
                            mc.playerController.attackEntity(mc.thePlayer, (Entity) this.targets);
                            break;
                        case "Single":
                            target = this.targets.get(0);
                            attack(target);
                            break;
                        case "Switch":
                            target = this.targets.get(this.index);
                            attack(target);
                            break;
                    }
                }
                int maxValue = (int) ((this.min.getMax() - this.max.getValue().doubleValue()) * 20.0d);
                int minValue = (int) ((this.min.getMin() - this.min.getValue().doubleValue()) * 20.0d);
                this.cps = MathUtils.getRandomInRange(minValue, maxValue);
                this.attacktimer.reset();
            }
        } else {
            Sprint.keepSprinting = false;
            this.index = 0;
            this.cps = 0;
            this.switchTimer.reset();
            this.attacktimer.reset();
            this.targets.clear();
            target = null;
        }
        if (this.rotation.get().booleanValue()) {
            this.rotationTarget = findClosestEntity(this.Rotationrange.get().doubleValue());
            if (this.rotationTarget != null) {
                onRotation(this.rotationTarget);
            }
        }
    }

    @EventTarget
    public void onPostMotion(MotionEvent event) {
        if (event.isPre()) {
        }
        if (this.keepsprint.get().booleanValue()) {
            Sprint.keepSprinting = false;
        }
        if (this.autoblock.get().booleanValue()) {
            this.blockTarget = findClosestEntity(this.blockrange.get().doubleValue());
            if (this.blockTarget != null) {
                onAutoBlock();
            } else {
                StopAutoBlock();
            }
        }
        if (event.isPost() && !this.targets.isEmpty() && target != null && this.targets.size() > 1) {
            switch (this.priority.get()) {
                case "Armor":
                    this.targets.sort(Comparator.comparingInt((v0) -> {
                        return v0.getTotalArmorValue();
                    }));
                    break;
                case "Range":
                    targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
                    break;
                case "Health":
                    this.targets.sort(Comparator.comparingDouble((v0) -> {
                        return v0.getHealth();
                    }));
                    break;
                case "HurtTime":
                    this.targets.sort(Comparator.comparingInt(entity -> {
                        return entity.hurtTime;
                    }));
                    break;
            }
        }
    }

    public EntityLivingBase findClosestEntity(double range2) {
        EntityLivingBase closest = null;
        double minDistance = Double.MAX_VALUE;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entity;
                if (setTarget(living)) {
                    double distance = mc.thePlayer.getDistanceToEntity(living);
                    if (distance <= range2 && distance < minDistance) {
                        minDistance = distance;
                        closest = living;
                    }
                }
            }
        }
        return closest;
    }

    public void attack(Entity entity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (shouldAttack()) {
            AttackEvent event = new AttackEvent(entity);
            Client.Instance.getEventManager().call(event);
            AttackOrder.sendFixedAttack(mc.thePlayer, entity);
        }
    }

    public void onRotation(Entity entity) {
        float[] rotaiton = new float[0];
        if (shouldRotation(entity)) {
            switch (this.rotationmode.get()) {
                case "Smart":
                    rotaiton = RotationUtil.getAngles(target);
                    break;
                case "Normal":
                    Vector2f vec = RotationUtil.calculate(target, true, range.getValue().doubleValue() + 0.20000000298023224d, range.getValue().doubleValue() + 0.2d, true, true);
                    rotaiton = new float[]{vec.x, vec.y};
                    break;
                case "HvH":
                    rotaiton = RotationUtil.getHVHRotation(entity, this.Rotationrange.getValue().doubleValue());
                    break;
            }
            Client.Instance.getRotationManager().setRotation(new Vector2f(rotaiton[0], rotaiton[1]), this.rotationspeed.get().intValue(), this.movefix.get().booleanValue(), this.strictValue.getValue().booleanValue());
        }
    }

    public void onAutoBlock() {
        if (shouldAutoBlock(this.blockTarget)) {
            switch (this.blockmode.get()) {
                case "Grim":
                    PacketWrapper useItem = PacketWrapper.create(29, (ByteBuf) null, (UserConnection) Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItem.write(Types.VAR_INT, 1);
                    useItem.sendToServer(Protocol1_9To1_8.class);
                    PacketWrapper useItem2 = PacketWrapper.create(29, (ByteBuf) null, (UserConnection) Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItem2.write(Types.VAR_INT, 0);
                    useItem2.sendToServer(Protocol1_9To1_8.class);
                    mc.gameSettings.keyBindUseItem.pressed = true;
                    this.blocking = true;
                    break;
                case "Fake":
                    this.blocking = true;
                    break;
                case "LEGIT":
                    if (mc.thePlayer.ticksExisted % 4 == 0) {
                        mc.gameSettings.keyBindUseItem.setPressed(true);
                    } else {
                        mc.gameSettings.keyBindUseItem.setPressed(false);
                    }
                    this.blocking = true;
                    break;
                case "Interact":
                    KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                    break;
                case "Blink":
                    switch (mc.thePlayer.ticksExisted % 4) {
                        case 2:
                            qwq.arcane.utils.pack.PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            qwq.arcane.utils.pack.PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            BlinkComponent.blinking = false;
                            break;
                    }
                    this.blocking = true;
                    break;
            }
        }
    }

    public void StopAutoBlock() {
        if (this.blocking) {
            switch (this.blockmode.get()) {
                case "Grim":
                    mc.gameSettings.keyBindUseItem.pressed = false;
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    if (target != null) {
                        mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                    }
                    this.blocking = false;
                    return;
                case "Interact":
                case "LEGIT":
                    mc.gameSettings.keyBindUseItem.setPressed(false);
                    this.blocking = false;
                    break;
                case "Blink":
                    break;
                case "Fake":
                    this.blocking = false;
                    return;
                default:
                    return;
            }
            qwq.arcane.utils.pack.PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            BlinkComponent.blinking = false;
            BlinkUtils.stopBlink();
            this.blocking = false;
        }
    }

    public static boolean shouldAttack() {
        if (target == null) {
            return false;
        }
        if (!rayCastValue.get().booleanValue()) {
            return ((double) (mc.thePlayer.canEntityBeSeen(target) ? mc.thePlayer.getClosestDistanceToEntity(target) : mc.thePlayer.getDistanceToEntity(target))) <= range.get().doubleValue();
        }
        MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
        return !(((Scaffold) Client.Instance.getModuleManager().getModule(Scaffold.class)).getState() && noscaffold.get().booleanValue()) && ((double) mc.thePlayer.getClosestDistanceToEntity(target)) <= range.get().doubleValue() && movingObjectPosition != null && movingObjectPosition.entityHit == target;
    }

    public boolean shouldAutoBlock(EntityLivingBase target2) {
        return this.autoblock.get().booleanValue() && target2 != null && ((double) target2.getDistanceToEntity(mc.thePlayer)) <= this.blockrange.get().doubleValue() && isSword();
    }

    public boolean shouldRotation(Entity entity) {
        return this.rotation.get().booleanValue() && entity != null && ((double) entity.getDistanceToEntity(mc.thePlayer)) <= this.Rotationrange.get().doubleValue();
    }

    public boolean isSword() {
        return mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword);
    }

    public List<EntityLivingBase> setTargets() {
        this.targets.clear();
        List<EntityLivingBase> entities = new ArrayList<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase target2 = (EntityLivingBase) entity;
                if (!target2.equals(mc.thePlayer) && setTarget(target2) && mc.thePlayer.getDistanceToEntity(target2) <= range.get().doubleValue()) {
                    entities.add(target2);
                } else {
                    entities.remove(target2);
                }
            }
        }
        return entities;
    }

    public boolean setTarget(Entity entity) {
        if (((Blink) Client.Instance.getModuleManager().getModule(Blink.class)).getState()) {
            return false;
        }
        if ((this.sorttargets.isEnabled("Teams") && PlayerUtil.isInTeam(entity)) || !(entity instanceof EntityLivingBase)) {
            return false;
        }
        if ((this.sorttargets.isEnabled("Dead") || entity.isEntityAlive()) && entity != mc.thePlayer) {
            if ((!this.sorttargets.isEnabled("Invisible") && entity.isInvisible()) || !this.sorttargets.isEnabled("Players") || !(entity instanceof EntityPlayer)) {
                return (this.sorttargets.isEnabled("Mobs") && PlayerUtil.isMob(entity)) || (this.sorttargets.isEnabled("Animals") && PlayerUtil.isAnimal(entity));
            }
            if (isEnabled(AntiBot.class)) {
                if (AntiBot.isBot((EntityPlayer) entity)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @EventTarget
    public void onRender3DEvent(Render3DEvent event) {
        this.auraESPAnim.setDirection(target != null ? Direction.FORWARDS : Direction.BACKWARDS);
        if (target != null) {
            this.auraESPTarget = target;
        }
        if (this.auraESPAnim.finished(Direction.BACKWARDS)) {
            this.auraESPTarget = null;
        }
        Color color = InterFace.color(1);
        if (this.auraESP.isEnabled("Custom Color")) {
            color = this.customColor.get();
        }
        if (this.auraESPTarget != null) {
            if (this.auraESP.isEnabled("Box")) {
                RenderUtil.renderBoundingBox((EntityLivingBase) this.auraESPTarget, color, this.auraESPAnim.getOutput().floatValue());
            }
            if (this.auraESP.isEnabled("Circle")) {
                RenderUtil.drawCircle(this.auraESPTarget, event.partialTicks(), 0.75d, color.getRGB(), this.auraESPAnim.getOutput().floatValue());
            }
            if (this.auraESP.isEnabled("Tracer")) {
                RenderUtil.drawTracerLine(this.auraESPTarget, 4.0f, Color.BLACK, this.auraESPAnim.getOutput().floatValue());
                RenderUtil.drawTracerLine(this.auraESPTarget, 2.5f, color, this.auraESPAnim.getOutput().floatValue());
            }
        }
    }
}
