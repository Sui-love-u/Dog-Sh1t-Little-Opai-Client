package qwq.arcane.module.impl.combat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.LWJGLException;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.utils.chats.ChatUtils;
import qwq.arcane.utils.rotation.RayCastUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/combat/TickBase.class */
public class TickBase extends Module {
    public static TickBase INSTANCE;
    private final ModeValue mode;
    private final NumberValue minDistance;
    private final NumberValue maxDistance;
    private final ModeValue rangeMode;
    private final NumberValue maxTimeValue;
    private final NumberValue delayValue;
    private final NumberValue maxHurtTimeValue;
    private final BoolValue onlyKillAura;
    private final BoolValue auraClick;
    private final BoolValue onlyPlayer;
    private final BoolValue debug;
    private final BoolValue betterAnimation;
    private final BoolValue reverseValue;
    private final NumberValue maxReverseRange;
    private final NumberValue minReverseRange;
    private final NumberValue reverseTime;
    private final NumberValue reverseTickTime;
    private final NumberValue reverseDelay;
    private final NumberValue reverseTargetMaxHurtTime;
    private KillAura killAura;
    private static boolean working = false;
    private static boolean stopWorking = false;
    private static double lastNearest = 10.0d;
    private static int cooldown = 0;
    private static int freezeTicks = 0;
    private static boolean reverseFreeze = true;
    private static boolean firstAnimation = true;

    public TickBase() {
        super("TickBase", Category.Combat);
        this.mode = new ModeValue("Mode", "RayCast", new String[]{"RayCast", "Radius"});
        this.minDistance = new NumberValue("MinDistance", 3.0d, 0.0d, 4.0d, 0.1d);
        this.maxDistance = new NumberValue("MaxDistance", 4.0d, 3.0d, 7.0d, 0.1d);
        this.rangeMode = new ModeValue("RangeMode", "Smart", new String[]{"Setting", "Smart"});
        this.maxTimeValue = new NumberValue("MaxTime", 3.0d, 0.0d, 20.0d, 1.0d);
        this.delayValue = new NumberValue("Delay", 5.0d, 0.0d, 20.0d, 1.0d);
        this.maxHurtTimeValue = new NumberValue("TargetMaxHurtTime", 2.0d, 0.0d, 10.0d, 1.0d);
        this.onlyKillAura = new BoolValue("OnlyKillAura", true);
        this.auraClick = new BoolValue("AuraClick", true);
        this.onlyPlayer = new BoolValue("OnlyPlayer", true);
        this.debug = new BoolValue("Debug", false);
        this.betterAnimation = new BoolValue("BetterAnimation", true);
        this.reverseValue = new BoolValue("Reverse", false);
        this.maxReverseRange = new NumberValue("MaxReverseRange", 2.8d, 1.0d, 4.0d, 0.1d);
        this.minReverseRange = new NumberValue("MinReverseRange", 2.5d, 1.0d, 4.0d, 0.1d);
        this.reverseTime = new NumberValue("ReverseStopTime", 3.0d, 1.0d, 10.0d, 1.0d);
        this.reverseTickTime = new NumberValue("ReverseTickTime", 3.0d, 0.0d, 10.0d, 1.0d);
        this.reverseDelay = new NumberValue("ReverseDelay", 5.0d, 0.0d, 20.0d, 1.0d);
        this.reverseTargetMaxHurtTime = new NumberValue("ReverseTargetMaxHurtTime", 3.0d, 0.0d, 10.0d, 1.0d);
        INSTANCE = this;
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.killAura = (KillAura) getModule(KillAura.class);
    }

