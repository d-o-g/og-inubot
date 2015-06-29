package com.inubot.client.natives;

import com.inubot.client.Artificial;
import com.inubot.client.listener.HealthListener;

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
