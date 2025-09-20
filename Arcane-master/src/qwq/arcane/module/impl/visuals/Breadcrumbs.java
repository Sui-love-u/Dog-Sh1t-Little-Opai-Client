package qwq.arcane.module.impl.visuals;

import java.util.ArrayDeque;
import net.minecraft.util.Vec3;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.MotionEvent;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/Breadcrumbs.class */
public final class Breadcrumbs extends Module {
    private final ArrayDeque<Vec3> path;
    private final BoolValue timeoutBool;
    private final NumberValue timeout;

    public Breadcrumbs() {
        super("Breadcrumbs", Category.Visuals);
        this.path = new ArrayDeque<>();
        this.timeoutBool = new BoolValue("Timeout", true);
        this.timeout = new NumberValue("Time", 15.0d, 1.0d, 150.0d, 0.10000000149011612d);
    }

    @Override // qwq.arcane.module.Module
    public void onEnable() {
        this.path.clear();
    }

    @EventTarget
    public void onPreMotion(MotionEvent e) {
        if (e.isPre()) {
            if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
                this.path.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            }
            if (this.timeoutBool.get().booleanValue()) {
                while (this.path.size() > this.timeout.get().intValue()) {
                    this.path.removeFirst();
                }
            }
        }
    }

    @EventTarget
    public void onRender3DEvent(Render3DEvent e) {
        RenderUtil.renderBreadCrumbs(this.path);
    }
}
