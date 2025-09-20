package net.minecraft.network.play.client;

import java.io.IOException;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer> {
    private static final BlockPos field_179726_a = new BlockPos(-1, -1, -1);
    public BlockPos position;
    private int placedBlockDirection;
    private ItemStack stack;
    public float facingX;
    public float facingY;
    public float facingZ;
    public boolean test;
    public EnumFacing sb;

    public C08PacketPlayerBlockPlacement() {
    }

    public C08PacketPlayerBlockPlacement(ItemStack stackIn) {
        this(field_179726_a, 255, stackIn, 0.0f, 0.0f, 0.0f);
    }

    public C08PacketPlayerBlockPlacement(BlockPos positionIn, int placedBlockDirectionIn, ItemStack stackIn, float facingXIn, float facingYIn, float facingZIn, EnumFacing sb) {
        this.position = positionIn;
        this.placedBlockDirection = placedBlockDirectionIn;
        this.stack = stackIn != null ? stackIn.copy() : null;
        this.facingX = facingXIn;
        this.facingY = facingYIn;
        this.facingZ = facingZIn;
        this.sb = sb;
    }

    public C08PacketPlayerBlockPlacement(BlockPos positionIn, int placedBlockDirectionIn, ItemStack stackIn, float facingXIn, float facingYIn, float facingZIn) {
        this.position = positionIn;
        this.placedBlockDirection = placedBlockDirectionIn;
        this.stack = stackIn != null ? stackIn.copy() : null;
        this.facingX = facingXIn;
        this.facingY = facingYIn;
        this.facingZ = facingZIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        float unsignedByte = ViaLoadingBase.getInstance().getTargetVersion().getVersion() <= 47 ? (float) buf.readUnsignedByte() / 16.0f : (float) buf.readUnsignedByte();
        this.position = buf.readBlockPos();
        this.placedBlockDirection = buf.readUnsignedByte();
        this.stack = buf.readItemStackFromBuffer();
        this.facingX = unsignedByte;
        this.facingY = unsignedByte;
        this.facingZ = unsignedByte;
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.position);
        if (this.test) {
            buf.writeByte(255);
        } else if (this.sb != null) {
            buf.writeEnumValue(this.sb);
        } else {
            buf.writeByte(this.placedBlockDirection);
        }
        buf.writeItemStackToBuffer(this.stack);
        buf.writeByte((int) (ViaLoadingBase.getInstance().getTargetVersion().getVersion() <= 47 ? this.facingX * 16.0f : this.facingX));
        buf.writeByte((int) (ViaLoadingBase.getInstance().getTargetVersion().getVersion() <= 47 ? this.facingY * 16.0f : this.facingY));
        buf.writeByte((int) (ViaLoadingBase.getInstance().getTargetVersion().getVersion() <= 47 ? this.facingZ * 16.0f : this.facingZ));
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processPlayerBlockPlacement(this);
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public int getPlacedBlockDirection() {
        return this.placedBlockDirection;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void setStack(ItemStack itemStack) {
        this.stack = itemStack;
    }

    public float getPlacedBlockOffsetX() {
        return this.facingX;
    }

    public float getPlacedBlockOffsetY() {
        return this.facingY;
    }

    public float getPlacedBlockOffsetZ() {
        return this.facingZ;
    }

    public float getFacingX() {
        return this.facingX;
    }

    public float getFacingY() {
        return this.facingY;
    }

    public float getFacingZ() {
        return this.facingZ;
    }
}
