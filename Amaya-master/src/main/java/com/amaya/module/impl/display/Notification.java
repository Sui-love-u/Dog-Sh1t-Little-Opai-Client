package com.amaya.module.impl.display;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.render.Render2DEvent;
import com.amaya.gui.notification.NotificationManager;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import com.amaya.utils.fontrender.FontManager;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @Author: Guyuemang
 * 2025/5/10
 */
@ModuleInfo(name = "Notification",category = Category.Display)
public class Notification extends Module {
    private final NumberSetting time = new NumberSetting("Time on Screen", 2, 1, 10, .5);
    private final ModeSetting modes = new ModeSetting("Mode", "Default", new String[]{"Default"});
    public static final BooleanSetting toggleNotifications = new BooleanSetting("Show Toggle", true);

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        switch (modes.get()) {
            case "Default": {
                float yOffset = 0;
                int notificationHeight;
                int notificationWidth;
                int actualOffset;
                ScaledResolution sr = new ScaledResolution(mc);

                NotificationManager.setToggleTime(time.getValue().floatValue());

                for (com.amaya.gui.notification.Notification notification : NotificationManager.getNotifications()) {
                    Animation animation = notification.getAnimation();
                    animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);
                    if (animation.finished(Direction.BACKWARDS)) {
                        NotificationManager.getNotifications().remove(notification);
                        continue;
                    }
                    float x, y;

                    animation.setDuration(250);
                    actualOffset = 10;
                    notificationHeight = 24;
                    notificationWidth = (int) Math.max(FontManager.REGULAR.get(20).getStringWidth(notification.getTitle()), FontManager.REGULAR.get(20).getStringWidth(notification.getDescription())) + 25;

                    x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + 0 + notificationHeight + (15));

                    notification.drawDefault(x, y, notificationWidth, notificationHeight);

                    yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();


                }
                break;
            }
        }
    }
}
