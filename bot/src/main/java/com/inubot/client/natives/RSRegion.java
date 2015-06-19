package com.inubot.client.natives;

public interface RSRegion extends ClientNative {
    RSTile[][][] getTiles();
    RSInteractableEntity[] getObjects();
}
