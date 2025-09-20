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
package cn.gym.events.impl.render;

import cn.gym.events.impl.Event;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

public record Render3DEvent(float partialTicks, ScaledResolution scaledResolution) implements Event {

}
