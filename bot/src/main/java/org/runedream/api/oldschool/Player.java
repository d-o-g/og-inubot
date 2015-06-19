/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool;

import org.runedream.RuneDream;
import org.runedream.api.methods.Client;
import org.runedream.api.oldschool.action.ActionOpcodes;
import org.runedream.api.oldschool.action.tree.Action;
import org.runedream.api.oldschool.action.tree.PlayerAction;
import org.runedream.client.natives.RSPlayer;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Player extends Character<RSPlayer> {

    public Player(RSPlayer raw, int index) {
        super(raw, index);
    }

    @Override
    public int getId() {
        return arrayIndex;
    }

    public String getName() {
        return raw.getName();
    }

    public String[] getActions() {
        return RuneDream.getInstance().getClient().getPlayerActions();
    }

    @Override
    public void processAction(int opcode, String action) {
        String name = getName();
        if (name != null)
            Client.processAction(new PlayerAction(opcode, arrayIndex), action, name);
    }

    public void processAction(String action) {
        String[] actions = RuneDream.getInstance().getClient().getPlayerActions();
        if (actions == null)
            return;
        int index = Action.indexOf(actions, action);
        if (index >= 0)
            processAction(ActionOpcodes.PLAYER_ACTION_0 + index, action);
    }
}
