/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.util.filter;

import com.inubot.apis.util.Identifiable;

public class NameFilter<I extends Identifiable> implements Filter<I> {

    private final String[] names;
    private final boolean contains;

    public NameFilter(final boolean contains, final String... names) {
        this.contains = contains;
        this.names = names;
    }

    public NameFilter(final String... names) {
        this(false, names);
    }

    @Override
    public boolean accept(I i) {
        if (i.getName() == null)
            return false;
        for (final String name : names) {
            if (contains ? i.getName().contains(name) : i.getName().equals(name))
                return true;
        }
        return false;
    }
}
