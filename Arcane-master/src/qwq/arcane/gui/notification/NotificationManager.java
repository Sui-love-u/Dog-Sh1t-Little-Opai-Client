package qwq.arcane.gui.notification;

import java.util.ArrayList;
import java.util.List;
import qwq.arcane.Client;
import qwq.arcane.gui.notification.Notification;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/notification/NotificationManager.class */
public class NotificationManager {
    private final List<Notification> notifications = new ArrayList();

    public void normalrender(double height) {
        if (this.notifications.size() > 4) {
            this.notifications.remove(0);
        }
        double startY = height - 36.0d;
        for (int i = 0; i < this.notifications.size(); i++) {
            Notification notification = this.notifications.get(i);
            this.notifications.removeIf((v0) -> {
                return v0.shouldDelete();
            });
            notification.render(startY, startY);
            startY -= notification.getHeight() + 3.0d;
        }
    }

    public void normalshader(double height) {
        if (this.notifications.size() > 4) {
            this.notifications.remove(0);
        }
        double startY = height - 36.0d;
        for (int i = 0; i < this.notifications.size(); i++) {
            Notification notification = this.notifications.get(i);
            this.notifications.removeIf((v0) -> {
                return v0.shouldDelete();
            });
            notification.shader(startY, startY);
            startY -= notification.getHeight() + 3.0d;
        }
    }

    public void custom(double height) {
        if (this.notifications.size() > 4) {
            this.notifications.remove(0);
        }
        double startY = height - 36.0d;
        for (int i = 0; i < this.notifications.size(); i++) {
            Notification notification = this.notifications.get(i);
            this.notifications.removeIf((v0) -> {
                return v0.shouldDelete();
            });
            notification.custom(startY, startY);
            startY -= 25.0d;
        }
    }

    public void customshader(double height) {
        if (this.notifications.size() > 4) {
            this.notifications.remove(0);
        }
        double startY = height - 36.0d;
        for (int i = 0; i < this.notifications.size(); i++) {
            Notification notification = this.notifications.get(i);
            this.notifications.removeIf((v0) -> {
                return v0.shouldDelete();
            });
            notification.customshader(startY, startY);
            startY -= 25.0d;
        }
    }

    public void type1render(double height) {
        if (this.notifications.size() > 4) {
            this.notifications.remove(0);
        }
        double startY = height;
        for (int i = 0; i < this.notifications.size(); i++) {
            Notification notification = this.notifications.get(i);
            this.notifications.removeIf((v0) -> {
                return v0.shouldDelete();
            });
            notification.render1(startY, startY);
            startY += 18.0d;
        }
    }

    public void type1shader(double height) {
        if (this.notifications.size() > 4) {
            this.notifications.remove(0);
        }
        double startY = height;
        for (int i = 0; i < this.notifications.size(); i++) {
            Notification notification = this.notifications.get(i);
            this.notifications.removeIf((v0) -> {
                return v0.shouldDelete();
            });
            notification.shader1(startY, startY);
            startY += 18.0d;
        }
    }

    public void add(String title, String message, Notification.Type type) {
        if (((qwq.arcane.module.impl.display.Notification) Client.Instance.getModuleManager().getModule(qwq.arcane.module.impl.display.Notification.class)).getState()) {
            this.notifications.add(new Notification(title, message, type, 1500L));
        }
    }

    public void add(String message, Notification.Type type) {
        if (((qwq.arcane.module.impl.display.Notification) Client.Instance.getModuleManager().getModule(qwq.arcane.module.impl.display.Notification.class)).getState()) {
            this.notifications.add(new Notification(type.getName(), message, type, 1500L));
        }
    }

    public void add(String title, String message, Notification.Type type, long time) {
        if (((qwq.arcane.module.impl.display.Notification) Client.Instance.getModuleManager().getModule(qwq.arcane.module.impl.display.Notification.class)).getState()) {
            this.notifications.add(new Notification(title, message, type, time));
        }
    }

    public void add(String message, Notification.Type type, long time) {
        if (((qwq.arcane.module.impl.display.Notification) Client.Instance.getModuleManager().getModule(qwq.arcane.module.impl.display.Notification.class)).getState()) {
            this.notifications.add(new Notification(type.getName(), message, type, time));
        }
    }

    public List<Notification> getNotifications() {
        return this.notifications;
    }
}
