package com.inubot.api.event.concurrent;

import com.inubot.api.event.*;
import com.inubot.api.event.EventListener;

import java.util.*;

public class SynchronousEventBus implements EventBus {

    private final Set<Event> events;
    private final Object greedyLock;

    public SynchronousEventBus() {
        this.events = new LinkedHashSet<>();
        this.greedyLock = new Object();
    }

    @Override
    public void fire() {

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
