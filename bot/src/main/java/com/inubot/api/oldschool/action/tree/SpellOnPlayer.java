/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.methods.Players;
import com.inubot.client.natives.oldschool.RSPlayer;
import com.inubot.api.oldschool.Player;

public class SpellOnPlayer extends CharacterAction {

    public SpellOnPlayer(int opcode, int entityId) {
        super(opcode, entityId);
    }

    public Player getPlayer() {
        RSPlayer[] players = Players.internal();
        int entityId = getEntityId();
        return players != null && entityId >= 0 && entityId < players.length ? new Player(players[entityId], entityId) : null;
    }
}
