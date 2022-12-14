/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.Inubot;
import com.inubot.api.methods.Game;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.util.filter.Filter;
import com.inubot.client.natives.oldschool.RSIntegerNode;
import com.inubot.client.natives.oldschool.RSInterfaceComponent;

import java.awt.*;

public class InterfaceComponent extends Wrapper<RSInterfaceComponent> implements Processable {

    private int index;
    private int ownerIndex;

    public InterfaceComponent(int ownerIndex, RSInterfaceComponent raw, int index) {
        super(raw);
        this.ownerIndex = ownerIndex;
        this.index = index;
    }

    public InterfaceComponent(RSInterfaceComponent raw, int index) {
        super(raw);
        this.index = index;
    }

    public int getConfig() {
        NodeTable configs = new NodeTable(Game.getClient().getInterfaceConfigs());
        RSIntegerNode node = (RSIntegerNode) configs.lookup(((long) raw.getId() << 32) + (long) raw.getIndex());
        if (node != null) {
            return node.getValue();
        }
        return raw.getConfig();
    }

    public int getXPadding() {
        return raw.getXPadding();
    }

    public int getYPadding() {
        return raw.getYPadding();
    }

    public int getSpriteId() {
        return raw.getSpriteId();
    }

    public int getModelId() {
        return raw.getModelId();
    }

    public int getShadowColor() {
        return raw.getShadowColor();
    }

    public int getTextColor() {
        return raw.getTextColor();
    }

    public int getBorderThickness() {
        return raw.getBorderThickness();
    }

    public int getFontId() {
        return raw.getFontId();
    }

    public String getSelectedAction() {
        return raw.getSelectedAction();
    }

    public int getContentType() {
        return raw.getContentType();
    }

    public InterfaceComponent[] getComponents() {
        RSInterfaceComponent[] children = raw.getChildren();
        if (children == null)
            return new InterfaceComponent[0];
        int index = 0;
        InterfaceComponent[] array = new InterfaceComponent[children.length];
        for (RSInterfaceComponent widget : children) {
            if (widget != null)
                array[index] = new InterfaceComponent(ownerIndex, widget, index);
            index++;
        }
        return array;
    }

    public int getParentHash() {
        return raw.getOwnerId();
    }

    public InterfaceComponent getOwner() {
        int uid = getParentHash();
        if (uid == -1)
            return null;
        int parent = uid >> 16;
        int child = uid & 0xFFFF;
        return Interfaces.getComponent(parent, child);
    }

    public int getX() {
        int[] positionsX = Inubot.getInstance().getClient().getInterfacePositionsX();
        int index = getBoundsArrayIndex();
        int relX = getRelativeX();
        InterfaceComponent owner = getOwner();
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
        int[] positionsY = Inubot.getInstance().getClient().getInterfacePositionsY();
        int index = getBoundsArrayIndex();
        int relY = getRelativeY();
        InterfaceComponent owner = getOwner();
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

    public int getIndex_() {
        return index;
    }

    public int[] getItemIds() {
        return raw.getItemIds();
    }

    public int[] getItemQuantities() {
        return raw.getStackSizes();
    }

    public int getMaterialId() {
        return raw.getMaterialId();
    }

    public String getText() {
        return raw.getText() != null ? raw.getText() : "";
    }

    public String[] getActions() {
        return raw.getActions();
    }

    public String[] getTableActions() {
        return raw.getTableActions();
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public boolean isHidden() {
        InterfaceComponent owner = getOwner();
        return (owner != null && owner.isHidden()) || raw.isHidden();
    }

    public boolean isExplicitlyHidden() {
        return raw.isHidden();
    }

    public boolean isVisible() {
        return getBoundsArrayIndex() != -1;
    }

    public InterfaceComponent[] getComponents(Filter<InterfaceComponent> filter) {
        RSInterfaceComponent[] children = raw.getChildren();
        if (children == null)
            return new InterfaceComponent[0];
        int index = 0;
        InterfaceComponent[] array = new InterfaceComponent[children.length];
        for (RSInterfaceComponent widget : children) {
            if (widget != null) {
                InterfaceComponent w = new InterfaceComponent(ownerIndex, widget, index);
                if (!filter.accept(w))
                    continue;
                array[index] = w;
            }
            index++;
        }
        return array;
    }

    public InterfaceComponent getComponent(Filter<InterfaceComponent> filter) {
        for (InterfaceComponent child : getComponents()) {
            if (child == null)
                continue;
            if (filter.accept(child))
                return child;
            InterfaceComponent result = child.getComponent(filter);
            if (result != null)
                return result;
        }
        return null;
    }

    public int getOwnerIndex() {
        return ownerIndex;
    }

    public boolean processAction(int opcode, String action) {
        return com.inubot.api.methods.Menu.processAction(this, action);
    }

    public boolean processAction(String action) {
        return com.inubot.api.methods.Menu.processAction(this, action);
    }

    public boolean processAction(String action, String target) {
        return com.inubot.api.methods.Menu.processAction(this, action, target);
    }

    public int getArea() {
        return getWidth() * getHeight();
    }

    public boolean containsAction(String action) {
        String[] actions = getActions();
        if (actions == null) {
            return false;
        }
        for (String action0 : actions) {
            if (action0.equals(action))
                return true;
        }
        return false;
    }

    public boolean isInteractable() {
        return raw.isInteractable();
    }
}
