/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.packet;

import com.amaya.utils.Wrapper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class Channel
implements Wrapper {
    private final String name;

    public void sendToServer(String name, PacketBuffer buffer) {
        Channel.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload(name, buffer));
    }

    public Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

