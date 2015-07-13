package com.inubot.client.natives.modern;

import com.inubot.client.natives.ClientNative;

import java.awt.*;

public interface RSClient extends ClientNative {
    int getMapOffset();
    int getMapScale();
    int getMapState();
    float getMapAngle();
    Rectangle[] getInterfaceBounds();
}
