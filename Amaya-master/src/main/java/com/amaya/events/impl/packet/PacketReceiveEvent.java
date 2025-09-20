package com.amaya.events.impl.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import com.amaya.events.events.callables.EventCancellable;

@Getter
@AllArgsConstructor
public class PacketReceiveEvent extends EventCancellable{
    private Packet<?> packet;
}
