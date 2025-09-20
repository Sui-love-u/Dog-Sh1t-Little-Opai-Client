package cn.gym.events.impl.player;

import cn.gym.events.impl.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * @author FuMeng
 * @since 2024/6/1 1:14
 */
@Setter
@Getter
public class PlaceEvent extends CancellableEvent {
    private boolean shouldRightClick;
    private int slot;

    public PlaceEvent(int slot) {
        this.slot = slot;
    }

}
