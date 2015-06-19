/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool;

import org.runedream.api.methods.Client;
import org.runedream.api.oldschool.action.ActionOpcodes;
import org.runedream.api.oldschool.action.Processable;
import org.runedream.api.oldschool.action.tree.*;
import org.runedream.api.util.CacheLoader;
import org.runedream.api.util.Identifiable;
import org.runedream.client.natives.RSItemDefinition;

import java.awt.*;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class WidgetItem implements Identifiable, Processable {

    //The client sets the bounds of an item as 32x32 [Constant]
    public static final Dimension DEFAULT_SIZE = new Dimension(32, 32);
    private int id;
    private int quantity;
    private int index;
    private Widget owner;

    public WidgetItem(Widget owner, int index) {
        this.id = owner.getType() == 2 ? owner.getItemIds()[index] - 1 : owner.getItemId();
        this.quantity = owner.getType() == 2 ? owner.getItemQuantities()[index] : owner.getItemQuantity();
        this.owner = owner;
        this.index = index;
    }

    public Widget getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        RSItemDefinition def = getDefinition();
        if(def == null) {
            return "";
        }
        return def.getName() != null ? def.getName() : "";
    }

    public boolean isInTable() {
        return owner.getType() == 2;
    }

    public void processAction(int opcode, String action) {
        String itemName = getName();
        if (itemName == null)
            return;
        if (isInTable()) {
            Client.processAction(new TableItemAction(opcode, getId(), getIndex(), getOwner().getRaw().getId()), action, itemName);
        } else {
            int index = Action.indexOf(owner.getActions(), action) + 1;
            if (index > 4) {
                Client.processAction(new WidgetAction(true, index, this.index, owner.getId()), action, itemName);
            } else {
                Client.processAction(new WidgetAction(opcode, index, this.index, owner.getId()), action, itemName);
            }
        }
    }

    public RSItemDefinition getDefinition() {
        return CacheLoader.findItemDefinition(isInTable() ? id : id + 1);
    }

    public void processAction(String action) {
        if (isInTable()) {
            RSItemDefinition def = getDefinition();
            if (def == null)
                return;
            processAction(ActionOpcodes.ITEM_ACTION_0 + Action.indexOf(def.getActions(), action), action);
        } else {
            int index = Action.indexOf(owner.getActions(), action) + 1;
            Client.processAction(new WidgetAction(index > 4, index, this.index, owner.getId()), action, getName());
        }
    }

    public void processAction(String action, String option) {
        if (isInTable()) {
            RSItemDefinition def = getDefinition();
            if (def == null)
                return;
            processAction(ActionOpcodes.ITEM_ACTION_0 + Action.indexOf(def.getActions(), action), action);
        } else {
            int index = Action.indexOf(owner.getActions(), action) + 1;
            Client.processAction(new WidgetAction(index > 4, index, this.index - 1, owner.getId()), action, option);
        }
    }

    public void use(WidgetItem target) {
        Client.processAction(new UseItemAction(getId(), getIndex(), getOwner().getId()), "Use", "Use");
        Client.processAction(new ItemOnItemAction(target.getId(), target.index, target.owner.getId()), "Use", "Use " + getName() + " -> " + target.getName());
    }

    public void use(GameObject target) {
        Client.processAction(new UseItemAction(getId(), getIndex(), getOwner().getId()), "Use", "Use");
        Client.processAction(new ItemOnEntityAction(ActionOpcodes.ITEM_ON_OBJECT, target.getRaw().getId(), target.getRegionX(), target.getRegionY()), "Use", "Use " + getName() + " -> " + target.getName());
    }

    public void use(GroundItem target) {
        Client.processAction(new UseItemAction(getId(), getIndex(), getOwner().getId()), "Use", "Use");
        Client.processAction(new ItemOnEntityAction(ActionOpcodes.ITEM_ON_GROUND_ITEM, target.getId(), target.getRaw().getRegionX(), target.getRaw().getRegionY()), "Use", "Use " + getName() + " -> " + target.getName());
    }

    public void drop() {
        processAction(ActionOpcodes.ITEM_ACTION_4, "Drop");
    }

    public boolean containsAction(String act) {
        RSItemDefinition def = getDefinition();
        if (def == null)
            return false;
        String[] actions = getDefinition().getActions();
        if (actions == null)
            return false;
        for (String action : actions) {
            if (action == null) continue;
            if (action.equals(act)) {
                return true;
            }
        }
        return false;
    }
}