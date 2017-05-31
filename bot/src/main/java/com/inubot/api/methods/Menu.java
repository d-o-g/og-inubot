/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.tree.*;
import com.inubot.client.natives.oldschool.RSItemDefinition;
import com.inubot.client.natives.oldschool.RSObjectDefinition;

import java.util.Arrays;

public class Menu {

    public static boolean processAction(Npc npc, int opcode, String action) {
        if (npc == null) {
            return false;
        }
        String name = npc.getName();
        if (name != null) {
            Client.processAction(new NpcAction(opcode, npc.getArrayIndex()), action, name);
            return true;
        }
        return false;
    }

    public static boolean processAction(Npc npc, String action) {
        if (npc == null || npc.getDefinition() == null) {
            return false;
        }
        String[] actions = npc.getDefinition().getActions();
        if (actions == null) {
            return false;
        }
        int index = Action.indexOf(actions, action);
        return index >= 0 && processAction(npc, ActionOpcodes.NPC_ACTION_0 + index, action);
    }

    public static boolean processAction(Player player, int opcode, String action) {
        if (player == null) {
            return false;
        }
        String name = player.getName();
        if (name != null) {
            Client.processAction(new PlayerAction(opcode, player.getArrayIndex()), action, name);
            return true;
        }
        return false;
    }

    public static boolean processAction(Player player, String action) {
        String[] actions = Inubot.getInstance().getClient().getPlayerActions();
        if (actions == null) {
            return false;
        }
        int index = Action.indexOf(actions, action);
        return index >= 0 && processAction(player, ActionOpcodes.PLAYER_ACTION_0 + index, action);
    }

    public static boolean processAction(GameObject object, int opcode, String action) {
        if (object == null) {
            return false;
        }
        RSObjectDefinition definition = object.getDefinition();
        if (definition == null) {
            return false;
        }
        String name = definition.getName();
        if (name == null) {
            return false;
        }
        Action menuAction = Action.valueOf(opcode, object.getRaw().getId(), object.getRegionX(), object.getRegionY());
        Client.processAction(menuAction, action, name);
        return true;
    }

    public static boolean processAction(GameObject object, String action) {
        if (object == null) {
            return false;
        }
        RSObjectDefinition definition = object.getDefinition();
        if (definition == null) {
            return false;
        }
        String[] actions = definition.getActions();
        if (actions == null) {
            return false;
        }
        int index = Action.indexOf(actions, action);
        return index >= 0 && processAction(object, ActionOpcodes.OBJECT_ACTION_0 + index, action);
    }

    public static boolean processAction(InterfaceComponent interfaceComponent, String action) {
        if (interfaceComponent == null) {
            return false;
        }
        int index = Action.indexOf(interfaceComponent.getActions(), action) + 1;
        if (index == -1) {
            return false;
        }
        Client.processAction(new InterfaceComponentAction(index > 4, index,
                interfaceComponent.getParentHash() == -1 ? interfaceComponent.getIndex_() : interfaceComponent.getIndex(), interfaceComponent.getId()), action, "");
        return true;
    }

    public static boolean processAction(InterfaceComponent interfaceComponent, String action, String target) {
        if (interfaceComponent == null) {
            return false;
        }
        int index = Action.indexOf(interfaceComponent.getActions(), action) + 1;
        if (index == -1) {
            return false;
        }
        Client.processAction(new InterfaceComponentAction(index > 4, index,
                interfaceComponent.getParentHash() == -1 ? interfaceComponent.getIndex_() : -1, interfaceComponent.getId()), action, target);
        return true;
    }

    public static boolean processAction(GroundItem item, int opcode, String action) {
        if (item == null) {
            return false;
        }
        String name = item.getName();
        if (name == null) {
            return false;
        }
        Client.processAction(new GroundItemAction(opcode, item.getId(), item.getRaw().getRegionX(),
                item.getRaw().getRegionY()), action, name);
        return true;
    }

    public static boolean processAction(GroundItem item, String action) {
        if (item == null) {
            return false;
        }
        RSItemDefinition definition = item.getDefinition();
        if (definition == null) {
            return false;
        }
        String[] actions = definition.getGroundActions();
        if (actions == null) {
            return false;
        }
        int index = Arrays.asList(actions).indexOf(action);
        if (index == -1 && (actions[2] == null || actions[2].equals("null")) && action.equals("Take")) {
            return processAction(item, ActionOpcodes.GROUND_ITEM_ACTION_2, action);
        } else {
            return processAction(item, ActionOpcodes.GROUND_ITEM_ACTION_0 + index, action);
        }
    }
}
