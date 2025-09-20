package com.amaya.events.impl.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;
import com.amaya.events.events.callables.EventCancellable;

/**
 * @author TG_format
 * @since 2024/8/11 下午5:11
 */
@AllArgsConstructor
@Getter
@Setter
public class PacketSendHigherEvent extends EventCancellable{
    private Packet<?> packet;
    private boolean isNoEvent;
}
