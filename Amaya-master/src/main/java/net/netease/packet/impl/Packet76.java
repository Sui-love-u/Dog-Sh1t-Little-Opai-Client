/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.packet.impl;

import com.amaya.utils.Wrapper;
import net.minecraft.network.PacketBuffer;
import net.netease.PacketProcessor;
import net.netease.gui.GermGameGui;
import net.netease.packet.GermPacket;

public class Packet76
implements GermPacket,
        Wrapper {
    private String name;

    @Override
    public void process() {
        PacketProcessor.INSTANCE.sendPacket(new Packet04("germ_gui_loading"));
        PacketProcessor.INSTANCE.sendPacket(new Packet04(this.name));
        switch (this.name) {
            case "mainmenu": {
                GermGameGui.INSTANCE.setGuiName(this.name);
                mc.addScheduledTask(() -> mc.displayGuiScreen(GermGameGui.INSTANCE));
            }
        }
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) {
        this.name = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
    }

    @Override
    public int getPacketId() {
        return 76;
    }
}

