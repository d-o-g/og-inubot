/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.oldschool.action.ActionOpcodes;

public class InterfaceComponentAction extends Action {

    public InterfaceComponentAction(int opcode, int actionIndex, int widgetIndex, int widgetId) {
        super(opcode, actionIndex, widgetIndex, widgetId);
    }

    public InterfaceComponentAction(boolean type2, int actionIndex, int widgetIndex, int widgetId) {
        super(type2 ? ActionOpcodes.COMPONENT_ACTION_2 : ActionOpcodes.COMPONENT_ACTION,
                actionIndex, widgetIndex, widgetId);
    }

    public static boolean isInstance(int opcode) {
        opcode = pruneOpcode(opcode);
        return opcode == ActionOpcodes.COMPONENT_ACTION || opcode == ActionOpcodes.COMPONENT_ACTION_2;
    }

    @Override
    public int getSignificantArgs() {
        return SIG_ALL;
    }

    // Arg0 is automatically lowered by one when is derived from the client, when its a widgetAction
    public int getActionIndex() {
        return arg0;
    }

    public int getInterfaceIndex() {
        return arg1;
    }

    public int getComponentUid() {
        return arg2;
    }

    public boolean isType2() {
        return opcode == ActionOpcodes.COMPONENT_ACTION_2;
    }

   /* public InterfaceComponent get() {
        final int UID = getParentUID();
        final int parent0 = Interface.getParentIndex(UID);
        final int child0  = Interface.getChildIndex(UID);
        final int index0  = widgetIndex();
        RSClient client = Game.getClient();
        RSInterface parent = client.getInterfaces()[parent0][child0];
        if(parent == null) return null;
        RSInterface[] children = parent.getComponents();
        if(children != null && index0 > 0 && index0 < children.length)
            return children[index0];
        return parent;
    }*/


    @Override
    public boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg0 == arg0 && this.arg1 == arg1 && this.arg2 == arg2;
    }

    @Override
    public String toString() {
        final int uid = getComponentUid();
        final int parent = getInterfaceIndex();
        final int child = uid & 0xffff;
        final int index = child & 0xffff;
        final int action = getActionIndex();
        final int type = isType2() ? 2 : 1;
        return "InterfaceComponentAction:[Address=<" + parent + "#" + child + "#" + index + "> | ActionIndex=" + action +
                " | ActionType=" + type + "]" /*+ get()*/;
    }
}
