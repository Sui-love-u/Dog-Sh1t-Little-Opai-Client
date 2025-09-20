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
package qwq.arcane.event.impl.events.render;

import net.minecraft.client.gui.ScaledResolution;
import qwq.arcane.event.impl.Event;

public record Render3DEvent(float partialTicks, ScaledResolution scaledResolution) implements Event {

}
