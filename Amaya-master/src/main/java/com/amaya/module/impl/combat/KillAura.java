package com.amaya.module.impl.combat;

import com.amaya.Amaya;
import com.amaya.events.EventManager;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.player.AttackEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.render.Render3DEvent;
import com.amaya.manager.BlinkManager;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.misc.Teams;
import com.amaya.module.impl.player.Blink;
import com.amaya.module.impl.render.HUD;
import com.amaya.module.impl.world.Scaffold;
import com.amaya.module.setting.impl.*;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import com.amaya.utils.math.MathUtils;
import com.amaya.utils.math.Vector2f;
import com.amaya.utils.math.Vector3d;
import com.amaya.utils.pack.PacketUtil;
import com.amaya.utils.player.PlayerUtil;
import com.amaya.utils.player.RayCastUtil;
import com.amaya.utils.player.Rise.MovementFix;
import com.amaya.component.RotationComponent;
import com.amaya.utils.player.Rise.RotationRise;
import com.amaya.utils.player.Rise.RotationUtils;
import com.amaya.utils.render.RenderUtil;
import com.amaya.utils.time.TimerUtil;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.viamcp.fixes.AttackOrder;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@ModuleInfo(name = "KillAura", category = Category.Combat)
public class KillAura extends Module {
    public KillAura() {
        this.setKey(Keyboard.KEY_R);
    }

    private final ModeSetting mode = new ModeSetting("AttackMode","Single",new String[]{"Single","Switch"});
    public final NumberSetting switchDelayValue = new NumberSetting("SwitchDelay", () -> mode.is("Switch"),15, 0, 20, 1);
    private static final NumberSetting range = new NumberSetting("Range",3,1,7,0.1);
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", 18, 1, 20, 1);
    private final NumberSetting minCPS = new NumberSetting("Min CPS", 12, 1, 20, 1);
    private final BooleanSetting rotations = new BooleanSetting("rotations",true);
    public ModeSetting rotMode = new ModeSetting("Rotation Mode",rotations::get, "Normal", new String[]{"Normal", "HvH", "CNM","New"});
    private final NumberSetting RotationsSpeed = new NumberSetting("RotationsSpeed", rotations::get,8, 1, 10, 1);
    private final ModeSetting moveFix = new ModeSetting("MovementFix","Silent",new String[]{ "Silent", "Strict", "None", "BackSprint"});
    private final BooleanSetting autoblock = new BooleanSetting("AutoBlock",true);
    private final ModeSetting autoblockmode = new ModeSetting("AutoBlockMode", autoblock::getValue,"Grim",new String[]{"Grim","WatchDog","WatchDogs","Off"});
    private final ModeSetting sortMode = new ModeSetting("Sort Mode","Range",new String[]{"Range", "Hurt Time", "Health", "Armor"});
    private final EnumSetting auraESP = new EnumSetting("TargetHUD ESP",Arrays.asList(
            new BooleanSetting("Circle", true),
            new BooleanSetting("Tracer", false),
            new BooleanSetting("Box", false),
            new BooleanSetting("Custom Color", false)));
    private final ColorSetting customColor = new ColorSetting("Custom Color", Color.WHITE);
    private final EnumSetting mod = new EnumSetting("AttackEntity", Arrays.asList(
            new BooleanSetting("Players", true),
            new BooleanSetting("Mobs", true),
            new BooleanSetting("Animal", false),
            new BooleanSetting("Dead", false)));
    public List<EntityLivingBase> targets = new ArrayList<>();
    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil switchTimer = new TimerUtil();
    public static EntityLivingBase target;
    public boolean blinked;
    public int blinkTicks;
    public static boolean attack;
    private int index;
    private int cps;
    public float[] rotation;
    private Entity auraESPTarget;
    @Getter
    public static boolean blocking;
    private Double killaurarange;
    @EventTarget
    public void onWorld(WorldEvent e){
        this.setState(false);
    }

    @Override
    public void onEnable() {
        blocking = false;
        targets.clear();
        target = null;
        attack = false;
        switchTimer.reset();
        attackTimer.reset();
        index = 0;
    }

