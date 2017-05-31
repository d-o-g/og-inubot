package com.inubot.client.natives.oldschool;

import com.inubot.client.natives.ClientNative;

import java.util.ArrayList;
import java.util.List;

public interface RSTile extends ClientNative {
    RSEntityMarker[] getObjects();

    RSFloorDecoration getDecoration();

    RSBoundary getBoundary();

    RSBoundaryDecoration getBoundaryDecoration();

    int getX();

    int getY();

    int getPlane();

    default RSTileComponent[] getComponents() {
        List<RSTileComponent> components = new ArrayList<>();
        for (RSEntityMarker entity : getObjects()) {
            if (entity != null) {
                components.add(entity);
            }
        }
        RSBoundary boundary = getBoundary();
        RSBoundaryDecoration boundaryDecoration = getBoundaryDecoration();
        RSFloorDecoration floorDecoration = getDecoration();
        if (boundary != null) {
            components.add(boundary);
        }
        if (boundaryDecoration != null) {
            components.add(boundaryDecoration);
        }
        if (floorDecoration != null) {
            components.add(floorDecoration);
        }
        return components.toArray(new RSTileComponent[components.size()]);
    }
}
