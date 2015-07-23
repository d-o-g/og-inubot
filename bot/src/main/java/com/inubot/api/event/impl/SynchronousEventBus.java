package com.inubot.api.event.impl;

import com.inubot.api.event.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A concurrent implementation of an {@link com.inubot.api.event.EventBus}
 */
public class SynchronousEventBus implements EventBus {

    private final Set<Event> events;
    private final Object writeLock;

    public SynchronousEventBus() {
        this.events = new LinkedHashSet<>();
        this.writeLock = new Object();
    }

    @Override
    public void fire() {
        if (events.size() > 0) {
            synchronized (this) {
                for (Event event : events) {
                    event.setState(Event.RUNNING);
                    event.execute();
                    for (EventListener listener : event.getListeners()) {
                        listener.onEvent(event);
                    }
                    event.setState(Event.COMPLETE);
                    synchronized (writeLock) {
                        events.remove(event);
                    }
                    delegate(event.getDelegates());
                }
            }
        }
    }

    private void delegate(Event[] events) {
        for (Event event : events) {
            event.setState(Event.RUNNING);
            event.execute();
            for (EventListener listener : event.getListeners()) {
                listener.onEvent(event);
            }
            event.setState(Event.COMPLETE);
            synchronized (writeLock) {
                event.setParent(null);
                this.events.remove(event);
            }
            delegate(event.getDelegates());
        }
    }

    @Override
    public void register(Event e) {
        synchronized (this) {
            events.add(e);
        }
    }

    @Override
    public void deregister(Event e) {
        synchronized (this) {
            events.remove(e);
        }
    }
}
