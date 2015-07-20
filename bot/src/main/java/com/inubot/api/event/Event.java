package com.inubot.api.event;

public interface Event {

    void fire();
    void addListener(EventListener listener);
    void removeListener(EventListener listener);

    default String verbose() {
        return getClass().getSimpleName();
    }
}
