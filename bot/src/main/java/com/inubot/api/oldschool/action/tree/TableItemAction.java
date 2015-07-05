/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.oldschool.action.ActionOpcodes;

/**
 * Item actions only occur on widget tables
 */
public class TableItemAction extends AbstractTableAction {

    public TableItemAction(int opcode, int itemId, int itemIndex, int containerUid) {
        super(opcode, itemId, itemIndex, containerUid);
    }

    public static int actionIndexOpcode(int index) {
        if (index < 0 || index > 4) return -1;
        return ActionOpcodes.ITEM_ACTION_0 + index;
    }

    public static boolean isInstance(int op) {
        return op >= ActionOpcodes.ITEM_ACTION_0
                && op <= ActionOpcodes.ITEM_ACTION_4;
    }

    public int getActionIndex() {
        return opcode - ActionOpcodes.ITEM_ACTION_0;
    }

    /*  public Widget getContainer() {
          final int parent = parent();
          final int child = child();
          return Widget.get(parent, child);
      }

      public Item getItem() {
          Widget container = getContainer();
          if(container == null) return null;
          return new WidgetItem(container,itemIndex());
      }
  */
    @Override
    public String toString() {
        final int parent = getParent();
        final int child = getChild();
        final int index = getItemIndex();
        final int address = getTableUid();
        return "ItemAction:[TableAddress(" + address + "," + index + ")=<" + parent + "#" + child + "#" + index +
                "> | ItemId=" + getItemId() + " | ItemIndex=" + getItemIndex() + " | ActionIndex=" + getActionIndex() + "]";
    }
}
