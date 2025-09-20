package qwq.arcane.utils.fontrender;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import java.awt.Font;
import java.io.InputStream;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/fontrender/FontManager.class */
public enum FontManager {
    Bold("bold"),
    Icon("icon"),
    Regular("regular"),
    Light("light"),
    Semibold("semibold");

    private final String fontFileName;
    private final Float2ObjectMap<FontRenderer> fontCache = new Float2ObjectArrayMap();

    FontManager(String fontFileName) {
        this.fontFileName = fontFileName;
    }

    public FontRenderer get(float size) {
        return get(size, true);
    }

    public FontRenderer get(float size, boolean antiAlias) {
        return (FontRenderer) this.fontCache.computeIfAbsent(size, s -> {
            try {
                return createFontRenderer(this.fontFileName, size, antiAlias);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load font: " + name(), e);
            }
        });
    }

    private FontRenderer createFontRenderer(String fontName, float size, boolean antiAlias) {
        try {
            InputStream fontStream = (InputStream) Preconditions.checkNotNull(getClass().getResourceAsStream("/assets/minecraft/nothing/font/" + fontName + ".ttf"), "Font resource not found");
            Font font = Font.createFont(0, fontStream).deriveFont(0, size);
            return new FontRenderer(font, antiAlias);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create font renderer", e);
        }
    }
}
