/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes.blastfurnace.dispenser;

import org.runedream.script.memes.blastfurnace.conveyer.Ore;

/**
 * @author unsigned
 * @since 29-04-2015
 */
public class BarConstraint {

    private final Ore ore;
    private final int quantity;

    public BarConstraint(final Ore ore, final int quantity) {
        this.ore = ore;
        this.quantity = quantity;
    }

    public Ore getOre() {
        return ore;
    }

    public int getQuantity() {
        return quantity;
    }
}

