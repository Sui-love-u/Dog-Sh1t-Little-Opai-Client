package qwq.arcane;

import de.florianmichael.viamcp.ViaMCP;
import org.lwjgl.opengl.Display;
import qwq.arcane.command.CommandManager;
import qwq.arcane.config.ConfigManager;
import qwq.arcane.event.EventManager;
import qwq.arcane.gui.clickgui.arcane.ArcaneClickGui;
import qwq.arcane.gui.clickgui.dropdown.DropDownClickGui;
import qwq.arcane.gui.notification.NotificationManager;
import qwq.arcane.module.ClientApplication;
import qwq.arcane.module.ModuleManager;
import qwq.arcane.utils.Instance;
import qwq.arcane.utils.pack.BlinkComponent;
import qwq.arcane.utils.player.PingerUtils;
import qwq.arcane.utils.player.SlotSpoofComponent;
import qwq.arcane.utils.rotation.RotationManager;

/* loaded from: Arcane 8.10.jar:qwq/arcane/Client.class */
public class Client implements Instance {
    private EventManager eventManager;
    private ModuleManager moduleManager;
    private ArcaneClickGui arcaneClickGui;
    private DropDownClickGui dropDownClickGui;
    private ConfigManager configManager;
    private NotificationManager notification;
    private CommandManager commandManager;
    private RotationManager rotationManager;
    private PingerUtils pingerUtils;
    int startTime;
    public static Client Instance = new Client();
    public static String name = "Arcane";
    public static String version = " Release 1.7";
    public static boolean debug = true;

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public void setArcaneClickGui(ArcaneClickGui arcaneClickGui) {
        this.arcaneClickGui = arcaneClickGui;
    }

    public void setDropDownClickGui(DropDownClickGui dropDownClickGui) {
        this.dropDownClickGui = dropDownClickGui;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void setNotification(NotificationManager notification) {
        this.notification = notification;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void setRotationManager(RotationManager rotationManager) {
        this.rotationManager = rotationManager;
    }

    public void setPingerUtils(PingerUtils pingerUtils) {
        this.pingerUtils = pingerUtils;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public ArcaneClickGui getArcaneClickGui() {
        return this.arcaneClickGui;
    }

    public DropDownClickGui getDropDownClickGui() {
        return this.dropDownClickGui;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public NotificationManager getNotification() {
        return this.notification;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public RotationManager getRotationManager() {
        return this.rotationManager;
    }

    public PingerUtils getPingerUtils() {
        return this.pingerUtils;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void Init() {

        this.startTime = (int) System.currentTimeMillis();

//        if (ClientApplication.Hwid) {
//            System.exit(0);
//        }
//        if (debug) {
//            System.exit(0);
//        }

        ViaMCP.create();
        ViaMCP.INSTANCE.initAsyncSlider();
        Display.setTitle(name + " " + version);
        this.eventManager = new EventManager();
        this.eventManager.register(this);
        this.eventManager.register(new BlinkComponent());
        this.eventManager.register(new SlotSpoofComponent());
        this.rotationManager = new RotationManager();
        this.eventManager.register(this.rotationManager);
        this.pingerUtils = new PingerUtils();
        this.eventManager.register(this.pingerUtils);
        this.moduleManager = new ModuleManager();
        this.moduleManager.Init();
        this.notification = new NotificationManager();
        this.configManager = new ConfigManager();
        ConfigManager configManager = this.configManager;
        ConfigManager.loadConfig("config", this.moduleManager);
        this.commandManager = new CommandManager(this.moduleManager);
        this.arcaneClickGui = new ArcaneClickGui();
        this.dropDownClickGui = new DropDownClickGui();
    }
}
