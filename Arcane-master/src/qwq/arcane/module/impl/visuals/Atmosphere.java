package qwq.arcane.module.impl.visuals;

import java.awt.Color;
import java.util.Objects;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import qwq.arcane.event.annotations.EventTarget;
import qwq.arcane.event.impl.events.packet.PacketSendEvent;
import qwq.arcane.event.impl.events.player.UpdateEvent;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;
import qwq.arcane.value.impl.ModeValue;
import qwq.arcane.value.impl.NumberValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/Atmosphere.class */
public class Atmosphere extends Module {
    private final BoolValue time;
    private final NumberValue timeValue;
    private static final BoolValue weather = new BoolValue("Weather Editor", true);
    public static final ModeValue weatherValue;
    public static final BoolValue forceSnow;
    public final BoolValue worldColor;
    public final ColorValue worldColorRGB;
    public final BoolValue worldFog;
    public final ColorValue worldFogRGB;
    public final NumberValue worldFogDistance;

    static {
        BoolValue boolValue = weather;
        Objects.requireNonNull(boolValue);
        weatherValue = new ModeValue("Weather", boolValue::get, "Clean", new String[]{"Clean", "Rain", "Thunder", "Snow", "Blizzard"});
        forceSnow = new BoolValue("Force Snow", false);
    }

    public Atmosphere() {
        super("Atmosphere", Category.Visuals);
        this.time = new BoolValue("Time Editor", true);
        BoolValue boolValue = this.time;
        Objects.requireNonNull(boolValue);
        this.timeValue = new NumberValue("Time", boolValue::get, 18000.0d, 0.0d, 24000.0d, 1000.0d);
        this.worldColor = new BoolValue("World Color", true);
        BoolValue boolValue2 = this.worldColor;
        Objects.requireNonNull(boolValue2);
        this.worldColorRGB = new ColorValue("World Color RGB", boolValue2::get, Color.WHITE);
        this.worldFog = new BoolValue("World Fog", false);
        BoolValue boolValue3 = this.worldFog;
        Objects.requireNonNull(boolValue3);
        this.worldFogRGB = new ColorValue("World Fog RGB", boolValue3::get, Color.WHITE);
        BoolValue boolValue4 = this.worldFog;
        Objects.requireNonNull(boolValue4);
        this.worldFogDistance = new NumberValue("World Fog Distance", boolValue4::get, 0.10000000149011612d, -1.0d, 0.8999999761581421d, 0.10000000149011612d);
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (this.time.get().booleanValue()) {
            mc.theWorld.setWorldTime(this.timeValue.get().longValue());
        }
        if (weather.get().booleanValue()) {
            switch (weatherValue.get()) {
                case "Rain":
                    mc.theWorld.setRainStrength(1.0f);
                    mc.theWorld.setThunderStrength(0.0f);
                    break;
                case "Thunder":
                    mc.theWorld.setRainStrength(1.0f);
                    mc.theWorld.setThunderStrength(1.0f);
                    break;
                case "Snow":
                    mc.theWorld.setRainStrength(0.5f);
                    mc.theWorld.setThunderStrength(0.0f);
                    break;
                case "Blizzard":
                    mc.theWorld.setRainStrength(1.0f);
                    mc.theWorld.setThunderStrength(0.0f);
                    break;
                default:
                    mc.theWorld.setRainStrength(0.0f);
                    mc.theWorld.setThunderStrength(0.0f);
                    break;
            }
        }
    }

    @EventTarget
    private void onPacket(PacketSendEvent event) {
        if (this.time.get().booleanValue() && (event.getPacket() instanceof S03PacketTimeUpdate)) {
            event.setCancelled(true);
        }
    }

    public static boolean shouldForceSnow() {
        return forceSnow.get().booleanValue() && (weatherValue.get().equals("Snow") || weatherValue.get().equals("Blizzard"));
    }
}
