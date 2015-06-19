/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool;

import org.runedream.api.methods.*;
import org.runedream.api.oldschool.action.ActionOpcodes;
import org.runedream.api.oldschool.action.Processable;
import org.runedream.api.oldschool.action.tree.GroundItemAction;
import org.runedream.api.util.CacheLoader;
import org.runedream.api.util.Identifiable;
import org.runedream.client.natives.RSItem;
import org.runedream.client.natives.RSItemDefinition;

import java.util.Arrays;

/**
 * @author unsigned
 * @since 22-04-2015
 */
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
    public void processAction(int opcode, String action) {
        String name = getName();
        if (name == null)
            return;
        Client.processAction(new GroundItemAction(opcode, getId(), raw.getRegionX(), raw.getRegionY()), action, name);
    }

    public void processAction(String action) {
        RSItemDefinition definition = getDefinition();
        if (definition == null)
            return;
        String[] actions = definition.getGroundActions();
        if (actions == null)
            return;
        int index = Arrays.asList(actions).indexOf(action);
        if (index == -1 && (actions[2] == null || actions[2].equals("null")) && action.equals("Take")) {
            processAction(ActionOpcodes.GROUND_ITEM_ACTION_2, action);
        } else {
            processAction(ActionOpcodes.GROUND_ITEM_ACTION_0 + index, action);
        }
    }
}
