/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package cn.gym.events.impl.player;

import cn.gym.events.impl.Event;
import cn.gym.utils.math.Vector2f;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookEvent implements Event {
    private Vector2f rotation;
    public LookEvent(Vector2f rotation) {
        this.rotation = rotation;
    }
}
