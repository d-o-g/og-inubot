package com.inubot.api.event;

import java.util.*;

public abstract class Event {

    /** Valid event states **/
    /* If an event is marked as pending, it means that it has been queued but has yet to be executed */
    public static final byte PENDING  = 0;
    /* If an event is marked as running, then the #fire method has been called */
    public static final byte RUNNING  = 1;
    /* This state means that the event was complete, successfully */
    public static final byte COMPLETE = 2;
    /* An event state marked as failure means that the event was unsuccessful */
    public static final byte FAILURE  = 3;

    /* The events children, these get fired along with this event */
    private final List<Event> delegates;
    /* Any EventListener's attached to this Event */
    private final List<EventListener<?>> listeners;

    /* The parent event, if there is one */
    private Event parent = null;
    /* The current state of the event, see constant values above */
    private byte state = PENDING;

    public Event(Event parent, List<Event> delegates) {
        setParent(parent);
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
        if (parent != null) {
            parent.delegate(this);
        }
    }

    public final void delegate(Event delegate) {
        delegate.setParent(this);
        delegates.add(delegate);
    }

    public final void revoke(Event delegate) {
        delegates.remove(delegate);
    }

    public abstract void execute();

    public Event[] getDelegates() {
        return delegates.toArray(new Event[delegates.size()]);
    }

}
