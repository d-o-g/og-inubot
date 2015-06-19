package org.runedream.client.natives;

import org.runedream.client.Artificial;
import org.runedream.client.listener.HealthListener;

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
