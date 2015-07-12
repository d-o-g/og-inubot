package com.inubot.client.natives.oldschool;

import com.inubot.client.natives.ClientNative;

public interface RSRegion extends ClientNative {
    RSTile[][][] getTiles();
    RSInteractableEntity[] getObjects();
}
