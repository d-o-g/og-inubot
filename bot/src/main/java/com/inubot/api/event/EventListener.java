package com.inubot.api.event;

public interface EventListener<T extends Event> {
    /**
     * This method gets called when an event has been fired
     * @param e the event
     */
    void onEvent(T e);
}
