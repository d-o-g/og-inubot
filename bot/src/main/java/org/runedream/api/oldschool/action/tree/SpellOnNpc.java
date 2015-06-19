/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

import org.runedream.api.methods.Npcs;
import org.runedream.api.oldschool.Npc;
import org.runedream.client.natives.RSNpc;

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
