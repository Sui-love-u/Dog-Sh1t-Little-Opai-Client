package com.amaya.module.impl.misc;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.player.PreUpdateEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.NumberSetting;
import com.amaya.module.setting.impl.StringSetting;
import com.amaya.utils.client.ChatUtils;
import com.amaya.utils.time.TimerUtil;

@ModuleInfo(name = "Spammer", category = Category.Misc)
public class Spammer extends Module {
    private final StringSetting messages = new StringSetting("Messages", "Check out our server at example.com!");
    private final NumberSetting delay = new NumberSetting("Delay", 1000.0, 100.0, 5000.0, 10.0);
    private final TimerUtil timer = new TimerUtil();

    @EventTarget
    public void onUpdate(PreUpdateEvent event) {
            // 随机选择一个消息发送
        if (timer.hasTimeElapsed(delay.getValue())) {
            ChatUtils.sendMessage(messages.get());
            timer.reset();
        }
    }
}