package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;

public interface RSPathingEntity extends RSEntity {

    int getX();

    int getY();

    int getAnimation();

    int getInteractingIndex();

    int getQueueSize();

    int getOrientation();

    int[] getHitsplatCycles();

    RSNodeIterable<RSHealthBar> getHealthBars();

    @Artificial
    default int getSceneX() {
        return getX() >> 7;
    }

    @Artificial
    default int getSceneY() {
        return getY() >> 7;
    }
}
