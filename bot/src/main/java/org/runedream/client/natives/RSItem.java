package org.runedream.client.natives;

import org.runedream.client.Artificial;

public interface RSItem extends RSRenderable {

    int getId();
    int getStackSize();

    @Artificial
    int getStrictX();

    @Artificial
    int getStrictY();

    default int getRegionX() {
        return -((getStrictX() >> 7) + 1);
    }

    default int getRegionY() {
        return -((getStrictY() >> 7) + 1);
    }
}
