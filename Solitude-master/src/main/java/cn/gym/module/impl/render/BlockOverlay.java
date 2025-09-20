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
package cn.gym.module.impl.render;

import cn.gym.Solitude;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.render.Render3DEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.utils.player.PlayerUtil;
import cn.gym.utils.render.RenderUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ColorValue;
import net.minecraft.block.BlockAir;

import java.awt.*;

public class BlockOverlay extends Module {

    public final BooleanValue outline = new BooleanValue("Outline", true);
    public final BooleanValue filled = new BooleanValue("Filled", false);
    public final BooleanValue syncColor = new BooleanValue("Sync Color", false);
    public final ColorValue color = new ColorValue("Color",() -> !syncColor.get(),new Color(255,255,255));

    public BlockOverlay() {
        super("BlockOverlay",Category.Render);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {

        if(PlayerUtil.getBlock(mc.objectMouseOver.getBlockPos()) instanceof BlockAir)
            return;

        if (syncColor.get()) {
            RenderUtil.renderBlock(mc.objectMouseOver.getBlockPos(), Solitude.Instance.getModuleManager().getModule(Interface.class).color(0), outline.get(), filled.get());
        } else {
            RenderUtil.renderBlock(mc.objectMouseOver.getBlockPos(), color.get().getRGB(), outline.get(), filled.get());
        }

    }
}
