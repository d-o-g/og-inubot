package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;

public interface RSHitbar extends RSNode {

    int getCycle();

    int getHealth();

    @Artificial
    default int getHealthPercent() {
        return getHealth() * 100 / 255;
    }
}