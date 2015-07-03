/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.api.methods.Client;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.util.CacheLoader;
import com.inubot.client.natives.RSNpc;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.tree.NpcAction;
import com.inubot.api.util.Identifiable;
import com.inubot.client.natives.RSNpcDefinition;

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
    public void processAction(int opcode, String action) {
        String name = getName();
        if (name != null)
            Client.processAction(new NpcAction(opcode, arrayIndex), action, name);
    }

    public void processAction(String action) {
        if (definition == null)
            return;
        String[] actions = definition.getActions();
        if (actions == null)
            return;
        int index = Arrays.asList(actions).indexOf(action);
        if (index >= 0)
            processAction(ActionOpcodes.NPC_ACTION_0 + index, action);
    }
}
