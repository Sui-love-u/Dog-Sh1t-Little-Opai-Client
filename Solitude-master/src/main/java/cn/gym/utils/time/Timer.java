package cn.gym.utils.time;

public class Timer {

    private long prevMS;
    private long lastMS = -1L;

    public Timer() {
        this.lastMS = System.currentTimeMillis();
        this.prevMS = getTime();
    }

    public boolean delay(float milliSec) {
        return (float) (getTime() - this.prevMS) >= milliSec;
    }

    public void reset() {
        this.lastMS = System.currentTimeMillis();
        this.prevMS = getTime();
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset)
                reset();

            return true;
        }

        return false;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }
    public long getTime2() {
        return System.currentTimeMillis() - this.lastMS;
    }
    public long getDifference() {
        return getTime() - this.prevMS;
    }

    public void setDifference(long difference) {
        this.prevMS = (getTime() - difference);
    }

    public boolean hasReached(double delay) {
        return System.currentTimeMillis() - this.lastMS >= delay;
    }

    public boolean hasReached(boolean active, double delay) {
        return active || hasReached(delay);
    }

    public long getLastMS() {
        return lastMS;
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - lastMS;
    }

    public long getCurrentTime() {
        return System.nanoTime() / 1000000L;
    }

    public void setTime(long time) {
        lastMS = time;
    }

}
