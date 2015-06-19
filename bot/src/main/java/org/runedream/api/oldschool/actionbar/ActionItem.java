/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.actionbar;

import org.runedream.api.methods.Inventory;
import org.runedream.api.methods.Sprites;
import org.runedream.api.oldschool.WidgetItem;
import org.runedream.api.util.filter.IdFilter;

import java.awt.*;

/**
 * @author unsigned
 * @since 16-05-2015
 * This class is part of make-osrs-playable
 * Notes:
 * - Click the action to select it
 * - Ctrl+Click the action to delete it
 */
public class ActionItem extends ActionSlot {

    private static final int BASE_X = 21, BASE_Y = 309;
    private static final int DIM = 26;
    private final String name;
    private final Image image;
    private String wikiaName;
    private int itemId;
    private String action = "Drop";

    public ActionItem(int slot, int itemId, String action, String name) {
        super(new Rectangle(BASE_X, BASE_Y, DIM, DIM), slot);
        this.itemId = itemId;
        this.action = action;
        this.name = this.wikiaName = name;
        if (wikiaName != null) {
            if (wikiaName.contains("("))
                wikiaName = wikiaName.substring(0, wikiaName.indexOf('('));
            this.image = Sprites.fromWikia(wikiaName);
        } else this.image = null;
        if (slot > 1)
            bounds.x += 34 * (slot - 1);
    }

    public ActionItem(int slot, int itemId, String action, String name, String wikiaName) {
        super(new Rectangle(BASE_X, BASE_Y, DIM, DIM), slot);
        this.itemId = itemId;
        this.action = action;
        this.name = name;
        this.image = wikiaName != null ? Sprites.fromWikia(wikiaName) : null;
        if (slot > 1)
            bounds.x += 34 * (slot - 1);
    }

    @Override
    public void render(Graphics2D g) { //TODO possibly hover effects with mouseMoved? no the background slot already changes colour
        if (image == null)
            return;
        double w = image.getWidth(null), h = image.getHeight(null);
        while (w > 26)
            w /= 1.2;
        while (h > 26)
            h /= 1.2;
        g.drawImage(image, bounds.x, bounds.y, (int) w, (int) h, null, null);
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void select(AWTEvent e) {
        if (itemId != -1 && action != null) {
            WidgetItem item = Inventory.getFirst(new IdFilter<>(itemId));
            if (item == null || action.equals("Use")) //make abstraction and add UseActionButton etc
                return;
            if (action.equals("Drop")) {
                item.drop();
            } else {
                item.processAction(action);
            }
        }
    }

    public Image getImage() {
        return image;
    }
}
