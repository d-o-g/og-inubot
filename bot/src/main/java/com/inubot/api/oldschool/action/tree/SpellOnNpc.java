/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.client.natives.RSNpc;
import com.inubot.api.methods.Npcs;
import com.inubot.api.oldschool.Npc;

public class SpellOnNpc extends CharacterAction {

    public SpellOnNpc(int opcode, int npcIndex) {
        super(opcode, npcIndex);
    }

    public Npc npc() {
        RSNpc[] npcs = Npcs.raw();
        int entityId = getEntityId();
        return npcs != null && entityId >= 0 && entityId < npcs.length ? new Npc(npcs[entityId], entityId) : null;
    }
}
