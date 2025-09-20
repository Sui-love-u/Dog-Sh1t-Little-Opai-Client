package qwq.arcane.utils.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.Packet;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.math.Vector3d;
import qwq.arcane.utils.pack.PacketUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/player/BlinkUtils.class */
public class BlinkUtils {
    private static final Mine mc = Mine.getMinecraft();
    private static Double prevYMotion = null;
    private static boolean isStarted = false;
    public static boolean limiter = false;
    public static boolean blinking = false;
    private static final List<Packet<?>> packets = Collections.synchronizedList(new ArrayList());
    private static final List<Vector3d> positions = Collections.synchronizedList(new ArrayList());

    public static void addPacket(Packet<?> packet) {
        packets.add(packet);
    }

    public static void doBlink() {
        if (mc.isIntegratedServerRunning()) {
            return;
        }
        blinking = true;
        if (prevYMotion == null && mc.thePlayer != null) {
            prevYMotion = Double.valueOf(mc.thePlayer.motionY);
        }
        if (!isStarted && mc.thePlayer != null) {
            synchronized (positions) {
                positions.add(new Vector3d(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + (mc.thePlayer.getEyeHeight() / 2.0f), mc.thePlayer.posZ));
                positions.add(new Vector3d(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ));
            }
            isStarted = true;
            return;
        }
        if (mc.thePlayer != null) {
            synchronized (positions) {
                positions.add(new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            }
        }
    }

    public static void sync(boolean blinkSync, boolean noSyncResetPos) {
        if (blinkSync) {
            try {
                limiter = true;
                while (!packets.isEmpty()) {
                    PacketUtil.sendPacket(packets.remove(0));
                }
                limiter = false;
            } catch (Exception e) {
            } finally {
                limiter = false;
            }
            synchronized (positions) {
                positions.clear();
            }
            return;
        }
        try {
            limiter = true;
            packets.clear();
            limiter = false;
        } catch (Exception e2) {
        } catch (Throwable th) {
            limiter = false;
            throw th;
        }
        if (noSyncResetPos && mc.thePlayer != null) {
            synchronized (positions) {
                if (!positions.isEmpty() && positions.size() > 1) {
                    mc.thePlayer.setPosition(positions.get(1).x, positions.get(1).y, positions.get(1).z);
                }
            }
            if (prevYMotion != null) {
                mc.thePlayer.motionY = prevYMotion.doubleValue();
            }
        }
    }

    public static void stopBlink() {
        synchronized (positions) {
            positions.clear();
        }
        prevYMotion = null;
        isStarted = false;
        blinking = false;
    }
}
