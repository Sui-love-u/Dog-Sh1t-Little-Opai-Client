package cn.gym.events.impl.packet;

import cn.gym.events.impl.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@AllArgsConstructor
public class HigherPacketEvent implements Event {
    @Setter
    private Packet<?> packet;
}
