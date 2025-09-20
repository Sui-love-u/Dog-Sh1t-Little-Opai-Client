package qwq.arcane.utils.time;

import org.apache.commons.lang3.RandomUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/time/TimerUtil.class */
public class TimerUtil {
    public long lastMS = System.currentTimeMillis();
    private long currentMs;

    public final long getDifference() {
        return getCurrentMS() - this.lastMS;
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000;
    }

    public long hasTimeLeft(long ms) {
        return (ms + this.lastMS) - System.currentTimeMillis();
    }

    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }

    public boolean hasReached(double milliseconds) {
        return ((double) (getCurrentMS() - this.lastMS)) >= milliseconds;
    }

    public static long randomDelay(int minDelay, int maxDelay) {
        return RandomUtils.nextInt(minDelay, maxDelay);
    }

    public boolean reached(long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - this.lastMS) >= currentTime;
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - this.lastMS > time) {
            if (reset) {
                reset();
                return true;
            }
            return true;
        }
        return false;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - this.lastMS > time;
    }

    public boolean hasElapsed(long milliseconds) {
        return elapsed() > milliseconds;
    }

    public long elapsed() {
        return System.currentTimeMillis() - this.currentMs;
    }

    public boolean delay(float time) {
        return ((float) (System.currentTimeMillis() - this.lastMS)) >= time;
    }

    public void reset2() {
        this.currentMs = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(double time) {
        return hasTimeElapsed((long) time);
    }

    public long getTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public void setTime(long time) {
        this.lastMS = time;
    }
}
