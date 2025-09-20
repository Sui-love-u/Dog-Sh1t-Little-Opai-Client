package cn.gym.module.impl.combat;

import cn.gym.Solitude;
import cn.gym.events.EventManager;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.player.AttackEvent;
import cn.gym.events.impl.player.MotionEvent;
import cn.gym.events.impl.render.Render3DEvent;
import cn.gym.manager.RotationComponent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.module.impl.movement.Scaffold;
import cn.gym.module.impl.render.Interface;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.utils.math.MathUtils;
import cn.gym.utils.math.Vector2f;
import cn.gym.utils.pack.BlinkComponent;
import cn.gym.utils.pack.PacketUtil;
import cn.gym.utils.player.*;
import cn.gym.utils.player.Rise.MovementFix;
import cn.gym.utils.player.Rise.RotationUtils;
import cn.gym.utils.render.RenderUtil;
import cn.gym.utils.time.TimerUtil;
import cn.gym.value.impl.*;
import com.viaversion.viarewind.protocol.v1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_9;
import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 14:18
 */
public class KillAura extends Module {
    public KillAura() {
        super("KillAura",Category.Combat);
    }

    private final ModeValue mode = new ModeValue("AttackMode","Single",new String[]{"Single","Switch"});
    public final NumberValue switchDelayValue = new NumberValue("SwitchDelay", () -> mode.is("Switch"),15, 0, 20, 1);
    private final NumberValue maxCPS = new NumberValue("Max CPS", 18, 1, 20, 1);
    private final NumberValue minCPS = new NumberValue("Min CPS", 12, 1, 20, 1);
    public static NumberValue range = new NumberValue("Range", 3.0,  0.0, 5.0, 0.1);
    private final ModeValue priority = new ModeValue("Priority", "Health", new String[]{"Range", "Armor", "Health", "HurtTime"});
    private final BooleanValue raycase = new BooleanValue("RayCase",true);
    private final BooleanValue rotations = new BooleanValue("rotations",true);
    public ModeValue rotMode = new ModeValue("Rotation Mode",rotations::get, "Normal", new String[]{"Normal", "HvH", "CNM","New"});
    private final NumberValue RotationsSpeed = new NumberValue("RotationsSpeed", rotations::get,8, 1, 10, 1);
    private final ModeValue moveFix = new ModeValue("MovementFix","Silent",new String[]{ "Silent", "Strict", "None", "BackSprint"});
    public static BooleanValue autoblock = new BooleanValue("AutoBlock",true);
    public static ModeValue autoblockmode = new ModeValue("AutoBlockMode", autoblock::getValue,"Grim",new String[]{"Grim","Watchdog","Off"});
    private final MultiBooleanValue targetOption = new MultiBooleanValue("Targets", Arrays.asList(new BooleanValue("Players", true), new BooleanValue("Mobs", false),
            new BooleanValue("Animals", false), new BooleanValue("Invisible", true), new BooleanValue("Dead", false)));
    public final MultiBooleanValue filter = new MultiBooleanValue("Filter", Arrays.asList(new BooleanValue("Teams", true), new BooleanValue("Friends", true)));
    private final MultiBooleanValue auraESP = new MultiBooleanValue("TargetHUD ESP", Arrays.asList(
            new BooleanValue("Circle", true),
            new BooleanValue("Tracer", false),
            new BooleanValue("Box", false),
            new BooleanValue("Custom Color", false)));
    private final ColorValue customColor = new ColorValue("Custom Color", Color.WHITE);
    public List<EntityLivingBase> targets = new ArrayList<>();
    public static EntityLivingBase target;
    public boolean blocking;
    private final TimerUtil switchTimer = new TimerUtil();
    private final TimerUtil attackTimer = new TimerUtil();
    private int index;
    private final Animation auraESPAnim = new DecelerateAnimation(300, 1);
    private Entity auraESPTarget;
    int cps = 0;

    public boolean blinked;
    public int blinkTicks;

