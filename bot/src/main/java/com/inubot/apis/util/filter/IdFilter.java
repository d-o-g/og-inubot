/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.util.filter;

import com.inubot.apis.util.Identifiable;

public class IdFilter<I extends Identifiable> implements Filter<I> {

    private final int[] ids;

    public IdFilter(final int... ids) {
        this.ids = ids;
    }

    @Override
    public boolean accept(I i) {
        for (final int id : ids) {
            if (id == i.getId())
                return true;
        }
        return false;
    }
}
