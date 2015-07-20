package com.inubot.api.event.concurrent;

import com.inubot.api.event.*;

public class SynchronousEventBus implements EventBus {
    @Override
    public void fire(Event e) {

    }

    @Override
    public void register(Event e) {

    }

    @Override
    public void deregister(Event e) {

    }

    @Override
    public EventListener[] listenersFor(Class<? extends Event> c) {
        return new EventListener[0];
    }
}
