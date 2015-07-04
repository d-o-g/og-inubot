/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.oldschool.action.tree;

// Though would be more explicit if it extends its
// type entity (eg. SpellOnNpc extends NpcAction extends SpellAction),
// we can not extend multiple classes

// ^ It's more important that its marked as a entity action,
// with the entity type known through EntityAction.entityType

import com.inubot.apis.oldschool.EntityType;
import com.inubot.apis.oldschool.action.ActionOpcodes;

public class SpellOnEntityAction extends EntityAction {

    public SpellOnEntityAction(int opcode, int entityId, int localX, int localY) {
        super(opcode, entityId, localX, localY);
    }

    public static EntityType spellOp2EntityType(int opcode) {
        switch (pruneOpcode(opcode)) {
            case ActionOpcodes.SPELL_ON_OBJECT: {
                return EntityType.OBJECT;
            }
            case ActionOpcodes.SPELL_ON_NPC: {
                return EntityType.NPC;
            }
            case ActionOpcodes.SPELL_ON_PLAYER: {
                return EntityType.PLAYER;
            }
            case ActionOpcodes.SPELL_ON_GROUND_ITEM: {
                return EntityType.GROUND_ITEM;
            }
        }
        return null;
    }

    @Override
    public boolean validate() {
        return super.validate() && type != null;
    }
}
