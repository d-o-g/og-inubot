/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

import org.runedream.api.oldschool.EntityType;
import org.runedream.api.oldschool.action.ActionOpcodes;

public class ExamineEntityAction extends EntityAction {

    public ExamineEntityAction(int opcode, int entity_id, int local_x, int local_y) {
        super(opcode, entity_id, local_x, local_y);
    }

    // Keeps within the scope of examine opcodes
    public static EntityType examineOp2EntityType(int opcode) {
        switch (pruneOpcode(opcode)) {
            case ActionOpcodes.EXAMINE_OBJECT: {
                return EntityType.OBJECT;
            }
            case ActionOpcodes.EXAMINE_NPC: {
                return EntityType.NPC;
            }
            case ActionOpcodes.EXAMINE_GROUND_ITEM: {
                return EntityType.GROUND_ITEM;
            }
        }
        return null;
    }
}
