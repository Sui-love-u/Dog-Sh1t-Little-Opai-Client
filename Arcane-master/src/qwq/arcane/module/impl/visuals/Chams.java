package qwq.arcane.module.impl.visuals;

import java.awt.Color;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import qwq.arcane.module.Category;
import qwq.arcane.module.Module;
import qwq.arcane.utils.render.RenderUtil;
import qwq.arcane.value.impl.BoolValue;
import qwq.arcane.value.impl.ColorValue;

/* loaded from: Arcane 8.10.jar:qwq/arcane/module/impl/visuals/Chams.class */
public class Chams extends Module {
    public final BoolValue occludedFlatProperty;
    public final BoolValue visibleFlatProperty;
    public final BoolValue textureOccludedProperty;
    public final BoolValue textureVisibleProperty;
    public final ColorValue visibleColorProperty;
    public final ColorValue occludedColorProperty;

    public Chams() {
        super("Chams", Category.Visuals);
        this.occludedFlatProperty = new BoolValue("Occluded Flat", true);
        this.visibleFlatProperty = new BoolValue("Visible Flat", true);
        this.textureOccludedProperty = new BoolValue("Tex Occluded", false);
        this.textureVisibleProperty = new BoolValue("Tex Visible", false);
        this.visibleColorProperty = new ColorValue("V-Color", Color.RED);
        this.occludedColorProperty = new ColorValue("O-Color", Color.GREEN);
    }

    public static void preRenderOccluded(boolean disableTexture, int occludedColor, boolean occludedFlat) {
        if (disableTexture) {
            GL11.glDisable(3553);
        }
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        if (occludedFlat) {
            GL11.glDisable(2896);
        }
        GL11.glEnable(32823);
        GL11.glPolygonOffset(0.0f, -1000000.0f);
        OpenGlHelper.setLightmapTextureCoords(1, 240.0f, 240.0f);
        GL11.glDepthMask(false);
        RenderUtil.color(occludedColor);
    }

    public static void preRenderVisible(boolean disableTexture, boolean enableTexture, int visibleColor, boolean visibleFlat, boolean occludedFlat) {
        if (enableTexture) {
            GL11.glEnable(3553);
        } else if (disableTexture) {
            GL11.glDisable(3553);
        }
        GL11.glDepthMask(true);
        if (occludedFlat && !visibleFlat) {
            GL11.glEnable(2896);
        } else if (!occludedFlat && visibleFlat) {
            GL11.glDisable(2896);
        }
        RenderUtil.color(visibleColor);
        GL11.glDisable(32823);
    }

    public static void postRender(boolean enableTexture, boolean visibleFlat) {
        if (visibleFlat) {
            GL11.glEnable(2896);
        }
        if (enableTexture) {
            GL11.glEnable(3553);
        }
        GL11.glDisable(3042);
    }
}