    @EventTarget
    public void onMotion(MotionEvent event) throws IllegalAccessException, IllegalArgumentException, LWJGLException, InvocationTargetException {
        if (event.isPre()) {
            return;
        }
        Mine mc = Mine.getMinecraft();
        EntityPlayer thePlayer = mc.thePlayer;
        if (!this.onlyKillAura.get().booleanValue() || this.killAura.getState()) {
            if (this.mode.get().equals("RayCast")) {
                Entity entity = RayCastUtil.raycastEntity(this.maxDistance.get().doubleValue() + 1.0d, new RayCastUtil.IEntityFilter() { // from class: qwq.arcane.module.impl.combat.TickBase.1
                    @Override // qwq.arcane.utils.rotation.RayCastUtil.IEntityFilter
                    public boolean canRaycast(Entity entity2) {
                        return entity2 != null && (entity2 instanceof EntityLivingBase) && (!TickBase.this.onlyPlayer.get().booleanValue() || (entity2 instanceof EntityPlayer));
                    }
                });
                if (entity == null || !(entity instanceof EntityLivingBase)) {
                    lastNearest = 10.0d;
                    return;
                }
                Vec3 vecEyes = thePlayer.getPositionEyes(1.0f);
                Vec3 predictEyes = this.rangeMode.get().equals("Smart") ? thePlayer.getPositionEyes((float) (this.maxTimeValue.get().doubleValue() + 1.0d)) : thePlayer.getPositionEyes(3.0f);
                AxisAlignedBB entityBox = entity.getEntityBoundingBox().expands(entity.getCollisionBorderSize(), true, true);
                Vec3 box = EntityPlayerSP.getNearestPointBB(vecEyes, entityBox);
                Vec3 box2 = EntityPlayerSP.getNearestPointBB(predictEyes, entity instanceof EntityOtherPlayerMP ? entityBox.offset(((EntityOtherPlayerMP) entity).getOtherPlayerMPX() - entity.posX, ((EntityOtherPlayerMP) entity).getOtherPlayerMPY() - entity.posY, ((EntityOtherPlayerMP) entity).getOtherPlayerMPZ() - entity.posZ) : entityBox);
                double range = box.distanceTo(vecEyes);
                if (this.killAura.setTarget(entity)) {
                    double afterRange = box2.distanceTo(predictEyes);
                    if (!working && this.reverseValue.get().booleanValue() && range <= this.maxReverseRange.get().doubleValue() && range >= this.minReverseRange.get().doubleValue() && cooldown <= 0 && ((EntityLivingBase) entity).hurtTime <= this.reverseTargetMaxHurtTime.get().doubleValue()) {
                        freezeTicks = this.reverseTime.get().intValue();
                        firstAnimation = false;
                        reverseFreeze = true;
                        return;
                    }
                    if (range < this.minDistance.get().doubleValue()) {
                        stopWorking = true;
                    } else if (((this.rangeMode.get().equals("Smart") && range > this.minDistance.get().doubleValue() && afterRange < this.minDistance.get().doubleValue() && afterRange < range) || (this.rangeMode.get().equals("Setting") && range <= this.maxDistance.get().doubleValue() && range < lastNearest && afterRange < range)) && ((EntityLivingBase) entity).hurtTime <= this.maxHurtTimeValue.get().doubleValue()) {
                        stopWorking = false;
                        foundTarget();
                    }
                    lastNearest = range;
                    return;
                }
                return;
            }
            List<Entity> entityList = mc.theWorld.getEntitiesWithinAABBExcludingEntity(thePlayer, thePlayer.getEntityBoundingBox().expands(this.maxDistance.get().doubleValue() + 1.0d, true, true));
            if (!entityList.isEmpty()) {
                Vec3 vecEyes2 = thePlayer.getPositionEyes(1.0f);
                Vec3 afterEyes = this.rangeMode.get().equals("Smart") ? thePlayer.getPositionEyes((float) (this.maxTimeValue.get().doubleValue() + 1.0d)) : thePlayer.getPositionEyes(3.0f);
                boolean targetFound = false;
                boolean targetInRange = false;
                double nearest = 10.0d;
                Iterator<Entity> it = entityList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Entity entity2 = it.next();
                    if ((entity2 instanceof EntityLivingBase) && (!this.onlyPlayer.get().booleanValue() || (entity2 instanceof EntityPlayer))) {
                        AxisAlignedBB entityBox2 = entity2.getEntityBoundingBox().expands(entity2.getCollisionBorderSize(), true, true);
                        Vec3 box3 = EntityPlayerSP.getNearestPointBB(vecEyes2, entityBox2);
                        Vec3 box22 = EntityPlayerSP.getNearestPointBB(afterEyes, entity2 instanceof EntityOtherPlayerMP ? entityBox2.offset(((EntityOtherPlayerMP) entity2).getOtherPlayerMPX() - entity2.posX, ((EntityOtherPlayerMP) entity2).getOtherPlayerMPY() - entity2.posY, ((EntityOtherPlayerMP) entity2).getOtherPlayerMPZ() - entity2.posZ) : entityBox2);
                        double range2 = box3.distanceTo(vecEyes2);
                        if (this.killAura.setTarget(entity2)) {
                            double afterRange2 = box22.distanceTo(afterEyes);
                            if (!working && this.reverseValue.get().booleanValue() && range2 <= this.maxReverseRange.get().doubleValue() && range2 >= this.minReverseRange.get().doubleValue() && cooldown <= 0 && ((EntityLivingBase) entity2).hurtTime <= this.reverseTargetMaxHurtTime.get().doubleValue()) {
                                freezeTicks = this.reverseTime.get().intValue();
                                firstAnimation = false;
                                reverseFreeze = true;
                                return;
                            } else {
                                if (range2 < this.minDistance.get().doubleValue()) {
                                    targetInRange = true;
                                    break;
                                }
                                if (range2 <= this.maxDistance.get().doubleValue() && afterRange2 < range2 && ((EntityLivingBase) entity2).hurtTime <= this.maxHurtTimeValue.get().doubleValue()) {
                                    targetFound = true;
                                }
                                nearest = Math.min(nearest, range2);
                            }
                        } else {
                            continue;
                        }
                    }
                }
                if (targetInRange) {
                    stopWorking = true;
                } else if (targetFound && nearest < lastNearest) {
                    stopWorking = false;
                    foundTarget();
                }
                lastNearest = nearest;
                return;
            }
            lastNearest = 10.0d;
        }
    }

    public void foundTarget() throws IllegalAccessException, IllegalArgumentException, LWJGLException, InvocationTargetException {
        if (cooldown > 0 || freezeTicks != 0 || this.maxTimeValue.get().doubleValue() == 0.0d) {
            return;
        }
        cooldown = this.delayValue.get().intValue();
        working = true;
        freezeTicks = 0;
        if (this.betterAnimation.get().booleanValue()) {
            firstAnimation = false;
        }
        while (true) {
            if (freezeTicks > this.maxTimeValue.get().doubleValue() - (this.auraClick.get().booleanValue() ? 1 : 0) || stopWorking) {
                break;
            }
            freezeTicks++;
            try {
                mc.runTick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (this.debug.get().booleanValue()) {
            ChatUtils.sendMessage("BalanceTimer-ed");
        }
        if (this.auraClick.get().booleanValue()) {
            freezeTicks++;
            try {
                mc.runTick();
                if (this.debug.get().booleanValue()) {
                    ChatUtils.sendMessage("Clicked");
                }
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }
        stopWorking = false;
        working = false;
    }

    public boolean handleTick() {
        if (working || freezeTicks < 0) return true;
        if (getState() && freezeTicks > 0) {
            --freezeTicks;
            return true;
        }
        if (reverseFreeze) {
            reverseFreeze = false;
            int time = reverseTickTime.get().intValue();
            working = true;
//            if (reverseAuraClick.get().equals("BeforeTimer")) killAura.setClicks(killAura.getClicks() + 1);
            while (time > 0) {
                --time;
                try {
                    mc.runTick();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            working = false;
            cooldown = reverseDelay.get().intValue();
//            if (reverseAuraClick.get().equals("AfterTimer")) killAura.setClicks(killAura.getClicks() + 1);
        }
        if (cooldown > 0) --cooldown;
        return false;
    }

    public boolean freezeAnimation() {
        if (freezeTicks != 0) {
            if (!firstAnimation) {
                firstAnimation = true;
                return false;
            }
            return true;
        }
        return false;
    }
}
