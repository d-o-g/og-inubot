/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.oldschool.action.tree;

import com.inubot.apis.oldschool.action.ActionOpcodes;

public class ItemOnItemAction extends AbstractTableAction {

    public ItemOnItemAction(int itemId, int itemIndex, int tableId) {
        super(ActionOpcodes.ITEM_ON_ITEM, itemId, itemIndex, tableId);
    }

    public static boolean isInstance(int opcode) {
        return pruneOpcode(opcode) == ActionOpcodes.ITEM_ON_ITEM;
    }
}
