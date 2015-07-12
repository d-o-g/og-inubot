package com.inubot.client.natives.modern;

import java.awt.*;

public interface RSClient {
    int getMapOffset();
    int getMapScale();
    int getMapState();
    float getMapAngle();
    Rectangle[] getInterfaceBounds();
}
