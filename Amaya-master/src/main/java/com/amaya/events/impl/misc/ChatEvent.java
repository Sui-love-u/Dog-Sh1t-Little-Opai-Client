package com.amaya.events.impl.misc;

import com.amaya.events.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@Getter
@AllArgsConstructor
public class ChatEvent extends EventCancellable {
    private final String message;
}
