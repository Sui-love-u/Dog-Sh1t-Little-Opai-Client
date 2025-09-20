package com.amaya.events.events.callables;


import com.amaya.events.events.Cancellable;
import com.amaya.events.events.Event;

public abstract class EventCancellable implements Event, Cancellable
{
    private boolean cancelled;
    
    protected EventCancellable() {
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean state) {
        this.cancelled = state;
    }
    
    @Override
    public void setCancelled() {
        this.cancelled = true;
    }
}
