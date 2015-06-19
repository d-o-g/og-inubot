/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.collection;

import org.runedream.api.oldschool.Locatable;
import org.runedream.api.util.Identifiable;
import org.runedream.api.util.filter.*;

import java.util.regex.Pattern;

public class IdentifiableEntityPool<T extends Locatable & Identifiable, K extends IdentifiableEntityPool<T, K>> extends LocatablePool<T, K> {

    public IdentifiableEntityPool(final T... elements) {
        super(elements);
    }

    public IdentifiableEntityPool(final Iterable<T>... elements) {
        super(elements);
    }

    public K identify(final String... names) {
        return include(new NameFilter<T>(names));
    }

    public K identify(final Pattern... patterns) {
        return include(new Filter<T>() {
            @Override
            public boolean accept(T t) {
                for (final Pattern pattern : patterns) {
                    if (t.getName().matches(pattern.pattern())) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public K identify(final int... ids) {
        return include(new IdFilter<T>(ids));
    }
}

