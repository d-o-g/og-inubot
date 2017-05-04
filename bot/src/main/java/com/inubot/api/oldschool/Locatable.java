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

    Tile getLocation();

    /**
     * Throws an {@link java.lang.UnsupportedOperationException} if this {@link com.inubot.api.oldschool.Locatable}
     * cannot have a {@link com.inubot.api.oldschool.Model}
     *
     * @return The {@link com.inubot.api.oldschool.Model} for this {@link com.inubot.api.oldschool.Locatable}
     */
    default Model getModel() {
        throw new UnsupportedOperationException(String.format("Models unsupported for %s!", getClass().getSimpleName()));
    }

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
