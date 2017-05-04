package com.inubot.bundledscripts.proframework;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public abstract class ProModel {

    private final List<Listener> listeners;

    public ProModel() {
        this.listeners = new ArrayList<>();
    }

    public final void addLater(Listener listener) {
        synchronized (this) {
            listeners.add(listener);
        }
    }

    public final void fireLater(String property) {
        Event<ProModel> event = new Event<>(this, property);
        synchronized (this) {
            listeners.forEach(o -> o.encounter(event));
        }
    }

    public interface Listener {
        void encounter(Event<?> event);
    }

    public static final class Event<T> extends EventObject {

        private final String property;

        /**
         * Constructs a prototypical Event.
         *
         * @param source The object on which the Event initially occurred.
         * @throws IllegalArgumentException if source is null.
         */
        public Event(T source, String property) {
            super(source);
            this.property = property;
        }

        /**
         * @return The source event object
         */
        public final T getSource() {
            return (T) super.getSource();
        }

        /**
         * @return The changed property
         */
        public String getProperty() {
            return property;
        }
    }
}
