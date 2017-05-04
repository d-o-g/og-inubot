/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.api.methods.Client;
import com.inubot.api.methods.Menu;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.util.CacheLoader;
import com.inubot.client.natives.oldschool.RSNpc;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.tree.NpcAction;
import com.inubot.api.util.Identifiable;
import com.inubot.client.natives.oldschool.RSNpcDefinition;

import java.util.Arrays;

public class Npc extends Character<RSNpc> implements Identifiable, Processable {

    private final RSNpcDefinition definition;

    public Npc(RSNpc raw, int index) {
        super(raw, index);
        RSNpcDefinition rawDef = raw.getDefinition();
        this.definition = rawDef != null ? CacheLoader.findNpcDefinition(rawDef.getId()) : rawDef;
    }

    @Override
    public boolean validate() {
        return definition != null;
    }

    @Override
    public int getId() {
        return definition.getId();
    }

    public RSNpcDefinition getDefinition() {
        return definition;
    }

    @Override
    public String getName() {
        return definition == null ? null : definition.getName();
    }

    @Override
    public boolean processAction(int opcode, String action) {
        return Menu.processAction(this, opcode, action);
    }

    @Override
    public boolean processAction(String action) {
        return Menu.processAction(this, action);
    }

    @Override
    public String[] getActions() {
        RSNpcDefinition definition = getDefinition();
        if (definition != null) {
            return definition.getActions();
        }
        return new String[0];
    }
}
