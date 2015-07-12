package com.inubot.client.natives.oldschool;

public interface RSCharacter extends RSRenderable {

    int getX();
    int getY();
    int getAnimation();
    int getInteractingIndex();
    int getHealth();
    int getMaxHealth();
    int getHealthBarCycle();
    int getQueueSize();

}
