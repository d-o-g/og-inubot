/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.skill.constraints;

import com.inubot.api.methods.ItemTables;
import com.inubot.api.oldschool.skill.Resource;

import java.util.*;

public class ResourceConstraint implements Constraint {

    private final List<Resource> resources;

    public ResourceConstraint() {
        this.resources = new ArrayList<>();
    }

    public ResourceConstraint(Resource... resources) {
        this();
        Collections.addAll(this.resources, resources);
    }

    public void add(Resource... resources) {
        Collections.addAll(this.resources, resources);
    }

    @Override
    public boolean isMet() {
        boolean met = true;
        for (Resource resource : resources) {
            int table = resource.isEquipment() ? ItemTables.EQUIPMENT : ItemTables.INVENTORY;
            if (!contains(table, resource)) {
                met = false;
                break;
            }
        }
        return met;
    }

    private boolean contains(int tableKey, Resource resource) {
        for (ItemTables.Entry entry : ItemTables.getEntriesIn(tableKey)) {
            if (resource.getId() == entry.getId() && entry.getQuantity() >= resource.getQuantity()) {
                return true;
            }
        }
        return false;
    }
}
