/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

import org.runedream.api.oldschool.action.ActionOpcodes;

public class GroundItemAction extends EntityAction {

    public GroundItemAction(int opcode, int entityId, int localX, int localY) {
        super(opcode, entityId, localX, localY);
    }

    public static boolean isInstance(int opcode) {
        opcode = pruneOpcode(opcode);
        return opcode >= ActionOpcodes.GROUND_ITEM_ACTION_0 && opcode <= ActionOpcodes.GROUND_ITEM_ACTION_4;
    }

    public int getItemId() {
        return getEntityId();
    }

    public int getActionIndex() {
        return opcode - ActionOpcodes.GROUND_ITEM_ACTION_0;
    }
}
