package com.amaya.events.impl.render;

import lombok.Getter;
import com.amaya.events.events.callables.EventCancellable;

/**
 * @Author: Guyuemang
 * 2025/4/22
 */
@Getter
public class ChatGUIEvent extends EventCancellable{
    private final int mouseX,mouseY;

    public ChatGUIEvent(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
