package com.inubot.api.event;

public interface EventListener<T extends Event> {
    void onEvent(T e);
}
