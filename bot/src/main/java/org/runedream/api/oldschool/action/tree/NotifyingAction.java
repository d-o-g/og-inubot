/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

import org.runedream.api.oldschool.action.ActionOpcodes;

//Any action that holds no information about what its interaction with,
// or in other words, it has no useful augments.
public abstract class NotifyingAction extends Action {

    public NotifyingAction(int opcode) {
        super(opcode, 0, 0, 0);
    }

    public static NotifyingAction valueOf(int opcode) {
        switch (Action.pruneOpcode(opcode)) {
            case ActionOpcodes.CANCEL:
                return new CancelAction();
            case ActionOpcodes.WALK_HERE:
                return new WalkHereAction();
        }
        return null;
    }

    @Override
    public final int getSignificantArgs() {
        return 0;
    }

    @Override
    public final boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode;
    }
}
