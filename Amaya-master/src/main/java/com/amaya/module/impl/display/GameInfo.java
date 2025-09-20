package com.amaya.module.impl.display;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.PreUpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.impl.combat.KillAura;
import com.amaya.module.setting.impl.NumberSetting;
import net.minecraft.entity.EntityLivingBase;

/**
 * @Author: Guyuemang
 * 2025/5/10
 */
@ModuleInfo(name = "Session",category = Category.Display)
public class GameInfo extends Module {
    public GameInfo(){
        kills = 0;
    }
    public NumberSetting radius = new NumberSetting("radius",3,0,8,1);
    public int kills;
    @EventTarget
    public void onUpdate(PreUpdateEvent event){
        EntityLivingBase target = KillAura.target;
        if(target.getHealth() <= 0.0f) {
            kills++;
        }
    }
}
