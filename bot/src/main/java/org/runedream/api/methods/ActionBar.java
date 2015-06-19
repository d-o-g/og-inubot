/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.methods;

import org.runedream.api.oldschool.WidgetItem;
import org.runedream.api.oldschool.actionbar.*;
import org.runedream.api.oldschool.actionbar.MassActionSlot.ActionSupplier;
import org.runedream.api.util.Paintable;
import org.runedream.api.util.filter.Filter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author unsigned
 * @since 16-05-2015
 * This class is part of make-osrs-playable
 */
public class ActionBar extends MouseKeyAdapter implements Paintable {

    private static final List<ActionNode> NODES;
    private static final Rectangle BOUNDS;
    private static final ImageIcon ACTION_BAR_IMAGE;
    private static boolean visible;

    static {
        NODES = new ArrayList<>();
        BOUNDS = new Rectangle(4, 303, 512, 36);
        ACTION_BAR_IMAGE = new ImageIcon(ActionBar.class.getClassLoader().getResource("res/actionbar.png"));
        visible = false;
        for (int i = 0; i < 8; i++)
            NODES.add(new ActionItem(i, -1, "Drop", null));
        set(1, new ActionItem(1, 2558, "Rub", "Ring of dueling(5)", "Ring of dueling"));
        set(2, new ActionNpc(2, "Attack", "Cow"));

        set(3, new MassActionSlot(3, "Ranged", v -> v.containsAction("Wield") ? "Wield" : "Wear",
                "Maple shortbow", "Green d'hide body", "Green d'hide chaps", "Green d'hide vamb"));

        set(4, new MassActionSlot(4, "Attack", v -> v.containsAction("Wield") ? "Wield" : "Wear",
                "Rune scimitar", "Rune kiteshield", "Rune full helm", "Rune platebody", "Rune plateskirt"));

        NODES.add(new GlobeButton());
    }

    public static Rectangle getBounds() {
        return BOUNDS;
    }

    /**
     * @return true if the action bar is currently toggled visible
     */
    public static boolean isVisible() {
        return visible;
    }

    /**
     * @param visible - true to set visible, else false
     */
    public static void setVisible(boolean visible) {
        ActionBar.visible = visible;
    }

    public static void delete(int slot) {
        set(slot, null);
    }

    public static void set(int slot, ActionSlot target) {
        NODES.set(slot - 1, target);
    }

    public static ActionNode getButtonAt(int slot) {
        return slot > -1 && slot < NODES.size() ? NODES.get(slot) : null;
    }

    public static ActionNode getButton(Filter<ActionNode> filter) {
        for (ActionNode button : NODES) {
            if (filter.accept(button))
                return button;
        }
        return null;
    }

    public static ActionNode[] getButtons(int... slots) {
        ActionNode[] buttons = new ActionNode[slots.length];
        for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];
            if (slot > 0 && slot < 10)
                buttons[i] = NODES.get(slot);
        }
        return buttons;
    }

    public static ActionNode[] getButtons(Filter<ActionNode> filter) {
        List<ActionNode> buttons = NODES.stream().filter(filter::accept).collect(Collectors.toList());
        return buttons.toArray(new ActionNode[buttons.size()]);
    }

    public static void clear() {
        for (int i = 0; i < NODES.size(); i++)
            NODES.set(i, null);
    }

    public static void select(int slot) {
        NODES.get(slot - 1).select(null);
    }

    @Override
    public void render(Graphics2D g) {
        if (!visible || !Game.isLoggedIn())
            return;
        g.drawImage(ACTION_BAR_IMAGE.getImage(), 4, 303, null);
        for (ActionNode slot : NODES)
            slot.render(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isVisible() && BOUNDS.contains(e.getPoint())) {
            for (ActionNode button : NODES)
                button.mouseClicked(e);
            //handle here other non-slot clicks on the action bar. special attack, globe etc
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (ActionNode button : NODES)
            button.mouseMoved(e);
    }
}
