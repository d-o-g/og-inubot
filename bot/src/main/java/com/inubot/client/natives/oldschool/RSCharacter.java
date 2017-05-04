package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;

public interface RSCharacter extends RSRenderable {

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
