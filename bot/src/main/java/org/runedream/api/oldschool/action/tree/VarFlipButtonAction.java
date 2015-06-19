/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

import org.runedream.api.oldschool.action.ActionOpcodes;

public class VarFlipButtonAction extends ButtonAction {

    public VarFlipButtonAction(int widgetUid) {
        super(ActionOpcodes.BUTTON_VAR_FLIP, widgetUid);
    }

    public static boolean isInstance(int opcode) {
        return pruneOpcode(opcode) == ActionOpcodes.BUTTON_VAR_FLIP;
    }
}
