package com.amaya.events.impl.misc;

import lombok.Getter;
import com.amaya.events.events.callables.EventCancellable;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@Getter
public class KeyEvent extends EventCancellable{
    private final int keyCode;

    public KeyEvent(int keyCode) {
        this.keyCode = keyCode;
    }
}