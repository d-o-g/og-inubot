/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.actionbar;

import org.runedream.api.methods.Npcs;
import org.runedream.api.methods.Sprites;
import org.runedream.api.oldschool.Npc;
import org.runedream.api.util.filter.Filter;
import org.runedream.api.util.filter.NameFilter;

import java.awt.*;

/**
 * @author unsigned
 * @since 21-05-2015
 */
public class ActionNpc extends ActionSlot {

    private static final int BASE_X = 21, BASE_Y = 309;
    private static final int DIM = 26;
    private final String name;
    private final Image image;
    private String wikiaName;
    private String action = "Attack";

    public ActionNpc(int slot, String action, String name) {
        super(new Rectangle(BASE_X, BASE_Y, DIM, DIM), slot);
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

    public ActionNpc(int slot, String action, String name, String wikiaName) {
        super(new Rectangle(BASE_X, BASE_Y, DIM, DIM), slot);
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void select(AWTEvent e) {
        if (name != null && action != null) {
            Npc target = Npcs.getNearest(action.equals("Attack") ? new Filter<Npc>() {
                @Override
                public boolean accept(Npc npc) {
                    return npc.getTargetIndex() == -1 && npc.getName() != null && npc.getName().equals(name);
                }
            } : new NameFilter<>(name));
            if (target == null)
                return;
            target.processAction(action);
        }
    }

    public Image getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
