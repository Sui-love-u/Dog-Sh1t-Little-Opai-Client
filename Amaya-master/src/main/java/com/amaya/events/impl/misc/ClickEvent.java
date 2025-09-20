package com.amaya.events.impl.misc;

import com.amaya.events.events.callables.EventCancellable;
import lombok.Getter;

@Getter
public class ClickEvent extends EventCancellable {
    private int key;

    public ClickEvent(int key) {
        this.key = key;
    }

}
