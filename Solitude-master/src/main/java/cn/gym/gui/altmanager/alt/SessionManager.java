package cn.gym.gui.altmanager.alt;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;

import static cn.gym.utils.Instance.mc;

public class SessionManager {
  private static Field sessionField = null;

  public static Field getSessionField() {
    if (sessionField == null) {
      try {
        for (Field f : Minecraft.class.getDeclaredFields()) {
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
