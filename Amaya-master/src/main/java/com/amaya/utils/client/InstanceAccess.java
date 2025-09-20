/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package com.amaya.utils.client;

import com.amaya.Amaya;
import net.minecraft.client.Minecraft;

public interface InstanceAccess {

    Minecraft mc = Minecraft.getMinecraft();

    Amaya INSTANCE = Amaya.Instance;
}

