package com.inubot.client.natives.oldschool;

public interface RSItemDefinition extends RSCacheNode {
    String getName();
    int getId();
    String[] getActions();
    String[] getGroundActions();
}
