package com.inubot.api.event;

import com.inubot.Bot;

public interface EventBus {

    static EventBus currentBus() {
        return Bot.getInstance().getEventBus();
    }

    void fire();

    void register(Event e);

    void deregister(Event e);

    EventListener[] listenersFor(Class<? extends Event> c);

    /**
     * Fires the Event asynchronously
     *
     * @param e the event to fire
     */
    default void fireLater() {
        throw new UnsupportedOperationException();
    }
}
