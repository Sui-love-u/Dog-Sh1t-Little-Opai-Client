/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease;

import com.amaya.Amaya;
import com.amaya.utils.Wrapper;
import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.KeyEvent;
import com.amaya.module.impl.misc.Protocol;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.netease.chunk.WorldLoader;
import net.netease.gui.GermGameSubElement;
import net.netease.packet.Channel;
import net.netease.packet.GermPacket;
import net.netease.packet.impl.*;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class PacketProcessor
        implements Wrapper {
    private final Channel forgeChannel = new Channel("FML|HS");
    private final Channel germChannel = new Channel("germplugin-netease");
    private final Channel hyt0Channel = new Channel("hyt0");
    private GermGameSubElement lastGameElement;
    public static byte[] MOD_LIST;
    public static byte[] HYT_REGISTER;
    private static byte[] VEXVIEW_VERSION;
    private final Map<Integer, GermPacket> registry = new HashMap<Integer, GermPacket>();
    private FMLHandshakeClientState currentState;
    private final Set<Integer> outstandingKeys = new HashSet<Integer>();
    public static PacketProcessor INSTANCE;

    public PacketProcessor() {
        this.register(new PacketNegative1());
        this.register(new Packet04());
        this.register(new Packet16());
        this.register(new Packet26());
        this.register(new Packet67());
        this.register(new Packet72());
        this.register(new Packet76());
        this.register(new Packet73());
        this.register(new Packet79());
        this.register(new Packet731());
        this.register(new Packet714());
        this.register(new Packet723());
        this.register(new Packet2141());
        this.register(new Packet01());
        this.register(new Packet03());
        String key = "klooy7Bm6jmf4734";
        MOD_LIST = new byte[]{2, 28, 9, 109, 105, 110, 101, 99, 114, 97, 102, 116, 6, 49, 46, 49, 50, 46, 50, 9, 100, 101, 112, 97, 114, 116, 109, 111, 100, 3, 49, 46, 48, 13, 115, 99, 114, 101, 101, 110, 115, 104, 111, 116, 109, 111, 100, 3, 49, 46, 48, 3, 101, 115, 115, 5, 49, 46, 48, 46, 50, 7, 118, 101, 120, 118, 105, 101, 119, 6, 50, 46, 54, 46, 49, 48, 18, 98, 97, 115, 101, 109, 111, 100, 110, 101, 116, 101, 97, 115, 101, 99, 111, 114, 101, 5, 49, 46, 57, 46, 52, 10, 115, 105, 100, 101, 98, 97, 114, 109, 111, 100, 3, 49, 46, 48, 11, 115, 107, 105, 110, 99, 111, 114, 101, 109, 111, 100, 6, 49, 46, 49, 50, 46, 50, 15, 102, 117, 108, 108, 115, 99, 114, 101, 101, 110, 112, 111, 112, 117, 112, 12, 49, 46, 49, 50, 46, 50, 46, 51, 56, 48, 48, 48, 8, 115, 116, 111, 114, 101, 109, 111, 100, 3, 49, 46, 48, 3, 109, 99, 112, 4, 57, 46, 52, 50, 7, 115, 107, 105, 110, 109, 111, 100, 3, 49, 46, 48, 13, 112, 108, 97, 121, 101, 114, 109, 97, 110, 97, 103, 101, 114, 3, 49, 46, 48, 13, 100, 101, 112, 97, 114, 116, 99, 111, 114, 101, 109, 111, 100, 6, 49, 46, 49, 50, 46, 50, 9, 109, 99, 98, 97, 115, 101, 109, 111, 100, 3, 49, 46, 48, 17, 109, 101, 114, 99, 117, 114, 105, 117, 115, 95, 117, 112, 100, 97, 116, 101, 114, 3, 49, 46, 48, 3, 70, 77, 76, 9, 56, 46, 48, 46, 57, 57, 46, 57, 57, 11, 110, 101, 116, 101, 97, 115, 101, 99, 111, 114, 101, 6, 49, 46, 49, 50, 46, 50, 7, 97, 110, 116, 105, 109, 111, 100, 3, 50, 46, 48, 11, 102, 111, 97, 109, 102, 105, 120, 99, 111, 114, 101, 5, 55, 46, 55, 46, 52, 10, 110, 101, 116, 119, 111, 114, 107, 109, 111, 100, 6, 49, 46, 49, 49, 46, 50, 7, 102, 111, 97, 109, 102, 105, 120, 9, 64, 86, 69, 82, 83, 73, 79, 78, 64, 5, 102, 111, 114, 103, 101, 12, 49, 52, 46, 50, 51, 46, 53, 46, 50, 55, 54, 56, 13, 102, 114, 105, 101, 110, 100, 112, 108, 97, 121, 109, 111, 100, 3, 49, 46, 48, 4, 108, 105, 98, 115, 5, 49, 46, 48, 46, 50, 9, 102, 105, 108, 116, 101, 114, 109, 111, 100, 3, 49, 46, 48, 7, 103, 101, 114, 109, 109, 111, 100, 5, 51, 46, 52, 46, 50, 9, 112, 114, 111, 109, 111, 116, 105, 111, 110, 14, 49, 46, 48, 46, 48, 45, 83, 78, 65, 80, 83, 72, 79, 84};
        HYT_REGISTER = AESUtil.decrypt("60ygBu+SjDGX5p08vofaGSoSCYcXcAlAlJebyBAEMqIapu4mzVEDZ76MkfeWGABVqmSaZrQsXbpPHOKeWhQAAm6Hf9nubL4S3jy5rK7ot2zZVYUKivtmdYJP7tuyalmxTzzs/nmeYS+xdCQH1SzAexELf39J/GxHgm/YdvFUqCNgPljdTiVUBzc9zwGav0yh", key);
        VEXVIEW_VERSION = AESUtil.decrypt("GEssAnITtBCuIdmR3B7D1uBMMM0FYrg+4OxZqfGPldMV5/KEAS22q45DK3eeF4Sr1/KSvuplp0LcB4CVKqRHS0ioLY7yjVozG2H0c4JKZ5Q=", key);
    }

    private void register(GermPacket packet) {
        this.registry.put(packet.getPacketId(), packet);
    }

    public void handleS3F(S3FPacketCustomPayload wrapper) {
        Protocol protocol = Amaya.Instance.moduleManager.getModule(Protocol.class);
        if (protocol.getState() && ((Boolean)Protocol.germ.get()).booleanValue()) {
            String channelName = wrapper.getChannelName();
            PacketBuffer packetBuffer = wrapper.getBufferData();
            if (channelName.equals(this.germChannel.getName())) {
                this.processPacket(wrapper.getBufferData());
                return;
            }
            if (channelName.equals(this.hyt0Channel.getName())) {
                byte by = packetBuffer.readByte();
                if (by == 0) {
                    String string = packetBuffer.readStringFromBuffer(123456);
                    WorldLoader.getInstance().setWorldDirectoryName(string);
                    return;
                }
                if (by != 1) {
                    return;
                }
                WorldClient world = PacketProcessor.mc.theWorld;
                if (world == null) {
                    return;
                }
                WorldLoader.getInstance().loadWorldData(world);
                int n2 = packetBuffer.readShort();
                for (int i = 0; i < n2; ++i) {
                    int n22 = packetBuffer.readInt();
                    int n3 = packetBuffer.readInt();
                    WorldLoader.getInstance().loadChunk(n22, n3);
                }
                return;
            }
            if ("FML|HS".equals(channelName) || "REGISTER".equals(channelName) || "UNREGISTER".equals(channelName)) {
                this.forgeHandshake(packetBuffer.readByte(), packetBuffer);
                return;
            }
            if (!"VexView".equalsIgnoreCase(channelName)) {
                return;
            }
            try {
                byte[] data = new byte[wrapper.getBufferData().readableBytes()];
                wrapper.getBufferData().getBytes(0, data);
                String string = new String(PacketProcessor.n(data));
                JsonObject object = GsonUtil.fromJson(string, JsonObject.class);
                if (!object.get("packet_type").getAsString().equals("ver")) {
                    return;
                }
                mc.getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("VexView", new PacketBuffer(Unpooled.buffer().writeBytes(VEXVIEW_VERSION))));
                return;
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    @EventTarget
    private void onKey(KeyEvent event) {
        if (this.outstandingKeys.contains(Keyboard.getEventKey())) {
            this.sendPacket(new Packet03(Keyboard.getEventKey(), Keyboard.getEventKeyState()));
        }
    }

    private static byte[] n(byte[] b2) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b2);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        return IOUtils.toByteArray(gzipInputStream);
    }

    public void sendPacket(GermPacket packet) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeInt(packet.getPacketId());
        packet.writePacketData(buffer);
        this.germChannel.sendToServer("germmod-netease", buffer);
    }

    private void forgeHandshake(int id, ByteBuf packetBuffer) {
        this.currentState.accept(id, packetBuffer, s -> {
            this.currentState = s;
        });
    }

    public void processPacket(PacketBuffer packetBuffer) {
        int id = packetBuffer.readInt();
        GermPacket packet = this.registry.get(id);
        if (packet == null) {
            return;
        }
        packet.readPacketData(packetBuffer);
        packet.process();
    }

    public Channel getForgeChannel() {
        return this.forgeChannel;
    }

    public Channel getGermChannel() {
        return this.germChannel;
    }

    public Channel getHyt0Channel() {
        return this.hyt0Channel;
    }

    public GermGameSubElement getLastGameElement() {
        return this.lastGameElement;
    }

    public Map<Integer, GermPacket> getRegistry() {
        return this.registry;
    }

    public FMLHandshakeClientState getCurrentState() {
        return this.currentState;
    }

    public Set<Integer> getOutstandingKeys() {
        return this.outstandingKeys;
    }

    public void setLastGameElement(GermGameSubElement lastGameElement) {
        this.lastGameElement = lastGameElement;
    }

    public void setCurrentState(FMLHandshakeClientState currentState) {
        this.currentState = currentState;
    }

    static {
        INSTANCE = new PacketProcessor();
    }
}

