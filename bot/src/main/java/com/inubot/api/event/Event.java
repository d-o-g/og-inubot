package com.inubot.api.event;

import java.util.*;

public abstract class Event {

    public static final byte PENDING  = 0;
    public static final byte RUNNING  = 1;
    public static final byte COMPLETE = 2;
    public static final byte FAILURE  = 3;

    private final List<Event> delegates;
    private final List<EventListener<?>> listeners;

    private Event parent = null;
    private boolean parallel = true;
    private byte state = PENDING;

    public Event(Event parent, List<Event> delegates) {
        this.parent = null;
        this.delegates = delegates;
        this.listeners = new LinkedList<>();
    }

    public Event(Event parent) {
        this(parent, new LinkedList<>());
    }

    public Event(List<Event> delegates) {
        this(null, delegates);
    }

    public Event() {
        this(new LinkedList<>());
    }

    public String verbose() {
        return getClass().getSimpleName();
    }

    public final void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    public final EventListener<?>[] getListeners() {
        return listeners.toArray(new EventListener[listeners.size()]);
    }

    public final byte getState() {
        return state;
    }

    public final void setState(byte state) {
        this.state = state;
    }

    public final Event getParent() {
        return parent;
    }

    public final void setParent(Event parent) {
        this.parent = parent;
    }

    public final void delegate(Event delegate) {
        delegates.add(delegate);
    }

    public final void revoke(Event delegate) {
        delegates.remove(delegate);
    }

    //TODO handle event parallelism
    public final boolean isParallel() {
        return parallel;
    }

    public final void setParallel(boolean parallel) {
        this.parallel = parallel;
    }
}
