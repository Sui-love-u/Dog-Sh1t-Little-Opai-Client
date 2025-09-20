package qwq.arcane.utils.player;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.network.OldServerPinger;
import qwq.arcane.Client;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.misc.TickEvent;
import qwq.arcane.module.Mine;
import qwq.arcane.module.impl.visuals.InterFace;
import qwq.arcane.utils.Instance;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/PingerUtils.class */
public class PingerUtils implements Instance {
    public static long SERVER_UPDATE_TIME = 30000;
    private final OldServerPinger serverPinger = new OldServerPinger();
    private final Map<String, Long> serverUpdateTime = new HashMap();
    private final Map<String, Boolean> serverUpdateStatus = new HashMap();
    private Long serverPing = null;

    public Long getServerPing() {
        return this.serverPing;
    }

    @EventTarget
    public void onTickEvent(TickEvent event) {
        updateManually(Mine.getMinecraft().getCurrentServerData());
    }

    public PingerUtils() throws SecurityException {
        Client.Instance.getEventManager().register(this);
    }

    public static String getPing() {
        int temp;
        int latency = 0;
        if (!mc.isSingleplayer()) {
            NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            if (info != null) {
                latency = info.getResponseTime();
            }
            if (InterFace.isOnHypixel() && latency == 1 && (temp = Client.INSTANCE.getPingerUtils().getServerPing().intValue()) != -1) {
                latency = temp;
            }
            return latency == 0 ? "?" : String.valueOf(latency);
        }
        return "SinglePlayer";
    }

    public void updateManually(ServerData server) {
        if (server != null) {
            Long updateTime = this.serverUpdateTime.get(server.serverIP);
            if ((updateTime == null || updateTime.longValue() + SERVER_UPDATE_TIME <= System.currentTimeMillis()) && !this.serverUpdateStatus.getOrDefault(server.serverIP, false).booleanValue()) {
                this.serverUpdateStatus.put(server.serverIP, true);
                new Thread(() -> {
                    try {
                        this.serverPinger.ping(server);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    this.serverUpdateStatus.put(server.serverIP, false);
                    this.serverUpdateTime.put(server.serverIP, Long.valueOf(System.currentTimeMillis()));
                }).start();
            }
            if (!InterFace.isOnHypixel() || server.pingToServer != 1) {
                this.serverPing = Long.valueOf(server.pingToServer);
            }
        }
    }
}
