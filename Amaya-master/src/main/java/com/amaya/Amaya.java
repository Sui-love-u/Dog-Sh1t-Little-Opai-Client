package com.amaya;

import com.amaya.command.CommandManager;
import com.amaya.component.FallDistanceManager;
import com.amaya.component.MovementComponent;
import com.amaya.component.SlotSpoofComponent;
import com.amaya.config.ConfigManager;
import com.amaya.events.EventManager;
import com.amaya.gui.clickgui.dropdown.DropDownClickGui;
import com.amaya.gui.clickgui.neverlose.NegativeClickGui;
import com.amaya.gui.widget.WidgetManager;
import com.amaya.manager.BlinkManager;
import com.amaya.module.ModuleManager;
import com.amaya.network.user.User;
import com.amaya.network.user.UserManager;
import com.amaya.utils.component.VideoComponent;
import com.amaya.component.RotationComponent;
import com.amaya.utils.render.menu.VideoPlayer;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.vialoadingbase.ViaLoadingBase;
import net.viamcp.ViaMCP;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@Getter
public class Amaya {
    public static Amaya Instance = new Amaya();
    public final Minecraft mc = Minecraft.getMinecraft();
    public final String Name = "Amaya";
    public ModuleManager moduleManager;
    public ConfigManager configManager;
    public CommandManager commandManager;
    public VideoComponent videoComponent;
    public WidgetManager widgetManager;
    public UserManager userManager;
    private SlotSpoofComponent slotSpoofComponent;
    public NegativeClickGui negative;
    public DropDownClickGui dropDownClickGui;
    public FallDistanceManager fallDistanceManager;
    public EventManager eventManager;
    public static boolean updateGuiScale;
    public static int prevGuiScale;

    @SneakyThrows
    public void Init(String name){
        userManager = new UserManager();
        eventManager = new EventManager();
        eventManager.register(this);
        this.fallDistanceManager = new FallDistanceManager();
        this.slotSpoofComponent = new SlotSpoofComponent();
        eventManager.register(new MovementComponent());
        eventManager.register(this.fallDistanceManager);
        eventManager.register(new BlinkManager());
        eventManager.register(new RotationComponent());

        try {
            ViaMCP.create();
            ViaMCP.INSTANCE.initAsyncSlider();
            ViaLoadingBase.getInstance().reload(ProtocolVersion.v1_12_2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        moduleManager = new ModuleManager();
        moduleManager.Init(name);
        widgetManager = new WidgetManager();

        negative = new NegativeClickGui(name);
        dropDownClickGui = new DropDownClickGui(name);

        configManager = new ConfigManager();
        configManager.loadConfig("config",moduleManager,widgetManager);

        commandManager = new CommandManager(moduleManager,widgetManager);

        this.videoComponent = new VideoComponent();
        VideoPlayer.init(new File(Minecraft.getMinecraft().mcDataDir + "/" + "AmayaClient", "background.mp4"));
        //改标题为Amaya
        Display.setTitle(Name);
        userManager.setUser(new User(name, null, null, Date.from(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()), ""));
        if (mc.gameSettings.guiScale != 2) {
            prevGuiScale = mc.gameSettings.guiScale;
            updateGuiScale = true;
            mc.gameSettings.guiScale = 2;
            mc.resize(mc.displayWidth - 1, mc.displayHeight);
            mc.resize(mc.displayWidth + 1, mc.displayHeight);
        }
    }

    public void Shutdown(){
        configManager.saveConfig("config",moduleManager,widgetManager);
        try {
            VideoPlayer.stop();
        } catch (FFmpegFrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }
}
