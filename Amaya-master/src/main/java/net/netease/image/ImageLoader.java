/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.image;

import net.netease.gui.GermGameElement;
import net.netease.gui.GermGameGui;

import java.io.IOException;

public class ImageLoader {
    public static boolean loadedImage;

    public static void loadImage() throws IOException {
        if (!loadedImage) {
            for (GermGameElement element : GermGameGui.INSTANCE.getElements()) {
                element.loadTexture();
                element.loadHoverTexture();
            }
            loadedImage = true;
        }
    }
}

