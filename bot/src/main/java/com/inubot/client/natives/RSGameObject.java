package com.inubot.client.natives;

import com.inubot.client.Artificial;
import com.inubot.client.Artificial;

/**
 * @author unsigned
 * @since 26-04-2015
 */
@Artificial
public interface RSGameObject extends ClientNative {
    int getX();
    int getY();
    int getPlane();
    int getId();
}
