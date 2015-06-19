package org.runedream.client.natives;

public interface RSRegion extends ClientNative {
    RSTile[][][] getTiles();
    RSInteractableEntity[] getObjects();
}
