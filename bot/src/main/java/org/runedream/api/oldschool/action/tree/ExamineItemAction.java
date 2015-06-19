/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

import org.runedream.api.oldschool.action.ActionOpcodes;

public class ExamineItemAction extends AbstractTableAction {

    public ExamineItemAction(int itemId, int itemIndex, int tableId) {
        super(ActionOpcodes.EXAMINE_ITEM, itemId, itemIndex, tableId);
    }

    public static boolean isInstance(int opcode) {
        return pruneOpcode(opcode) == ActionOpcodes.EXAMINE_ITEM;
    }
}
