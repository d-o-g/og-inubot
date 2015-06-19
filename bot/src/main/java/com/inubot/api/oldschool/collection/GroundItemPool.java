/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.collection;

import com.inubot.api.oldschool.GroundItem;
import com.inubot.api.util.filter.Filter;

import java.util.ArrayList;

public class GroundItemPool extends IdentifiableEntityPool<GroundItem, GroundItemPool> {

    public GroundItemPool() {
        super(new ArrayList<>());
    }

    public GroundItemPool(final GroundItem... elements) {
        super(elements);
    }

    public GroundItemPool(final Iterable<GroundItem>... elements) {
        super(elements);
    }

    public GroundItemPool action(String action) {
        return include(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem groundItem) {
                String[] actions0 = groundItem.getDefinition().getGroundActions();
                if (action == null)
                    return false;
                for (String action0 : actions0) {
                    if (action0.equals(action))
                        return true;
                }
                return false;
            }
        });
    }
}
