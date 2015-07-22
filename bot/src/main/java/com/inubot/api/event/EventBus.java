package com.inubot.api.event;

import com.inubot.Bot;

public interface EventBus {

    /**
     * @return The current {@link com.inubot.api.event.EventBus} that is in use
     */
    static EventBus currentBus(boolean concurrent) {
        return concurrent ? Bot.getInstance().getSyncEventBus() : Bot.getInstance().getAsyncEventBus();
    }

    static EventBus currentBus() {
        return currentBus(true);
    }

    /**
     * Sets the current EventBus
     * @param eventBus The new {@link com.inubot.api.event.EventBus} instance
     */
    static void setAsyncBus(EventBus eventBus) {
        Bot.getInstance().setAsyncEventBus(eventBus);
    }

    static void setSyncBus(EventBus eventBus) {
        Bot.getInstance().setSyncEventBus(eventBus);
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
     * Fires the queued events asynchronously
     */
    default void fireLater() {
        throw new UnsupportedOperationException();
    }
}
