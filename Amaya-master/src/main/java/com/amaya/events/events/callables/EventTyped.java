package com.amaya.events.events.callables;


import com.amaya.events.events.Event;
import com.amaya.events.events.Typed;

public abstract class EventTyped implements Event, Typed
{
    private final byte type;
    
    protected EventTyped(final byte eventType) {
        this.type = eventType;
    }
    
    @Override
    public byte getType() {
        return this.type;
    }
}
