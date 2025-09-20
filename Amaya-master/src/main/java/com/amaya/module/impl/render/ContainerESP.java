package com.amaya.module.impl.render;

import com.amaya.Amaya;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.render.Render3DEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ColorSetting;
import com.amaya.utils.render.RenderUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

import java.awt.*;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "ContainerESP",category = Category.Render)
public class ContainerESP extends Module {
    public final BooleanSetting outline = new BooleanSetting("Outline", false);
    public final BooleanSetting filled = new BooleanSetting("Filled", true);
    public final BooleanSetting syncColor = new BooleanSetting("Sync Color", false);
    public final ColorSetting color = new ColorSetting("Color",()-> !syncColor.get(),new Color(128, 244, 255));

    @EventTarget
    public void onRender3D(Render3DEvent event) {

        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest) {
                if (!tileEntity.isInvalid() && mc.theWorld.getBlockState(tileEntity.getPos()) != null) {
                    if (syncColor.get()) {
                        RenderUtil.renderBlock(tileEntity.getPos(), Amaya.Instance.moduleManager.getModule(HUD.class).FirstColor.get().getRGB(),outline.get(),filled.get());
                    } else {
                        RenderUtil.renderBlock(tileEntity.getPos(),color.get().getRGB(),outline.get(),filled.get());
                    }
                }
            }
        }
    }
}
