/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.oldschool.action.ActionOpcodes;

public class WalkHereAction extends NotifyingAction {

    public WalkHereAction() {
        super(ActionOpcodes.WALK_HERE);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.WALK_HERE;
    }

    public int getScreenX() {
        return arg1;
    }

    public int getScreenY() {
        return arg2;
    }
}
