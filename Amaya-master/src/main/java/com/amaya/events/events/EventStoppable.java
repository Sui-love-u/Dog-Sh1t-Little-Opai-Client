package com.amaya.events.events;

import lombok.Getter;

@Getter
public abstract class EventStoppable implements Event
{
    private boolean stopped;
    
    protected EventStoppable() {
    }
    
    public void stop() {
        this.stopped = true;
    }

}
