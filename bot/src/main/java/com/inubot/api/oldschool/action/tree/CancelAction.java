/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.oldschool.action.ActionOpcodes;

public class CancelAction extends NotifyingAction {

    public CancelAction() {
        super(ActionOpcodes.CANCEL);
    }

    public static boolean isInstance(int opcode) {
        return opcode == ActionOpcodes.CANCEL;
    }
}
