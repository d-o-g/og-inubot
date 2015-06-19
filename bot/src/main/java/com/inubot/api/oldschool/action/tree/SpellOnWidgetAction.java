/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.oldschool.action.ActionOpcodes;

public class SpellOnWidgetAction extends Action {

    public SpellOnWidgetAction(int widgetIndex, int widgetId) {
        super(ActionOpcodes.SPELL_ON_WIDGET, 0, widgetIndex, widgetId);
    }

    public static boolean isInstance(int opcode) {
        return pruneOpcode(opcode) == ActionOpcodes.SPELL_ON_WIDGET;
    }

    @Override
    public int getSignificantArgs() {
        return ARG1 | ARG2;
    }

    @Override
    public boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg1 == arg1 && this.arg2 == arg2;
    }
}
