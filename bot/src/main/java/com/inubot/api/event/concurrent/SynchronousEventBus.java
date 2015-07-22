package com.inubot.api.event.concurrent;

import com.inubot.api.event.*;
import com.inubot.api.event.EventListener;

import java.util.*;

/**
 * A concurrent implementation of an {@link com.inubot.api.event.EventBus}
 */
public class SynchronousEventBus implements EventBus {

    private final Set<Event> events;
    private final Object greedyLock;

    public SynchronousEventBus() {
        this.events = new LinkedHashSet<>();
        this.greedyLock = new Object();
    }

    @Override
    public void fire() {
        if (events.size() > 0) {
            synchronized (greedyLock) {
                for (Event event : events) {
                    for (Event delegrate : event.getDelegates()) {
                        for (EventListener el : delegrate.getListeners()) {
                            el.onEvent(delegrate);
                        }
                        delegrate.execute();
                    }
                    for (EventListener el : event.getListeners()) {
                        el.onEvent(event);
                    }
                    event.execute();
                }
            }
        }
    }

    @Override
    public void register(Event e) {
        if (!events.contains(e))
            events.add(e);
    }

    @Override
    public void deregister(Event e) {
        if (events.contains(e))
            events.remove(e);
    }

    @Override
    public EventListener[] listenersFor(Class<? extends Event> c) {
        return new EventListener[0];
    }
}
