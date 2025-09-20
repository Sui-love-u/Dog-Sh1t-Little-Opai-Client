package com.amaya.events.impl.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.Packet;
import com.amaya.events.events.callables.EventCancellable;

@Getter
@AllArgsConstructor
public class PacketSendEvent extends EventCancellable{
    private Packet<?> packet;
}
