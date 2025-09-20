package cn.gym.module.impl.render;

import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.render.Render3DEvent;
import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.utils.render.RenderUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ColorValue;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityBrewingStand;

import java.awt.*;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
public class ContainerESP extends Module {
    public final BooleanValue outline = new BooleanValue("Outline", false);
    public final BooleanValue filled = new BooleanValue("Filled", true);
    public final BooleanValue syncColor = new BooleanValue("Sync Color", false);
    public final ColorValue color = new ColorValue("Color",()-> !syncColor.get(),new Color(128, 244, 255));

    public final BooleanValue chests = new BooleanValue("Chests", true);
    public final BooleanValue furnaces = new BooleanValue("Furnaces", true);
    public final BooleanValue brewingStands = new BooleanValue("Brewing Stands", true);

    public ContainerESP() {
        super("ContainerESP",Category.Render);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if ((chests.get() && (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest)) ||
                    (furnaces.get() && tileEntity instanceof TileEntityFurnace) ||
                    (brewingStands.get() && tileEntity instanceof TileEntityBrewingStand)) {
                if (!tileEntity.isInvalid() && mc.theWorld.getBlockState(tileEntity.getPos()) != null) {
                    if (syncColor.get()) {
                        RenderUtil.renderBlock(tileEntity.getPos(),getModule(Interface.class).color(0),outline.get(),filled.get());
                    } else {
                        RenderUtil.renderBlock(tileEntity.getPos(),color.get().getRGB(),outline.get(),filled.get());
                    }
                }
            }
        }
    }
}