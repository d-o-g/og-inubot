package com.inubot.client.natives;

public interface RSTile extends ClientNative {
    RSInteractableEntity[] getObjects();
    RSFloorDecoration getDecoration();
    RSBoundary getBoundary();
    RSBoundaryDecoration getBoundaryDecoration();
    int getX();
    int getY();
    int getPlane();
}
