package qwq.arcane.module.impl.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.TickEvent;
import qwq.arcane.event.impl.events.misc.WorldLoadEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.module.impl.misc.Teams;
import qwq.arcane.utils.player.HYTUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/world/PlayerTracker.class */
public class PlayerTracker extends Module {
    public static List<Entity> flaggedEntity = new ArrayList();

    public PlayerTracker() {
        super("PlayerTracker", Category.World);
    }

    @EventTarget
    public void onWorld(WorldLoadEvent e) {
        flaggedEntity.clear();
    }

    @EventTarget
    public void onTick(TickEvent e) {
        if (mc.theWorld != null && !mc.theWorld.loadedEntityList.isEmpty() && mc.thePlayer.ticksExisted % 6 == 0) {
            for (Entity ent : mc.theWorld.loadedEntityList) {
                if ((ent instanceof EntityPlayer) && ent != mc.thePlayer) {
                    EntityPlayer player = (EntityPlayer) ent;
                    if (HYTUtils.isStrength(player) > 0 && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                        flaggedEntity.add(player);
                    }
                    if (HYTUtils.isRegen(player) > 0 && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                        flaggedEntity.add(player);
                    }
                    if (HYTUtils.isHoldingGodAxe(player) && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                        flaggedEntity.add(player);
                    }
                    if (HYTUtils.isKBBall(player.getHeldItem()) && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                        flaggedEntity.add(player);
                    }
                    if (HYTUtils.hasEatenGoldenApple(player) > 0 && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                        flaggedEntity.add(player);
                    }
                }
            }
        }
    }
}
