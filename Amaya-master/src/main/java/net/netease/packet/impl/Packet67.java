/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.packet.GermPacket;

public class Packet67
implements GermPacket {
    private String message;
    private String message2;

    @Override
    public void process() {
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) {
        this.message = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
        this.message2 = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
    }

    @Override
    public int getPacketId() {
        return 67;
    }
}

