package qwq.arcane.gui.alt.auth;

import java.lang.reflect.Field;
import net.minecraft.util.Session;
import qwq.arcane.module.Mine;
import qwq.arcane.utils.Instance;

import static qwq.arcane.utils.Instance.mc;

public class SessionManager {
    private static Field sessionField = null;

    public static Field getSessionField() {
        if (sessionField == null) {
            try {
                for (Field f : Mine.class.getDeclaredFields()) {
                    if (f.getType().isAssignableFrom(Session.class)) {
                        sessionField = f;
                        sessionField.setAccessible(true);
                        break;
                    }
                }
            } catch (Exception e) {
                sessionField = null;
            }
        }
        return sessionField;
    }

    public static Session getSession() {
        return mc.getSession();
    }

    public static void setSession(Session session) {
        try {
            getSessionField().set(mc, session);
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
    }
}
