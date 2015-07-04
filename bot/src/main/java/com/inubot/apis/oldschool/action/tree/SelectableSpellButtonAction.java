/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.oldschool.action.tree;

import com.inubot.apis.oldschool.action.ActionOpcodes;

// Selects a spell
public class SelectableSpellButtonAction extends ButtonAction {

    public SelectableSpellButtonAction(int widgetUid) {
        super(ActionOpcodes.BUTTON_SELECTABLE_SPELL, widgetUid);
    }

    public static boolean isInstance(int opcode) {
        return pruneOpcode(opcode) == ActionOpcodes.BUTTON_SELECTABLE_SPELL;
    }
}
