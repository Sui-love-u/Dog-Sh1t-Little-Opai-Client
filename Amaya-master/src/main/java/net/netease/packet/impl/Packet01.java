/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.packet.GermPacket;

public class Packet01
implements GermPacket {
    private String key;

    public Packet01() {
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) {
        packetBuffer.writeString(this.key);
    }

    @Override
    public int getPacketId() {
        return 1;
    }

    public Packet01(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

