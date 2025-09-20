package qwq.arcane.utils.render;

import qwq.arcane.utils.Instance;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/render/SoundUtil.class */
public class SoundUtil implements Instance {
    private int ticksExisted;

    public static void playSound(String sound) {
        playSound(sound, 1.0f, 1.0f);
    }

    public static void playSound(String sound, float volume, float pitch) {
        mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, sound, volume, pitch, false);
    }
}
