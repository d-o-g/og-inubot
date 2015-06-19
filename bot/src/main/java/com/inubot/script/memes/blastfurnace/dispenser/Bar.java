/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.memes.blastfurnace.dispenser;

import com.inubot.api.methods.Inventory;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.client.natives.RSVarpBit;
import com.inubot.script.memes.blastfurnace.conveyer.Ore;

/**
 * @author unsigned
 * @since 29-04-2015
 */
public enum Bar {

    /* Ore       ID   Var              CONSTRAINTS                                                  */
    STEEL(2353, BarStock.STEEL, new BarConstraint(Ore.COAL, 1), new BarConstraint(Ore.IRON, 1)),
    MITHRIL(2359, BarStock.MITHRIL, new BarConstraint(Ore.COAL, 2), new BarConstraint(Ore.MITHRIL, 1)),
    ADAMANTITE(2361, BarStock.ADAMANTITE, new BarConstraint(Ore.COAL, 3), new BarConstraint(Ore.ADAMANTITE, 1));

    private final int itemId;
    private final RSVarpBit def;
    private final BarConstraint[] constraints;

    private Bar(final int itemId, final RSVarpBit def, final BarConstraint... constraints) {
        this.itemId = itemId;
        this.def = def;
        this.constraints = constraints;
    }

    public int getStockCount() {
        return def.getValue();
    }

    public int getItemId() {
        return itemId;
    }

    public int getInventoryCount() {
        return Inventory.getItems(new IdFilter<>(itemId)).length;
    }

    public BarConstraint[] getConstraints() {
        return constraints;
    }
}
