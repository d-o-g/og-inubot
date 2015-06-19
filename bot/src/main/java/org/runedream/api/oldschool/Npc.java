/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool;

import org.runedream.api.methods.Client;
import org.runedream.api.oldschool.action.ActionOpcodes;
import org.runedream.api.oldschool.action.Processable;
import org.runedream.api.oldschool.action.tree.NpcAction;
import org.runedream.api.util.CacheLoader;
import org.runedream.api.util.Identifiable;
import org.runedream.client.natives.RSNpc;
import org.runedream.client.natives.RSNpcDefinition;

import java.util.Arrays;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Npc extends Character<RSNpc> implements Identifiable, Processable {

    private final RSNpcDefinition definition;

    public Npc(RSNpc raw, int index) {
        super(raw, index);
        RSNpcDefinition rawDef = raw.getDefinition();
        this.definition = rawDef != null ? CacheLoader.findNpcDefinition(rawDef.getId()) : rawDef;
    }

    @Override
    public boolean validate() {
        return super.validate() && definition != null;
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
