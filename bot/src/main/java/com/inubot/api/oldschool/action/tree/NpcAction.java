/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.Inubot;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.util.CacheLoader;
import com.inubot.client.natives.RSNpc;
import com.inubot.api.methods.Npcs;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.client.natives.RSNpcDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class NpcAction extends CharacterAction {

    private static final Logger logger = LoggerFactory.getLogger(NpcAction.class);

    public NpcAction(int opcode, int npcIndex) {
        super(opcode, npcIndex);
    }

    public static boolean isInstance(int opcode) {
        opcode = pruneOpcode(opcode);
        return opcode >= ActionOpcodes.NPC_ACTION_0 && opcode <= ActionOpcodes.NPC_ACTION_4;
    }

    public int getActionIndex() {
        return opcode - ActionOpcodes.NPC_ACTION_0;
    }

    public int getNpcIndex() {
        return getEntityId();
    }

    public Npc getNpc() {
        int index = getNpcIndex();
        if (index < 0 || index > Short.MAX_VALUE)
            return null;
        RSNpc npc = Npcs.raw()[index];
        return npc != null ? new Npc(npc, index) : null;
    }

    public RSNpcDefinition getDefinition() {
        return CacheLoader.findNpcDefinition(getEntityId());
    }

    public String getName() {
        return getDefinition().getName();
    }

    public String getAction() {
        RSNpcDefinition def = getDefinition();
        if (def == null)
            return null;
        String[] actions = def.getActions();
        if (actions == null)
            return null;
        Inubot.LOGGER.debug(Arrays.toString(actions));
        int actionIndex = getActionIndex();
        return actionIndex >= 0 && actionIndex < actions.length ? actions[actionIndex] : null;
    }

    @Override
    public String toString() {
        return "Npc Interaction [action-name(index=" + getActionIndex() + ")=" + getAction() + "] on " + getNpc();
    }
}
