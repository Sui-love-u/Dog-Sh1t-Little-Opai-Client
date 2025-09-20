package cn.gym.events.impl.misc;

import cn.gym.events.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@Getter
@AllArgsConstructor
public class ChatEvent extends CancellableEvent {
    private final String message;
}
