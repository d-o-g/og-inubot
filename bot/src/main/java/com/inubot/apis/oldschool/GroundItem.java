/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.oldschool;

import com.inubot.apis.methods.Client;
import com.inubot.apis.methods.Game;
import com.inubot.apis.methods.Players;
import com.inubot.apis.methods.Projection;
import com.inubot.apis.oldschool.action.Processable;
import com.inubot.apis.oldschool.action.tree.GroundItemAction;
import com.inubot.apis.util.CacheLoader;
import com.inubot.client.natives.RSItem;
import com.inubot.client.natives.RSItemDefinition;
import com.inubot.apis.oldschool.action.ActionOpcodes;
import com.inubot.apis.util.Identifiable;

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