    @Override
    public void onDisable() {
        if (blinked) {
            BlinkManager.dispatch();
        }
        blinkTicks = 0;
        blocking = false;
        targets.clear();
        target = null;
        attack = false;
        switchTimer.reset();
        attackTimer.reset();
        index = 0;
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
    @EventTarget
    public void onMotion(MotionEvent e) {
        setSuffix(mode.get());
        if (Amaya.Instance.moduleManager.getModule(Gapple.class).getState()){
            killaurarange = 3.0;
        }else killaurarange = range.getValue();
        targets.clear();
        targets = gettarget();
        if (maxCPS.getValue() < minCPS.getValue()) minCPS.setValue(minCPS.getValue() - 1);
        if (!targets.isEmpty()) {
            if (target != null) {
                if (blinked) {
                    BlinkManager.dispatch();
                    blinked = false;
                }
            };
            if (targets.size() > 1) {
                switch (sortMode.getValue()) {
                    case "Range":
                        targets.sort(Comparator.comparingDouble(mc.thePlayer :: getDistanceToEntity));
                        break;
                    case "Hurt Time":
                        targets.sort(Comparator.comparingInt(entity -> entity.hurtTime));
                        break;
                    case "Health":
                        targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                        break;
                    case "Armor":
                        targets.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
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
            attack = !targets.isEmpty();
            blocking = autoblock.get() && isSword();
            if (attack) {
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
            }

            if (rotations.get()){
                onRotations(target);
            }

            onAutoBlock();

        }else {
            if (blinked) {
                BlinkManager.dispatch();
                blinked = false;
            }
            blinkTicks = 0;
            blocking = false;
            targets.clear();
            target = null;
            switchTimer.reset();
            attackTimer.reset();
            index = 0;
            StopAutoBlock();
        }
    }
    private boolean preTickBlock() {
        switch (autoblockmode.get()) {
            case "WatchDog":
                if (blinkTicks >= 3) {
                    blinkTicks = 0;
                }
                blinkTicks++;
                switch (blinkTicks) {
                    case 0:
                        return true;
                    case 1:
                        if(blocking) {
                            BlinkManager.blinking = true;
                            StopAutoBlock();
                            blinked = true;
                            return true;
                        }
                    case 2:
                        return false;
                }
                break;
        }
        return false;
    }

    public void onAutoBlock(){
        if (autoblock.get()) {
            if (isSword()) {
                switch (autoblockmode.getValue()) {
                    case "WatchDogs":
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                        if (target != null) {
                            this.block(false, true);
                        }
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        this.block(false, false);
                        if (!mc.isSingleplayer()) {
                            PacketWrapper use = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            use.write(Type.VAR_INT, 1);
                            com.viaversion.viarewind.utils.PacketUtil.sendToServer(use, Protocol1_8To1_9.class, true, true);
                        }
                        break;
                    case "WatchDog":
                        if (target != null) {
                            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                        }
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        if (!mc.isSingleplayer()) {
                            PacketWrapper use = PacketWrapper.create(29, null,
                                    Via.getManager().getConnectionManager().getConnections().iterator().next());
                            use.write(Type.VAR_INT, 1);
                            PacketUtil.sendToServer(use, Protocol1_8To1_9.class, true, true);
                            BlinkManager.dispatch();
                        }
                    break;
                    case "Grim":
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                        PacketWrapper use_0 = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                        use_0.write(Type.VAR_INT, 0);
                        com.viaversion.viarewind.utils.PacketUtil.sendToServer(use_0, Protocol1_8To1_9.class, true, true);

                        PacketWrapper use_1 = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                        use_1.write(Type.VAR_INT, 1);
                        com.viaversion.viarewind.utils.PacketUtil.sendToServer(use_1, Protocol1_8To1_9.class, true, true);
                        break;
                    case "Off":
                        break;
                }
            }
        }
        blocking = true;
    }

    private void block(boolean check, boolean interact) {
        if (!blocking || !check) {
            MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationComponent.lastRotations, 3);

            if (interact && shouldAttack() && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                this.interact(movingObjectPosition);
            }
            PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(getItemStack()));
            blocking = true;
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

    public void StopAutoBlock(){
        if (autoblock.get() && blocking) {
            if (isSword()) {
                switch (autoblockmode.getValue()) {
                    case "WatchDogs":
                        mc.gameSettings.keyBindUseItem.pressed = false;
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        if (target != null) {
                            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                        }
                        blocking = false;
                        break;
                    case "WatchDog":
                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        blocking = false;
                        break;
                    case "Grim":
                        if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        } else {
                            mc.gameSettings.keyBindUseItem.setPressed(false);
                        }
                        blocking = false;
                        break;
                    case "Off":
                        blocking = false;
                        break;
                }
            }
        }
    }

    public boolean isBlocking() {
        return target != null && !autoblockmode.is("Off") && isSword() && getState() && !blocking;
    }

    private void onRotations(Entity target){
        float[] rotation = getRot();
        RotationComponent.setRotations(new Vector2f(rotation[0], rotation[1]), RotationsSpeed.getValue(),getMovementFixType());
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

    private void attack(Entity traget){
        if (shouldAttack()) {
            AttackEvent event = new AttackEvent(target);
            EventManager.call(event);
            AttackOrder.sendFixedAttackByPacket(traget);
        }
    }

    private List<EntityLivingBase> gettarget() {
        targets.clear();
        final List<EntityLivingBase> entities = new ArrayList<>();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase e) {
                if (isValid(e))
                    entities.add(e);
                else entities.remove(e);

            }
        }
        return entities;
    }

    public boolean isValid(Entity entity) {
        if (Amaya.Instance.moduleManager.getModule(Blink.class).getState()){
            return false;
        }
        if (Amaya.Instance.moduleManager.getModule(Teams.class).getState()) {
            return Teams.isSameTeam(entity);
        }
        if (entity != mc.thePlayer) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= killaurarange) {
                if (entity.isEntityAlive()){
                    if (mod.isEnabled("Players") && entity instanceof EntityPlayer) return true;
                    if (mod.isEnabled("Mobs") && PlayerUtil.isMob(entity)) return true;
                    if (mod.isEnabled("Animal") && PlayerUtil.isAnimal(entity)) return true;
                }else if (mod.isEnabled("Dead")){
                    return true;
                }
            }
        }
        return false;
    }

    private final Animation auraESPAnim = new DecelerateAnimation(300, 1);

    private final ScaledResolution sr = new ScaledResolution(mc);
    @EventTarget
    public void onRender3DEvent(Render3DEvent event) {
        auraESPAnim.setDirection(KillAura.target != null ? Direction.FORWARDS : Direction.BACKWARDS);
        if (KillAura.target != null) {
            auraESPTarget = KillAura.target;
        }

        if (auraESPAnim.finished(Direction.BACKWARDS)) {
            auraESPTarget = null;
        }

        Color color = Amaya.Instance.moduleManager.getModule(HUD.class).FirstColor.get();

        if (auraESP.isEnabled("Custom Color")) {
            color = customColor.get();
        }
        if (auraESPTarget != null) {
            if (auraESP.isEnabled("Box")) {
                RenderUtil.renderBoundingBox((EntityLivingBase) auraESPTarget, color, auraESPAnim.getOutput().floatValue());
            }
            if (auraESP.isEnabled("Circle")) {
                RenderUtil.drawCircle(this.auraESPTarget, event.getPartialTicks(), 0.75, color.getRGB(), this.auraESPAnim.getOutput().floatValue());
            }
            if (auraESP.isEnabled("Tracer")) {
                RenderUtil.drawTracerLine(auraESPTarget, 4f, Color.BLACK, auraESPAnim.getOutput().floatValue());
                RenderUtil.drawTracerLine(auraESPTarget, 2.5f, color, auraESPAnim.getOutput().floatValue());
            }
        }
    }

    public boolean isSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public boolean shouldAttack() {
        final MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
        if (Amaya.Instance.getModuleManager().getModule(Scaffold.class).getState()) return false;
        return (mc.thePlayer.getClosestDistanceToEntity(target) <= killaurarange) && (movingObjectPosition != null && movingObjectPosition.entityHit == target);
    }
}