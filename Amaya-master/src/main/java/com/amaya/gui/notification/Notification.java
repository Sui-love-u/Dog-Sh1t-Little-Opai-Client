package com.amaya.gui.notification;

import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.impl.EaseOutSine;
import com.amaya.utils.client.InstanceAccess;
import com.amaya.utils.fontrender.FontManager;
import com.amaya.utils.render.RoundedUtil;
import com.amaya.utils.time.TimerUtil;
import lombok.Getter;
import net.minecraft.client.gui.Gui;

import java.awt.*;

@Getter
public class Notification implements InstanceAccess {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtil timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;

        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new EaseOutSine(250, 1);
    }

    public void drawDefault(float x, float y, float width, float height) {
        RoundedUtil.drawRound(x, y, width, height, 2, new Color(0, 0, 0, 100));
        FontManager.SEMIBOLD.get(18).drawString(getTitle(), x + 11, y + 4f, new Color(255, 255, 255, 255).getRGB());
        FontManager.SEMIBOLD.get(18).drawString(getDescription(), x + 11, y + 14, new Color(255, 255, 255, 255).getRGB());
        RoundedUtil.drawRound(x + 2f, y + 6f, 2, 12, 0.5f,  notificationType.getColor());
        RoundedUtil.drawGradientHorizontal(x, y, ((getTime() - getTimerUtil().getTime()) / getTime()) * width, height, 2,new Color(0, 249, 255, 119),new Color(211, 24, 255, 97));
    }
}
