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
package com.amaya.events.impl.render;

import lombok.Getter;
import net.minecraft.entity.Entity;
import com.amaya.events.events.callables.EventCancellable;

@Getter
public class RenderNameTagEvent extends EventCancellable{

    final Entity entity;

    public RenderNameTagEvent(Entity entity) {
        this.entity = entity;
    }

}