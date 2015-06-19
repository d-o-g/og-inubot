/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.action.ActionOpcodes;

public abstract class ButtonAction extends Action {

    public ButtonAction(int opcode, int widgetUid) {
        super(opcode, 0, 0, widgetUid);
    }

    // widgetUid is known to be the arg2
    public static ButtonAction valueOf(int opcode, int buttonIndex, int widgetUid) {
        switch (opcode) {
            case ActionOpcodes.BUTTON_INPUT: {
                return new InputButtonAction(widgetUid);
            }
            case ActionOpcodes.BUTTON_SELECTABLE_SPELL: {
                return new SelectableSpellButtonAction(widgetUid);
            }
            case ActionOpcodes.BUTTON_CLOSE: {
                return new CloseButtonAction(widgetUid);
            }
            case ActionOpcodes.BUTTON_VAR_FLIP: {
                return new VarFlipButtonAction(widgetUid);
            }
            case ActionOpcodes.BUTTON_DIALOG: {
                return new DialogButtonAction(widgetUid, buttonIndex);
            }
        }
        return null;
    }

    public static int buttonForOpcode(final int opcode) {
        switch (Action.pruneOpcode(opcode)) {
            case ActionOpcodes.BUTTON_INPUT: {
                return Interfaces.BUTTON_INPUT;
            }
            case ActionOpcodes.BUTTON_SELECTABLE_SPELL: {
                return Interfaces.BUTTON_SPELL;
            }
            case ActionOpcodes.BUTTON_CLOSE: {
                return Interfaces.BUTTON_CLOSE;
            }
            case ActionOpcodes.BUTTON_VAR_FLIP: {
                return Interfaces.BUTTON_VAR_FLIP;
            }
            case ActionOpcodes.BUTTON_VAR_SET: {
                return Interfaces.BUTTON_VAR_SET;
            }
            case ActionOpcodes.BUTTON_DIALOG: {
                return Interfaces.BUTTON_DIALOG;
            }
        }
        return -1;
    }

    public final int getSignificantArgs() {
        return ARG2;
    }

    public int getButtonUid() {
        return arg2;
    }

    public int getButtonParent() {
        return getButtonUid() >> 16;
    }

    public int getButtonChild() {
        return getButtonUid() & 0xffff;
    }

    @Override
    public final boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg2 == arg2;
    }
}
