package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;
import com.inubot.client.natives.ClientNative;

@Artificial
public interface RSGameObject extends ClientNative {
    int getX();
    int getY();
    int getPlane();
    int getId();
}
