package com.inubot.api.event;

public interface EventHandler<T extends Event> {
    boolean handle(T e);
}
