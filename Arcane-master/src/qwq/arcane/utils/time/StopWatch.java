package qwq.arcane.utils.time;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/time/StopWatch.class */
public final class StopWatch {
    private long millis;
    public long lastMS = System.currentTimeMillis();

    public StopWatch() {
        reset();
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= this.millis;
    }

    public boolean hasTimeElapsed(double time) {
        return !hasTimeElapsed((long) time);
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - this.lastMS > time;
    }

    public boolean hasTimePassed(long time) {
        return System.currentTimeMillis() - this.millis > time;
    }

    public void reset() {
        this.millis = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.millis;
    }
}
