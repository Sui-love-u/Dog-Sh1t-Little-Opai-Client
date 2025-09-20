package com.amaya.module.impl.world;

import com.amaya.events.EventTarget;
import com.amaya.events.impl.misc.TickEvent;
import com.amaya.events.impl.packet.PacketReceiveEvent;
import com.amaya.events.impl.player.MotionEvent;
import com.amaya.module.Category;
import com.amaya.module.Module;
import com.amaya.module.ModuleInfo;
import com.amaya.module.setting.impl.BooleanSetting;
import com.amaya.module.setting.impl.ModeSetting;
import com.amaya.module.setting.impl.NumberSetting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@ModuleInfo(name = "Ambience", category = Category.World)
public class Ambience extends Module {
    private final ModeSetting mode = new ModeSetting("Time-Mode", "Static", new String[]{"Static","Cycle"});
    private final ModeSetting weathermode = new ModeSetting("Weather-Mode", "Rain", new String[]{"Rain","Clear"});
    private final NumberSetting cycleSpeed = new NumberSetting("Cycle-Speed", 24.0, 21.0, 24.0, 1.0);
    private final BooleanSetting reverseCycle = new BooleanSetting("Reverse-Cycle", false);
    private final NumberSetting time = new NumberSetting("Static-Time", 24000.0, 0.0, 24000.0, 100.0);
    private final NumberSetting rainstrength = new NumberSetting("Rain-Strength", 0.1, 0.1, 0.5, 0.1);
    private int timeCycle = 0;

    @Override
    public void onEnable() {
        this.timeCycle = 0;
    }
    @EventTarget
    public void onMotion(MotionEvent event) {
        setSuffix(mode.get());
    }
    @EventTarget
    public void onUpdate(TickEvent event) {
        switch (mode.getValue()) {
            case "Static": {
                mc.theWorld.setWorldTime(((Double) this.time.getValue()).longValue());
            }
            break;
            case "Cycle": {
                mc.theWorld.setWorldTime(this.timeCycle);
                this.timeCycle = (int) ((double) this.timeCycle + ((Boolean) this.reverseCycle.get() != false ? -((Double) this.cycleSpeed.getValue()).doubleValue() : (Double) this.cycleSpeed.getValue()) * 10.0);
                if (this.timeCycle > 24000) {
                    this.timeCycle = 0;
                } else if (this.timeCycle < 0) {
                    this.timeCycle = 24000;
                }
            }
            break;
        }
        switch (weathermode.getValue()) {
            case "Clear": {
                mc.theWorld.setRainStrength(0.0f);
            }
            break;
            case "Rain": {
                mc.theWorld.setRainStrength(((Double) this.rainstrength.getValue()).longValue());
            }
            break;
        }
    }

    @EventTarget
    public void onPacket(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    }
}