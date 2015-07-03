/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.api.methods.Players;
import com.inubot.api.methods.Projection;

public interface Locatable {

    public Tile getLocation();

    default int distance(Locatable locatable) {
        return (int) Projection.distance(this, locatable);
    }

    default int distance() {
        return distance(Players.getLocal());
    }

    default int getX() {
        return getLocation().getX();
    }

    default int getY() {
        return getLocation().getY();
    }

    default int getPlane() {
        return getLocation().getPlane();
    }
}
