/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.collection;

import com.inubot.api.oldschool.PathingEntity;
import com.inubot.api.util.filter.Filter;

public class PathingEntityPool<T extends PathingEntity, K extends PathingEntityPool<T, K>> extends IdentifiableEntityPool<T, K> {

    public PathingEntityPool(final Iterable<T>... elements) {
        super(elements);
    }

    public PathingEntityPool(final T... elements) {
        super(elements);
    }

    public K targeting(final PathingEntity<?>... targets) {
        return include(new Filter<T>() {
            @Override
            public boolean accept(T character) {
                for (final PathingEntity c : targets) {
                    if (c.equals(character.getTarget())) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public K targeting(final boolean targeting) {
        return include(new Filter<T>() {
            @Override
            public boolean accept(T t) {
                return targeting == (t.getTargetIndex() != -1);
            }
        });
    }

    public K animating(final boolean animating) {
        return include(new Filter<T>() {
            @Override
            public boolean accept(T t) {
                return animating == (t.getAnimation() != -1);
            }
        });
    }

    public K animation(final int... animations) {
        return include(t -> {
            for (final int animation : animations) {
                if (t.getAnimation() == animation) {
                    return true;
                }
            }
            return false;
        });
    }

    public K targeting(final int... indexes) {
        return include(t -> {
            for (final int index : indexes) {
                if (t.getTargetIndex() == index) {
                    return true;
                }
            }
            return false;
        });
    }

    public K healthBarVisible(final boolean visible) {
        return include(new Filter<T>() {
            @Override
            public boolean accept(T t) {
                return visible == t.isHealthBarVisible();
            }
        });
    }

    public K healthPercent(Filter<Integer> hpFilter) {
        return include(new Filter<T>() {
            @Override
            public boolean accept(T t) {
                return hpFilter.accept(t.getHealthPercent());
            }
        });
    }
}

