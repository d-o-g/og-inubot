package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;
import com.inubot.client.natives.ClientNative;

@Artificial
public interface RSGameObject extends RSTileComponent {
    int getX();

    int getY();

    int getPlane();

    int getId();

    RSRenderable getRenderable();
}
