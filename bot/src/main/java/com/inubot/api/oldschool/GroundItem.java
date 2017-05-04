/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.oldschool.action.tree.GroundItemAction;
import com.inubot.api.util.CacheLoader;
import com.inubot.client.natives.oldschool.RSItem;
import com.inubot.client.natives.oldschool.RSItemDefinition;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.util.Identifiable;

import java.util.Arrays;

public class GroundItem extends Wrapper<RSItem> implements Locatable, Identifiable, Processable {

    private final int plane;

    public GroundItem(RSItem raw, int plane) {
        super(raw);
        this.plane = plane;
    }

    public int getId() {
        return raw.getId();
    }

    public int getQuantity() {
        return raw.getStackSize();
    }

    @Override
    public Tile getLocation() {
        return new Tile(Game.getRegionBaseX() + raw.getRegionX(), Game.getRegionBaseY() + raw.getRegionY(), plane);
    }

    @Override
    public Model getModel() {
        return raw.getModel();
    }

    @Override
    public int distance(Locatable locatable) {
        return (int) Projection.distance(this, locatable);
    }

    @Override
    public int distance() {
        return distance(Players.getLocal());
    }

    public RSItemDefinition getDefinition() {
        return CacheLoader.findItemDefinition(getId());
    }

    public String getName() {
        RSItemDefinition def = getDefinition();
        return def == null ? null : def.getName();
    }

    @Override
    public boolean processAction(int opcode, String action) {
        return Menu.processAction(this, opcode, action);
    }

    public boolean processAction(String action) {
        return Menu.processAction(this, action);
    }

    @Override
    public String[] getActions() {
        RSItemDefinition definition = getDefinition();
        if (definition != null) {
            return definition.getGroundActions();
        }
        return new String[0];
    }
}
