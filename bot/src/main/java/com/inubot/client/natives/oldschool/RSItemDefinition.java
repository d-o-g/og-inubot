package com.inubot.client.natives.oldschool;

public interface RSItemDefinition extends RSDoublyLinkedNode {
    String getName();
    int getId();
    String[] getActions();
    String[] getGroundActions();
}
