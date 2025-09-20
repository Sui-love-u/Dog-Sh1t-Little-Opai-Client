package qwq.arcane.module.impl.visuals;

import java.awt.Color;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/ContainerESP.class */
public class ContainerESP extends Module {
    public final BoolValue outline;
    public final BoolValue filled;
    public final BoolValue syncColor;
    public final ColorValue color;
    public final BoolValue chests;
    public final BoolValue furnaces;
    public final BoolValue brewingStands;

    public ContainerESP() {
        super("ContainerESP", Category.Visuals);
        this.outline = new BoolValue("Outline", false);
        this.filled = new BoolValue("Filled", true);
        this.syncColor = new BoolValue("SyncColor", false);
        this.color = new ColorValue("Color", () -> {
            return !this.syncColor.get().booleanValue();
        }, new Color(128, 244, 255));
        this.chests = new BoolValue("Chests", true);
        this.furnaces = new BoolValue("Furnaces", false);
        this.brewingStands = new BoolValue("BrewingStands", false);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if ((this.chests.get().booleanValue() && ((tileEntity instanceof TileEntityChest) || (tileEntity instanceof TileEntityEnderChest))) || ((this.furnaces.get().booleanValue() && (tileEntity instanceof TileEntityFurnace)) || (this.brewingStands.get().booleanValue() && (tileEntity instanceof TileEntityBrewingStand)))) {
                if (!tileEntity.isInvalid() && mc.theWorld.getBlockState(tileEntity.getPos()) != null) {
                    if (this.syncColor.get().booleanValue()) {
                        BlockPos pos = tileEntity.getPos();
                        RenderUtil.renderBlock(pos, InterFace.color(20).getRGB(), this.outline.get().booleanValue(), this.filled.get().booleanValue());
                    } else {
                        RenderUtil.renderBlock(tileEntity.getPos(), this.color.get().getRGB(), this.outline.get().booleanValue(), this.filled.get().booleanValue());
                    }
                }
            }
        }
    }
}
