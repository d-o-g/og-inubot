/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.actionbar;

import org.runedream.api.methods.*;
import org.runedream.api.oldschool.*;
import org.runedream.api.oldschool.Character;
import org.runedream.api.util.filter.NameFilter;

import java.awt.*;

/**
 * @author unsigned
 * @since 21-05-2015
 */
public class MassActionSlot extends ActionSlot {

    private static final int BASE_X = 21, BASE_Y = 309;
    private static final int DIM = 26;

    private final Image image;
    private final ActionSupplier<WidgetItem> supplier;
    private final String[] items;

    public MassActionSlot(int slot, String image, ActionSupplier<WidgetItem> supplier, String... items) {
        super(new Rectangle(BASE_X, BASE_Y, DIM, DIM), slot);
        this.image = Sprites.fromWikia(image);
        this.supplier = supplier;
        this.items = items;
        if (slot > 1)
            bounds.x += 34 * (slot - 1);
    }

    public ActionSupplier<WidgetItem> getSupplier() {
        return supplier;
    }

    @Override
    public void select(AWTEvent e) {
        Character target = Players.getLocal().getTarget();
        Inventory.apply(new NameFilter<>(items), item -> item.processAction(supplier.actionFor(item)));
        if (target != null && target.getTargetIndex() != -1)
            target.processAction("Attack");
    }

    @Override
    public void render(Graphics2D g) {
        if (image == null)
            return;
        double w = image.getWidth(null), h = image.getHeight(null);
        while (w > 26)
            w /= 1.2;
        while (h > 26)
            h /= 1.2;
        g.drawImage(image, bounds.x, bounds.y, (int) w, (int) h, null, null);
    }

    public interface ActionSupplier<T> {
        String actionFor(T v);
    }
}
