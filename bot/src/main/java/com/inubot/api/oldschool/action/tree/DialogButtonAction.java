/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.oldschool.action.ActionOpcodes;

public class DialogButtonAction extends ButtonAction {

    private final int buttonNumber;

    public DialogButtonAction(int widgetUid, int buttonNumber) {
        super(ActionOpcodes.BUTTON_DIALOG, widgetUid);
        this.buttonNumber = buttonNumber;
        super.arg1 = buttonNumber;
    }

    public static DialogButtonAction clickHereToContinue(int widgetUid) {
        return new DialogButtonAction(widgetUid, -1);
    }

    public static boolean isInstance(int opcode) {
        return pruneOpcode(opcode) == ActionOpcodes.BUTTON_DIALOG;
    }

    public int getButtonNumber() {
        return buttonNumber;
    }
}
