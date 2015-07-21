package com.inubot.api.event;

import com.inubot.Bot;

public interface EventBus {

    /**
     * @return The current {@link com.inubot.api.event.EventBus} that is in use
     */
    static EventBus currentBus() {
        return Bot.getInstance().getEventBus();
    }

    /**
     * Sets the current EventBus
     * @param eventBus The new {@link com.inubot.api.event.EventBus} instance
     */
    static void setCurrent(EventBus eventBus) {
        Bot.getInstance().setEventBus(eventBus);
    }

    /**
     * Fires all queued events
     */
    void fire();

    /**
     * Registers an event
     * @param e the {@link com.inubot.api.event.Event} to register
     */
    void register(Event e);

    /**
     * Deregisters an event
     * @param e the {@link com.inubot.api.event.Event} to deregister
     */
    void deregister(Event e);

    /**
     * @param c the event class type
     * @return an array of {@link com.inubot.api.event.EventListener}'s registered to the specified Event class
     */
    EventListener[] listenersFor(Class<? extends Event> c);

    /**
     * Fires the queued events asynchronously
     */
    default void fireLater() {
        throw new UnsupportedOperationException();
    }
}
