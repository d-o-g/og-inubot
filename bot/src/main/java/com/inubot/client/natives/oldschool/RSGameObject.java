package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;

@Artificial
public interface RSGameObject extends RSTileComponent {
    int getX();

    int getY();

    int getPlane();

    int getId();

    RSEntity getEntity();
}
