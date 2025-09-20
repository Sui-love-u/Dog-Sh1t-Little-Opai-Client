/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package com.amaya.module.impl.render;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.render.Render3DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ColorSetting;
import com.amaya.utils.player.PlayerUtil;
import com.amaya.utils.render.RenderUtil;
import net.minecraft.block.BlockAir;

import java.awt.*;

@ModuleInfo(name = "BlockOverlay",category = Category.Render)
public class BlockOverlay extends Module {

    public final BooleanSetting outline = new BooleanSetting("Outline", true);
    public final BooleanSetting filled = new BooleanSetting("Filled", false);
    public final BooleanSetting syncColor = new BooleanSetting("Sync Color", false);
    public final ColorSetting color = new ColorSetting("Color",() -> !syncColor.get(),new Color(255,255,255));

    @EventTarget
    public void onRender3D(Render3DEvent event) {

        if(PlayerUtil.getBlock(mc.objectMouseOver.getBlockPos()) instanceof BlockAir)
            return;

        if (syncColor.get()) {
            RenderUtil.renderBlock(mc.objectMouseOver.getBlockPos(), Amaya.Instance.moduleManager.getModule(HUD.class).FirstColor.get().getRGB(), outline.get(), filled.get());
        } else {
            RenderUtil.renderBlock(mc.objectMouseOver.getBlockPos(), color.get().getRGB(), outline.get(), filled.get());
        }

    }
}
