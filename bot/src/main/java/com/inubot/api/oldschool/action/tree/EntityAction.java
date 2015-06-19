/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.methods.Game;
import com.inubot.api.oldschool.EntityType;
import com.inubot.api.oldschool.action.ActionOpcodes;

// For all entity types
public abstract class EntityAction extends Action {

    protected final EntityType type;

    public EntityAction(int opcode, int entityId, int localX, int localY) {
        super(opcode, entityId, localX, localY);
        this.type = op2EntityType(opcode); // Must match with the respected opcode
    }

    // Determines the entity type the action targets or is directly references by its opcode
    public static EntityType op2EntityType(int opcode) {
        switch (Action.pruneOpcode(opcode)) {
            case ActionOpcodes.OBJECT_ACTION_0:
            case ActionOpcodes.OBJECT_ACTION_1:
            case ActionOpcodes.OBJECT_ACTION_2:
            case ActionOpcodes.OBJECT_ACTION_3:
            case ActionOpcodes.OBJECT_ACTION_4:
            case ActionOpcodes.EXAMINE_OBJECT:
            case ActionOpcodes.ITEM_ON_OBJECT:
            case ActionOpcodes.SPELL_ON_OBJECT: {
                return EntityType.OBJECT;
            }
            case ActionOpcodes.GROUND_ITEM_ACTION_0:
            case ActionOpcodes.GROUND_ITEM_ACTION_1:
            case ActionOpcodes.GROUND_ITEM_ACTION_2:
            case ActionOpcodes.GROUND_ITEM_ACTION_3:
            case ActionOpcodes.GROUND_ITEM_ACTION_4:
            case ActionOpcodes.EXAMINE_GROUND_ITEM:
            case ActionOpcodes.ITEM_ON_GROUND_ITEM:
            case ActionOpcodes.SPELL_ON_GROUND_ITEM: {
                return EntityType.GROUND_ITEM;
            }
            case ActionOpcodes.NPC_ACTION_0:
            case ActionOpcodes.NPC_ACTION_1:
            case ActionOpcodes.NPC_ACTION_2:
            case ActionOpcodes.NPC_ACTION_3:
            case ActionOpcodes.NPC_ACTION_4:
            case ActionOpcodes.EXAMINE_NPC:
            case ActionOpcodes.ITEM_ON_NPC:
            case ActionOpcodes.SPELL_ON_NPC: {
                return EntityType.NPC;
            }
            case ActionOpcodes.PLAYER_ACTION_0:
            case ActionOpcodes.PLAYER_ACTION_1:
            case ActionOpcodes.PLAYER_ACTION_2:
            case ActionOpcodes.PLAYER_ACTION_3:
            case ActionOpcodes.PLAYER_ACTION_4:
            case ActionOpcodes.PLAYER_ACTION_5:
            case ActionOpcodes.PLAYER_ACTION_6:
            case ActionOpcodes.PLAYER_ACTION_7:
            case ActionOpcodes.ITEM_ON_PLAYER:
            case ActionOpcodes.SPELL_ON_PLAYER: {
                return EntityType.PLAYER;
            }
        }
        return null;

    }

    public int getSignificantArgs() {
        return SIG_ALL;
    }

    // The type of entity this action is targeting
    public final EntityType entityType() {
        return type;
    }

    // Not all -general- types have multiple actions, like spellOnEntity, or ItemOnEntity
    public int getActionIndex() {
        return -1;
    }

    public int getEntityId() {
        return arg0;
    }

    // The regional position of the entity when this action was created, can not be guaranteed to be real time
    public int getRegionX() {
        return arg1;
    }

    public int getRegionY() {
        return arg2;
    }

    public int getX() {
        return Game.getRegionBaseX() + getRegionX();
    }

    public int getY() {
        return Game.getRegionBaseY() + getRegionY();
    }

    @Override
    public boolean validate() {
        if (!super.validate())
            return false;
        int x = getRegionX(), y = getRegionY();
        return x >= 0 && x <= 104 && y >= 0 && y <= 104;
    }

    @Override
    public boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg0 == arg0 && this.arg1 == arg1 && this.arg2 == arg2;
    }
}
