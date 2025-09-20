/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.packet;

import net.minecraft.network.PacketBuffer;

public interface GermPacket {
    default public void process() {
    }

    default public void writePacketData(PacketBuffer packetBuffer) {
    }

    default public void readPacketData(PacketBuffer packetBuffer) {
    }

    public int getPacketId();
}

