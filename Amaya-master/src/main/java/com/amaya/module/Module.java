package com.amaya.module;

import com.amaya.Amaya;
import com.amaya.events.EventManager;
import com.amaya.gui.notification.NotificationManager;
import com.amaya.gui.notification.NotificationType;
import com.amaya.module.impl.combat.Gapple;
import com.amaya.module.impl.display.Notification;
import com.amaya.module.setting.Setting;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.animations.impl.DecelerateAnimation;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
public class Module {
    public static Minecraft mc = Minecraft.getMinecraft();
    public float animation = 0f;
    public String suffix;
    private boolean state;
    private int key = Keyboard.KEY_NONE;
    @Getter
    private final List<Setting<?>> settings = new ArrayList<>();
    @Getter
    private final Animation animations = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    public Module(){
        ModuleInfo info = getClass().getAnnotation(ModuleInfo.class);
        if (info == null) {
            throw new IllegalStateException("Module must be annotated with @ModuleInfo");
        }
    }
    public boolean hasMode() {
        return suffix != null;
    }
    public void onEnable() {}
    public void onDisable() {}

    public String getName() {
        return getClass().getAnnotation(ModuleInfo.class).name();
    }

    public Category getCategory() {
        return getClass().getAnnotation(ModuleInfo.class).category();
    }

    public Setting<?> getSetting(final String SettingName) {
        return this.settings.stream().filter(setting -> setting.getName().toLowerCase().equals(SettingName.toLowerCase())).findFirst().orElse(null);
    }
    public String getSuffix() {
        return suffix;
    }
    public <M extends Module> M getModule(Class<M> clazz) {
        return Amaya.Instance.moduleManager.getModule(clazz);
    }
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        if (mc.theWorld != null) {
            mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 0.5f, state ? 0.6f : 0.5f, false);
        }
        if (this.state != state) {
            this.state = state;
            if (state) {
                EventManager.register(this);
                if (Notification.toggleNotifications.get()) {
                    NotificationManager.post(NotificationType.SUCCESS, getName(), "Enable Module");
                }
                onEnable();
            } else {
                EventManager.unregister(this);
                if (Notification.toggleNotifications.get()) {
                    NotificationManager.post(NotificationType.DISABLE, getName(), "Disable Module");
                }
                onDisable();
            }
        }
    }
    public void setStateSilent(boolean state) {
        if (this.state == state) return;

        this.state = state;

        if (state) {
            EventManager.register(this);
            onEnable();
        } else {
            EventManager.unregister(this);
            onDisable();
        }
    }
    public boolean isGapple() {
        return Gapple.eating || Gapple.eating;
    }

    public void toggle() {
        setState(!state);
    }
}