    @Override
    public void onEnable() {
        targets.clear();
        target = null;
        blocking = false;
        switchTimer.reset();
        attackTimer.reset();
        index = 0;
        cps = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (blinked) {
            BlinkComponent.dispatch();
            blinked = false;
        }
        blinkTicks = 0;
        targets.clear();
        target = null;
        attackTimer.reset();
        switchTimer.reset();
        index = 0;
        cps = 0;
        StopAutoBlock();
        blocking = false;
        super.onDisable();
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setSuffix(mode.get());
        targets = gettargets();
        if (!targets.isEmpty()) {
            if (targets.size() > 1) {
                switch (priority.get()) {
                    case "Armor":
                        targets.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
                        break;
                    case "Range":
                        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
                        break;
                    case "Health":
                        targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                        break;
                    case "HurtTime":
                        targets.sort(Comparator.comparingInt(entity -> entity.hurtTime));
                        break;
                }
            }
            if (switchTimer.hasTimeElapsed((long) (switchDelayValue.get() * 100L)) && targets.size() > 1) {
                ++index;
                switchTimer.reset();
            }
            if (index >= targets.size()) {
                index = 0;
            }
            if (preTickBlock()) return;
            if (attackTimer.hasTimeElapsed(cps, true)) {
                final int maxValue = (int) ((minCPS.getMax() - maxCPS.getValue()) * 20);
                final int minValue = (int) ((minCPS.getMax() - minCPS.getValue()) * 20);
                cps = MathUtils.getRandomInRange(minValue, maxValue);
                if (!targets.isEmpty()) {
                    switch (mode.getValue()) {
                        case "Single":
                            target = targets.get(0);
                            attack(target);

                            break;
                        case "Switch":
                            target = targets.get(index);
                            attack(target);
                            break;
                    }
                }
            }
            if (rotations.get()){
                onRotations(target);
            }
            if(event.isPre()) return;

            onAutoBlock();
            if (autoblockmode.is("Watchdog") && shouldBlock() && !blocking) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                if (!mc.isSingleplayer()) {
                    PacketWrapper blockPlace = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                    blockPlace.write(Types.VAR_INT, 1);
                    try {
                        blockPlace.sendToServer(Protocol1_9To1_8.class,true);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("AutoBlock");
                blocking = true;
            }
            if (autoblockmode.is("Watchdog") && shouldBlock()) {
                BlinkComponent.dispatch();
            }
        } else {
            if (autoblockmode.is("Watchdog") && !shouldBlock() && blocking) {
                System.out.println("STOPAutoBlock");
                PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                blocking = false;
            }
            if (blinked) {
                System.out.println("STOPAutoBlock222");
                BlinkComponent.dispatch();
                blinked = false;
            }
            blinkTicks = 0;
            targets.clear();
            target = null;
            attackTimer.reset();
            switchTimer.reset();
            index = 0;
            cps = 0;
            StopAutoBlock();
        }
    }
    public static boolean shouldBlock() {
        return target != null && mc.thePlayer.getClosestDistanceToEntity(target) <= range.getValue() + 0.3f;
    }
    public void onAutoBlock(){
        if (autoblock.get()) {
            if (isSword()) {
                switch (autoblockmode.getValue()) {
                    case "Watchdog":
                        break;

                    case "Off":
                        blocking = true;
                        break;
                }
            }
        }
    }
    public void StopAutoBlock(){
        if (autoblock.get() && blocking) {
            if (isSword()) {
                switch (autoblockmode.getValue()) {
                    case "Watchdog":
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        System.out.println("STOPAutoBlock333");

                        blocking = false;
                        break;
                    case "Off":
                        blocking = false;
                        break;
                }
            }
        }
    }
    private boolean preTickBlock() {
        if (autoblockmode.is("Watchdog")) {
            if (blinkTicks >= 3) {
                blinkTicks = 0;
            }
            blinkTicks++;
            switch (blinkTicks) {
                case 0:
                    return true;
                case 1:
                    if (blocking) {
//                        DebugUtil.log("1");
                        BlinkComponent.blinking = true;
                        StopAutoBlock();
                        blinked = true;
                        return true;
                    }
                case 2:
                    return false;
            }
        }
        return false;
    }

    public boolean shouldAttack() {
        if (raycase.get()) {
            final MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
            if (Solitude.Instance.getModuleManager().getModule(Scaffold.class).getState()) return false;
            return (mc.thePlayer.getClosestDistanceToEntity(target) <= range.get()) && (movingObjectPosition != null && movingObjectPosition.entityHit == target);
        } else {
            return (double) (mc.thePlayer.canEntityBeSeen(target) ? mc.thePlayer.getClosestDistanceToEntity(target) : mc.thePlayer.getDistanceToEntity(target)) <= range.get();
        }
    }

    private void onRotations(Entity target){
        float[] rotation = getRot();
        RotationComponent.setRotations(new Vector2f(rotation[0], rotation[1]), RotationsSpeed.getValue(),getMovementFixType());
    }

    private MovementFix getMovementFixType() {
        return switch (moveFix.getValue()) {
            case "None" -> MovementFix.OFF;
            case "Silent" -> MovementFix.NORMAL;
            case "Strict" -> MovementFix.TRADITIONAL;
            case "BackSprint" -> MovementFix.BACKWARDS_SPRINT;
            default -> throw new IllegalStateException("Unexpected value: " + moveFix.getValue());
        };
    }
    private float[] getRot() {
        float[] rot = RotationUtils.getHVHRotation(target, range.getValue());
        switch (rotMode.getValue()) {
            case "New":
                RotationUtils.getNewRotation(target);
                break;
            case "Normal":
                Vector2f vec = RotationUtils.calculate(target, true, range.getValue() + 0.2f, range.getValue() + 0.2, true, true);
                rot = new float[]{vec.x, vec.y};
                break;
            case "CNM":
                rot = RotationUtils.getAngles(target);
                break;
            case "HvH":
                rot = RotationUtils.getHVHRotation(target, range.getValue());
                break;
        }
        return rot;
    }

    public List<EntityLivingBase> gettargets(){
        targets.clear();
        final List<EntityLivingBase> entities = new ArrayList<>();
        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase) entity;
                if (!target.equals(Minecraft.getMinecraft().thePlayer) && isValid(target) && target.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) <= range.get()) {
                    entities.add(target);
                }else entities.remove(target);
            }
        }
        return entities;
    }

    public void attack(Entity entity){
        if (shouldAttack()) {
            AttackEvent attackEvent = new AttackEvent(entity);
            Solitude.Instance.getEventManager().call(attackEvent);
            AttackOrder.sendFixedAttack(mc.thePlayer,entity);
        }
    }

    public boolean isValid(Entity entity) {
        if ((filter.isEnabled("Teams") && PlayerUtil.isInTeam(entity))) {
            return false;
        }
        if (entity instanceof EntityLivingBase && (targetOption.isEnabled("Dead") || entity.isEntityAlive()) && entity != mc.thePlayer) {
            if (targetOption.isEnabled("Invisible") || !entity.isInvisible()) {
                if (targetOption.isEnabled("Players") && entity instanceof EntityPlayer) {
                    if (filter.isEnabled("Friends") && Solitude.Instance.getFriendManager().isFriend((EntityPlayer) entity))
                        return false;
                    return !isEnabled(AntiBot.class) || !getModule(AntiBot.class).isBot((EntityPlayer) entity);
                }
            }
            return (targetOption.isEnabled("Mobs") && PlayerUtil.isMob(entity)) || (targetOption.isEnabled("Animals") && PlayerUtil.isAnimal(entity));
        }
        return false;
    }

    @EventTarget
    public void onRender3DEvent(Render3DEvent event) {
        auraESPAnim.setDirection(KillAura.target != null ? Direction.FORWARDS : Direction.BACKWARDS);
        if (KillAura.target != null) {
            auraESPTarget = KillAura.target;
        }

        if (auraESPAnim.finished(Direction.BACKWARDS)) {
            auraESPTarget = null;
        }

        Color color = Solitude.Instance.getModuleManager().getModule(Interface.class).FirstColor.get();

        if (auraESP.isEnabled("Custom Color")) {
            color = customColor.get();
        }
        if (auraESPTarget != null) {
            if (auraESP.isEnabled("Box")) {
                RenderUtil.renderBoundingBox((EntityLivingBase) auraESPTarget, color, auraESPAnim.getOutput().floatValue());
            }
            if (auraESP.isEnabled("Circle")) {
                RenderUtil.drawCircle(this.auraESPTarget, event.partialTicks(), 0.75, color.getRGB(), this.auraESPAnim.getOutput().floatValue());
            }
            if (auraESP.isEnabled("Tracer")) {
                RenderUtil.drawTracerLine(auraESPTarget, 4f, Color.BLACK, auraESPAnim.getOutput().floatValue());
                RenderUtil.drawTracerLine(auraESPTarget, 2.5f, color, auraESPAnim.getOutput().floatValue());
            }
        }
    }

    private int getItemIndex() {
        final InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
        return inventoryPlayer.currentItem;
    }

    public ItemStack getItemStack() {
        return (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null ? null : mc.thePlayer.inventoryContainer.getSlot(getItemIndex() + 36).getStack());
    }
    public void interact(MovingObjectPosition mouse) {
        if (!mc.playerController.isPlayerRightClickingOnEntity(mc.thePlayer, mouse.entityHit, mouse)) {
            mc.playerController.interactWithEntitySendPacket(mc.thePlayer, mouse.entityHit);
        }
    }
    public boolean isSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }
}
