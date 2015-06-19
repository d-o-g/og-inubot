/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.oldschool.action.ActionOpcodes;

public class TableAction extends AbstractTableAction {

    public TableAction(int opcode, int item_id, int item_index, int containerUID) {
        super(opcode, item_id, item_index, containerUID);
    }

    public static int actionIndexOpcode(int index) {
        return index < 0 || index > 4 ? -1 : ActionOpcodes.TABLE_ACTION_0 + index;
    }

    public static boolean isInstance(int opcode) {
        opcode = pruneOpcode(opcode);
        return opcode >= ActionOpcodes.TABLE_ACTION_0 && opcode <= ActionOpcodes.TABLE_ACTION_4;
    }

    public int actionIndex() {
        return opcode - ActionOpcodes.TABLE_ACTION_0;
    }
}
