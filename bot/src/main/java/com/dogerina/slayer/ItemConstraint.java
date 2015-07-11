/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogerina.slayer;

import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.ItemTables;
import com.inubot.api.methods.ItemTables.Entry;

/**
 * @author Dogerina
 * @since 07-07-2015
 */
public final class ItemConstraint implements Constraint {

    private final String name;
    private final boolean equipment;

    public ItemConstraint(String name, boolean equipment) {
        this.name = name;
        this.equipment = equipment;
    }

    public ItemConstraint(String name) {
        this(name, false);
    }

    @Override
    public boolean isMet() {
        if (equipment) {
            for (Entry entry : ItemTables.getEntriesIn(ItemTables.EQUIPMENT)) {
                if (entry.getName().equals(name))
                    return true;
            }
            return false;
        }
        return Inventory.contains(name);
    }
}
