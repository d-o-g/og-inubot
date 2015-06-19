/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.memes.blastfurnace.conveyer;

import com.inubot.api.methods.Inventory;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.client.natives.RSVarpBit;
import com.inubot.api.methods.Inventory;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.client.natives.RSVarpBit;

/**
 * @author unsigned
 * @since 29-04-2015
 */
public enum Ore {

    COPPER(436, OreStock.COPPER),
    TIN(438, OreStock.TIN),
    IRON(440, OreStock.IRON),
    SILVER(442, OreStock.SILVER),
    COAL(453, OreStock.COAL),
    GOLD(444, OreStock.GOLD),
    MITHRIL(447, OreStock.MITHRIL),
    ADAMANTITE(449, OreStock.ADAMANTITE);
    //RUNITE(-1, OreStock.RUNITE); //TODO get level for runite

    private final int itemId;
    private final RSVarpBit def;

    private Ore(final int itemId, final RSVarpBit def) {
        this.itemId = itemId;
        this.def = def;
    }

    public int getItemId() {
        return itemId;
    }

    public RSVarpBit getBit() {
        return def;
    }

    public int getStockCount() {
        return def.getValue();
    }

    public int getInventoryCount() {
        return itemId == -1 ? 0 : Inventory.getItems(new IdFilter<>(itemId)).length;
    }
}
