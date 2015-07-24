/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.client.natives.oldschool.RSPlayer;
import com.inubot.api.oldschool.Player;

public class PlayerAction extends CharacterAction {

    public PlayerAction(int opcode, int player_index) {
        super(opcode, player_index);
    }

    public static boolean isInstance(int opcode) {
        opcode = pruneOpcode(opcode);
        return opcode >= ActionOpcodes.PLAYER_ACTION_0
                && opcode <= ActionOpcodes.PLAYER_ACTION_7;
    }

    public int getArrayIndex() {
        return arg0;
    }

    public int getActionIndex() {
        return opcode - ActionOpcodes.PLAYER_ACTION_0;
    }

    public Player getPlayer() {
        int index = getArrayIndex();
        if (index < 0 || index > Players.MAX_PLAYERS)
            return null;
        RSPlayer player = Players.raw()[index];
        return player != null ? new Player(player, index) : null;
    }

    @Override
    public String toString() {
        return "Player Action[" + getArrayIndex() + "](" + "@" + getActionIndex() + ") on " + getPlayer();
    }
}
