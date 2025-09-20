package qwq.arcane.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import qwq.arcane.Client;
import qwq.arcane.gui.notification.Notification;
import qwq.arcane.module.impl.combat.Gapple;

import qwq.arcane.utils.Instance;
import qwq.arcane.utils.animations.Animation;
import qwq.arcane.utils.animations.Direction;
import qwq.arcane.utils.animations.impl.DecelerateAnimation;
import qwq.arcane.value.Value;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/Module.class */
public class Module implements Instance {
    public String name;
    public Category category;
    public boolean State;
    private String suffix = "";
    private int key = 0;
    private final List<Value<?>> settings = new ArrayList();
    private final Animation animations = new DecelerateAnimation(250, 1.0d).setDirection(Direction.BACKWARDS);

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public int getKey() {
        return this.key;
    }

    public List<Value<?>> getSettings() {
        return this.settings;
    }

    public Animation getAnimations() {
        return this.animations;
    }

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public <M extends Module> M getModule(Class<M> cls) {
        return (M) Client.Instance.getModuleManager().getModule(cls);
    }

    public boolean isGapple() {
        Gapple gapple = (Gapple) Client.Instance.getModuleManager().getModule(Gapple.class);
        if (gapple.getState()) {
            return true;
        }
        return false;
    }

    public <M extends Module> boolean isEnabled(Class<M> module) {
        Module mod = Client.Instance.getModuleManager().getModule(module);
        return mod != null && mod.isEnabled();
    }

    public void setsuffix(String tag) {
        if (tag != null && !tag.isEmpty()) {
            String tagStyle = ((String) Optional.ofNullable((qwq.arcane.module.impl.display.ArrayList) getModule(qwq.arcane.module.impl.display.ArrayList.class)).map(m -> {
                return m.tags.get();
            }).orElse("")).toLowerCase();
            if (((qwq.arcane.module.impl.display.ArrayList) getModule(qwq.arcane.module.impl.display.ArrayList.class)).suffixColor.getValue().booleanValue()) {
                switch (tagStyle) {
                    case "simple":
                        this.suffix = " " + tag;
                        break;
                    case "dash":
                        this.suffix = " - " + tag;
                        break;
                    case "bracket":
                        this.suffix = " [" + tag + "]";
                        break;
                    default:
                        this.suffix = "";
                        break;
                }
            }
            switch (tagStyle) {
                case "simple":
                    this.suffix = "§f " + tag;
                    break;
                case "dash":
                    this.suffix = "§f - " + tag;
                    break;
                case "bracket":
                    this.suffix = "§f [" + tag + "]";
                    break;
                default:
                    this.suffix = "";
                    break;
            }
        }
        this.suffix = "";
    }

    public boolean isEnabled() {
        return this.State;
    }

    public void setState(boolean state) throws SecurityException {
        if (mc.theWorld != null) {
            mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 0.5f, state ? 0.6f : 0.5f, false);
        }
        if (this.State != state) {
            this.State = state;
            if (state) {
                Client.Instance.getEventManager().register(this);

                qwq.arcane.module.impl.display.Notification notificationModule = (qwq.arcane.module.impl.display.Notification) Client.Instance.getModuleManager().getModule(qwq.arcane.module.impl.display.Notification.class);
                if (notificationModule != null && notificationModule.getState()) {
                    Client.Instance.getNotification().add("Module Toggle", "Module " + this.name + " Enabled", Notification.Type.SUCCESS);
                }
                onEnable();
                return;
            }
            Client.Instance.getEventManager().unregister(this);
            qwq.arcane.module.impl.display.Notification notificationModule2 = (qwq.arcane.module.impl.display.Notification) Client.Instance.getModuleManager().getModule(qwq.arcane.module.impl.display.Notification.class);
            if (notificationModule2 != null && notificationModule2.getState()) {
                Client.Instance.getNotification().add("Module Toggle", "Module " + this.name + " Disabled", Notification.Type.ERROR);
            }
            onDisable();
        }
    }

    public void toggle() throws SecurityException {
        setState(!this.State);
    }

    public boolean getState() {
        return this.State;
    }
}
