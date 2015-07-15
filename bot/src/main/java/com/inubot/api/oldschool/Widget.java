/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.Inubot;
import com.inubot.api.methods.Client;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.oldschool.action.tree.WidgetAction;
import com.inubot.api.util.filter.Filter;
import com.inubot.client.natives.oldschool.RSWidget;

import java.awt.*;

public class Widget extends Wrapper<RSWidget> implements Processable {

    private int index;
    private int ownerIndex;

    public Widget(int ownerIndex, RSWidget raw, int index) {
        super(raw);
        this.ownerIndex = ownerIndex;
        this.index = index;
    }

    public Widget(RSWidget raw, int index) {
        super(raw);
        this.index = index;
    }

    public Widget[] getChildren() {
        RSWidget[] children = raw.getChildren();
        if (children == null)
            return new Widget[0];
        int index = 0;
        Widget[] array = new Widget[children.length];
        for (RSWidget widget : children) {
            if (widget != null)
                array[index] = new Widget(ownerIndex, widget, index);
            index++;
        }
        return array;
    }

    public int getParentHash() {
        return raw.getOwnerId();
    }

    public Widget getOwner() {
        int uid = getParentHash();
        if (uid == -1)
            return null;
        int parent = uid >> 16;
        int child = uid & 0xFFFF;
        return Interfaces.getWidget(parent, child);
    }

    public int getX() {
        int[] positionsX = Inubot.getInstance().getClient().getWidgetPositionsX();
        int index = getBoundsArrayIndex();
        int relX = getRelativeX();
        Widget owner = getOwner();
        int x = 0;
        if (owner != null) {
            x = owner.getX() - getInsetX();
        } else {
            if (index >= 0 && positionsX != null && positionsX[index] > 0) {
                int absX = positionsX[index];
                if (getType() > 0)
                    absX += relX;
                return absX;
            }
        }
        return x + relX;
    }

    public int getY() {
        int[] positionsY = Inubot.getInstance().getClient().getWidgetPositionsY();
        int index = getBoundsArrayIndex();
        int relY = getRelativeY();
        Widget owner = getOwner();
        int x = 0;
        if (owner != null) {
            x = owner.getY() - getInsetY();
        } else {
            if (index >= 0 && positionsY != null && positionsY[index] > 0) {
                int absY = positionsY[index];
                if (getType() > 0)
                    absY += relY;
                return absY;
            }
        }
        return x + relY;
    }

    public int getId() {
        return raw.getId();
    }

    public int getBoundsArrayIndex() {
        return raw.getBoundsIndex();
    }

    public int getItemId() {
        return raw.getItemId();
    }

    public int getItemQuantity() {
        return raw.getItemAmount();
    }

    public int getRelativeX() {
        return raw.getX();
    }

    public int getRelativeY() {
        return raw.getY();
    }

    public int getWidth() {
        return raw.getWidth();
    }

    public int getHeight() {
        return raw.getHeight();
    }

    public int getInsetX() {
        return raw.getScrollX();
    }

    public int getInsetY() {
        return raw.getScrollY();
    }

    public int getType() {
        return raw.getType();
    }

    public int getIndex() {
        return raw.getIndex();
    }

    public int[] getItemIds() {
        return raw.getItemIds();
    }

    public int[] getItemQuantities() {
        return raw.getStackSizes();
    }

    public int getTextureId() {
        return raw.getTextureId();
    }

    public String getText() {
        return raw.getText() != null ? raw.getText() : "";
    }

    public String[] getActions() {
        return raw.getActions();
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public boolean isHidden() {
        Widget owner = getOwner();
        return (owner != null && owner.isHidden()) || raw.isHidden();
    }

    public boolean isExplicitlyHidden() {
        return raw.isHidden();
    }

    public boolean isVisible() {
        return getBoundsArrayIndex() != -1;
    }

    public Widget[] getChildren(Filter<Widget> filter) {
        RSWidget[] children = raw.getChildren();
        if (children == null)
            return new Widget[0];
        int index = 0;
        Widget[] array = new Widget[children.length];
        for (RSWidget widget : children) {
            if (widget != null) {
                Widget w = new Widget(ownerIndex, widget, index);
                if (!filter.accept(w))
                    continue;
                array[index] = w;
            }
            index++;
        }
        return array;
    }

    public Widget getChild(Filter<Widget> filter) {
        for (Widget child : getChildren()) {
            if (child == null)
                continue;
            if (filter.accept(child))
                return child;
            Widget result = child.getChild(filter);
            if (result != null)
                return result;
        }
        return null;
    }

    public int getOwnerIndex() {
        return ownerIndex;
    }

    /**
     * Use #processAction(String) instead
     */
    @Deprecated
    public void processAction(int opcode, String action) {
        int index = Action.indexOf(getActions(), action) + 1;
        Client.processAction(new WidgetAction(index > 4, index, getParentHash() == -1 ? this.index : -1, getId()), action, "");
    }

    public void processAction(String action) {
        int index = Action.indexOf(getActions(), action) + 1;
        Client.processAction(new WidgetAction(index > 4, index, getParentHash() == -1 ? this.index : -1, getId()), action, "");
    }

    public void processAction(String action, String target) {
        int index = Action.indexOf(getActions(), action) + 1;
        Client.processAction(new WidgetAction(index > 4, index, getParentHash() == -1 ? this.index : -1, getId()), action, target);
    }

    public int getArea() {
        return getWidth() * getHeight();
    }

    public boolean containsAction(String action) {
        String[] actions = getActions();
        if (actions == null)
            return false;
        for (String action0 : actions) {
            if (action0.equals(action))
                return true;
        }
        return false;
    }
}
