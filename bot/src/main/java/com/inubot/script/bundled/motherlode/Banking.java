/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Time;

public class Banking {

    //Object destination
    public static final Tile BANK = new Tile(3760,5666);

    private final MotherloadMine script;

    public Banking(MotherloadMine script) {
        this.script = script;
    }

    public boolean openBank() {
        if(Bank.isClosed()) {
            if(script.walkTo(BANK)) {
                GameObject bank = GameObjects.getNearest("Bank chest");
                if(bank == null) return false;
                bank.processAction("Use");
                Time.sleep(600);
            }
        } else {
            return true;
        }
        return false;
    }

    public static void withdraw(int item) {
        if(Bank.isClosed()) return;
        int index = indexOf(item);
        if(index == -1) return;
        Game.getClient().processAction(index, 786442, 57, 1, "Withdraw-1", "", Mouse.getX(), Mouse.getY());
    }

    public static void depositAll() {
        if(Bank.isClosed()) return;
        Game.getClient().processAction(-1, 786457, 57, 1, "Deposit inventory", "", Mouse.getX(), Mouse.getY());
    }


    public static int indexOf(int itm) {
        ItemTables.Entry[] entries = ItemTables.getBank();
        for(int i = 0; i < entries.length; i++) {
            if(entries[i].getId() == itm)
                return i;
        }
        return -1;
    }
}