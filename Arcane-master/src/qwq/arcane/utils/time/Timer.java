package qwq.arcane.utils.time;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/time/Timer.class */
public class Timer {
    private long prevMS = getTime();
    private long lastMS;

    public Timer() {
        this.lastMS = -1L;
        this.lastMS = System.currentTimeMillis();
    }

    public boolean delay(float milliSec) {
        return ((float) (getTime() - this.prevMS)) >= milliSec;
    }

    public void reset() {
        this.lastMS = System.currentTimeMillis();
        this.prevMS = getTime();
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

    public long getTime() {
        return System.nanoTime() / 1000000;
    }

    public long getTime2() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public long getDifference() {
        return getTime() - this.prevMS;
    }

    public void setDifference(long difference) {
        this.prevMS = getTime() - difference;
    }

    public boolean hasReached(double delay) {
        return ((double) (System.currentTimeMillis() - this.lastMS)) >= delay;
    }

    public boolean hasReached(boolean active, double delay) {
        return active || hasReached(delay);
    }

    public long getLastMS() {
        return this.lastMS;
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }

    public void setTime(long time) {
        this.lastMS = time;
    }
}
