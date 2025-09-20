package cn.gym.module;

import cn.gym.Solitude;
import cn.gym.events.EventManager;
import cn.gym.gui.notification.Notification;
import cn.gym.gui.notification.NotificationManager;
import cn.gym.utils.Instance;
import cn.gym.utils.animations.Animation;
import cn.gym.utils.animations.Direction;
import cn.gym.utils.animations.impl.DecelerateAnimation;
import cn.gym.value.Value;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：Guyuemang
 * @Date：2025/6/1 00:47
 */
@Getter
@Setter
public class Module implements Instance {
    public String name;
    public Category category;
    public String suffix;
    public boolean State;
    private int key = Keyboard.KEY_NONE;
    private final List<Value<?>> settings = new ArrayList<>();
    private final Animation animations = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    public Module(String name,Category category){
        this.name = name;
        this.category = category;
    }

    public void onEnable(){
    }
    public void onDisable(){
    }

    public boolean hasMode() {
        return suffix != null;
    }

    public <M extends Module> M getModule(Class<M> clazz) {
        return Solitude.Instance.getModuleManager().getModule(clazz);
    }

    public <M extends Module> boolean isEnabled(Class<M> module) {
        Module mod = Solitude.Instance.getModuleManager().getModule(module);
        return mod != null && mod.isEnabled();
    }

    public boolean isEnabled() {
        return State;
    }

    public void setState(boolean state) {
        if (mc.theWorld != null) {
            mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 0.5f, state ? 0.6f : 0.5f, false);
        }
        if (this.State != state) {
            this.State = state;
            if (state) {
                EventManager.register(this);
                Solitude.Instance.getNotification().add("Module Toggle", "Module " + this.name + " Enabled", Notification.Type.SUCCESS);
                onEnable();
            } else {
                EventManager.unregister(this);
                Solitude.Instance.getNotification().add("Module Toggle", "Module " + this.name + " Disabled", Notification.Type.ERROR);
                onDisable();
            }
        }
    }

    public void toggle() {
        setState(!State);
    }

    public boolean getState() {
        return State;
    }
}
