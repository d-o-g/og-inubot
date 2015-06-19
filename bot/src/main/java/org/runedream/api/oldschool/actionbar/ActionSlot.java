/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.actionbar;

import java.awt.*;

/**
 * @author unsigned
 * @since 17-05-2015
 * Represents a square slot on the action bar
 */
public abstract class ActionSlot extends ActionNode {

    private final int slot;

    protected ActionSlot(Rectangle bounds, int slot) {
        super(bounds);
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
