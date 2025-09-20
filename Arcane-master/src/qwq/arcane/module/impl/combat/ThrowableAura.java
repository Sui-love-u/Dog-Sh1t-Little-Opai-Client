package qwq.arcane.module.impl.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSnowball;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Mine;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.misc.Teams;
import qwq.arcane.module.impl.player.Blink;
import qwq.arcane.module.impl.world.Scaffold;
import qwq.arcane.utils.math.Vector2f;
import qwq.arcane.utils.pack.PacketUtil;
import qwq.arcane.utils.rotation.RayCastUtil;
import qwq.arcane.utils.rotation.RotationUtil;
import qwq.arcane.utils.time.TimerUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/combat/ThrowableAura.class */
public class ThrowableAura extends Module {
    private final NumberValue dealy;
    private final NumberValue range;
    private final NumberValue Fov;
    public BoolValue playersValue;
    public BoolValue animalsValue;
    public BoolValue mobsValue;
    public BoolValue invisibleValue;
    private final TimerUtil attackTimer;
    private final TimerUtil timer;
    private int index;
    public static final List<EntityLivingBase> targets = new ArrayList();
    public static EntityLivingBase target;

    public ThrowableAura() {
        super("ThrowableAura", Category.Combat);
        this.dealy = new NumberValue("Delay", 8.0d, 0.0d, 1000.0d, 1.0d);
        this.range = new NumberValue("Range", 5.0d, 1.0d, 8.0d, 1.0d);
        this.Fov = new NumberValue("Fov", 90.0d, 0.0d, 360.0d, 1.0d);
        this.playersValue = new BoolValue("Players", true);
        this.animalsValue = new BoolValue("Animals", true);
        this.mobsValue = new BoolValue("Mobs", false);
        this.invisibleValue = new BoolValue("Invisible", false);
        this.attackTimer = new TimerUtil();
        this.timer = new TimerUtil();
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.index = 0;
        targets.clear();
        super.onEnable();
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        this.index = 0;
        targets.clear();
        super.onDisable();
    }

    @EventTarget
    public void onWorldEvent(WorldLoadEvent event) {
        this.index = 0;
        targets.clear();
    }

    @EventTarget
    public void onUpdateEvent(MotionEvent e) {
        setsuffix(this.dealy.get().toString());
        if (e.isPost()) {
            if (((Boolean) Objects.requireNonNull(Boolean.valueOf(((Scaffold) Client.Instance.getModuleManager().getModule(Scaffold.class)).getState() || isGapple() || ((Gapple) Client.Instance.getModuleManager().getModule(Gapple.class)).getState() || (((KillAura) Client.Instance.getModuleManager().getModule(KillAura.class)).getState() && KillAura.target != null)))).booleanValue()) {
                return;
            }
            int slot = -1;
            if (getEggSlot() != -1) {
                slot = getEggSlot();
            } else if (getSnowballSlot() != -1) {
                slot = getSnowballSlot();
            }
            if (slot == -1) {
                return;
            }
            findTarget();
            if (this.index >= targets.size()) {
                this.index = 0;
            }
            if (targets.isEmpty()) {
                return;
            }
            target = targets.get(this.index);
            if (target != null && mc.thePlayer.canEntityBeSeen(target)) {
                float[] rotation = RotationUtil.getRotationsNeededBall(target);
                Client.Instance.getRotationManager().setRotation(new Vector2f(rotation[0], rotation[1]), 360.0f, true);
                if (RayCastUtil.rayCast(Client.Instance.getRotationManager().lastRotation, this.range.getValue().doubleValue()).entityHit == null || !this.attackTimer.hasTimeElapsed(100L, true)) {
                    return;
                }
                int prevSlot = mc.thePlayer.inventory.currentItem;
                if (this.timer.delay((long) (this.dealy.getValue().doubleValue() * 10.0d))) {
                    PacketUtil.sendPacket(new C09PacketHeldItemChange(slot));
                    PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    PacketUtil.sendPacket(new C09PacketHeldItemChange(prevSlot));
                    mc.thePlayer.inventory.currentItem = prevSlot;
                    this.timer.reset();
                }
            }
        }
    }

    private void findTarget() {
        targets.clear();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (mc.thePlayer.getDistanceToEntity(entity) <= this.range.getValue().doubleValue() && shouldAdd(entity) && mc.thePlayer != entityLivingBase) {
                    targets.add(entityLivingBase);
                }
            }
        }
        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
    }

    public int getEggSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() && (mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemEgg)) {
                return i;
            }
        }
        return -1;
    }

    public int getSnowballSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() && (mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemSnowball)) {
                return i;
            }
        }
        return -1;
    }

    public boolean shouldAdd(Entity target2) {
        float entityFov = (float) RotationUtil.getRotationDifference(target2);
        float fov = this.Fov.getValue().floatValue();
        Blink blink = (Blink) Client.Instance.getModuleManager().getModule(Blink.class);
        double d2 = mc.thePlayer.getDistanceToEntity(target2);
        double d3 = this.range.getValue().doubleValue();
        if (d2 > d3) {
            return false;
        }
        if ((target2.isInvisible() && !this.invisibleValue.getValue().booleanValue()) || !target2.isEntityAlive()) {
            return false;
        }
        if ((fov != 360.0f && entityFov > fov) || target2 == Mine.getMinecraft().thePlayer || target2.isDead || Mine.getMinecraft().thePlayer.getHealth() == 0.0f) {
            return false;
        }
        if (((target2 instanceof EntityMob) || (target2 instanceof EntityGhast) || (target2 instanceof EntityGolem) || (target2 instanceof EntityDragon) || (target2 instanceof EntitySlime)) && this.mobsValue.getValue().booleanValue()) {
            return true;
        }
        if (((target2 instanceof EntitySquid) || (target2 instanceof EntityBat) || (target2 instanceof EntityVillager)) && this.animalsValue.getValue().booleanValue()) {
            return true;
        }
        if ((target2 instanceof EntityAnimal) && this.animalsValue.getValue().booleanValue()) {
            return true;
        }
        return !blink.getState() && !Teams.isSameTeam(target2) && (target2 instanceof EntityPlayer) && this.playersValue.getValue().booleanValue();
    }
}
