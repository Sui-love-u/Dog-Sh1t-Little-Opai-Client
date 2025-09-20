/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.packet.impl;

import net.minecraft.network.PacketBuffer;
import net.netease.PacketProcessor;
import net.netease.packet.GermPacket;

public class Packet714
implements GermPacket {
    private static String message;

    @Override
    public void process() {
        Packet714.send();
    }

    public static void send() {
        PacketProcessor.INSTANCE.sendPacket(new Packet04(message, 2));
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) {
        message = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
    }

    @Override
    public int getPacketId() {
        return 714;
    }
}

