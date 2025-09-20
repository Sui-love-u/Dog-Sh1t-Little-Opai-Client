package cn.gym;

import cn.gym.command.CommandManager;
import cn.gym.config.ConfigManager;
import cn.gym.events.EventManager;
import cn.gym.gui.clickgui.dropdown.DropDownClickGui;
import cn.gym.gui.clickgui.neverloseclickgui.NeverLoseClickGui;
import cn.gym.gui.mainmenu.VideoComponent;
import cn.gym.gui.mainmenu.VideoPlayer;
import cn.gym.gui.notification.NotificationManager;
import cn.gym.manager.RotationComponent;
import cn.gym.manager.SlotSpoofComponent;
import cn.gym.module.ModuleManager;
import cn.gym.module.friend.FriendManager;
import cn.gym.utils.pack.BlinkComponent;
import com.viaversion.viarewind.protocol.v1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viarewind.protocol.v1_9to1_8.storage.PlayerPositionTracker;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.Protocol1_20To1_20_2;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.packet.ServerboundPackets1_20_2;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_9;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.lwjgl.opengl.Display;

import java.io.File;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 00:47
 */
@Getter
public class Solitude {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Solitude Instance = new Solitude();
    public static String Name = "Solitude";
    public static String Version = "Beta1.0";
    public EventManager eventManager;
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private CommandManager commandManager;
    public VideoComponent videoComponent;
    public static boolean updateGuiScale;
    public static int prevGuiScale;
    public DropDownClickGui dropDownClickGui;
    public NeverLoseClickGui neverLoseClickGui;
    public NotificationManager notification;
    private FriendManager friendManager;
    int startTime;

    public void Init(){
        eventManager = new EventManager();
        eventManager.register(this);
        eventManager.register(new BlinkComponent());
        eventManager.register(new SlotSpoofComponent());
        eventManager.register(new RotationComponent());

        startTime = (int) System.currentTimeMillis();

        Display.setTitle(Name + " | " + Version);

        try {
            ViaMCP.create();
            ViaMCP.INSTANCE.initAsyncSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fixviamcp();
        notification = new NotificationManager();

        moduleManager = new ModuleManager();
        moduleManager.Init();

        friendManager = new FriendManager();

        configManager = new ConfigManager();
        configManager.loadConfig("config",moduleManager);

        commandManager = new CommandManager(moduleManager);

        this.videoComponent = new VideoComponent();

        try {
            VideoPlayer.init(new File(Minecraft.getMinecraft().mcDataDir, "background.mp4"));
        } catch (FFmpegFrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        dropDownClickGui = new DropDownClickGui();
        neverLoseClickGui = new NeverLoseClickGui();
    }

    public void fixviamcp() {
        Protocol1_9To1_8 protocol = Via.getManager().getProtocolManager().getProtocol(Protocol1_9To1_8.class);
        if (protocol != null) {
            (protocol).registerClientbound(ClientboundPackets1_9.PLAYER_POSITION, new PacketHandlers() {
                public void register() {
                    this.map(Types.DOUBLE);
                    this.map(Types.DOUBLE);
                    this.map(Types.DOUBLE);
                    this.map(Types.FLOAT);
                    this.map(Types.FLOAT);
                    this.map(Types.BYTE);
                    this.handler((wrapper) -> {
                        PlayerPositionTracker pos = (PlayerPositionTracker)wrapper.user().get(PlayerPositionTracker.class);
                        int teleportId = wrapper.read(Types.VAR_INT);
                        pos.setConfirmId(teleportId);
                        byte flags = wrapper.get(Types.BYTE, 0);
                        double x = wrapper.get(Types.DOUBLE, 0);
                        double y = wrapper.get(Types.DOUBLE, 1);
                        double z = wrapper.get(Types.DOUBLE, 2);
                        float yaw = wrapper.get(Types.FLOAT, 0);
                        float pitch = wrapper.get(Types.FLOAT, 1);
                        UserConnection userConnection = wrapper.user();
                        try {
                            PacketWrapper acceptTeleport = PacketWrapper.create(ServerboundPackets1_9.ACCEPT_TELEPORTATION, userConnection);
                            acceptTeleport.write(Types.VAR_INT, teleportId);
                            acceptTeleport.sendToServer(Protocol1_9To1_8.class);
                        } catch (Exception e) {
                        }
                        wrapper.set(Types.BYTE, 0, (byte)0);
                        if (flags != 0) {
                            if ((flags & 1) != 0) {
                                x += pos.getPosX();
                                wrapper.set(Types.DOUBLE, 0, x);
                            }

                            if ((flags & 2) != 0) {
                                y += pos.getPosY();
                                wrapper.set(Types.DOUBLE, 1, y);
                            }

                            if ((flags & 4) != 0) {
                                z += pos.getPosZ();
                                wrapper.set(Types.DOUBLE, 2, z);
                            }

                            if ((flags & 8) != 0) {
                                yaw += pos.getYaw();
                                wrapper.set(Types.FLOAT, 0, yaw);
                            }

                            if ((flags & 16) != 0) {
                                pitch += pos.getPitch();
                                wrapper.set(Types.FLOAT, 1, pitch);
                            }
                        }

                        pos.setPos(x, y, z);
                        pos.setYaw(yaw);
                        pos.setPitch(pitch);
                    });
                }
            });
            System.out.println("Registered CustomPlayerPacketRewriter for Protocol1_9To1_8");
        } else {
            System.err.println("Failed to find Protocol1_9To1_8");
        }
    }

    public void stop() {
        configManager.saveConfig("config",moduleManager);
        try {
            VideoPlayer.stop();
        } catch (FFmpegFrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? 1.0 / (double) Minecraft.getDebugFPS() : 1.0;
    }
}
