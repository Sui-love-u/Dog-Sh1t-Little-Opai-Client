package com.amaya.module.impl.render;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.PreUpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.ModeSetting;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(name = "Cape", category = Category.Render)
public class Cape extends Module {
    private final ModeSetting capemods = new ModeSetting("Style","Yuzaki",new String[]{"JiaRan", "Chimera", "Hanabi", "Astolfo", "Yuzaki", "KuChaZi", "Furina", "Mika", "Nekocat", "Nekocat2", "Paimon", "Cherry", "Rise","RavenAnime", "RavenAqua", "RavenGreen", "RavenPurple", "RavenRed", "RavenWhite", "RavenYellow"});

    @Override
    public void onDisable() {
        mc.thePlayer.setLocationOfCape(null);
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(PreUpdateEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        mc.thePlayer.setLocationOfCape(new ResourceLocation("amaya/cape/" + capemods.getValue().toLowerCase() + ".png"));
    }

}
