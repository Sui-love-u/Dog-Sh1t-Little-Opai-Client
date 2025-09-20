package qwq.arcane.gui.alt.utils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/utils/Notification.class */
public class Notification {
    private final String message;
    private final long duration;
    private final long startTime = System.currentTimeMillis();

    public String getMessage() {
        return this.message;
    }

    public Notification(String message, long duration) {
        this.message = message;
        this.duration = duration;
    }

    public boolean isExpired() {
        return this.duration >= 0 && this.duration < System.currentTimeMillis() - this.startTime;
    }
}
