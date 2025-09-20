package qwq.arcane.module.impl.visuals;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/FullBright.class */
public class FullBright extends Module {
    public FullBright() {
        super("FullBright", Category.Visuals);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 5200, 1));
    }

    @Override // qwq.arcane.module.Module
    public void onDisable() {
        if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
            mc.thePlayer.removePotionEffect(Potion.nightVision.id);
        }
    }
}
