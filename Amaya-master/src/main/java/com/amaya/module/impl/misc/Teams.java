package com.amaya.module.impl.misc;

import com.amaya.Amaya;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "Teams",category = Category.Misc)
public class Teams extends Module {
    private static final BooleanSetting armor = new BooleanSetting("ArmorColor", true);
    private static final BooleanSetting color = new BooleanSetting("Color", true);
    private static final BooleanSetting scoreboard = new BooleanSetting("ScoreboardTeam", true);
    public static boolean isSameTeam(final Entity entity) {
        if (entity instanceof EntityPlayer entityPlayer) {
            if (entityPlayer == mc.thePlayer){
                return false;
            }
            return Amaya.Instance.moduleManager.getModule(Teams.class).getState() && ((armor.get() && PlayerUtil.armorTeam(entityPlayer))
                    || (color.get() && PlayerUtil.colorTeam(entityPlayer))
                    || (scoreboard.get() && PlayerUtil.scoreTeam(entityPlayer)));
        }
        return false;
    }
}
