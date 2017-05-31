/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.Inubot;
import com.inubot.api.methods.Menu;
import com.inubot.client.natives.oldschool.RSPlayer;

public class Player extends PathingEntity<RSPlayer> {

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
        return Inubot.getInstance().getClient().getPlayerActions();
    }

    @Override
    public boolean processAction(int opcode, String action) {
        return Menu.processAction(this, opcode, action);
    }

    @Override
    public boolean processAction(String action) {
        return Menu.processAction(this, action);
    }
}
