package qwq.arcane.utils.animations;

import qwq.arcane.utils.time.TimerUtil;

/* loaded from: Arcane 8.10.jar:qwq/arcane/utils/animations/Animation.class */
public abstract class Animation {
    public TimerUtil timerUtil;
    protected int duration;
    protected double endPoint;
    protected Direction direction;

    protected abstract double getEquation(double d);

    public boolean isState() {
        return this.direction.forwards();
    }

    public void setState(boolean sb) {
        if (sb) {
            setDirection(Direction.FORWARDS);
        } else {
            setDirection(Direction.BACKWARDS);
        }
    }

    public Animation(int ms, double endPoint) {
        this(ms, endPoint, Direction.FORWARDS);
    }

    public Animation(int ms, double endPoint, Direction direction) {
        this.timerUtil = new TimerUtil();
        this.duration = ms;
        this.endPoint = endPoint;
        this.direction = direction;
    }

    public boolean finished(Direction direction) {
        return isDone() && this.direction.equals(direction);
    }

    public double getLinearOutput() {
        return 1.0d - ((this.timerUtil.getTime() / this.duration) * this.endPoint);
    }

    public double getEndPoint() {
        return this.endPoint;
    }

    public void setEndPoint(double endPoint) {
        this.endPoint = endPoint;
    }

    public void reset() {
        this.timerUtil.reset();
    }

    public boolean isDone() {
        return this.timerUtil.hasTimeElapsed(this.duration);
    }

    public void changeDirection() {
        setDirection(this.direction.opposite());
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Animation setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            this.timerUtil.setTime(System.currentTimeMillis() - (this.duration - Math.min(this.duration, this.timerUtil.getTime())));
        }
        return this;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    protected boolean correctOutput() {
        return false;
    }

    public Double getOutput() {
        if (this.direction.forwards()) {
            if (isDone()) {
                return Double.valueOf(this.endPoint);
            }
            return Double.valueOf(getEquation(this.timerUtil.getTime() / this.duration) * this.endPoint);
        }
        if (isDone()) {
            return Double.valueOf(0.0d);
        }
        if (correctOutput()) {
            double revTime = Math.min(this.duration, Math.max(0L, this.duration - this.timerUtil.getTime()));
            return Double.valueOf(getEquation(revTime / this.duration) * this.endPoint);
        }
        return Double.valueOf((1.0d - getEquation(this.timerUtil.getTime() / this.duration)) * this.endPoint);
    }
}
