/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

import org.runedream.api.methods.Players;
import org.runedream.api.oldschool.Player;
import org.runedream.client.natives.RSPlayer;

public class SpellOnPlayer extends CharacterAction {

    public SpellOnPlayer(int opcode, int entityId) {
        super(opcode, entityId);
    }

    public Player getPlayer() {
        RSPlayer[] players = Players.raw();
        int entityId = getEntityId();
        return players != null && entityId >= 0 && entityId < players.length ? new Player(players[entityId], entityId) : null;
    }
}
