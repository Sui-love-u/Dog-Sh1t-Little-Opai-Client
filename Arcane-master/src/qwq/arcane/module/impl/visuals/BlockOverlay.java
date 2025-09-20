package qwq.arcane.module.impl.visuals;

import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.render.Render3DEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/BlockOverlay.class */
public class BlockOverlay extends Module {
    public final BoolValue outline;
    public final BoolValue filled;
    public final BoolValue syncColor;
    public final ColorValue color;

    public BlockOverlay() {
        super("BlockOverlay", Category.Visuals);
        this.outline = new BoolValue("Outline", true);
        this.filled = new BoolValue("Filled", false);
        this.syncColor = new BoolValue("Sync Color", false);
        this.color = new ColorValue("Color", () -> {
            return !this.syncColor.get().booleanValue();
        }, new Color(255, 255, 255));
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (getBlock(mc.objectMouseOver.getBlockPos()) instanceof BlockAir) {
            return;
        }
        if (this.syncColor.get().booleanValue()) {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            RenderUtil.renderBlock(blockPos, InterFace.color(0).getRGB(), this.outline.get().booleanValue(), this.filled.get().booleanValue());
            return;
        }
        RenderUtil.renderBlock(mc.objectMouseOver.getBlockPos(), this.color.get().getRGB(), this.outline.get().booleanValue(), this.filled.get().booleanValue());
    }

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }
}
