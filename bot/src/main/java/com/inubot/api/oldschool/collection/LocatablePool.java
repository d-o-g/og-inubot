/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.collection;

import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Locatable;
import com.inubot.api.oldschool.Tile;

public class LocatablePool<T extends Locatable, K extends LocatablePool<T, K>> extends Pool<T, K> {

    /**
     * {@inheritDoc}
     */
    public LocatablePool(final T... elements) {
        super(elements);
    }

    /**
     * {@inheritDoc}
     */
    public LocatablePool(final Iterable<T>... elements) {
        super(elements);
    }

    /**
     * sorts the elements in this pool by distance, nearest to furthest
     *
     * @return self
     */
    public final K nearest() {
        return sortByDistance(true);
    }

    private T firstSorted(final boolean closest) {
        return sortByDistance(closest).first();
    }

    public final T closest() {
        return firstSorted(true);
    }

    public final T furthest() {
        return firstSorted(false);
    }

    /**
     * sorts the elements in this pool by distance, furthest to nearest
     *
     * @return self
     */
    public final K farthest() {
        return sortByDistance(false);
    }

    private K sortByDistance(final boolean closest) {
        return sort((o1, o2) -> o1.distance() == o2.distance() ? 0 : (closest ? 1 : -1) * Double.compare(o1.distance(), o2.distance()));
    }

    public final K at(final Tile where) {
        return include(t -> t.getLocation().equals(where));
    }

    public final K within(final Locatable renderable, final double distance) {
        return include(t -> t.distance(renderable) <= distance);
    }

    public final K within(final double distance) {
        return within(Players.getLocal(), distance);
    }

    public final K floorLevel(final int... z) {
        return include(t -> {
            for (final int _z : z) {
                if (t.getPlane() == _z) {
                    return true;
                }
            }
            return false;
        });
    }
}

