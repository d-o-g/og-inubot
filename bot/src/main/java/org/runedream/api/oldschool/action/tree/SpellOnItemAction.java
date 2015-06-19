/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

import org.runedream.api.oldschool.action.ActionOpcodes;

// We can extend out respected action type since we are not a entity, and allow for more simplicity
public class SpellOnItemAction extends AbstractTableAction {

    public SpellOnItemAction(int itemId, int itemIndex, int tableId) {
        super(ActionOpcodes.SPELL_ON_ITEM, itemId, itemIndex, tableId);
    }

    public static boolean isInstance(int opcode) {
        return pruneOpcode(opcode) == ActionOpcodes.SPELL_ON_ITEM;
    }
}
