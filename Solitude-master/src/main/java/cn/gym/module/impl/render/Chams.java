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
package cn.gym.module.impl.render;

import cn.gym.module.Category;
import cn.gym.module.Module;
import cn.gym.utils.render.RenderUtil;
import cn.gym.value.impl.BooleanValue;
import cn.gym.value.impl.ColorValue;
import net.minecraft.client.renderer.OpenGlHelper;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Chams extends Module {

    public final BooleanValue occludedFlatProperty = new BooleanValue("Occluded Flat", true);
    public final BooleanValue visibleFlatProperty = new BooleanValue("Visible Flat", true);
    public final BooleanValue textureOccludedProperty = new BooleanValue("Tex Occluded", false);
    public final BooleanValue textureVisibleProperty = new BooleanValue("Tex Visible", false);
    public final ColorValue visibleColorProperty = new ColorValue("V-Color", Color.RED);
    public final ColorValue occludedColorProperty = new ColorValue("O-Color", Color.GREEN);

    public Chams() {
        super("Chams",Category.Render);
    }

    public static void preRenderOccluded(boolean disableTexture, int occludedColor, boolean occludedFlat) {
        if (disableTexture)
            glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        if (occludedFlat)
            glDisable(GL_LIGHTING);
        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(0.0F, -1000000.0F);
        OpenGlHelper.setLightmapTextureCoords(1, 240.0F, 240.0F);
        glDepthMask(false);
        RenderUtil.color(occludedColor);
    }

    public static void preRenderVisible(boolean disableTexture, boolean enableTexture, int visibleColor, boolean visibleFlat, boolean occludedFlat) {
        if (enableTexture)
            glEnable(GL_TEXTURE_2D);
        else if (disableTexture)
            glDisable(GL_TEXTURE_2D);

        glDepthMask(true);
        if (occludedFlat && !visibleFlat)
            glEnable(GL_LIGHTING);
        else if (!occludedFlat && visibleFlat)
            glDisable(GL_LIGHTING);

        RenderUtil.color(visibleColor);
        glDisable(GL_POLYGON_OFFSET_FILL);
    }

    public static void postRender(boolean enableTexture, boolean visibleFlat) {
        if (visibleFlat)
            glEnable(GL_LIGHTING);
        if (enableTexture)
            glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }
}