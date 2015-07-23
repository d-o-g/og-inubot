/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.event.impl;

import com.inubot.api.event.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A concurrent implementation of an {@link com.inubot.api.event.EventBus}
 */
public class AsynchronousEventBus implements EventBus {

    private final Set<Event> events;

    public AsynchronousEventBus() {
        this.events = new LinkedHashSet<>();
    }

    @Override
    public void fire() {
        for (Event event : events) {
            event.setState(Event.RUNNING);
            event.execute();
            for (EventListener listener : event.getListeners()) {
                listener.onEvent(event);
            }
            event.setState(Event.COMPLETE);
            synchronized (this) {
                events.remove(event);
            }
            delegate(event.getDelegates());
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
            synchronized (this) {
                event.setParent(null);
                this.events.remove(event);
            }
            delegate(event.getDelegates());
        }
    }

    @Override
    public void register(Event e) {
        events.add(e);
    }

    @Override
    public void deregister(Event e) {
        events.remove(e);
    }
}