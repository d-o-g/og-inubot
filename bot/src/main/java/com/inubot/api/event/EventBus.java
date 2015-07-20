package com.inubot.api.event;

public interface EventBus {

    void fire(Event e);
    void register(Event e);
    void deregister(Event e);
    EventListener[] listenersFor(Class<? extends Event> c);

    default void fireAsynchonously(Event e) {
        throw new UnsupportedOperationException();
    }
}
