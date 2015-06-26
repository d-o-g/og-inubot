/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.others;

import com.inubot.api.methods.Bank;
import com.inubot.api.methods.Inventory;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.Script;

/**
 * @author unsigned
 * @since 26-06-2015
 */
public class HerbCleaner extends Script {

    private static final String GRIMY = "Grimy guam leaf";

    @Override
    public int loop() {
        if (Inventory.getCount(GRIMY) == 0) {
            if (!Bank.isOpen()) {
                Bank.open();
            } else {
                Bank.depositInventory();
                WidgetItem item = Bank.getFirst(new NameFilter<>(GRIMY));
                if (item != null)
                    item.processAction("Withdraw-All");
            }
        } else {
            if (Bank.isOpen()) {
                Bank.close();
            } else {
                Inventory.apply(new NameFilter<>(GRIMY), e -> e.processAction("Clean"));
            }
        }
        return 500;
    }
}
