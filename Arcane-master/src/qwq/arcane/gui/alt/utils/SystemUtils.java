package qwq.arcane.gui.alt.utils;

import java.net.URI;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/utils/SystemUtils.class */
public class SystemUtils {
    public static void openWebLink(URI url) {
        try {
            Class<?> desktop = Class.forName("java.awt.Desktop");
            Object object = desktop.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            desktop.getMethod("browse", URI.class).invoke(object, url);
        } catch (Throwable throwable) {
            System.err.println(throwable.getCause().getMessage());
        }
    }
}
