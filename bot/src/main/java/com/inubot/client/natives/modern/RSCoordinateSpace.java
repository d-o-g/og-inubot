package com.inubot.client.natives.modern;

import com.inubot.client.natives.ClientNative;

public interface RSCoordinateSpace extends ClientNative {
    RSVector3f getRotation();
    RSVector3f getTranslation();
}
