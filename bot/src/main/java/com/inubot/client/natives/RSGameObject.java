package com.inubot.client.natives;

import com.inubot.client.Artificial;

@Artificial
public interface RSGameObject extends ClientNative {
    int getX();
    int getY();
    int getPlane();
    int getId();
}
