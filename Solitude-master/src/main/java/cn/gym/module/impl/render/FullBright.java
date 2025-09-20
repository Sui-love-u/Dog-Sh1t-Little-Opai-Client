/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package cn.gym.module.impl.render;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.player.UpdateEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FullBright extends Module {

    public FullBright() {
        super("FullBright",Category.Render);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event){
        mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 5200, 1));
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
            mc.thePlayer.removePotionEffect(Potion.nightVision.id);
        }
    }
}
