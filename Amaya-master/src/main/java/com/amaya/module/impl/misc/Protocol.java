package com.amaya.module.impl.misc;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.WorldEvent;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.packet.PacketSendEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import io.netty.buffer.Unpooled;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.netease.chunk.RegionFileManager;
import net.netease.chunk.WorldLoader;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @Author: Guyuemang
 * 2025/5/1
 */
@ModuleInfo(name = "Protocol", category = Category.Misc)
public class Protocol extends Module{
    public static final BooleanSetting germ = new BooleanSetting("Germ", true);
    public static final BooleanSetting bypassGreen = new BooleanSetting("BypassGreen", true);
    public static final ModeSetting mode = new ModeSetting("Mode", "Quick Macro",new String[]{"Quick Macro"});
    public static final BooleanSetting worldLoader = new BooleanSetting("WorldLoader", true);

    @EventTarget
    private void onWorld(WorldEvent event) {
        setSuffix(mode.get());
        WorldLoader.INSTANCE.chunkManager = null;
        WorldLoader.INSTANCE.setLoaded(!(Boolean)worldLoader.get());
        RegionFileManager.clearRegionFileCache();
    }

    @EventTarget
    public void onPacketSend(PacketSendEvent event) {
    }
}