package cn.gym.events.impl.render;

import cn.gym.events.impl.CancellableEvent;
import lombok.Getter;

/**
 * @Author: Guyuemang
 * 2025/4/22
 */
@Getter
public class ChatGUIEvent extends CancellableEvent {
    private final int mouseX,mouseY;

    public ChatGUIEvent(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
