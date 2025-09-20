package qwq.arcane.utils.player;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/PlaceData.class */
public class PlaceData {
    public BlockPos blockPos;
    public EnumFacing facing;

    public PlaceData(BlockPos blockPos, EnumFacing facing) {
        this.blockPos = blockPos;
        this.facing = facing;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }
}
