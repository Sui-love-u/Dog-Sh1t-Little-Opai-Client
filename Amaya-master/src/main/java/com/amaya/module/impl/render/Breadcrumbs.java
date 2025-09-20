package com.amaya.module.impl.render;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.events.impl.render.Render3DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.render.RenderUtil;
import net.minecraft.util.Vec3;
import java.util.ArrayDeque;

@ModuleInfo(name = "Breadcrumbs", category = Category.Render)
public final class Breadcrumbs extends Module {

    private final ArrayDeque<Vec3> path = new ArrayDeque<>();

    private final BooleanSetting timeoutBool = new BooleanSetting("Timeout", true);
    private final NumberSetting timeout = new NumberSetting("Time", 15.0, 1.0,150.0, 1.0);

    @Override
    public void onEnable() {
        path.clear();
    }

    @EventTarget
    public void onPreMotion(MotionEvent e) {
        if (e.isPre()) {
            if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
                path.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            }

            if (timeoutBool.get()) {
                while (path.size() > timeout.get().intValue()) {
                    path.removeFirst();
                }
            }
        }
    }

    @EventTarget
    public void onRender3DEvent(Render3DEvent e) {
        RenderUtil.renderBreadCrumbs(path);
    }
}