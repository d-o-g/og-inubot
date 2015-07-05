/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action.tree;

import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;

/**
 * Named abstract since TableAction can not be named
 * The rest follow for simplicity through commonality
 */
public abstract class AbstractTableAction extends Action {

    public AbstractTableAction(int opcode, int item_id, int item_index, int table_uid) {
        super(opcode, item_id, item_index, table_uid);
    }

    @Override
    public final int getSignificantArgs() {
        return SIG_ALL;
    }

    public int getItemId() {
        return arg0;
    }

    public int getItemIndex() {
        return arg1;
    }

    public int getTableUid() {
        return arg2;
    }

    public int getParent() {
        return getTableUid() >> 16;
    }

    public int getChild() {
        return getTableUid() & 0xffff;
    }

    public int getActionIndex() {
        return -1;
    }

    public Widget getTable() {
        final int parent = getParent();
        final int child = getChild();
        return Interfaces.getWidget(parent, child);
    }

    @Override
    public final boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg0 == arg0 && this.arg1 == arg1 && this.arg2 == arg2;
    }

    @Override
    public String toString() {
        return "TableAction:[Address(uid=" + getTableUid() + ")=<" + getParent() + "#" + getChild() + "> ] " +
                "Item(index=" + getItemIndex() + ")=" /*+ getItem()*/;
    }
}
