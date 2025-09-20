package com.amaya.events.events;

public interface Cancellable
{
    boolean isCancelled();
    
    void setCancelled(final boolean p0);
    
    void setCancelled();
}
