package com.amaya.events.impl.player;

import lombok.Getter;
import lombok.Setter;
import com.amaya.events.events.callables.EventCancellable;

/**
 * @author FuMeng
 * @since 2024/6/1 1:14
 */
@Setter
@Getter
public class PlaceEvent extends EventCancellable{
    private boolean shouldRightClick;
    private int slot;

    public PlaceEvent(int slot) {
        this.slot = slot;
    }

}
