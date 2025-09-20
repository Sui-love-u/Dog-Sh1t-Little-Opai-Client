package com.amaya.utils.fontrender;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;

import java.awt.*;
import java.io.InputStream;

public enum FontManager {
    BOLD("bold"),
    ICON("icon"),
    REGULAR("regular"),
    SEMIBOLD("semibold"),
    neverlose("nlicon"),
    iconfont("iconfont"),

    MINECRAFT("minecraft");

    private final String fontFileName;
    private final Float2ObjectMap<FontRenderer> fontCache = new Float2ObjectArrayMap<>();

    FontManager(String fontFileName) {
        this.fontFileName = fontFileName;
    }

    public FontRenderer get(float size) {
        return get(size, true);
    }

    public FontRenderer get(float size, boolean antiAlias) {
        return fontCache.computeIfAbsent(size, s -> {
            try {
                return createFontRenderer(fontFileName, size, antiAlias);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load font: " + this.name(), e);
            }
        });
    }

    private FontRenderer createFontRenderer(String fontName, float size, boolean antiAlias) {
        try {
            InputStream fontStream = Preconditions.checkNotNull(
                getClass().getResourceAsStream("/assets/minecraft/amaya/fonts/" + fontName + ".ttf"),
                "Font resource not found"
            );
            
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream)
                           .deriveFont(Font.PLAIN, size);
            
            return new FontRenderer(font, antiAlias);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create font renderer", e);
        }
    }
}