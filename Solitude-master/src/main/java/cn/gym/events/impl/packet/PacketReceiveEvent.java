package cn.gym.events.impl.packet;

import cn.gym.events.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.Packet;

@Getter
@AllArgsConstructor
public class PacketReceiveEvent extends CancellableEvent {
    private Packet<?> packet;
}
