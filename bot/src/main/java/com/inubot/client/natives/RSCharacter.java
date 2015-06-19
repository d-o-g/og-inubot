package com.inubot.client.natives;

import com.inubot.client.Artificial;
import com.inubot.client.listener.HealthListener;
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

    @Artificial
    HealthListener getHealthListener();

    @Artificial
    void setHealthListener(HealthListener listener);
}
