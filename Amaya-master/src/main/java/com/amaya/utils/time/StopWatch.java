package com.amaya.utils.time;

/**
 * @Author: Guyuemang
 * 2025/5/11
 */
public final class StopWatch {
    private long millis;
    public long lastMS = System.currentTimeMillis();

    public StopWatch() {
        this.reset();
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= this.millis;
    }

    public boolean hasTimeElapsed(double time) {
        return !hasTimeElapsed((long) time);
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
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
