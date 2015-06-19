/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.oldschool.action.ActionOpcodes;

public class UseItemAction extends AbstractTableAction {

    public UseItemAction(int itemId, int tableIndex, int tableId) {
        super(ActionOpcodes.USE_ITEM, itemId, tableIndex, tableId);
    }

    public static boolean isInstance(int opcode) {
        return pruneOpcode(opcode) == ActionOpcodes.USE_ITEM;
    }
}
