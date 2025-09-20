package com.amaya.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.amaya.events.events.callables.EventCancellable;

/**
 * @Author: Guyuemang
 * 2025/4/23
 */
@Getter
@Setter
@AllArgsConstructor
public class MotionEvent extends EventCancellable{
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public State state;

    public MotionEvent(State state) {
        this.state = state;
    }

    public enum State {
        PRE,
        POST
    }

    public boolean isPre() {
        return state.equals(State.PRE);
    }

    public boolean isPost() {
        return state.equals(State.POST);
    }
}
