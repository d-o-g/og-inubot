/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.oldschool.action.tree;

// Though would be more explicit if it extends its
// type entity (eg. SpellOnNpc extends NpcAction extends ItemOnEntity),
// we can not extend multiple classes

import com.inubot.apis.oldschool.EntityType;
import com.inubot.apis.oldschool.action.ActionOpcodes;

public class ItemOnEntityAction extends EntityAction {

    public ItemOnEntityAction(int opcode, int entityId, int localX, int localY) {
        super(opcode, entityId, localX, localY);
    }

    public static boolean isInstance(int opcode) {
        switch (pruneOpcode(opcode)) {
            case ActionOpcodes.ITEM_ON_OBJECT:
            case ActionOpcodes.ITEM_ON_NPC:
            case ActionOpcodes.ITEM_ON_PLAYER:
            case ActionOpcodes.ITEM_ON_GROUND_ITEM: {
                return true;
            }
        }
        return false;
    }

    //Keeps within the scope of itemOn opcodes, when asserting an instance of itemOn
    public static EntityType itemOp2EntityType(int opcode) {
        switch (pruneOpcode(opcode)) {
            case ActionOpcodes.ITEM_ON_OBJECT: {
                return EntityType.OBJECT;
            }
            case ActionOpcodes.ITEM_ON_NPC: {
                return EntityType.NPC;
            }
            case ActionOpcodes.ITEM_ON_PLAYER: {
                return EntityType.PLAYER;
            }
            case ActionOpcodes.ITEM_ON_GROUND_ITEM: {
                return EntityType.GROUND_ITEM;
            }
        }
        return null;
    }
}
