package com.amaya.utils.animations.impl;

import com.amaya.utils.animations.Animation;
import com.amaya.utils.animations.Direction;
import lombok.Getter;
import lombok.Setter;


public class ContinualAnimation {
    @Setter
    private float output, endpoint;

    @Getter
    private Animation animation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

    public void animate(float destination, int ms) {
        output = (float) (endpoint - animation.getOutput());
        endpoint = destination;
        if (output != (endpoint - destination)) {
            animation = new SmoothStepAnimation(ms, endpoint - output, Direction.BACKWARDS);
        }
    }

    public boolean isDone() {
        return output == endpoint || animation.isDone();
    }


    public float getOutput() {
        output = (float) (endpoint - animation.getOutput());
        return output;
    }
}
