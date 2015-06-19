/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.actionbar;

import org.runedream.api.util.Paintable;

import java.awt.*;
import java.awt.event.*;

/**
 * @author unsigned
 * @since 17-05-2015
 * Represents an entity on the action bar
 */
public abstract class ActionNode extends MouseKeyAdapter implements Paintable {

    protected final Rectangle bounds;
    private boolean hovered = false;

    protected ActionNode(Rectangle bounds) {
        this.bounds = bounds;
    }

    public boolean isHovered() {
        return hovered;
    }

    /**
     * @param e can be key or mouse
     */
    public abstract void select(AWTEvent e);

    @Override
    public final void mouseClicked(MouseEvent e) {
        if (!bounds.contains(e.getPoint()))
            return;
        select(e);
    }

    @Override
    public final void mouseMoved(MouseEvent e) {
        hovered = bounds.contains(e.getPoint());
    }
}
